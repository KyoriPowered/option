/*
 * This file is part of option, licensed under the MIT License.
 *
 * Copyright (c) 2023 KyoriPowered
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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A representation of a configurable option.
 *
 * <p>Keys must be unique among all feature flag instances.</p>
 *
 * @param <V> the value type
 * @since 1.0.0
 */
@ApiStatus.NonExtendable
public interface Option<V> {

  /**
   * Create an option with a boolean value type.
   *
   * <p>Flag keys must not be reused between flag instances.</p>
   *
   * @param id the flag id
   * @param defaultValue the default value
   * @return the flag instance
   * @since 1.0.0
   */
  static Option<Boolean> booleanOption(final String id, final boolean defaultValue) {
    return OptionImpl.option(id, Boolean.class, defaultValue);
  }

  /**
   * Create an option with an enum value type.
   *
   * <p>Flag keys must not be reused between flag instances.</p>
   *
   * @param id the flag id
   * @param enumClazz the value type
   * @param defaultValue the default value
   * @param <E> the enum type
   * @return the flag instance
   * @since 1.0.0
   */
  static <E extends Enum<E>> Option<E> enumOption(final String id, final Class<E> enumClazz, final E defaultValue) {
    return OptionImpl.option(id, enumClazz, defaultValue);
  }

  /**
   * Get the option id.
   *
   * <p>This must be unique among options.</p>
   *
   * @return the flag id
   * @since 1.0.0
   */
  @NotNull String id();

  /**
   * Get the type of the option value.
   *
   * @return the value type
   * @since 1.0.0
   */
  @NotNull Class<V> type();

  /**
   * Get a default value for the option, if any is present.
   *
   * @return the default value
   * @since 1.0.0
   */
  @Nullable V defaultValue();
}
