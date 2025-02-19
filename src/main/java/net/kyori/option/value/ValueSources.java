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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.option.Option;
import org.jspecify.annotations.Nullable;

final class ValueSources {
  static final ValueSource ENVIRONMENT = new EnvironmentVariable("");
  static final ValueSource SYSTEM_PROPERTIES = new SystemProperty("");

  private ValueSources() {
  }

  static final class EnvironmentVariable implements ValueSource {
    private static final Pattern ENVIRONMENT_SUBST_PATTERN = Pattern.compile("[:\\-/]");
    private static final String ENVIRONMENT_VAR_SEPARATOR = "_";

    private final String prefix;

    EnvironmentVariable(final String prefix) {
      this.prefix = prefix.isEmpty() ? "" : prefix.toUpperCase(Locale.ROOT) + ENVIRONMENT_VAR_SEPARATOR;
    }

    @Override
    public <T> @Nullable T value(final Option<T> option) {
      final StringBuffer buf = new StringBuffer(option.id().length() + this.prefix.length());
      buf.append(this.prefix);
      final Matcher match = ENVIRONMENT_SUBST_PATTERN.matcher(option.id());
      while (match.find()) {
        match.appendReplacement(buf, ENVIRONMENT_VAR_SEPARATOR);
      }
      match.appendTail(buf);

      final String value = System.getenv(buf.toString().toUpperCase(Locale.ROOT));
      if (value == null) {
        return null;
      }

      return option.valueType().parse(value);
    }
  }

  static final class SystemProperty implements ValueSource {
    private static final Pattern SYSTEM_PROP_SUBST_PATTERN = Pattern.compile("[:/]");
    private static final String SYSTEM_PROPERTY_SEPARATOR = ".";

    private final String prefix;

    SystemProperty(final String prefix) {
      this.prefix = prefix.isEmpty() ? "" : prefix + SYSTEM_PROPERTY_SEPARATOR;
    }

    @Override
    public <T> @Nullable T value(final Option<T> option) {
      final StringBuffer buf = new StringBuffer(option.id().length() + this.prefix.length());
      buf.append(this.prefix);
      final Matcher match = SYSTEM_PROP_SUBST_PATTERN.matcher(option.id());
      while (match.find()) {
        match.appendReplacement(buf, SYSTEM_PROPERTY_SEPARATOR);
      }
      match.appendTail(buf);

      final String value = System.getProperty(buf.toString());
      if (value == null) {
        return null;
      }

      return option.valueType().parse(value);
    }
  }
}
