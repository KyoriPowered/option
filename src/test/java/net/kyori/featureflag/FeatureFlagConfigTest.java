/*
 * This file is part of feature-flag, licensed under the MIT License.
 *
 * Copyright (c) 2017-2023 KyoriPowered
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
package net.kyori.featureflag;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagConfigTest {

  enum TestEnum {
    ONE, TWO, THREE
  }

  private static final FeatureFlag<Boolean> ONE = FeatureFlag.booleanFlag(key("one"), true);
  private static final FeatureFlag<Boolean> TWO = FeatureFlag.booleanFlag(key("two"), false);
  private static final FeatureFlag<TestEnum> ENUM_FLAG = FeatureFlag.enumFlag(key("enum_flag"), TestEnum.class, TestEnum.ONE);

  @Test
  void testEmpty() {
    assertFalse(FeatureFlagConfig.empty().has(ONE));
    assertFalse(FeatureFlagConfig.empty().has(TWO));
    assertFalse(FeatureFlagConfig.empty().has(ENUM_FLAG));
  }

  @Test
  void testFixedValue() {
    final FeatureFlagConfig set = FeatureFlagConfig.builder()
      .value(ONE, false)
      .build();

    assertTrue(set.has(ONE));
    assertFalse(set.value(ONE));
  }

  @Test
  void testDefaultValues() {
    final FeatureFlagConfig set = FeatureFlagConfig.builder()
      .build();

    assertFalse(set.has(ONE));
    assertTrue(set.value(ONE));
    assertFalse(set.has(ENUM_FLAG));
    assertEquals(TestEnum.ONE, set.value(ENUM_FLAG));
  }

  @Test
  void testMixedTypes() {
  }

  @Test
  void testBuilderFromExisting() {
  }

  private static @NotNull String key(final String path) {
    return "feature-flag:test/" + path;
  }
}
