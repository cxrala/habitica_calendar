package taskmanager;

/**
 * Base task class. To be converted to JSON file format.
 * For more info about Habitica's API on tasks, see here: https://habitica.com/apidoc/#api-Task-CreateUserTasks
 */

public class Task {
    String text;
    String type;
    Number priority;
    String notes;


    // TODO: add integration for checklist given stated in block
    // TODO: also add integration for dates?

    public static class Builder {
        // required
        String text;
        Types type;

        // optional
        private Priorities priority = Priorities.MEDIUM;
        private String notes = "";

        public Builder(String text, Types type) {
            this.text = text;
            this.type = type;
        }

        public Builder setPriority(Priorities priority) {
            this.priority = priority;
            return this;
        }

        public Builder setPriority(Number priority) {
            return setPriority(Priorities.valueOfLabel(priority));
        }

        public Builder setNotes(String description) {
            notes = description;
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }

    private Task(Builder builder) {
        text = builder.text;
        type = builder.type.label;
        priority = builder.priority.num;
        notes = builder.notes;
    }

}
