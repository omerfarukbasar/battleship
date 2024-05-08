import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class views {
    public static JPanel getHome(JFrame parent) {
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
                    handshake.startConnection();
                    parent.setContentPane(getGame(parent));
                    parent.revalidate();
                    parent.repaint();
                }
        );
        homePanel.add(hostButton, gbc);

        // Join Button
        JButton joinButton = new JButton("Join Game");
        joinButton.addActionListener(e ->
                {
                    System.out.println("Join pressed");
                    handshake.joinConnection();
                    parent.setContentPane(getGame(parent));
                    parent.revalidate();
                    parent.repaint();
                }
        );
        homePanel.add(joinButton, gbc);

        // Instructions Button
        JButton instructButton = new JButton("Instructions");
        instructButton.addActionListener(e ->
                {
                    System.out.println("Instructions pressed");
                    parent.setContentPane(getInstructions(parent));
                    parent.revalidate();
                    parent.repaint();
                }
        );
        homePanel.add(instructButton, gbc);

        // Return panel
        return  homePanel;
    }

    public static JPanel getInstructions(JFrame parent) {
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
                    parent.setContentPane(getHome(parent));
                    parent.revalidate();
                    parent.repaint();
                }
        );
        homePanel.add(menuButton,gbc);

        // Return panel
        return homePanel;
    }
    public static JPanel getGame(JFrame parent) {
        // Set up the background image panel
        ImagePanel homePanel = new ImagePanel(new ImageIcon("background.png").getImage());

        // Set gridbag layout to allow for buttons and background image
        homePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Create a JPanel with a grid layout for the 10x10 grid
        JPanel panel = new JPanel(new GridLayout(10, 10));

        // Populate the grid with buttons
        for (int i = 0; i < 100; i++) {
            JButton button = new JButton(String.valueOf(i + 1));
            button.setOpaque(true);
            if(i % 2 == 0)
                button.setBackground(Color.RED);
            else if(i % 3 == 0)
                button.setBackground(Color.GREEN);
            else
                button.setBackground(Color.BLUE);
            panel.add(button);

        }
        homePanel.add(panel,gbc);

        // Create a JPanel with a grid layout for the 10x10 grid
        JPanel panel1 = new JPanel(new GridLayout(10, 10));

        // Populate the grid with buttons
        for (int i = 0; i < 100; i++) {
            JButton button = new JButton(String.valueOf(i + 1)); // Or use different labels if needed
            button.setEnabled(false);
            button.setOpaque(true);
            if(i % 2 == 0)
                button.setBackground(Color.RED);
            else if(i % 3 == 0)
                button.setBackground(Color.GREEN);
            else
                button.setBackground(Color.BLUE);
            panel1.add(button);
        }
        homePanel.add(panel1,gbc);

        // Return panel
        return homePanel;
    }
}
