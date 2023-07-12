import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileReaderGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private JLabel fileLabel;
    private JLabel validationStatusLabel;
    private JTextField patternTextField;
    private JTextField inputTextField;
    private boolean bValidate = false; // Validation flag
    private JButton runButton; // Run button instance variable
    private JLabel accumulatorLabel; // Accumulator label
    private int accumulatorValue = 0; // Accumulator value
    private JPanel mainPanel;
    private File selectedFile;
    private JPanel inputPanel;
    private boolean fileNotInitialized = true;
    public FileReaderGUI() {

        // Basic Window information

        //System.out.println("Running GUI Constructor");

        setTitle("UV Sim"); //
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        fileLabel = new JLabel();

        patternTextField = new JTextField();
        patternTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = patternTextField.getText().trim();
                bValidate = validatePattern(input); // Update validation flag
                if (!bValidate) {
                    JOptionPane.showMessageDialog(FileReaderGUI.this, "Invalid input! Please follow the pattern (+ or - followed by a max of 5 numbers).", "Error", JOptionPane.ERROR_MESSAGE);
                }
                updateRunButtonState(); // Update "Run" button state
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
                        bValidate = true; // Set validation flag to true
                        updateRunButtonState(); // Update "Run" button state
                    } else {
                        JOptionPane.showMessageDialog(FileReaderGUI.this, "File validation failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        fileLabel.setText("");
                        validationStatusLabel.setText("");
                        bValidate = false; // Set validation flag to false
                        updateRunButtonState(); // Update "Run" button state
                    }
                }
            }
        });

        validationStatusLabel = new JLabel();

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(chooseFileButton, BorderLayout.WEST);
        buttonPanel.add(fileLabel, BorderLayout.CENTER);

        JPanel validationPanel = new JPanel();
        validationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        validationPanel.add(validationStatusLabel);

        inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Input Text:"));
        inputPanel.add(inputTextField);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        JPanel orPanel = new JPanel(new BorderLayout());
        JLabel orLabel = new JLabel("or");
        orLabel.setHorizontalAlignment(SwingConstants.CENTER);
        orPanel.add(orLabel, BorderLayout.NORTH);

        JPanel accumulatorPanel = new JPanel();
        accumulatorPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        accumulatorLabel = new JLabel("Accumulator: " + accumulatorValue);
        accumulatorPanel.add(accumulatorLabel);

        centerPanel.add(orPanel, BorderLayout.NORTH);
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(accumulatorPanel, BorderLayout.SOUTH);

        runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform the processing logic here
                fileNotInitialized = false;
                JOptionPane.showMessageDialog(FileReaderGUI.this, "Processing the request...");

            }
        });
        runButton.setEnabled(false); // Disable the "Run" button initially

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the GUI window
            }
        });

        JPanel exitPanel = new JPanel(new BorderLayout());
        exitPanel.add(exitButton, BorderLayout.EAST);
        exitPanel.add(runButton, BorderLayout.WEST);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(validationPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(patternTextField, BorderLayout.SOUTH);
        mainPanel.add(exitPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
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
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);
        return matcher.matches();
    }

    private boolean validateLine(String line) {
        String pattern = "[+-]\\d{1,5}";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(line);
        return matcher.matches();
    }

    public void updateRunButtonState() {
        // Enable or disable the "Run" button based on the validation flag
        runButton.setEnabled(bValidate);
    }

//    public UVSim getSimulator() {
//        return simInstance;
//    }

    public File getProgramFile() {
        return selectedFile;
    }

    public boolean isFileNotInitialized() {
        System.out.println(fileNotInitialized);
        return fileNotInitialized;
    }

    public static void runGUI() {

        // Moved the gui run() method from main() into runGUI() to be able to control it from UV_Sim_Runner

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FileReaderGUI app = new FileReaderGUI();
                app.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {

    }
}
