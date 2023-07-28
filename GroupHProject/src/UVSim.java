import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class UVSim {
    public static final int HALT_INSTRUCTION = 43;
    private int[] oldMemory; // For 4-digit instructions
    private int[] newMemory; // For 6-digit instructions
    private int accumulator;
    private boolean isHalted;
    private int instructionPointer = 0;

    public UVSim() {
        oldMemory = new int[100];
        newMemory = new int[250];
        accumulator = 0;
        isHalted = false;
    }

    public void loadProgram(int[] program) {
        if (program.length > 250) {
            System.out.println("Program size exceeds memory limit.");
            return;
        }

        if (isOldFile(program)) {
            System.arraycopy(program, 0, oldMemory, 0, program.length);
        } else {
            System.arraycopy(program, 0, newMemory, 0, program.length);
        }
    }

    public void runProgram() {
        int[] memoryToUse = isOldFile(oldMemory) ? oldMemory : newMemory;

        while (!isHalted && instructionPointer < 100) {
            int instruction = memoryToUse[instructionPointer];
            int opcode = instruction / 100;
            int operand = instruction % 100;

            switch (opcode) {
                case 010:
                    read(operand);
                    break;
                case 011:
                    write(operand);
                    break;
                case 020:
                    load(operand);
                    break;
                case 021:
                    store(operand);
                    break;
                case 030:
                    add(operand);
                    break;
                case 031:
                    subtract(operand);
                    break;
                case 032:
                    divide(operand);
                    break;
                case 033:
                    multiply(operand);
                    break;
                case 040:
                    branch(operand);
                    break;
                case 041:
                    branchNeg(operand);
                    break;
                case 042:
                    branchZero(operand);
                    break;
                case 043:
                    halt();
                    break;
                default:
                    System.out.println("Invalid opcode: " + opcode);
                    break;
            }

            instructionPointer++;
        }

        if (!isHalted) {
            System.out.println("Program reached the end without a halt instruction.");
        }
    }

    // I/O operation:
    void read(int location) {
        try (Scanner scanner = new Scanner(System.in)) {
            int value = scanner.nextInt();
            int[] memoryToUse = isOldFile(oldMemory) ? oldMemory : newMemory;
            memoryToUse[location] = value;
        }
    }

    void write(int location) {
        int[] memoryToUse = isOldFile(oldMemory) ? oldMemory : newMemory;
        System.out.println("Output: " + memoryToUse[location]);
    }

    // Load/store operations:
    void load(int location) {
        int[] memoryToUse = isOldFile(oldMemory) ? oldMemory : newMemory;
        accumulator = memoryToUse[location];
    }

    void store(int location) {
        int[] memoryToUse = isOldFile(oldMemory) ? oldMemory : newMemory;
        memoryToUse[location] = accumulator;
    }

    // Arithmetic operations:
    void add(int location) {
        int[] memoryToUse = isOldFile(oldMemory) ? oldMemory : newMemory;
        accumulator += memoryToUse[location];
    }

    void subtract(int location) {
        int[] memoryToUse = isOldFile(oldMemory) ? oldMemory : newMemory;
        accumulator -= memoryToUse[location];
    }

    void divide(int location) {
        int[] memoryToUse = isOldFile(oldMemory) ? oldMemory : newMemory;
        int value = memoryToUse[location];
        if (value != 0) {
            accumulator /= value;
        } else {
            System.out.println("Can't Divide by Zero.");
            isHalted = true;
        }
    }

    void multiply(int location) {
        int[] memoryToUse = isOldFile(oldMemory) ? oldMemory : newMemory;
        accumulator *= memoryToUse[location];
    }

    // Control operations:
    void branch(int location) {
        if (location >= 0 && location < 100) {
            instructionPointer = location;
        } else {
            System.out.println("Invalid branch location: " + location);
            isHalted = true;
        }
    }

    void branchNeg(int location) {
        if (accumulator < 0) {
            branch(location);
        }
    }

    void branchZero(int location) {
        if (accumulator == 0) {
            branch(location);
        }
    }

    void halt() {
        isHalted = true;
    }

    // Helper method to check if the given file is an "old" file (contains 4-digit instructions)
    private boolean isOldFile(int[] program) {
        for (int instruction : program) {
            if (instruction >= 10000 || instruction < 0) {
                return false;
            }
        }
        return true;
    }

    // Read a program from a txt file
    int[] readProgramFromFile(File inputFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(inputFile);

        int[] program = new int[100];
        int index = 0;

        while (scanner.hasNextLine() && index < 100) {
            String line = scanner.nextLine();
            try {
                int instruction = Integer.parseInt(line);
                program[index] = instruction;
                index++;
            } catch (NumberFormatException e) {
                System.out.println("Invalid instruction: " + line);
            }
        }

        scanner.close();
        return program;
    }

    int[] getOldMemory() {
        return oldMemory;
    }

    int[] getNewMemory() {
        return newMemory;
    }

    int getAccumulator() {
        return accumulator;
    }

    void setAccumulator(int accumulatorValue) {
        accumulator = accumulatorValue;
    }

    void setProgramCounter(int location) {
        instructionPointer = location;
    }

    int getProgramCounter() {
        return instructionPointer;
    }
}
