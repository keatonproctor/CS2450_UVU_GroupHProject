import java.io.File;

public class UVSim_Runner {

    public static void main(String[] args) {

        boolean fileNotSelected = true; // Should be turned to false when file has been validated by gui
        FileReaderGUI guiWindow = new FileReaderGUI(); //

        FileReaderGUI.runGUI(); // 'initialize' / start gui window

        UVSim simInstance = new UVSim(); // initialize, but don't run program. Running it prematurely without the file will result in nullPointereException in UVSim

        File programFile; // Input file for UVSim program

        while(fileNotSelected) { // While loop is used to continually 'search' for file
                fileNotSelected = guiWindow.isFileNotInitialized(); // Check if file has been selected in GUI instance. Default value for
                                                                    // .isFileNotInitialized is true
                // loop should break once file is selected and run
        }

        programFile = guiWindow.getProgramFile(); // 'transfer' gui input file to UVSim

        try {
            simInstance.readProgramFromFile(programFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
