
public class ProcessorMain {

	public static void main(String[] args) {
		System.out.print("RUNNING PROCESSORMAIN");

	}

}


// H-bot's test commit //
/*	H-bot's rough draft idea of the program */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class UVSim {
    private int[] memory;
    private int accumulator;
    private boolean isHalted;

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
        int instructionPointer = 0;

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
    
    //Idea for I/O operation://

    private void read(int location) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a value: ");
        int value = scanner.nextInt();
        memory[location] = value;
    }

    private void write(int location) {
        System.out.println("Output: " + memory[location]);
    }

    
    //Idea for Load/store operations://
    
    private void load(int location) {
        accumulator = memory[location];
    }

    private void store(int location) {
        memory[location] = accumulator;
    }
    
    //Idea for Arithmetic operation://

    private void add(int location) {
        accumulator += memory[location];
    }

    private void subtract(int location) {
        accumulator -= memory[location];
    }

    private void divide(int location) {
        int value = memory[location];
        if (value != 0) {
            accumulator /= value;
        } else {
            System.out.println("Can't Divide by Zero.");
            isHalted = true;
        }
    }

    private void multiply(int location) {
        accumulator *= memory[location];
    }
    
    
    //Idea for Control operation://

    private void branch(int location) {
        if (location >= 0 && location < 100) {
            instructionPointer = location;
        } else {
            System.out.println("Invalid branch location: " + location);
            isHalted = true;
        }
    }

    private void branchNeg(int location) {
        if (accumulator < 0) {
            branch(location);
        }
    }

    private void branchZero(int location) {
        if (accumulator == 0) {
            branch(location);
        }
    }

    private void halt() {
        isHalted = true;
    }

    
    //Idea for main function//
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide the input file path.");
            return;
        }

        String filePath = args[0];
        UVSim simulator = new UVSim();

        try {
            int[] program = readProgramFromFile(filePath);
            simulator.loadProgram(program);
            simulator.runProgram();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        }
    }
    
    //Idea to read a program from a txt file//

    private static int[] readProgramFromFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

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
}
