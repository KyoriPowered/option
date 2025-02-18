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

import java.util.Set;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Represents a 'universe' of known options.
 *
 * @since 1.1.0
 */
@ApiStatus.NonExtendable
public interface OptionSchema {
  /**
   * Retrieve the globally-shared option schema.
   *
   * <p>This mostly exists for backwards compatibility.</p>
   *
   * @return the global schema
   * @since 1.1.0
   */
  static OptionSchema.@NotNull Mutable globalSchema() {
    return OptionSchemaImpl.Instances.GLOBAL;
  }

  /**
   * Return a mutable schema that's a child of the specified schema.
   *
   * <p>This schema will inherit all options defined in the parent schema at the time the child schema is created.</p>
   *
   * @param schema the parent schema
   * @return the mutable child schema
   * @since 1.1.0
   */
  static OptionSchema.@NotNull Mutable childSchema(final @NotNull OptionSchema schema) {
    final OptionSchemaImpl impl;
    if (schema instanceof OptionSchemaImpl.MutableImpl) {
      impl = (OptionSchemaImpl) ((Mutable) schema).frozenView();
    } else {
      impl = (OptionSchemaImpl) schema;
    }

    return new OptionSchemaImpl(requireNonNull(impl, "impl")).new MutableImpl();
  }

  /**
   * Create an empty schema inheriting from nothing but the contents
   * of the global schema at invocation time.
   *
   * @return a mutable schema
   * @since 1.1.0
   */
  static OptionSchema.@NotNull Mutable emptySchema() {
    return childSchema(globalSchema());
  }

  /**
   * Return all known options contained within this schema, and recursively through its parents.
   *
   * @return known options
   * @since 1.1.0
   */
  @NotNull Set<Option<?>> knownOptions();

  /**
   * Return whether the provided option is known within this scheam.
   *
   * @param option the option
   * @return whether the option is known
   * @since 1.1.0
   */
  boolean has(final @NotNull Option<?> option);

  /**
   * Create a builder for an unversioned option state containing only options within this schema.
   *
   * @return the builder
   * @since 1.1.0
   */
  OptionState.@NotNull Builder stateBuilder();

  /**
   * Create a builder for a versioned option state containing only values for options within this schema.
   *
   * @return the builder
   * @since 1.1.0
   */
  OptionState.@NotNull VersionedBuilder versionedStateBuilder();

  /**
   * Create an empty option state within this schema.
   *
   * @return the empty state
   * @since 1.1.0
   */
  OptionState emptyState();

  /**
   * A mutable view of an option schema that allows registering new options into the schema.
   *
   * @since 1.1.0
   */
  @ApiStatus.NonExtendable
  interface Mutable extends OptionSchema {
    /**
     * Create an option with a string value type.
     *
     * <p>Flag keys must not be reused within a schema tree.</p>
     *
     * @param id the flag id
     * @param defaultValue the default value
     * @return the flag instance
     * @since 1.1.0
     */
    @NotNull Option<String> stringOption(final @NotNull String id, final @Nullable String defaultValue);

    /**
     * Create an option with a boolean value type.
     *
     * <p>Flag keys must not be reused within a schema tree.</p>
     *
     * @param id the flag id
     * @param defaultValue the default value
     * @return the flag instance
     * @since 1.1.0
     */
    @NotNull Option<Boolean> booleanOption(final @NotNull String id, final boolean defaultValue);

    /**
     * Create an option with an integer value type.
     *
     * <p>Flag keys must not be reused within a schema tree.</p>
     *
     * @param id the flag id
     * @param defaultValue the default value
     * @return the flag instance
     * @since 1.1.0
     */
    @NotNull Option<Integer> intOption(final @NotNull String id, final int defaultValue);

    /**
     * Create an option with a double value type.
     *
     * <p>Flag keys must not be reused within a schema tree.</p>
     *
     * @param id the flag id
     * @param defaultValue the default value
     * @return the flag instance
     * @since 1.1.0
     */
    @NotNull Option<Double> doubleOption(final @NotNull String id, final double defaultValue);

    /**
     * Create an option with an enum value type.
     *
     * <p>Flag keys must not be reused within a schema tree.</p>
     *
     * @param id the flag id
     * @param enumClazz the value type
     * @param defaultValue the default value
     * @param <E> the enum type
     * @return the flag instance
     * @since 1.1.0
     */
    <E extends Enum<E>> @NotNull Option<E> enumOption(final @NotNull String id, final @NotNull Class<E> enumClazz, final @Nullable E defaultValue);

    /**
     * Return a view of this schema which does not allow consumers to register new options.
     *
     * <p>This allows exposing known options within a schema without the risk of having values polluted.</p>
     *
     * @return the frozen view of this schema
     * @since 1.1.0
     */
    @NotNull OptionSchema frozenView();
  }
}
