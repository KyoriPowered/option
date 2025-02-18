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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A source for external option values.
 *
 * @since 1.1.0
 */
@FunctionalInterface
public interface ValueSource {
  /**
   * A value source that will extract option values from environment variables.
   *
   * <p>Any of the characters {@code :}, {@code /}, and {@code -} will be replaced with {@code _}.</p>
   *
   * @param prefix the prefix for environment lookup, which will be prepended to keys followed by a {@code _},
   *               or the empty string for no prefix
   * @return an environment variable-backed value source
   * @since 1.1.0
   */
  static @NotNull ValueSource environmentVariable(final @NotNull String prefix) {
    return new ValueSources.EnvironmentVariables(prefix);
  }

  /**
   * A value source that will extract option values from system properties.
   *
   * <p>Any of the characters {@code :} and {@code /} will be replaced with {@code .}.</p>
   *
   * @param prefix the prefix for property lookup, which will be prepended to properties followed by a {@code .},
   *               or the empty string for no prefix
   * @return a system property-backed value source
   * @since 1.1.0
   */
  static @NotNull ValueSource systemProperty(final @NotNull String prefix) {
    return new ValueSources.SystemProperties(prefix);
  }

  /**
   * Provide a value for the specified option, if any is set.
   *
   * @param option the option
   * @return a value, if set
   * @param <T> the value type
   * @since 1.1.0
   */
  <T> @Nullable T value(final @NotNull Option<T> option);
}
