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
            simInstance.loadProgram(program);

            // Pass the UVSim instance to the GUI for command modification
            guiWindow.setSimulator(simInstance);
            guiWindow.displayCommandsFromFile(programFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
