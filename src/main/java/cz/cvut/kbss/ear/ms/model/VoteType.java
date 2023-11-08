package cz.cvut.kbss.ear.ms.model;

public enum VoteType {
    POSITIVE("POSITIVE_VOTE"), NEGATIVE("NEGATIVE_VOTE"), NEUTRAL("NEUTRAL_VOTE");

    private final String name;

    VoteType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
