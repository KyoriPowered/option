/*
 * This file is part of option, licensed under the MIT License.
 *
 * Copyright (c) 2025 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.option.value;

import net.kyori.option.Option;
import net.kyori.option.OptionSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ValueSourceTest {
  static OptionSchema.Mutable SCHEMA = OptionSchema.emptySchema();

  static Option<String> A = SCHEMA.stringOption("a", null);
  static Option<String> A_B = SCHEMA.stringOption("a/b", null);
  static Option<String> C = SCHEMA.stringOption("c", null);
  static Option<String> C_D = SCHEMA.stringOption("c/d", null);

  @Test
  void testReadSystemProperties() {
    final ValueSource systemProperties = ValueSource.systemProperty("prefix");
    assertNull(systemProperties.value(A));
    assertNull(systemProperties.value(A_B));

    System.setProperty("a", "test");
    System.setProperty("a.b", "test2");

    assertNull(systemProperties.value(A));
    assertNull(systemProperties.value(A_B));

    System.setProperty("prefix.a", "test");
    System.setProperty("prefix.a.b", "test2");

    assertEquals("test", systemProperties.value(A));
    assertEquals("test2", systemProperties.value(A_B));
  }

  @Test
  void testReadSystemPropertiesNoPrefix() {
    final ValueSource systemProperties = ValueSource.systemProperty("");
    assertNull(systemProperties.value(C));
    assertNull(systemProperties.value(C_D));

    System.setProperty("c", "test");
    System.setProperty("c.d", "test2");

    assertEquals("test", systemProperties.value(C));
    assertEquals("test2", systemProperties.value(C_D));

    System.setProperty("some.c", "test3");
    System.setProperty("some.c.d", "test4");

    assertEquals("test", systemProperties.value(C));
    assertEquals("test2", systemProperties.value(C_D));
  }

  // we can't set environment variables :(
}
