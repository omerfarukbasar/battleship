import javax.swing.*;

public class client extends JFrame {
    // JFrame data fields
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 600;

    // Establishes main menu GUI for program
    private void createGUI() {
        // Set window size
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);

        // Land on homepage
        views yeet = new views();
        this.setContentPane(yeet.getHome(this));

        // Other stuff
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    // Overall window for client
    public client() {
        super("Battleship");
        createGUI();
    }

    public static void main(String[] args) {client Client = new client();}
}
