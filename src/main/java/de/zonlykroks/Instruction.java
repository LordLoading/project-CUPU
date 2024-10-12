package de.zonlykroks;

import java.util.List;

public record Instruction(String name, byte opcode, List<ArgumentType> argumentTypes) {

    public boolean validateArguments(List<String> args) {
        if (args.size() != argumentTypes.size()) {
            System.err.println("Argument count mismatch for instruction '" + name + "'. " +
                    "Expected: " + argumentTypes.size() + ", Provided: " + args.size());
            return false; // Argument count mismatch
        }

        for (int i = 0; i < argumentTypes.size(); i++) {
            ArgumentType expectedType = argumentTypes.get(i);
            String argument = args.get(i);

            if (!expectedType.validate(argument)) {
                System.err.println("Validation failed for argument " + (i + 1) + " ('" + argument + "') in instruction '" +
                        name + "'. Expected type: " + expectedType.getTypeName());
                return false; // Argument validation failed
            }
        }
        return true; // All arguments are valid
    }
}