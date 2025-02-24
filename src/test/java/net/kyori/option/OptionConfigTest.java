/*
 * This file is part of option, licensed under the MIT License.
 *
 * Copyright (c) 2017-2025 KyoriPowered
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
package net.kyori.option;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionConfigTest {

  enum TestEnum {
    ONE, TWO, THREE
  }

  private static final OptionSchema.Mutable UNSAFE_SCHEMA = OptionSchema.emptySchema();
  private static final OptionSchema SCHEMA = UNSAFE_SCHEMA.frozenView();
  private static final Option<Boolean> ONE = UNSAFE_SCHEMA.booleanOption(key("one"), true);
  private static final Option<Boolean> TWO = UNSAFE_SCHEMA.booleanOption(key("two"), false);
  private static final Option<TestEnum> ENUM_FLAG = UNSAFE_SCHEMA.enumOption(key("enum_flag"), TestEnum.class, TestEnum.ONE);

  @Test
  void testEmpty() {
    assertFalse(SCHEMA.emptyState().has(ONE));
    assertFalse(SCHEMA.emptyState().has(TWO));
    assertFalse(SCHEMA.emptyState().has(ENUM_FLAG));
  }

  @Test
  void testEmptyEqualToBuilder() {
    assertEquals(SCHEMA.emptyState(), SCHEMA.stateBuilder().build());
  }

  @Test
  void testFixedValue() {
    final OptionState set = SCHEMA.stateBuilder()
      .value(ONE, false)
      .build();

    assertTrue(set.has(ONE));
    assertFalse(set.has(TWO));
    assertFalse(set.value(ONE));
  }

  @Test
  void testDefaultValues() {
    final OptionState set = SCHEMA.stateBuilder()
      .build();

    assertFalse(set.has(ONE));
    assertTrue(set.value(ONE));
    assertFalse(set.has(ENUM_FLAG));
    assertEquals(TestEnum.ONE, set.value(ENUM_FLAG));
  }

  @Test
  void testMixedTypes() {
    final OptionState set = SCHEMA.stateBuilder()
      .value(ONE, false)
      .value(ENUM_FLAG, TestEnum.THREE)
      .build();

    assertTrue(set.has(ONE));
    assertFalse(set.has(TWO));
    assertFalse(set.value(ONE));
  }

  @Test
  void testBuilderFromExisting() {
    final OptionState existing = SCHEMA.stateBuilder()
      .value(ONE, false)
      .value(ENUM_FLAG, TestEnum.THREE)
      .build();

    final OptionState updated = SCHEMA.stateBuilder()
      .values(existing)
      .build();

    assertEquals(existing, updated);
  }

  @Test
  void testVersionedBaseLevel() {
    final OptionState.Versioned versioned = SCHEMA.versionedStateBuilder()
      .version(0, b -> b
        .value(TWO, true)
        .value(ENUM_FLAG, TestEnum.THREE))
      .version(3, b -> b
        .value(ONE, false))
      .version(5, b -> b
        .value(ENUM_FLAG, TestEnum.TWO))
      .build();

    assertEquals(TestEnum.TWO, versioned.value(ENUM_FLAG));
    assertEquals(true, versioned.value(TWO));
  }

  @Test
  void testVersionLower() {
    final OptionState.Versioned versioned = SCHEMA.versionedStateBuilder()
      .version(0, b -> b
        .value(TWO, true)
        .value(ENUM_FLAG, TestEnum.THREE))
      .version(3, b -> b
        .value(ONE, false))
      .version(5, b -> b
        .value(ENUM_FLAG, TestEnum.TWO))
      .build()
      .at(3);

    assertEquals(TestEnum.THREE, versioned.value(ENUM_FLAG));
    assertEquals(false, versioned.value(ONE));
    assertEquals(true, versioned.value(TWO));
  }

  @Test
  void testVersionHigher() {
    final OptionState.Versioned versioned = SCHEMA.versionedStateBuilder()
      .version(0, b -> b
        .value(TWO, true)
        .value(ENUM_FLAG, TestEnum.THREE))
      .version(3, b -> b
        .value(ONE, false))
      .version(5, b -> b
        .value(ENUM_FLAG, TestEnum.TWO))
      .build()
      .at(7);

    assertEquals(TestEnum.TWO, versioned.value(ENUM_FLAG));
    assertEquals(false, versioned.value(ONE));
    assertEquals(true, versioned.value(TWO));

  }

  @Test
  void testVersionBetweenSteps() {
    final OptionState.Versioned versioned = SCHEMA.versionedStateBuilder()
      .version(0, b -> b
        .value(TWO, true)
        .value(ENUM_FLAG, TestEnum.THREE))
      .version(3, b -> b
        .value(ONE, false))
      .version(5, b -> b
        .value(ENUM_FLAG, TestEnum.TWO))
      .build()
      .at(4);

    assertEquals(TestEnum.THREE, versioned.value(ENUM_FLAG));
    assertEquals(false, versioned.value(ONE));
    assertEquals(true, versioned.value(TWO));
  }

  private static String key(final String path) {
    return "option:test/" + path;
  }
}
