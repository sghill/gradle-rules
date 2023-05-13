package net.sghill.gradle.rules.acceptance;

import net.sghill.gradle.rules.DowngradeDependencyToPrefer;
import net.sghill.gradle.rules.RemoveClassifierFromDependency;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import static net.sghill.gradle.rules.Matchers.eq;

public class AcceptancePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getDependencies().components(c -> {
            c.withModule("com.github.javafaker:javafaker", RemoveClassifierFromDependency.class,
                    conf -> conf.params("org.yaml", "snakeyaml", "plugin removed/added to drop classifier"));
            
            c.withModule("com.squareup.retrofit2:converter-jackson", DowngradeDependencyToPrefer.class,
                    x -> x.params(eq("com.fasterxml.jackson.core"), eq("jackson-databind"), "downgraded to prefer by plugin"));
        });
    }
}
