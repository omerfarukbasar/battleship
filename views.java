import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;

public class views {
    // Data fields
    private String opponentIP = null;
    private JTextArea chatArea;
    private JTextArea announceArea;
    private static final int BOARD_SIZE = 10;

    // Basic constructor, prevents from being static
    public views(){}

    // Sets up the landing page of the application
    public JPanel getHome(JFrame parent) {
        // Set up the background image panel
        imagePanel homePanel = new imagePanel(new ImageIcon("background.png").getImage());

        // Set gridbag layout to allow for buttons and background image
        homePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Host Button
        JButton hostButton = new JButton("Host Game");
        hostButton.addActionListener(e ->
                {
                    System.out.println("Host pressed");
                    // Display loading screen
                    imagePanel load = new imagePanel(new ImageIcon("waiting.gif").getImage());
                    JPanel loadPanel = new JPanel(new BorderLayout());
                    loadPanel.add(load,BorderLayout.CENTER);
                    parent.setContentPane(loadPanel);
                    parent.revalidate();
                    parent.repaint();

                    // Setup game when opponent has joined
                    new Thread(() -> {
                        //Extract opponent's IP
                        opponentIP = handshake.startConnection();

                        // Sets up check for connection in case opponent disconnects
                        new Thread(()-> liveConnectionCheck.hostStatus(parent)).start();

                        //Sets up rest of game
                        parent.setContentPane(getGame(true,parent));
                        new Thread(() -> chatProtocols.listenToMsg(chatArea)).start();
                        parent.revalidate();
                        parent.repaint();
                    }).start();
                }
        );
        homePanel.add(hostButton, gbc);

        // Join Button
        JButton joinButton = new JButton("Join Game");
        joinButton.addActionListener(e ->
                {
                    System.out.println("Join pressed");
                    //Extract opponent's IP
                    opponentIP = handshake.joinConnection();

                    // If connection is made
                    if(opponentIP != null){
                        new Thread(()-> liveConnectionCheck.clientStatus(opponentIP,parent)).start();
                        parent.setContentPane(getGame(false, parent));
                        new Thread(() -> chatProtocols.listenToMsg(chatArea)).start();
                        parent.revalidate();
                        parent.repaint();
                    }
                    // If no connection is made within timeout period
                    else
                        JOptionPane.showMessageDialog(parent, "No match found on network.");
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

    // Sets up the game page for the application
    public JPanel getGame(boolean isHost, JFrame parent) {
        // Set gridbag layout to organize chat, announcer, and board components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Create a board panel using gridbag, prevents buttons from being cutoff compared to using other layouts
        JPanel boardContainer = new JPanel(new GridBagLayout());

        // Generate the player's ship placements
        int[][] playerBoard = shipGen.generateBoard();
        JButton[][] playerBoardButtons = new JButton[BOARD_SIZE][BOARD_SIZE];
        JButton[][] opponentBoardButtons = new JButton[BOARD_SIZE][BOARD_SIZE];

        // Track whose turn it is, for now host goes first
        final boolean[] isMyTurn = {isHost};

        // Create opponent's board
        JPanel board1 = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        board1.setBorder(BorderFactory.createTitledBorder("Opponent's Board"));
        for (int i = 0; i < 100; i++) {
            JButton button = new JButton(String.valueOf(i + 1));
            button.setOpaque(true);
            button.setBackground(Color.LIGHT_GRAY);
            int row = i / BOARD_SIZE;
            int col = i % BOARD_SIZE;

            // Set up action listener to send moves via UDP and update colors
            button.addActionListener(e -> {
                if (isMyTurn[0]) {
                    button.setEnabled(false);
                    gameProtocols.sendMove(opponentIP, announceArea, opponentBoardButtons, row, col,parent);
                    isMyTurn[0] = false;
                }
                else
                    JOptionPane.showMessageDialog(parent, "Wait for your opponent's move!");
            });
            opponentBoardButtons[row][col] = button;
            board1.add(button);
        }
        boardContainer.add(board1, gbc);

        // Create player's board and store the buttons
        JPanel board2 = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        board2.setBorder(BorderFactory.createTitledBorder("Your Board"));
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton button = new JButton(String.valueOf(row * 10 + col + 1));
                button.setEnabled(false);
                button.setOpaque(true);

                // Set button colors based on ship placement
                if (playerBoard[row][col] == 1)
                    button.setBackground(Color.GREEN);
                else
                    button.setBackground(Color.BLUE);

                playerBoardButtons[row][col] = button;
                board2.add(button);
            }
        }
        boardContainer.add(board2, gbc);

        // Create side panel for chat and announcer panels
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.add(createChatPanel(), BorderLayout.NORTH);
        sidePanel.add(createAnnouncerPanel(), BorderLayout.SOUTH);

        // Add boards and side panel to main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(boardContainer, BorderLayout.CENTER);
        mainPanel.add(sidePanel, BorderLayout.EAST);

        // Start a thread to listen for incoming moves and update the board
        new Thread(() -> gameProtocols.listenToMoves(announceArea, playerBoardButtons, playerBoard, isMyTurn, parent)).start();

        // Return the main panel containing everything
        return mainPanel;
    }


    // Sets up the chat panel that is displayed on the game page
    public JPanel createChatPanel() {
        // Panel for chat
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Chat"));

        // Text area for displaying chat messages
        chatArea = new JTextArea(15, 20);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Input field for entering new chat messages
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("Send");

        // Action listener for the send button
        ActionListener sendAction = e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                chatProtocols.sendMsg(message,opponentIP,chatArea);
                inputField.setText("");
            }
        };

        // Attach send action to button and hitting Enter
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);

        // Bottom panel for text input and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to the chat panel
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        // Return panel
        return chatPanel;
    }

    // Sets up the announcer panel that is displayed on the game page
    private JPanel createAnnouncerPanel() {
        // Setup panel layout
        JPanel announcerPanel = new JPanel(new BorderLayout());
        announcerPanel.setBorder(BorderFactory.createTitledBorder("Announcements"));

        // Text area for displaying announcements
        announceArea = new JTextArea(15, 20);
        announceArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(announceArea);

        // Add components to the announcement panel
        announcerPanel.add(scrollPane, BorderLayout.CENTER);

        // Return panel
        return announcerPanel;
    }

    // Sets up the instruction page for the application
    public JPanel getInstructions(JFrame parent) {
        // Set up the background image panel
        imagePanel homePanel = new imagePanel(new ImageIcon("background.png").getImage());

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
        catch(Exception e) {System.err.println("Error: Unable to read instructions from file.\n");}
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
}
