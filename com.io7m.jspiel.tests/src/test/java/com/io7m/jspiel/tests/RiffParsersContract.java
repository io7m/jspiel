/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> http://io7m.com
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

import com.io7m.jspiel.api.RiffFileParserProviderType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

public abstract class RiffParsersContract
{
  private static ByteBuffer copyToByteBuffer(
    final String name)
    throws IOException
  {
    final var path = "/com/io7m/jspiel/tests/" + name;
    try (var stream = RiffParsersContract.class.getResourceAsStream(path)) {
      try (var output = new ByteArrayOutputStream(1024)) {
        stream.transferTo(output);
        return ByteBuffer.wrap(output.toByteArray());
      }
    }
  }

  protected abstract RiffFileParserProviderType parsers();

  @Test
  public final void test000_12_be()
    throws Exception
  {
    final var data = copyToByteBuffer("000_12_be.wav");
    final var parsers = this.parsers();
    final var parser = parsers.createForByteBuffer(URI.create("000_12_be.wav"), data);
    final var chunks = parser.parse();

    Assertions.assertEquals(1L, (long) chunks.size(), "Expected one chunk");
    final var chunk = chunks.get(0);
    Assertions.assertEquals("RIFX", chunk.name().value(), "Chunk name matches");
    Assertions.assertEquals("WAVE", chunk.formType().get(), "Form type matches");
    Assertions.assertEquals(192050L, chunk.dataSize(), "Size matches");
    Assertions.assertEquals(3L, (long) chunk.subChunks().size(), "Expected 3 subchunks");

    final var sc0 = chunk.subChunks().get(0);
    Assertions.assertEquals("fmt ", sc0.name().value(), "Chunk name matches");
    Assertions.assertEquals(18L, sc0.dataSize(), "Size matches");

    final var sc1 = chunk.subChunks().get(1);
    Assertions.assertEquals("fact", sc1.name().value(), "Chunk name matches");
    Assertions.assertEquals(4L, sc1.dataSize(), "Size matches");

    final var sc2 = chunk.subChunks().get(2);
    Assertions.assertEquals("data", sc2.name().value(), "Chunk name matches");
    Assertions.assertEquals(192000L, sc2.dataSize(), "Size matches");
  }

  @Test
  public final void test000_12_le()
    throws Exception
  {
    final var data = copyToByteBuffer("000_12_le.wav");
    final var parsers = this.parsers();
    final var parser = parsers.createForByteBuffer(URI.create("000_12_le.wav"), data);
    final var chunks = parser.parse();

    Assertions.assertEquals(1L, (long) chunks.size(), "Expected one chunk");
    final var chunk = chunks.get(0);
    Assertions.assertEquals("RIFF", chunk.name().value(), "Chunk name matches");
    Assertions.assertEquals("WAVE", chunk.formType().get(), "Form type matches");
    Assertions.assertEquals(192036L, chunk.dataSize(), "Size matches");
    Assertions.assertEquals(2L, (long) chunk.subChunks().size(), "Expected 3 subchunks");

    final var sc0 = chunk.subChunks().get(0);
    Assertions.assertEquals("fmt ", sc0.name().value(), "Chunk name matches");
    Assertions.assertEquals(16L, sc0.dataSize(), "Size matches");

    final var sc1 = chunk.subChunks().get(1);
    Assertions.assertEquals("data", sc1.name().value(), "Chunk name matches");
    Assertions.assertEquals(192000L, sc1.dataSize(), "Size matches");
  }
}
