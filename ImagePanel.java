import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
    // Data fields
    private Image img;

    // Used to draw the background image displayed on the menu
    public ImagePanel(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setLayout(null);
    }

    public void paintComponent(Graphics g) {g.drawImage(img, 0, 0, getWidth(), getHeight(), this);}
}
