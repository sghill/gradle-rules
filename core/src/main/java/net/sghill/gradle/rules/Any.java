package net.sghill.gradle.rules;

class Any implements DependencyMatcher {
    private static final long serialVersionUID = -3259869222933343326L;
    
    @Override
    public boolean matches(String in) {
        return true;
    }
}
