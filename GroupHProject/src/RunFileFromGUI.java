import java.io.File;

public class RunFileFromGUI {

    public static void main(String[] args) {
        UVSim simInstance = new UVSim();
        FileReaderGUI guiWindow = new FileReaderGUI();
        guiWindow.setSimulator(simInstance); // Set the simulator instance
        FileReaderGUI.runGUI();

        while (guiWindow.isFileNotInitialized()) {
            // Wait until the file is selected in the GUI
        }

        File programFile = guiWindow.getProgramFile();
        try {
            int[] program = simInstance.readProgramFromFile(programFile);
            guiWindow.displayCommandsFromFile(programFile);
            simInstance.loadProgram(program);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Once the program is loaded, run it
        simInstance.runProgram();
    }
}
