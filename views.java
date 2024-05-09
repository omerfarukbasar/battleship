import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class views {

    handshake protocol = new handshake();
    JTextArea chatArea;

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
                    protocol.startConnection();
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
                    protocol.joinConnection();
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
    // Main game UI with two boards and the chat panel on the right
    public JPanel getGame(JFrame parent) {
        // GridBagLayout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Create the board panel with GridBagLayout
        JPanel boardContainer = new JPanel(new GridBagLayout());

        // Create the first board (10x10)
        JPanel board1 = new JPanel(new GridLayout(10, 10));
        board1.setBorder(BorderFactory.createTitledBorder("Opponent's Board"));
        for (int i = 0; i < 100; i++) {
            JButton button = new JButton(String.valueOf(i + 1));
            button.setOpaque(true);
            if (i % 2 == 0) button.setBackground(Color.RED);
            else if (i % 3 == 0) button.setBackground(Color.GREEN);
            else button.setBackground(Color.BLUE);
            board1.add(button);
        }
        boardContainer.add(board1, gbc);

        // Create the second board (10x10)
        JPanel board2 = new JPanel(new GridLayout(10, 10));
        board2.setBorder(BorderFactory.createTitledBorder("Your Board"));
        for (int i = 0; i < 100; i++) {
            JButton button = new JButton(String.valueOf(i + 1));
            button.setEnabled(false);
            button.setOpaque(true);
            if (i % 2 == 0) button.setBackground(Color.RED);
            else if (i % 3 == 0) button.setBackground(Color.GREEN);
            else button.setBackground(Color.BLUE);
            board2.add(button);
        }
        boardContainer.add(board2, gbc);

        // Create the main container to hold both game boards and the chat panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(boardContainer, BorderLayout.CENTER);
        JPanel sidePanel = new JPanel(new BorderLayout());

        sidePanel.add(createChatPanel(), BorderLayout.NORTH);
        sidePanel.add(createAnnouncerPanel(), BorderLayout.SOUTH);

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
                    chatArea.append("You: " + message + "\n");
                    sendMessage(message);
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

        listenForMessages();

        return chatPanel;
    }

    // Listens to ongoing chat messages relayed from server using another thread
    public void listenForMessages() {
        new Thread(() -> {
            try {
                DataInputStream fromServer = new DataInputStream(protocol.usedSocket.getInputStream());
                // While connection is still active
                while (true) {
                    String message = fromServer.readUTF();
                    chatArea.append(message + "\n");
                }
            }
            catch (IOException e) {System.err.println("Listening error: " + e.getMessage());}
            catch (Exception e) {System.err.println("Other Listening error: " + e.getMessage());}
        }).start();
    }

    // Allows for sending a message upon hitting send
    public void sendMessage(String message) {
        try {
            DataOutputStream toServer = new DataOutputStream(protocol.usedSocket.getOutputStream());
            toServer.writeUTF(message);
        }
        catch (IOException e) {System.err.println("Send error: " + e.getMessage());}
        catch (Exception e) {System.err.println("Other Send error: " + e.getMessage());}
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
