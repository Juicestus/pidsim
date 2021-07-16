package simulations;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import components.*;

public class SwerveWheel extends Simulation {
    private static final long serialVersionUID = 6871657960121883675L;

    private static int CONTROLS_WDITH = 600;
    private static int CONTROLS_HEIGHT = 400;

    private static int CANVAS_WIDTH = 600;
    private static int CANVAS_HEIGHT = 600;

    private static int CANVAS_CENTER_X = (int) (CANVAS_WIDTH / 2);
    private static int CANVAS_CENTER_Y = (int) (CANVAS_HEIGHT / 2);

    // Check out Bound 2 by Kanye West!
    private int BOUND_X = (int) ((SCREEN_WIDTH - CANVAS_WIDTH) / 2);
    private int BOUND_Y = (int) ((SCREEN_HEIGHT - CANVAS_HEIGHT) / 2);

    private static boolean rightPressed = false;
    private static boolean leftPressed = false;

    private static int pointIncrement = 1;

    public static Rect follower = new Rect(CANVAS_CENTER_X, CANVAS_CENTER_Y, 50, 350);
    public static Rect pointer = new Rect(CANVAS_CENTER_X, CANVAS_CENTER_Y, 25, 600);

    @Override
    public void keyPressed(KeyEvent e) {
        rightPressed = (e.getKeyCode() == KeyEvent.VK_RIGHT);
        leftPressed = (e.getKeyCode() == KeyEvent.VK_LEFT);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        rightPressed = false;
        leftPressed = false;
    }

    public void set() {
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.GREEN);
        Shape pointerShape = pointer.getShape();
        g2d.draw(pointerShape);
        g2d.fill(pointerShape);

        g2d.setColor(Color.BLACK);
        Shape followerShape = follower.getShape();
        g2d.draw(followerShape);
        g2d.fill(followerShape);
    }

    public SwerveWheel() {
        PID pid = new PID("swervewheel.txt");
        double[] arr;

        // Start UI etc.
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("PID Simulator - SwerveWheel");
        window.setBounds(30, 30, SCREEN_WIDTH, SCREEN_HEIGHT);

        this.setBorder(BorderFactory.createLineBorder(Color.black));
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

        JPanel output = new JPanel();
        output.setLayout(new GridLayout(1, 0));
        JLabel pointerPos = new JLabel("Initializing Positions...");
        pointerPos.setHorizontalAlignment(JLabel.CENTER);
        pointerPos.setFont(Fonts.serif);
        JLabel followerPos = new JLabel("Initializing Positions...");
        followerPos.setHorizontalAlignment(JLabel.CENTER);
        followerPos.setFont(Fonts.serif);
        output.add(pointerPos);
        output.add(followerPos);
        controlPanel.add(output);

        JPanel reqPosPane = new JPanel();
        reqPosPane.setLayout(new GridLayout(2, 0));

        JLabel label = new JLabel("Set Requested Positions (-180 < n < 180)");
        // make this set to wrap const maybe idk (M)
        label.setFont(Fonts.serif);
        label.setHorizontalAlignment(JLabel.CENTER);
        reqPosPane.add(label);

        JPanel reqFormPane = new JPanel();
        reqFormPane.setLayout(new FlowLayout());

        JTextField reqPosF = new JTextField("");
        reqPosF.setColumns(10);

        reqPosF.setFont(Fonts.mono);
        reqFormPane.add(reqPosF);

        JButton submit = new JButton("Change Position");
        submit.setFont(Fonts.serif);
        reqFormPane.add(submit);

        reqPosPane.add(reqFormPane);
        controlPanel.add(reqPosPane);

        controls.getContentPane().add(controlPanel);
        controls.setVisible(true);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String reqPosS = reqPosF.getText();
                try {
                    int reqPosI = Integer.parseInt(reqPosS);
                    if (-180 > reqPosI) {
                        pointer.rot = -180;
                    } else if (reqPosI > 180) {
                        pointer.rot = 180;
                    } else {
                        pointer.rot = reqPosI;
                    }
                } catch (NumberFormatException error) {
                    reqPosF.setText("Invalid!");
                }

            }
        });

        // End UI etc.

        double minChange = 0.;

        while (true) {
            arr = pid.readPIDVals();
            pid.setFromArray(arr);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            this.set();
            /* Start */

            if (rightPressed) {
                pointer.rot += pointIncrement;
            }
            if (leftPressed) {
                pointer.rot -= pointIncrement;
            }

            pointer.clampRotation(-180, 180);
            // follower.clampBetween(-180, 180);
            minChange = MinimumChange.getMinimumChange(follower.rot, pointer.rot);

            // pid.target = pointer.rot;
            pid.target = minChange;
            follower.rot = pid.update(follower.rot);

            /* End */
            this.setBounds(BOUND_X, BOUND_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
            window.getContentPane().add(this);

            pointerPos.setText(String.format("Pointer Pos θ: %03d°", (int) (pointer.rot)));
            followerPos.setText(String.format("Follower θ: %03d°", (int) (follower.rot)));

        }
    }

    // All of this could be so completely wrong but thats why im making it
    // ...
    // to see how wrong we r and then to cry about it
    static class MinimumChange {
        public static int m = 180; // this is the max in each direction
        // 180 is for degrees, 5600 is out setting for encoder units

        // My programming lead Jack understands this like 1000 times more than I do
        public static double getMinimumChange(double real, double value) {
            double realFloored = getFloored(real, real);
            double valueFloored = value;

            // Why is it dv?
            // What does that mean?
            // IDK either, Jack just thought it was a good variable on the whiteboard
            // https://www.justusl.com/static/img/whiteboard.jpg

            // It represents the "wrap around" diffrence
            double dv_a = (Math.abs(-MathUtils.nonZeroSignum(valueFloored) * m - realFloored)); // d to breakoff point
                                                                                                // from real
            double dv_b = (Math.abs(MathUtils.nonZeroSignum(valueFloored) * m - valueFloored)); // d to breakoff point
                                                                                                // from value
            double dv = dv_a + dv_b;

            // This represents the "wrap in" diffrence
            double ds = valueFloored - realFloored;
            System.out.printf("(%f, %f) (%f, %f)\n", real, realFloored, ds, dv); // Jacks debug

            if (Math.abs(ds) < dv) {
                return real + ds;
            }
            return (MathUtils.nonZeroSignum(realFloored - valueFloored) * dv) + real;
        }

        public static double getFloored(double real, double value) {
            int a = (int) Math.floor(Math.abs((real / m)));
            int sign = (int) (MathUtils.nonZeroSignum(real));
            double remainder = Math.abs(value % m);

            if (sign == -1) {
                if (a % 2 == 0) {
                    return -remainder;
                } else {
                    return m - remainder;
                }
            } else {
                if (a % 2 == 0) {
                    return remainder;
                } else {
                    return remainder - m;
                }
            }
        }
    }
}