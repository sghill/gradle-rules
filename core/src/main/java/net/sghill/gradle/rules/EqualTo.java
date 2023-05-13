package net.sghill.gradle.rules;

class EqualTo implements DependencyMatcher {
    private static final long serialVersionUID = -7407311108747776768L;
    
    private final String value;

    EqualTo(String value) {
        this.value = value;
    }

    @Override
    public boolean matches(String in) {
        return value != null && value.equals(in);
    }
}
