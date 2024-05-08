import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class client extends JFrame {
    // JFrame data fields
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 600;

    // Establishes GUI for program
    private void createGUI() {
        // Set window size
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);

        // Land on homepage
        this.setContentPane(getHome());

        // Other stuff
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public JPanel getHome() {
        // Set up the background image panel
        ImagePanel homePanel = new ImagePanel(new ImageIcon("background.png").getImage());

        // Set gridbag layout to allow for buttons and background image
        homePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Host Button
        JButton hostButton = new JButton("Host Game");
        hostButton.addActionListener(e ->
                {
                    System.out.println("Host pressed");
                    hostHandshake.startConnection();
                }
        );
        homePanel.add(hostButton, gbc);

        // Join Button
        JButton joinButton = new JButton("Join Game");
        joinButton.addActionListener(e ->
                {
                    System.out.println("Join pressed");
                    joinHandshake.joinConnection();
                }
        );
        homePanel.add(joinButton, gbc);

        // Instructions Button
        JButton instructButton = new JButton("Instructions");
        instructButton.addActionListener(e ->
                {
                    System.out.println("Instructions pressed");
                    this.setContentPane(getInstructions());
                    this.revalidate();
                    this.repaint();
                }
        );
        homePanel.add(instructButton, gbc);

        // Return panel
        return  homePanel;
    }

    public JPanel getInstructions() {
        // Set up the background image panel
        ImagePanel homePanel = new ImagePanel(new ImageIcon("background.png").getImage());

        // Set gridbag layout to allow for buttons and background image
        homePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Text panel holding instructions
        JTextArea textArea = new JTextArea(10, 52);
        textArea.setEditable(false);

        // Read in instructions from file
        String data = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("instructions.txt")))
        {
            String currentLine;
            while ((currentLine = reader.readLine()) != null)
                data += currentLine + "\n";
        }
        catch(Exception e) {System.out.println("Error: Unable to read instructions from file.\n");}
        textArea.append(data);

        // Add scroll panel
        JPanel nestedText = new JPanel();
        nestedText.add(textArea);
        homePanel.add(nestedText,gbc);

        // Main Menu Button
        JButton menuButton = new JButton("Main Menu");
        menuButton.addActionListener(e ->
                {
                    System.out.println("Main menu pressed");
                    this.setContentPane(getHome());
                    this.revalidate();
                    this.repaint();
                }
        );
        homePanel.add(menuButton,gbc);

        // Return panel
        return homePanel;
    }

    // Overall window for client
    public client() {
        super("Battleship");
        createGUI();
    }

    public static void main(String[] args) {
        client Client = new client();
    }
}
