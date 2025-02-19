/*
 * This file is part of option, licensed under the MIT License.
 *
 * Copyright (c) 2023-2025 KyoriPowered
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

import java.util.Objects;
import net.kyori.option.value.ValueType;
import org.jspecify.annotations.Nullable;

final class OptionImpl<V> implements Option<V> {

  private final String id;
  private final ValueType<V> type;
  private final @Nullable V defaultValue; // excluded from equality comparisons, it does not form part of the option identity

  OptionImpl(final String id, final ValueType<V> type, final @Nullable V defaultValue) {
    this.id = id;
    this.type = type;
    this.defaultValue = defaultValue;
  }

  @Override
  public String id() {
    return this.id;
  }

  @Override
  public ValueType<V> valueType() {
    return this.type;
  }

  @Override
  public @Nullable V defaultValue() {
    return this.defaultValue;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    final OptionImpl<?> that = (OptionImpl<?>) other;
    return Objects.equals(this.id, that.id)
      && Objects.equals(this.type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      this.id,
      this.type
    );
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "{"
      + "id=" + this.id + ","
      + "type=" + this.type + ","
      + "defaultValue=" + this.defaultValue
      + '}';
  }
}
