package de.zonlykroks;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CustomAssembler {
    private final InstructionSet instructionSet;

    public CustomAssembler() {
        this.instructionSet = new InstructionSet();
        initializeInstructions("instructions.txt");
    }

    private void initializeInstructions(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                String[] parts = line.split(",");
                String name = parts[0].trim();
                byte opcode = Byte.parseByte(parts[1].trim());
                List<ArgumentType> args = new ArrayList<>();

                for (int i = 2; i < parts.length; i++) {
                    String argType = parts[i].trim();
                    switch (argType) {
                        case "reg":
                            args.add(new ArgumentType("reg",
                                    arg -> arg.matches("reg[0-9]|1[0-5]")));
                            break;
                        case "data":
                            args.add(new ArgumentType("data",
                                    arg -> arg.matches("[0-9A-Fa-f]{2}")));
                            break;
                        default:
                            System.err.println("Unknown argument type: " + argType);
                            break;
                    }
                }

                instructionSet.addInstruction(new Instruction(name, opcode, args));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Instruction file not found: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid opcode format in instruction file: " + e.getMessage());
        }
    }

    public List<byte[]> assemble(List<String> assemblyCode) {
        List<byte[]> machineCode = new ArrayList<>();

        for (String line : assemblyCode) {
            String trimmedLine = line.trim();

            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("//")) {
                continue;
            }

            String[] tokens = trimmedLine.split("\\s+");
            String instructionName = tokens[0];

            List<String> args = new ArrayList<>();
            if (tokens.length > 1) {
                String argPart = String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
                args.addAll(List.of(argPart.split(",")));
                args.replaceAll(String::trim);
            }

            if (instructionSet.validateInstruction(instructionName, args)) {
                byte[] machineInstruction = encodeInstruction(instructionName, args);
                machineCode.add(machineInstruction);
            } else {
                System.err.println("Invalid instruction or arguments: " + line);
            }
        }
        return machineCode;
    }

    private byte[] encodeInstruction(String instructionName, List<String> args) {
        Instruction instruction = instructionSet.getInstruction(instructionName);
        List<Byte> bytecode = new ArrayList<>();
        bytecode.add(instruction.opcode());

        for (String arg : args) {
            if (arg.startsWith("reg")) {
                String regNumberStr = arg.substring(3);
                int regNumber = Integer.parseInt(regNumberStr, 16);
                bytecode.add((byte) regNumber);
            } else {
                byte data = (byte) Integer.parseInt(arg, 16);
                bytecode.add(data);
            }
        }

        byte[] byteArray = new byte[bytecode.size()];
        for (int i = 0; i < bytecode.size(); i++) {
            byteArray[i] = bytecode.get(i);
        }

        return byteArray;
    }

    public void exportToLogisimFormat(List<byte[]> machineCode, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("v3.0 hex words addressed\n");

            int address = 0;
            StringBuilder lineBuilder = new StringBuilder();

            for (byte[] instruction : machineCode) {
                for (byte b : instruction) {
                    lineBuilder.append(String.format("%02X ", b & 0xFF));
                }

                writer.write(String.format("%02X: %s\n", address, lineBuilder.toString().trim()));
                lineBuilder.setLength(0);
                address += instruction.length;
            }

            System.out.println("Exported to " + filename + " successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        CustomAssembler assembler = new CustomAssembler();

        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setDialogTitle("Select Assembly File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "asm"));

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File assemblyFile = fileChooser.getSelectedFile();
            List<String> assemblyCode = new ArrayList<>();

            try (Scanner scanner = new Scanner(assemblyFile)) {
                while (scanner.hasNextLine()) {
                    assemblyCode.add(scanner.nextLine().trim());
                }

                List<byte[]> machineCode = assembler.assemble(assemblyCode);
                String outputFilename = "program.hex";
                assembler.exportToLogisimFormat(machineCode, outputFilename);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e.getMessage());
            }
        }
    }
}