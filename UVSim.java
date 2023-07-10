import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class UVSim {
    public static final int HALT_INSTRUCTION = 43;
    private int[] memory;
    private int accumulator;
    private boolean isHalted;
    private int instructionPointer = 0;

    public UVSim() {
        memory = new int[100];
        accumulator = 0;
        isHalted = false;
    }

    public void loadProgram(int[] program) {
        if (program.length > 100) {
            System.out.println("Program size exceeds memory limit.");
            return;
        }

        System.arraycopy(program, 0, memory, 0, program.length);
    }

    public void runProgram() {

        while (!isHalted && instructionPointer < 100) {
            int instruction = memory[instructionPointer];
            int opcode = instruction / 100;
            int operand = instruction % 100;

            switch (opcode) {
                case 10:
                    read(operand);
                    break;
                case 11:
                    write(operand);
                    break;
                case 20:
                    load(operand);
                    break;
                case 21:
                    store(operand);
                    break;
                case 30:
                    add(operand);
                    break;
                case 31:
                    subtract(operand);
                    break;
                case 32:
                    divide(operand);
                    break;
                case 33:
                    multiply(operand);
                    break;
                case 40:
                    branch(operand);
                    break;
                case 41:
                    branchNeg(operand);
                    break;
                case 42:
                    branchZero(operand);
                    break;
                case 43:
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
            Scanner scanner = new Scanner(System.in);
            int value = scanner.nextInt();
            memory[location] = value;
    }

    void write(int location) {
        System.out.println("Output: " + memory[location]);
    }

    // Load/store operations:
    void load(int location) {
        accumulator = memory[location];
    }

    void store(int location) {
        memory[location] = accumulator;
    }

    // Arithmetic operations:
    void add(int location) {
        accumulator += memory[location];
    }

    void subtract(int location) {
        accumulator -= memory[location];
    }

    void divide(int location) {
        int value = memory[location];
        if (value != 0) {
            accumulator /= value;
        } else {
            System.out.println("Can't Divide by Zero.");
            isHalted = true;
        }
    }

    void multiply(int location) {
        accumulator *= memory[location];
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

    int[] getMemory() {
        return memory;
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
