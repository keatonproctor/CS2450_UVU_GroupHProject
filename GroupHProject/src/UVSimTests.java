package Modifications_June_24_2023;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UVSimTests {
    private UVSim simulator;

    @BeforeEach
    public void setUp() {
        simulator = new UVSim();
    }

    @Test
    public void testReadSuccess() {
        int location = 0;
        int expectedValue = 42;

        simulator.read(location);

        int actualValue = simulator.getMemory()[location];
        Assertions.assertEquals(expectedValue, actualValue, "Read operation failed to store the correct value in memory.");
    }

    @Test
    public void testReadInvalidInput() {
        int location = 0;

        MockInputProvider inputProvider = new MockInputProvider("InvalidInput");
        simulator.setInputProvider(inputProvider);
        simulator.read(location);

        int actualValue = simulator.getMemory()[location];
        Assertions.assertEquals(0, actualValue, "Read operation failed to handle invalid input gracefully.");
    }

    @Test
    public void testWriteSuccess() {
        int location = 0;
        int value = 42;

        MockOutputProvider outputProvider = new MockOutputProvider();
        simulator.setOutputProvider(outputProvider);
        simulator.getMemory()[location] = value;
        simulator.write(location);

        String expectedOutput = String.valueOf(value);
        String actualOutput = outputProvider.getOutput();
        Assertions.assertEquals(expectedOutput, actualOutput, "Write operation failed to output the correct value.");
    }

    @Test
    public void testLoadSuccess() {
        int location = 0;
        int value = 42;

        simulator.getMemory()[location] = value;
        simulator.load(location);

        int actualValue = simulator.getAccumulator();
        Assertions.assertEquals(value, actualValue, "Load operation failed to load the correct value into the accumulator.");
    }

    @Test
    public void testStoreSuccess() {
        int location = 0;
        int value = 42;

        simulator.setAccumulator(value);
        simulator.store(location);

        int actualValue = simulator.getMemory()[location];
        Assertions.assertEquals(value, actualValue, "Store operation failed to store the correct value in memory.");
    }

    @Test
    public void testAddSuccess() {
        int location = 0;
        int initialValue = 10;
        int valueToAdd = 5;
        int expectedValue = initialValue + valueToAdd;

        simulator.getMemory()[location] = valueToAdd;
        simulator.setAccumulator(initialValue);
        simulator.add(location);

        int actualValue = simulator.getAccumulator();
        Assertions.assertEquals(expectedValue, actualValue, "Add operation failed to add the correct value to the accumulator.");
    }

    @Test
    public void testSubtractSuccess() {
        int location = 0;
        int initialValue = 10;
        int valueToSubtract = 5;
        int expectedValue = initialValue - valueToSubtract;

        simulator.getMemory()[location] = valueToSubtract;
        simulator.setAccumulator(initialValue);
        simulator.subtract(location);

        int actualValue = simulator.getAccumulator();
        Assertions.assertEquals(expectedValue, actualValue, "Subtract operation failed to subtract the correct value from the accumulator.");
    }

    @Test
    public void testDivideSuccess() {
        int location = 0;
        int initialValue = 10;
        int valueToDivideBy = 5;
        int expectedValue = initialValue / valueToDivideBy;

        simulator.getMemory()[location] = valueToDivideBy;
        simulator.setAccumulator(initialValue);
        simulator.divide(location);

        int actualValue = simulator.getAccumulator();
        Assertions.assertEquals(expectedValue, actualValue, "Divide operation failed to divide the accumulator by the correct value.");
    }

    @Test
    public void testDivideByZero() {
        int location = 0;
        int initialValue = 10;
        int valueToDivideBy = 0;
        int expectedValue = 0;

        simulator.getMemory()[location] = valueToDivideBy;
        simulator.setAccumulator(initialValue);
        simulator.divide(location);

        int actualValue = simulator.getAccumulator();
        Assertions.assertEquals(expectedValue, actualValue, "Divide operation failed to handle division by zero gracefully.");
    }

    @Test
    public void testMultiplySuccess() {
        int location = 0;
        int initialValue = 10;
        int valueToMultiply = 5;
        int expectedValue = initialValue * valueToMultiply;

        simulator.getMemory()[location] = valueToMultiply;
        simulator.setAccumulator(initialValue);
        simulator.multiply(location);

        int actualValue = simulator.getAccumulator();
        Assertions.assertEquals(expectedValue, actualValue, "Multiply operation failed to multiply the correct value with the accumulator.");
    }

    @Test
    public void testBranchSuccess() {
        int location = 10;
        int expectedNextInstruction = location;

        simulator.setProgramCounter(location);
        simulator.branch(location);

        int actualNextInstruction = simulator.getProgramCounter();
        Assertions.assertEquals(expectedNextInstruction, actualNextInstruction, "Branch operation failed to set the program counter to the correct location.");
    }

    @Test
    public void testBranchNegativeSuccess() {
        int location = 10;
        int expectedNextInstruction = location;

        simulator.setProgramCounter(location);
        simulator.setAccumulator(-1); // Set the accumulator to a negative value
        simulator.branchNeg(location);

        int actualNextInstruction = simulator.getProgramCounter();
        Assertions.assertEquals(expectedNextInstruction, actualNextInstruction, "BranchNeg operation failed to set the program counter to the correct location when the accumulator is negative.");
    }

    @Test
    public void testBranchNegativeNoBranch() {
        int location = 10;
        int expectedNextInstruction = location + 1;

        simulator.setProgramCounter(location);
        simulator.setAccumulator(0); // Set the accumulator to a non-negative value
        simulator.branchNeg(location);

        int actualNextInstruction = simulator.getProgramCounter();
        Assertions.assertEquals(expectedNextInstruction, actualNextInstruction, "BranchNeg operation incorrectly branched when the accumulator is non-negative.");
    }

    @Test
    public void testBranchZeroSuccess() {
        int location = 10;
        int expectedNextInstruction = location;

        simulator.setProgramCounter(location);
        simulator.setAccumulator(0); // Set the accumulator to zero
        simulator.branchZero(location);

        int actualNextInstruction = simulator.getProgramCounter();
        Assertions.assertEquals(expectedNextInstruction, actualNextInstruction, "BranchZero operation failed to set the program counter to the correct location when the accumulator is zero.");
    }

    @Test
    public void testBranchZeroNoBranch() {
        int location = 10;
        int expectedNextInstruction = location + 1;

        simulator.setProgramCounter(location);
        simulator.setAccumulator(1); // Set the accumulator to a non-zero value
        simulator.branchZero(location);

        int actualNextInstruction = simulator.getProgramCounter();
        Assertions.assertEquals(expectedNextInstruction, actualNextInstruction, "BranchZero operation incorrectly branched when the accumulator is non-zero.");
    }

    @Test
    public void testHaltSuccess() {
        int location = 0;
        int expectedNextInstruction = UVSim.HALT_INSTRUCTION;

        simulator.setProgramCounter(location);
        simulator.halt();

        int actualNextInstruction = simulator.getProgramCounter();
        Assertions.assertEquals(expectedNextInstruction, actualNextInstruction, "Halt operation failed to set the program counter to the HALT instruction.");
    }
}
