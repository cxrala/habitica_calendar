package taskmanager;

// types for habitica api types.
public enum Types {
    HABIT("habit"), DAILY("daily"), TODO("todo"), REWARD("reward");

    public final String label;

    Types(String type) {
        this.label = type;
    }
}
