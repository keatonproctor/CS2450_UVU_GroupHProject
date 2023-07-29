import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileReaderGUI extends JFrame {

    private static final long serialVersionUID = 1L;
	private JLabel fileLabel;
    private JLabel validationStatusLabel;
    private JTextField patternTextField;
    private JTextField inputTextField;
    private JButton runButton;
    private JButton addButton;
    private JButton modifyButton;
    private JButton deleteButton;
    private JTextArea commandTextArea;
    private UVSim simulator;
    private JList<String> commandList;
    private DefaultListModel<String> commandListModel;
    private boolean bValidate = false;
    private File selectedFile;
    private boolean fileNotInitialized = true;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JPanel validationPanel;
    private JPanel centerPanel;
    private JPanel inputPanel;
    private JPanel commandPanel;
    private JPanel commandButtonPanel;
    private JPanel bottomPanel;

    private Color primaryColor = Color.decode("#4C721D"); // Default primary color: UVU green
    private Color offColor = Color.decode("#FFFFFF"); // Default off-color: White

    public FileReaderGUI() {
        setTitle("UV Sim");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

//        promptForColors();
        
        mainPanel = new JPanel(new BorderLayout());

        fileLabel = new JLabel();

        patternTextField = new JTextField();
        patternTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = patternTextField.getText().trim();
                bValidate = validatePattern(input);
                if (!bValidate) {
                    JOptionPane.showMessageDialog(FileReaderGUI.this, "Invalid input! Please follow the pattern (+ or - followed by a max of 5 numbers).", "Error", JOptionPane.ERROR_MESSAGE);
                }
                updateRunButtonState();
            }
        });

        inputTextField = new JTextField(23);
        inputTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || inputTextField.getText().length() >= 5) {
                    e.consume();
                }
            }
        });

        JButton chooseFileButton = new JButton("Choose File");
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(java.io.File f) {
                        return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Text Files (*.txt)";
                    }
                });

                int result = fileChooser.showOpenDialog(FileReaderGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    boolean isValidFile = validateFile(selectedFile);
                    if (isValidFile) {
                        fileLabel.setText(selectedFile.getName());

                        validationStatusLabel.setText("File validation successful.");
                        bValidate = true;
                        updateRunButtonState();
                        displayCommandsFromFile(selectedFile);
                    } else {
                        JOptionPane.showMessageDialog(FileReaderGUI.this, "File validation failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        fileLabel.setText("");
                        validationStatusLabel.setText("");
                        bValidate = false;
                        updateRunButtonState();
                    }
                }
            }
        });

        validationStatusLabel = new JLabel();

        buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(chooseFileButton, BorderLayout.WEST);
        buttonPanel.add(fileLabel, BorderLayout.CENTER);

        validationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        validationPanel.add(validationStatusLabel);

        centerPanel = new JPanel(new BorderLayout());

        inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Input Text:"));
        inputPanel.add(inputTextField);

        commandPanel = new JPanel(new BorderLayout());

        commandListModel = new DefaultListModel<>();
        commandList = new JList<>(commandListModel);
        commandList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commandList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateCommandButtonsState();
            }
        });
        JScrollPane commandScrollPane = new JScrollPane(commandList);

        commandButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCommand();
            }
        });

        modifyButton = new JButton("Modify");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyCommand();
            }
        });
        modifyButton.setEnabled(false);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCommand();
            }
        });
        deleteButton.setEnabled(false);

        commandButtonPanel.add(addButton);
        commandButtonPanel.add(modifyButton);
        commandButtonPanel.add(deleteButton);

        commandPanel.add(commandScrollPane, BorderLayout.CENTER);
        commandPanel.add(commandButtonPanel, BorderLayout.SOUTH);

        commandTextArea = new JTextArea(10, 30);
        commandTextArea.setEditable(false);
        JScrollPane textAreaScrollPane = new JScrollPane(commandTextArea);

        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(commandPanel, BorderLayout.WEST);
        centerPanel.add(textAreaScrollPane, BorderLayout.CENTER);

        runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeProgram();
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the GUI window
            }
        });

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(runButton);
        bottomPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(validationPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(patternTextField, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        applyColorScheme();

        setContentPane(mainPanel);
    }

    private void promptForColors() {
        String primaryColorHex = JOptionPane.showInputDialog(this, "Enter the Primary Color (in Hex format, e.g., #4C721D):");
        if (primaryColorHex != null) {
            try {
                primaryColor = Color.decode(primaryColorHex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Hex format for Primary Color. Using default green color.", "Error", JOptionPane.ERROR_MESSAGE);
                primaryColor = Color.decode("#4C721D");
            }
        } else {
            // User canceled, use default green color
            primaryColor = Color.decode("#4C721D");
        }

        String offColorHex = JOptionPane.showInputDialog(this, "Enter the Off-Color (in Hex format, e.g., #FFFFFF):");
        if (offColorHex != null) {
            try {
                offColor = Color.decode(offColorHex);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Hex format for Off-Color. Using default white color.", "Error", JOptionPane.ERROR_MESSAGE);
                offColor = Color.decode("#FFFFFF");
            }
        } else {
            // User canceled, use default white color
            offColor = Color.decode("#FFFFFF");
        }
    }

    private boolean validateFile(File file) {
        boolean isValidFile = true;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                boolean isValidLine = validateLine(line);
                if (!isValidLine) {
                    isValidFile = false;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isValidFile;
    }

    private boolean validatePattern(String input) {
        String pattern = "[+-]\\d{1,5}";
        return input.matches(pattern);
    }

    private boolean validateLine(String line) {
        String pattern = "[+-]\\d{1,5}";
        return line.matches(pattern);
    }

    public void updateRunButtonState() {
        runButton.setEnabled(bValidate);
    }

    public void setSimulator(UVSim simulator) {
        this.simulator = simulator;
    }

    public void displayCommandsFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            commandListModel.clear();
            while ((line = br.readLine()) != null) {
                commandListModel.addElement(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCommandTextArea() {
        int selectedIndex = commandList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < commandListModel.size()) {
            String command = commandListModel.getElementAt(selectedIndex);
            commandTextArea.setText(command);
        } else {
            commandTextArea.setText("");
        }
    }

    private void updateCommandButtonsState() {
        int selectedIndex = commandList.getSelectedIndex();
        boolean isValidIndex = selectedIndex >= 0 && selectedIndex < commandListModel.size();
        modifyButton.setEnabled(isValidIndex);
        deleteButton.setEnabled(isValidIndex);
    }

    private void addCommand() {
        int selectedIndex = commandList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < commandListModel.size()) {
            String instruction = promptForInstruction();
            if (instruction != null) {
                commandListModel.add(selectedIndex + 1, instruction);
                updateCommandTextArea();
            }
        }
    }

    private void modifyCommand() {
        int selectedIndex = commandList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < commandListModel.size()) {
            String instruction = promptForInstruction();
            if (instruction != null) {
                commandListModel.set(selectedIndex, instruction);
                updateCommandTextArea();
            }
        }
    }

    private void deleteCommand() {
        int selectedIndex = commandList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < commandListModel.size()) {
            commandListModel.remove(selectedIndex);
            updateCommandTextArea();
        }
    }

    private String promptForInstruction() {
        return JOptionPane.showInputDialog(FileReaderGUI.this, "Enter the instruction:", "Modify Instruction", JOptionPane.PLAIN_MESSAGE);
    }

    private void executeProgram() {
    	UVSim simulator = new UVSim();
        if (simulator != null) { // Check if simulator is initialized before using it
            List<Integer> program = new ArrayList<>();
            for (int i = 0; i < commandListModel.size(); i++) {
                String instruction = commandListModel.get(i);
                try {
                    int value = Integer.parseInt(instruction);
                    program.add(value);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid instruction: " + instruction);
                }
            }

            int[] programArray = new int[UVSim.HALT_INSTRUCTION];
            for (int i = 0; i < program.size(); i++) {
                programArray[i] = program.get(i);
            }

            simulator.loadProgram(programArray);
            simulator.runProgram();
        }
        
    }


    public File getProgramFile() {
        return selectedFile;
    }

    public boolean isFileNotInitialized() {
        return fileNotInitialized;
    }

    

    private void applyColorScheme() {
        mainPanel.setBackground(primaryColor);
        buttonPanel.setBackground(primaryColor);
        validationPanel.setBackground(primaryColor);
        centerPanel.setBackground(primaryColor);
        inputPanel.setBackground(primaryColor);
        commandPanel.setBackground(primaryColor);
        commandButtonPanel.setBackground(primaryColor);
        bottomPanel.setBackground(primaryColor);
        patternTextField.setBackground(offColor);
        inputTextField.setBackground(offColor);
        commandTextArea.setBackground(offColor);
        runButton.setBackground(offColor);
        addButton.setBackground(offColor);
        modifyButton.setBackground(offColor);
        deleteButton.setBackground(offColor);
        commandList.setBackground(offColor);
    }

    public static void runGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FileReaderGUI app = new FileReaderGUI();
                app.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        UVSim simulator = new UVSim();
        FileReaderGUI gui = new FileReaderGUI();
        gui.setSimulator(simulator);
        FileReaderGUI.runGUI();
    }    
    
}
