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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

final class OptionStateImpl implements OptionState {
  static final OptionState EMPTY = new OptionStateImpl(new IdentityHashMap<>());
  private final IdentityHashMap<Option<?>, Object> values;

  OptionStateImpl(final IdentityHashMap<Option<?>, Object> values) {
    this.values = new IdentityHashMap<>(values);
  }

  @Override
  public boolean has(final @NotNull Option<?> option) {
    return this.values.containsKey(requireNonNull(option, "flag"));
  }

  @Override
  public <V> V value(final @NotNull Option<V> option) {
    final V value = option.type().cast(this.values.get(requireNonNull(option, "flag")));
    return value == null ? option.defaultValue() : value;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    final OptionStateImpl that = (OptionStateImpl) other;
    return Objects.equals(this.values, that.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.values);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "{" +
      "values=" + this.values +
      '}';
  }

  static final class VersionedImpl implements Versioned {
    private final SortedMap<Integer, OptionState> sets;
    private final int targetVersion;
    private final OptionState filtered;

    VersionedImpl(final SortedMap<Integer, OptionState> sets, final int targetVersion, final OptionState filtered) {
      this.sets = sets;
      this.targetVersion = targetVersion;
      this.filtered = filtered;
    }

    @Override
    public boolean has(final @NotNull Option<?> option) {
      return this.filtered.has(option);
    }

    @Override
    public <V> V value(final @NotNull Option<V> option) {
      return this.filtered.value(option);
    }

    @Override
    public @NotNull Map<Integer, OptionState> childStates() {
      return Collections.unmodifiableSortedMap(this.sets.headMap(this.targetVersion + 1));
    }

    @Override
    public @NotNull Versioned at(final int version) {
      return new VersionedImpl(this.sets, version, flattened(this.sets, version));
    }

    public static OptionState flattened(final SortedMap<Integer, OptionState> versions, final int targetVersion) {
      final Map<Integer, OptionState> applicable = versions.headMap(targetVersion + 1);
      final OptionState.Builder builder = OptionState.optionState();
      for (final OptionState child : applicable.values()) {
        builder.values(child);
      }

      return builder.build();
    }

    @Override
    public boolean equals(final @Nullable Object other) {
      if (this == other) return true;
      if (other == null || getClass() != other.getClass()) return false;
      final VersionedImpl that = (VersionedImpl) other;
      return this.targetVersion == that.targetVersion
        && Objects.equals(this.sets, that.sets)
        && Objects.equals(this.filtered, that.filtered);
    }

    @Override
    public int hashCode() {
      return Objects.hash(
        this.sets,
        this.targetVersion,
        this.filtered
      );
    }

    @Override
    public String toString() {
      return this.getClass().getSimpleName() + "{" +
        "sets=" + this.sets +
        ", targetVersion=" + this.targetVersion +
        ", filtered=" + this.filtered +
        '}';
    }
  }

  static final class BuilderImpl implements OptionState.Builder {
    private final IdentityHashMap<Option<?>, Object> values = new IdentityHashMap<>();

    @Override
    public @NotNull OptionState build() {
      if (this.values.isEmpty()) return EMPTY;

      return new OptionStateImpl(this.values);
    }

    @Override
    public <V> @NotNull Builder value(final @NotNull Option<V> option, final @NotNull V value) {
      this.values.put(
        requireNonNull(option, "flag"),
        requireNonNull(value, "value")
      );
      return this;
    }

    @Override
    public @NotNull Builder values(final @NotNull OptionState existing) {
      if (existing instanceof OptionStateImpl) {
        this.values.putAll(((OptionStateImpl) existing).values);
      } else if (existing instanceof VersionedImpl) {
        this.values.putAll(((OptionStateImpl) ((VersionedImpl) existing).filtered).values);
      } else {
        throw new IllegalArgumentException("existing set " + existing + " is of an unknown implementation type");
      }
      return this;
    }
  }

  static final class VersionedBuilderImpl implements OptionState.VersionedBuilder {
    private final Map<Integer, OptionStateImpl.BuilderImpl> builders = new TreeMap<>();

    @Override
    public OptionState.@NotNull Versioned build() {
      if (this.builders.isEmpty()) {
        return new VersionedImpl(Collections.emptySortedMap(), 0, OptionState.emptyOptionState());
      }

      final SortedMap<Integer, OptionState> built = new TreeMap<>();
      for (final Map.Entry<Integer, OptionStateImpl.BuilderImpl> entry : this.builders.entrySet()) {
        built.put(entry.getKey(), entry.getValue().build());
      }
      // generate 'flattened' latest element
      return new VersionedImpl(built, built.lastKey(), VersionedImpl.flattened(built, built.lastKey()));
    }

    @Override
    public @NotNull VersionedBuilder version(final int version, final @NotNull Consumer<Builder> versionBuilder) {
      requireNonNull(versionBuilder, "versionBuilder")
        .accept(this.builders.computeIfAbsent(version, $ -> new OptionStateImpl.BuilderImpl()));
      return this;
    }
  }
}
