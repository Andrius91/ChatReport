package icu.yogurt.chatreport.common.config;

import java.util.HashMap;
import java.util.Map;

public class Replacer {
    private final Map<String, String> replacements;

    public Replacer() {
        this.replacements = new HashMap<>();
    }

    public Replacer(String... replacements) {
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements must come in pairs.");
        }
        this.replacements = new HashMap<>();
        for (int i = 0; i < replacements.length; i += 2) {
            this.replacements.put(replacements[i], replacements[i + 1]);
        }
    }

    public String apply(String input) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            input = input.replace(entry.getKey(), entry.getValue());
        }
        return input;
    }

    public void addReplacement(String replacer, String replacement) {
        replacements.put(replacer, replacement);
    }
}

