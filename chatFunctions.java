import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.awt.event.ActionListener;

public class chatFunctions {
    private static Socket socket;
    static JTextArea textArea;

    chatFunctions(Socket socket){
        chatFunctions.socket = socket;
    }

    // Create the chat panel method
    public static JPanel createChatPanel() {
        // Panel for chat
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Chat"));

        // Text area for chat messages
        JTextArea chatArea = new JTextArea(15, 20);
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

        return chatPanel;
    }

    // Listens to ongoing chat messages relayed from server using another thread
    public static void listenForMessages() {
        new Thread(() -> {
        try {
            DataInputStream fromServer = new DataInputStream(socket.getInputStream());
            // While connection is still active
            while (true) {
                // Decrypt message using communication key and display
                String message = fromServer.readUTF();
                textArea.append(message + "\n");
            }
        }
        catch (IOException e) {System.err.println("Listening error: " + e.getMessage());}
        catch (Exception e) {System.err.println("Other Listening error: " + e.getMessage());}
        }).start();
    }

    // Allows for sending a message upon hitting send
    public static void sendMessage(String message) {
        try {
            DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
            toServer.writeUTF(message);
        }
        catch (IOException e) {System.err.println("Send error: " + e.getMessage());}
        catch (Exception e) {System.err.println("Other Send error: " + e.getMessage());}
    }
}
