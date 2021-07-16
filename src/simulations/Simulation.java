package simulations;

import javax.swing.JComponent;
import java.awt.event.*;

/**
 * This class has methods utilized by all simulations.
 */
public abstract class Simulation extends JComponent implements KeyListener {
    private static final long serialVersionUID = 8017809351887677454L;

    protected int SCREEN_WIDTH = 1000;
    protected int SCREEN_HEIGHT = 800;

    public Simulation() {
    }

    public Simulation(int screenWidth, int screenHeight) {
        this.SCREEN_WIDTH = screenWidth;
        this.SCREEN_HEIGHT = screenHeight;
    }

    /*
     * === Default interface methods ===
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }
}
