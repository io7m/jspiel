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

package com.io7m.jspiel.api;

import java.nio.ByteOrder;
import java.util.List;
import java.util.stream.Stream;

/**
 * A parsed riff file.
 */

public interface RiffFileType
{
  /**
   * @return The chunks contained within the file
   */

  List<RiffChunkType> chunks();

  /**
   * @return The byte order of the underlying file
   */

  ByteOrder byteOrder();

  /**
   * @return The list of chunks in (depth-first) order
   */

  default Stream<RiffChunkType> linearizedDescendantChunks()
  {
    return this.chunks()
      .stream()
      .flatMap(RiffChunkType::linearizedDescendantChunks);
  }
}
