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

import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Collection of feature flags.
 *
 * @since 1.0.0
 */
@ApiStatus.NonExtendable
public interface OptionState {
  /**
   * Get an empty set of options.
   *
   * @return the empty option state
   * @since 1.0.0
   */
  static OptionState emptyOptionState() {
    return OptionStateImpl.EMPTY;
  }

  /**
   * Create a builder for an unversioned option state.
   *
   * @return the builder
   * @since 1.0.0
   */
  static @NotNull Builder optionState() {
    return new OptionStateImpl.BuilderImpl();
  }

  /**
   * Create a builder for a versioned option state.
   *
   * @return the builder
   * @since 1.0.0
   */
  static @NotNull VersionedBuilder versionedOptionState() {
    return new OptionStateImpl.VersionedBuilderImpl();
  }


  /**
   * Get whether this state contains a certain option at all.
   *
   * @param option the option to check.
   * @return whether the option has been touched.
   * @since 1.0.0
   */
  boolean has(final @NotNull Option<?> option);

  /**
   * Get the value set for a certain option.
   *
   * @param option the option to query
   * @return the option value
   * @param <V> the value type
   * @since 1.0.0
   */
  <V> V value(final @NotNull Option<V> option);

  /**
   * A composite option set.
   *
   * <p>By default, this returns results for the newest supported version.</p>
   *
   * @since 1.0.0
   */
  @ApiStatus.NonExtendable
  interface Versioned extends OptionState {
    /**
     * The individual changes in each supported version.
     *
     * @return the child sets that exist
     * @since 1.0.0
     */
    @NotNull Map<Integer, OptionState> childStates();

    /**
     * Request a view of this option state showing only option values available at versions up to and including {@code version}.
     *
     * @param version the version to query
     * @return a limited view of this set
     * @since 1.0.0
     */
    @NotNull Versioned at(final int version);
  }

  /**
   * A builder for option states.
   *
   * @since 1.0.0
   */
  @ApiStatus.NonExtendable
  interface Builder {
    /**
     * Set the value for a specific option.
     *
     * @param option the option to set the value for
     * @param value the value
     * @return this builder
     * @param <V> the value type
     * @since 1.0.0
     */
    <V> @NotNull Builder value(final @NotNull Option<V> option, final @NotNull V value);

    /**
     * Apply all values from the existing option state.
     *
     * @param existing the existing state
     * @return this builder
     * @since 1.0.0
     */
    @NotNull Builder values(final @NotNull OptionState existing);

    /**
     * Create a completed option state.
     *
     * @return the built state
     * @since 1.0.0
     */
    @NotNull OptionState build();
  }

  /**
   * A builder for versioned option states.
   *
   * @since 1.0.0
   */
  @ApiStatus.NonExtendable
  interface VersionedBuilder {
    /**
     * Register options for a specific version.
     *
     * @param version the version to register
     * @param versionBuilder the builder that will receive options
     * @return this builder
     * @since 1.0.0
     */
    @NotNull VersionedBuilder version(final int version, final @NotNull Consumer<Builder> versionBuilder);

    /**
     * Create a completed versioned option state.
     *
     * @return the built versioned state
     * @since 1.0.0
     */
    @NotNull Versioned build();
  }
}
