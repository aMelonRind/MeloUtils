package io.github.amelonrind.meloutils.feature;

public class SuppressLogs {

    public static void log(Runnable logAction) {
        log(logAction, () -> {});
    }

    public static void log(Runnable logAction, Runnable simplifiedLogAction) {
        // not yet planning to create an option for this.
        // exists for future usage lookup.
        simplifiedLogAction.run();
    }
}
