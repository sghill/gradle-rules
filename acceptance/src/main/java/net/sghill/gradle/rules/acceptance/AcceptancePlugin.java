package net.sghill.gradle.rules.acceptance;

import net.sghill.gradle.rules.RemoveClassifierFromDependency;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AcceptancePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getDependencies().components(c ->
                c.withModule("com.github.javafaker:javafaker", RemoveClassifierFromDependency.class,
                        conf -> conf.params("org.yaml", "snakeyaml", "plugin removed/added to drop classifier")));
    }
}
