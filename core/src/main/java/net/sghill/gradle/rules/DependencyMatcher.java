package net.sghill.gradle.rules;

import java.io.Serializable;

/**
 * Implementations aim to create clear intentions when wiring up the provided rule types.
 * Gradle requires these are {@link Serializable}.
 *
 * @see Matchers for creating bundled matchers
 */
public interface DependencyMatcher extends Serializable {
    boolean matches(String in);
}
