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

import static java.util.Objects.requireNonNull;

/**
 * A possible type for an option value.
 *
 * @param <T> the actual type
 * @since 1.1.0
 */
public interface ValueType<T> {
  /**
   * Provide a value type for string values.
   *
   * @return a value type for string values
   * @since 1.1.0
   */
  static ValueType<String> stringType() {
    return ValueTypeImpl.Types.STRING;
  }

  /**
   * Provide a value type for boolean values.
   *
   * @return a value type for boolean values
   * @since 1.1.0
   */
  static ValueType<Boolean> booleanType() {
    return ValueTypeImpl.Types.BOOLEAN;
  }

  /**
   * Provide a value type for integer values.
   *
   * @return a value type for integer values
   * @since 1.1.0
   */
  static ValueType<Integer> integerType() {
    return ValueTypeImpl.Types.INT;
  }

  /**
   * Provide a value type for double values.
   *
   * @return a value type for double values
   * @since 1.1.0
   */
  static ValueType<Double> doubleType() {
    return ValueTypeImpl.Types.DOUBLE;
  }

  /**
   * Provide a value type for enum values.
   *
   * @param enumClazz the enum class
   * @param <E> the enum type
   * @return a value type for enum values
   * @since 1.1.0
   */
  static <E extends Enum<E>> ValueType<E> enumType(final Class<E> enumClazz) {
    return new ValueTypeImpl.EnumType<>(requireNonNull(enumClazz, "enumClazz"));
  }

  /**
   * The type representing the value.
   *
   * @return the object type of the value
   * @since 1.1.0
   */
  Class<T> type();

  /**
   * Extract a value of this type from a plain string.
   *
   * @param plainValue a plain-text value
   * @return the value coerced to the correct type
   * @throws IllegalArgumentException if the value is not of an appropriate type
   * @since 1.1.0
   */
  T parse(final String plainValue) throws IllegalArgumentException;
}
