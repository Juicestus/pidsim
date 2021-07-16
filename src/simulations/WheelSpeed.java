package simulations;

import components.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.StrokeBorder;

public class WheelSpeed extends Simulation {
    private static final long serialVersionUID = 6232140420146939687L;

    private static int CONTROLS_WDITH = 400;
    private static int CONTROLS_HEIGHT = 200;

    private static int CANVAS_WIDTH = 600;
    private static int CANVAS_HEIGHT = 600;

    private static int CANVAS_CENTER_X = CANVAS_WIDTH / 2;
    private static int CANVAS_CENTER_Y = CANVAS_HEIGHT / 2;

    private int BOUND_X = (int) ((SCREEN_WIDTH - CANVAS_WIDTH) / 2);
    private int BOUND_Y = (int) ((SCREEN_HEIGHT - CANVAS_HEIGHT) / 2);

    private Stroke tenptStroke = new BasicStroke(10);

    private Circle circle;
    private Rect pointer;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);

        g2d.setStroke(tenptStroke);
        g2d.draw(circle.getShape());

        g2d.fill(pointer.getShape());
    }

    public WheelSpeed() {
        super(600, 600);
        PID pid = new PID("wheelspeed.txt");

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("PID Simulator - WheelSpeed");
        window.setBounds(30, 30, SCREEN_WIDTH, SCREEN_HEIGHT);

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.setBounds(BOUND_X, BOUND_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
        window.addKeyListener(this);
        window.getContentPane().add(this);
        window.setVisible(true);

        JFrame controls = new JFrame();
        controls.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controls.setBounds(30, 30, CONTROLS_WDITH, CONTROLS_HEIGHT);
        controls.setTitle("Control Panel");

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(0, 1));

        controls.getContentPane().add(controlPanel);
        controls.setVisible(true);

        circle = new Circle(CANVAS_CENTER_X, CANVAS_CENTER_Y, 300, 300);
        pointer = new Rect(CANVAS_CENTER_X, CANVAS_CENTER_Y, 15, 150);

        double targetRPM = 3600;

        /*
         * To get the RPM we do some ~~~mathematical magic~~~ To begin, RPM means
         * revolutions per minute. Obviously we won't run it for a full minute to get
         * RPM. Therefore, the time portion will be converted from seconds minutes then
         * divided by the revolutions. We won't use full revolutions - rather parts. For
         * example, if we move 180 degrees in 30 seconds then we are going at a rate of
         * 1 RPM.
         */
        double lt = System.currentTimeMillis();
        double lx = 0;

        while (true) {
            // pid update
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            repaint();

            double t = System.currentTimeMillis();
            double dt = t - lt;
            // Convert dt to minutes
            dt /= 60000.0;

            double x = pointer.rot;
            double dx = x - lx;

            double rpm = dx / dt;
            System.out.println("RPM: " + rpm);
            // Determine correct target
            pid.target = pointer.rot + targetRPM * dt;

            double pidOutput = pid.update(pointer.rot + dx);

            lx = pointer.rot;
            lt = t;
            pointer.rot += pidOutput;
            this.setBounds(BOUND_X, BOUND_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
            window.getContentPane().add(this);
        }
    }
}
