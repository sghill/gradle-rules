package net.sghill.gradle.rules;

import org.gradle.api.Action;
import org.gradle.api.artifacts.CacheableRule;
import org.gradle.api.artifacts.ComponentMetadataContext;
import org.gradle.api.artifacts.ComponentMetadataRule;
import org.gradle.api.artifacts.DirectDependenciesMetadata;
import org.gradle.api.artifacts.DirectDependencyMetadata;
import org.gradle.api.artifacts.VariantMetadata;

import javax.inject.Inject;

@CacheableRule
public abstract class RemoveClassifierFromDependency implements ComponentMetadataRule {
    private final String group;
    private final String name;
    private final String reason;

    @Inject
    public RemoveClassifierFromDependency(String group, String name, String reason) {
        this.group = group;
        this.name = name;
        this.reason = reason;
    }

    @Override
    public void execute(ComponentMetadataContext ctx) {
        ReplaceDependencyIfPresent action = new ReplaceDependencyIfPresent(group, name, reason);
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

    private static class ReplaceDependencyIfPresent implements Action<DirectDependenciesMetadata> {
        private final String group;
        private final String name;
        private final String reason;

        private ReplaceDependencyIfPresent(String group, String name, String reason) {
            this.group = group;
            this.name = name;
            this.reason = reason;
        }

        @Override
        public void execute(DirectDependenciesMetadata dependencies) {
            DirectDependencyMetadata found = null;
            for (DirectDependencyMetadata dep : dependencies) {
                if (group.equals(dep.getGroup()) && name.equals(dep.getName())) {
                    found = dep;
                    break;
                }
            }
            if (found == null) {
                return;
            }
            dependencies.remove(found);
            dependencies.add(found.getModule().toString(), new Rationale(reason));
        }
    }

    private static class Rationale implements Action<DirectDependencyMetadata> {
        private final String reason;

        private Rationale(String reason) {
            this.reason = reason;
        }

        @Override
        public void execute(DirectDependencyMetadata m) {
            m.because(reason);
        }
    }
}
