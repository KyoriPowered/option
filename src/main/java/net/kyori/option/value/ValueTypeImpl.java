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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class ValueTypeImpl<T> implements ValueType<T> {
  private final Class<T> type;

  ValueTypeImpl(final @NotNull Class<T> type) {
    this.type = type;
  }

  @Override
  public @NotNull Class<T> type() {
    return this.type;
  }

  static IllegalArgumentException doNotKnowHowToTurn(final String input, final Class<?> expected, final @Nullable String message) {
    throw new IllegalArgumentException("Do not know how to turn value '" + input + "' into a " + expected.getName() + (message == null ? "" : ": " + message));
  }

  static final class Types {
    private Types() {
    }

    static ValueType<String> STRING = new ValueTypeImpl<String>(String.class) {
      @Override
      public @NotNull String parse(final @NotNull String plainValue) throws IllegalArgumentException {
        return plainValue;
      }
    };
    static ValueType<Boolean> BOOLEAN = new ValueTypeImpl<Boolean>(Boolean.class) {
      @Override
      public @NotNull Boolean parse(final @NotNull String plainValue) throws IllegalArgumentException {
        if (plainValue.equalsIgnoreCase("true")) {
          return Boolean.TRUE;
        } else if (plainValue.equalsIgnoreCase("false")) {
          return Boolean.FALSE;
        } else {
          throw doNotKnowHowToTurn(plainValue, Boolean.class, null);
        }
      }
    };
    static ValueType<Integer> INT = new ValueTypeImpl<Integer>(Integer.class) {
      @Override
      public @NotNull Integer parse(final @NotNull String plainValue) throws IllegalArgumentException {
        try {
          return Integer.decode(plainValue);
        } catch (final NumberFormatException ex) {
          throw doNotKnowHowToTurn(plainValue, Integer.class, ex.getMessage());
        }
      }
    };
    static ValueType<Double> DOUBLE = new ValueTypeImpl<Double>(Double.class) {
      @Override
      public @NotNull Double parse(final @NotNull String plainValue) throws IllegalArgumentException {
        try {
          return Double.parseDouble(plainValue);
        } catch (final NumberFormatException ex) {
          throw doNotKnowHowToTurn(plainValue, Double.class, ex.getMessage());
        }
      }
    };
  }

  static final class EnumType<E extends Enum<E>> extends ValueTypeImpl<E> {
    private final Map<String, E> values = new HashMap<>();

    EnumType(final @NotNull Class<E> type) {
      super(type);
      for (final E entry : type.getEnumConstants()) {
        this.values.put(entry.name().toLowerCase(Locale.ROOT), entry);
      }
    }

    @Override
    public @NotNull E parse(final @NotNull String plainValue) throws IllegalArgumentException {
      final E result = this.values.get(plainValue.toLowerCase(Locale.ROOT));
      if (result == null) {
        throw doNotKnowHowToTurn(plainValue, this.type(), null);
      }
      return result;
    }
  }
}
