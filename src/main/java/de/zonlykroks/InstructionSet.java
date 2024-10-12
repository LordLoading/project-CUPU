package de.zonlykroks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructionSet {
    private final Map<String, Instruction> instructions = new HashMap<>();

    public void addInstruction(Instruction instruction) {
        instructions.put(instruction.name().toUpperCase(), instruction);
    }

    public Instruction getInstruction(String name) {
        return instructions.get(name.toUpperCase());
    }

    public boolean validateInstruction(String name, List<String> args) {
        Instruction instruction = getInstruction(name);
        if (instruction == null) {
            System.err.println("Instruction not found: " + name);
            return false;
        }
        boolean valid = instruction.validateArguments(args);
        if (!valid) {
            System.err.println("Validation failed for instruction: " + name + " with arguments: " + args);
        }
        return valid;
    }
}
