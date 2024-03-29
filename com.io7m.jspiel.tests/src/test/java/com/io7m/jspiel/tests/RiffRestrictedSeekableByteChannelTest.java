/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jspiel.tests;

import com.io7m.jspiel.api.RiffOutOfBoundsException;
import com.io7m.jspiel.vanilla.RiffRestrictedSeekableByteChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public final class RiffRestrictedSeekableByteChannelTest
{
  @Test
  public void testEmpty()
    throws Exception
  {
    final var path = Files.createTempFile("restricted-bytechannel-", ".bin");

    try (var base = FileChannel.open(path, WRITE)) {
      Assertions.assertThrows(IllegalArgumentException.class, () -> {
        RiffRestrictedSeekableByteChannel.create(base, 0L, 0L, false);
      });
    }
  }

  @Test
  public void testPositionAnywhereOK()
    throws Exception
  {
    final var path = Files.createTempFile("restricted-bytechannel-", ".bin");

    try (var base = FileChannel.open(path, WRITE)) {
      try (var channel = RiffRestrictedSeekableByteChannel.create(base, 0L, 1L, false)) {
        channel.position(1_0L);
        channel.position(1_00L);
        channel.position(1_000L);
      }
    }
  }

  @Test
  public void testWriteCorrect()
    throws Exception
  {
    final var path = Files.createTempFile("restricted-bytechannel-", ".bin");

    try (var base = FileChannel.open(path, WRITE, READ)) {
      try (var channel = RiffRestrictedSeekableByteChannel.create(base, 10L, 20L, false)) {
        channel.position(0L);
        channel.write(ByteBuffer.wrap(new byte[]{(byte) 0xff}));

        Assertions.assertEquals(1L, channel.position(), "Correct channel position");
        Assertions.assertEquals(11L, base.position(), "Correct channel position");

        final var buffer = ByteBuffer.allocate(1);
        base.read(buffer, 10L);

        Assertions.assertEquals(0xff, (int) buffer.get(0) & 0xff, "Correct read byte");
      }
    }
  }

  @Test
  public void testWriteOutOfRange()
    throws Exception
  {
    final var path = Files.createTempFile("restricted-bytechannel-", ".bin");

    try (var base = FileChannel.open(path, WRITE, READ)) {
      try (var channel = RiffRestrictedSeekableByteChannel.create(base, 10L, 20L, false)) {
        channel.position(20L);

        final var ex = Assertions.assertThrows(RiffOutOfBoundsException.class, () -> {
          channel.write(ByteBuffer.wrap(new byte[]{(byte) 0xff}));
        });

        Assertions.assertTrue(ex.getMessage().contains("[0, 10)"));
      }
    }
  }

  @Test
  public void testReadCorrect()
    throws Exception
  {
    final var path = Files.createTempFile("restricted-bytechannel-", ".bin");

    try (var base = FileChannel.open(path, WRITE, READ)) {
      try (var channel = RiffRestrictedSeekableByteChannel.create(base, 10L, 20L, false)) {
        base.position(10L);
        base.write(ByteBuffer.wrap(new byte[]{(byte) 0xff}));

        channel.position(0L);
        Assertions.assertEquals(0L, channel.position(), "Correct channel position");
        Assertions.assertEquals(10L, base.position(), "Correct channel position");

        final var buffer = ByteBuffer.allocate(1);
        channel.read(buffer);

        Assertions.assertEquals(0xff, (int) buffer.get(0) & 0xff, "Correct read byte");
      }
    }
  }

  @Test
  public void testReadOutOfRange()
    throws Exception
  {
    final var path = Files.createTempFile("restricted-bytechannel-", ".bin");

    try (var base = FileChannel.open(path, WRITE, READ)) {
      try (var channel = RiffRestrictedSeekableByteChannel.create(base, 10L, 20L, false)) {
        channel.position(20L);

        final var ex = Assertions.assertThrows(RiffOutOfBoundsException.class, () -> {
          channel.read(ByteBuffer.wrap(new byte[]{(byte) 0xff}));
        });

        Assertions.assertTrue(ex.getMessage().contains("[0, 10)"));
      }
    }
  }
}
