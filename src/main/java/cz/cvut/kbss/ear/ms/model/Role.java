package cz.cvut.kbss.ear.ms.model;

public enum Role {
    ADMIN("ROLE_ADMIN"), USER("ROLE_USER"), PERSON("ROLE_PERSON");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

