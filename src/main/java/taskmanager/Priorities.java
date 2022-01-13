package taskmanager;

import java.util.Objects;

public enum Priorities {
    TRIVIAL(0.1), EASY(1), MEDIUM(1.5), HARD(2);

    public final Number num;

    Priorities(double i) {
        this.num = i;
    }

    public static Priorities valueOfLabel(Number num) {
        for (Priorities priority : values()) {
            if (Objects.equals(priority.num, num)) {
                return priority;
            }
        }
        return null;
    }
}
