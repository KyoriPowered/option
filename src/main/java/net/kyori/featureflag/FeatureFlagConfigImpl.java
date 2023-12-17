/*
 * This file is part of feature-flag, licensed under the MIT License.
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
package net.kyori.featureflag;

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

final class FeatureFlagConfigImpl implements FeatureFlagConfig {
  static final FeatureFlagConfig EMPTY = new FeatureFlagConfigImpl(new IdentityHashMap<>());
  private final IdentityHashMap<FeatureFlag<?>, Object> values;

  FeatureFlagConfigImpl(final IdentityHashMap<FeatureFlag<?>, Object> values) {
    this.values = new IdentityHashMap<>(values);
  }

  @Override
  public boolean has(final @NotNull FeatureFlag<?> flag) {
    return this.values.containsKey(requireNonNull(flag, "flag"));
  }

  @Override
  public <V> V value(final @NotNull FeatureFlag<V> flag) {
    final V value = flag.type().cast(this.values.get(requireNonNull(flag, "flag")));
    return value == null ? flag.defaultValue() : value;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    final FeatureFlagConfigImpl that = (FeatureFlagConfigImpl) other;
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
    private final SortedMap<Integer, FeatureFlagConfig> sets;
    private final int targetVersion;
    private final FeatureFlagConfig filtered;

    VersionedImpl(final SortedMap<Integer, FeatureFlagConfig> sets, final int targetVersion, final FeatureFlagConfig filtered) {
      this.sets = sets;
      this.targetVersion = targetVersion;
      this.filtered = filtered;
    }

    @Override
    public boolean has(final @NotNull FeatureFlag<?> flag) {
      return this.filtered.has(flag);
    }

    @Override
    public <V> V value(final @NotNull FeatureFlag<V> flag) {
      return this.filtered.value(flag);
    }

    @Override
    public @NotNull Map<Integer, FeatureFlagConfig> childSets() {
      return Collections.unmodifiableSortedMap(this.sets.headMap(this.targetVersion + 1));
    }

    @Override
    public @NotNull Versioned at(final int version) {
      return new VersionedImpl(this.sets, version, flattened(this.sets, version));
    }

    public static FeatureFlagConfig flattened(final SortedMap<Integer, FeatureFlagConfig> versions, final int targetVersion) {
      final Map<Integer, FeatureFlagConfig> applicable = versions.headMap(targetVersion + 1);
      final FeatureFlagConfig.Builder builder = FeatureFlagConfig.builder();
      for (final FeatureFlagConfig child : applicable.values()) {
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

  static final class BuilderImpl implements FeatureFlagConfig.Builder {
    private final IdentityHashMap<FeatureFlag<?>, Object> values = new IdentityHashMap<>();

    @Override
    public @NotNull FeatureFlagConfig build() {
      if (this.values.isEmpty()) return EMPTY;

      return new FeatureFlagConfigImpl(this.values);
    }

    @Override
    public <V> @NotNull Builder value(final @NotNull FeatureFlag<V> flag, final @NotNull V value) {
      this.values.put(
        requireNonNull(flag, "flag"),
        requireNonNull(value, "value")
      );
      return this;
    }

    @Override
    public @NotNull Builder values(final @NotNull FeatureFlagConfig existing) {
      if (existing instanceof FeatureFlagConfigImpl) {
        this.values.putAll(((FeatureFlagConfigImpl) existing).values);
      } else if (existing instanceof VersionedImpl) {
        this.values.putAll(((FeatureFlagConfigImpl) ((VersionedImpl) existing).filtered).values);
      } else {
        throw new IllegalArgumentException("existing set " + existing + " is of an unknown implementation type");
      }
      return this;
    }
  }

  static final class VersionedBuilderImpl implements FeatureFlagConfig.VersionedBuilder {
    private final Map<Integer, FeatureFlagConfigImpl.BuilderImpl> builders = new TreeMap<>();

    @Override
    public FeatureFlagConfig.@NotNull Versioned build() {
      if (this.builders.isEmpty()) {
        return new VersionedImpl(Collections.emptySortedMap(), 0, FeatureFlagConfig.empty());
      }

      final SortedMap<Integer, FeatureFlagConfig> built = new TreeMap<>();
      for (final Map.Entry<Integer, FeatureFlagConfigImpl.BuilderImpl> entry : this.builders.entrySet()) {
        built.put(entry.getKey(), entry.getValue().build());
      }
      // generate 'flattened' latest element
      return new VersionedImpl(built, built.lastKey(), VersionedImpl.flattened(built, built.lastKey()));
    }

    @Override
    public @NotNull VersionedBuilder version(final int version, final @NotNull Consumer<Builder> versionBuilder) {
      requireNonNull(versionBuilder, "versionBuilder")
        .accept(this.builders.computeIfAbsent(version, $ -> new FeatureFlagConfigImpl.BuilderImpl()));
      return this;
    }
  }
}
