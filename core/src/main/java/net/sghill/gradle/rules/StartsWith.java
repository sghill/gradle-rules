package net.sghill.gradle.rules;

class StartsWith implements DependencyMatcher {
    private static final long serialVersionUID = 8878340562574226369L;
    
    private final String prefix;

    public StartsWith(String prefix) {
        this.prefix = prefix;
    }
    
    @Override
    public boolean matches(String in) {
        return in.startsWith(prefix);
    }
}
