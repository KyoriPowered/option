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
package net.kyori.option;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

final class OptionSchemaImpl implements OptionSchema {
  final OptionState emptyState;
  final ConcurrentMap<String, Option<?>> options = new ConcurrentHashMap<>();

  OptionSchemaImpl(final @Nullable OptionSchemaImpl parent) {
    if (parent != null) {
      this.options.putAll(parent.options);
    }
    this.emptyState = new OptionStateImpl(this, new IdentityHashMap<>());
  }

  @Override
  public @NotNull Set<Option<?>> knownOptions() {
    return Collections.unmodifiableSet(new HashSet<>(this.options.values()));
  }

  @Override
  public boolean has(final @NotNull Option<?> option) {
    final Option<?> own = this.options.get(option.id());
    return own != null && own.equals(option);
  }

  @Override
  public OptionState.@NotNull Builder stateBuilder() {
    return new OptionStateImpl.BuilderImpl(this);
  }

  @Override
  public OptionState.@NotNull VersionedBuilder versionedStateBuilder() {
    return new OptionStateImpl.VersionedBuilderImpl(this);
  }

  @Override
  public OptionState emptyState() {
    return this.emptyState;
  }

  @Override
  public String toString() {
    return "OptionSchemaImpl{" +
      "options=" + this.options +
      '}';
  }

  static final class Instances {
    static OptionSchemaImpl.MutableImpl GLOBAL = new OptionSchemaImpl(null).new MutableImpl();
  }

  final class MutableImpl implements OptionSchema.Mutable {
    <T> Option<T> register(final String id, final Class<T> type, final @Nullable T defaultValue) {
      final Option<T> ret = new OptionImpl<>(
        requireNonNull(id, "id"),
        requireNonNull(type, "type"),
        defaultValue
      );

      if (OptionSchemaImpl.this.options.putIfAbsent(id, ret) != null) {
        throw new IllegalStateException("Key " + id + " has already been used. Option keys must be unique within a schema.");
      }

      return ret;
    }

    @Override
    public @NotNull Option<String> stringOption(final @NotNull String id, final @Nullable String defaultValue) {
      return this.register(id, String.class, defaultValue);
    }

    @Override
    public @NotNull Option<Boolean> booleanOption(final @NotNull String id, final boolean defaultValue) {
      return this.register(id, Boolean.class, defaultValue);
    }

    @Override
    public @NotNull Option<Integer> intOption(final @NotNull String id, final int defaultValue) {
      return this.register(id, Integer.class, defaultValue);
    }

    @Override
    public @NotNull Option<Double> doubleOption(final @NotNull String id, final double defaultValue) {
      return this.register(id, Double.class, defaultValue);
    }

    @Override
    public @NotNull <E extends Enum<E>> Option<E> enumOption(final @NotNull String id, final @NotNull Class<E> enumClazz, final @Nullable E defaultValue) {
      return this.register(id, enumClazz, defaultValue);
    }

    @Override
    public @NotNull OptionSchema frozenView() {
      return OptionSchemaImpl.this;
    }

    // base scheam methods

    @Override
    public @NotNull Set<Option<?>> knownOptions() {
      return OptionSchemaImpl.this.knownOptions();
    }

    @Override
    public boolean has(final @NotNull Option<?> option) {
      return OptionSchemaImpl.this.has(option);
    }

    @Override
    public OptionState.@NotNull Builder stateBuilder() {
      return OptionSchemaImpl.this.stateBuilder();
    }

    @Override
    public OptionState.@NotNull VersionedBuilder versionedStateBuilder() {
      return OptionSchemaImpl.this.versionedStateBuilder();
    }

    @Override
    public OptionState emptyState() {
      return OptionSchemaImpl.this.emptyState();
    }

    @Override
    public String toString() {
      return "MutableImpl{schema=" + OptionSchemaImpl.this + "}";
    }
  }
}
