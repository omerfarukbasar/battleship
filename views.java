import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class views {

    String serverIP;
    JTextArea chatArea;
    //DataOutputStream toServer;

    public views(){
    }

    public JPanel getHome(JFrame parent) {
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
                    serverIP = handshake.startConnection();
                    parent.setContentPane(getGame(parent));
                    new Thread(() -> chatStuff.listenToMsg(chatArea)).start();
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
                    serverIP = handshake.joinConnection();
                    parent.setContentPane(getGame(parent));
                    new Thread(() -> chatStuff.listenToMsg(chatArea)).start();
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

    public JPanel getInstructions(JFrame parent) {
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
    public JPanel getGame(JFrame parent) {
        // GridBagLayout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Create the board panel with GridBagLayout
        JPanel boardContainer = new JPanel(new GridBagLayout());

        // Generate the player's ship placement using shipStuff
        int[][] playerBoard = shipStuff.generateBoard();

        // Create the first board (Opponent's Board)
        JPanel board1 = new JPanel(new GridLayout(10, 10));
        board1.setBorder(BorderFactory.createTitledBorder("Opponent's Board"));
        for (int i = 0; i < 100; i++) {
            JButton button = new JButton(String.valueOf(i + 1));
            button.setOpaque(true);
            button.setBackground(Color.LIGHT_GRAY); // Color for opponent's grid (unknown)
            board1.add(button);
        }
        boardContainer.add(board1, gbc);

        // Create the second board (Your Board) based on player's ship placement
        JPanel board2 = new JPanel(new GridLayout(10, 10));
        board2.setBorder(BorderFactory.createTitledBorder("Your Board"));
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                JButton button = new JButton(String.valueOf(row * 10 + col + 1));
                button.setEnabled(false); // Disable clicking for player's board
                button.setOpaque(true);
                // Set button colors based on ship placement
                if (playerBoard[row][col] == 1) {
                    button.setBackground(Color.GREEN); // Ship present
                } else {
                    button.setBackground(Color.BLUE); // Empty water
                }
                board2.add(button);
            }
        }
        boardContainer.add(board2, gbc);

        // Create the main container to hold both game boards and the chat panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(boardContainer, BorderLayout.CENTER);
        JPanel sidePanel = new JPanel(new BorderLayout());

        // Add chat and announcer panels
        sidePanel.add(createChatPanel(), BorderLayout.NORTH);
        sidePanel.add(createAnnouncerPanel(), BorderLayout.SOUTH);

        // Add the entire side panel to the main layout
        mainPanel.add(sidePanel, BorderLayout.EAST);

        // Return the main panel containing everything
        return mainPanel;
    }

    // Create the chat panel method
    public JPanel createChatPanel() {
        // Panel for chat
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Chat"));

        // Text area for chat messages
        chatArea = new JTextArea(15, 20);
        chatArea.setEditable(false); // Make it non-editable for incoming messages
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Text field for entering new chat messages
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("Send");

        // Action listener for the send button
        ActionListener sendAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText().trim();
                if (!message.isEmpty()) {
                    //chatArea.append("You: " + message + "\n");
                    chatStuff.sendMessage(message,serverIP,chatArea);
                    inputField.setText("");
                }
            }
        };

        // Attach the send action to both the button and the text field (on Enter)
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);

        // Bottom panel for text input and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to the chat panel
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        //listenForMessages();

        return chatPanel;
    }

    // Create the chat panel method
    private JPanel createAnnouncerPanel() {
        // Panel for announcer
        JPanel announcerPanel = new JPanel(new BorderLayout());
        announcerPanel.setBorder(BorderFactory.createTitledBorder("Announcements"));

        // Text area for announcements
        JTextArea chatArea = new JTextArea(15, 20);
        chatArea.setEditable(false); // Make it non-editable for incoming messages
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Add components to the announcement panel
        announcerPanel.add(scrollPane, BorderLayout.CENTER);

        return announcerPanel;
    }
}
