package net.sghill.gradle.rules;

import org.gradle.api.Action;
import org.gradle.api.artifacts.CacheableRule;
import org.gradle.api.artifacts.ComponentMetadataContext;
import org.gradle.api.artifacts.ComponentMetadataRule;
import org.gradle.api.artifacts.DirectDependenciesMetadata;
import org.gradle.api.artifacts.DirectDependencyMetadata;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.artifacts.VariantMetadata;
import org.gradle.api.artifacts.VersionConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * <p>
 * Allows downgrading specific version opinions.
 * </p>
 * <p>
 * As opposed to <a href="https://docs.gradle.org/current/userguide/dependency_downgrade_and_exclude.html">excludes</a>,
 * which omit the opinion entirely, this rule retains important information for
 * <a href="https://docs.gradle.org/current/userguide/viewing_debugging_dependencies.html#dependency_insights">dependencyInsight</a>.
 * </p>
 * <h3>Example: Favor Spring's Opinion</h3>
 *
 * Suppose you're writing a Spring app and you depend on Retrofit's converter-jackson library.
 * The library depends on Jackson 2.10.1 and your Spring version depends on Jackson 2.9.1.
 * Gradle conflict resolves this up to 2.10.1, but you really need 2.9.1 to run.
 * <pre>
 * import net.sghill.gradle.rules.DowngradeDependencyToPrefer
 * import static net.sghill.gradle.rules.Matchers.eq
 * 
 * dependencies {
 *     components {
 *         withModule('com.squareup.retrofit2:converter-jackson', DowngradeDependencyToPrefer) {
 *             params(eq("com.fasterxml.jackson.core"), eq("jackson-databind"), "Favor Spring's opinion on Jackson over Retrofit's")
 *         }
 *     }
 * }
 * </pre>
 *
 * @see <a href="https://docs.gradle.org/current/userguide/rich_versions.html">Gradle's Rich Versions</a>
 */
@CacheableRule
public abstract class DowngradeDependencyToPrefer implements ComponentMetadataRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(DowngradeDependencyToPrefer.class);
    private final DependencyMatcher group;
    private final DependencyMatcher name;
    private final String reason;

    @Inject
    public DowngradeDependencyToPrefer(DependencyMatcher group, DependencyMatcher name, String reason) {
        this.group = group;
        this.name = name;
        this.reason = reason;
    }

    @Override
    public void execute(ComponentMetadataContext ctx) {
        DowngradeDependencyIfPresent action = new DowngradeDependencyIfPresent(group, name, reason);
        ctx.getDetails().allVariants(new ModifyDependencies(action));
    }

    private static class ModifyDependencies implements Action<VariantMetadata> {
        private final Action<DirectDependenciesMetadata> action;

        private ModifyDependencies(Action<DirectDependenciesMetadata> action) {
            this.action = action;
        }

        @Override
        public void execute(VariantMetadata variant) {
            variant.withDependencies(action);
        }
    }

    private static class DowngradeDependencyIfPresent implements Action<DirectDependenciesMetadata> {
        private final DependencyMatcher group;
        private final DependencyMatcher name;
        private final String reason;

        private DowngradeDependencyIfPresent(DependencyMatcher group, DependencyMatcher name, String reason) {
            this.group = group;
            this.name = name;
            this.reason = reason;
        }

        @Override
        public void execute(DirectDependenciesMetadata dependencies) {
            for (DirectDependencyMetadata dep : dependencies) {
                if (group.matches(dep.getGroup()) && name.matches(dep.getName())) {
                    VersionConstraint versionConstraint = dep.getVersionConstraint();
                    String requiredVersion = versionConstraint.getRequiredVersion();
                    if (requiredVersion == null || requiredVersion.equals("")) {
                        LOGGER.debug("{}:{} found, but with no required version - skipping", group, name);
                    } else {
                        dep.version(new Action<MutableVersionConstraint>() {
                            @Override
                            public void execute(MutableVersionConstraint vc) {
                                String required = vc.getRequiredVersion();
                                vc.require("");
                                vc.prefer(required);
                            }
                        });
                        dep.because(reason);
                    }
                    break;
                }
            }
        }
    }
}
