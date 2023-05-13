package net.sghill.gradle.rules;

public class Matchers {
    private Matchers() {
    }
    
    public static DependencyMatcher startsWith(String prefix) {
        return new StartsWith(prefix);
    }
    
    public static DependencyMatcher any() {
        return new Any();
    }
    
    public static DependencyMatcher eq(String value) {
        return new EqualTo(value);
    }
}
