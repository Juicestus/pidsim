// PIDSim
// GUI Simulator for the SwerveWheel encoder,
// PID, and position selection.
//
// Written by Justus Languell (Juicestus) in July 2021
// for use at Texas Torque (FRC Team 1477) 

import java.lang.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;

class MainCanvas extends JComponent implements KeyListener {

  final private double DEG2RAD = (Math.PI / 180);
  final private double RAD2DEG = (180 / Math.PI);

  private static int SCREEN_WIDTH = 1000;
  private static int SCREEN_HEIGHT = 800;

  private static int CONTROLS_WDITH = 600;
  private static int CONTROLS_HEIGHT = 400;

  private static int CANVAS_WIDTH = 600;
  private static int CANVAS_HEIGHT = 600;

  private static int CANVAS_CENTER_X = (int) (CANVAS_WIDTH / 2);
  private static int CANVAS_CENTER_Y = (int) (CANVAS_HEIGHT / 2);

  // Check out Bound 2 by Kanye West!
  private static int BOUND_X = (int) ((SCREEN_WIDTH - CANVAS_WIDTH) / 2);
  private static int BOUND_Y = (int) ((SCREEN_HEIGHT - CANVAS_HEIGHT) / 2);

  private static boolean rightPressed = false;
  private static boolean leftPressed = false;

  private static int pointIncrement = 1;

  public static Rect follower = new Rect(CANVAS_CENTER_X, CANVAS_CENTER_Y, 50, 350);
  public static Rect pointer = new Rect(CANVAS_CENTER_X, CANVAS_CENTER_Y, 25, 600);

  @Override
  public void keyTyped(KeyEvent e) {
  }

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

  public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2d = (Graphics2D) g;

    g2d.setColor(Color.GREEN);
    Shape pointerShape = pointer.getShape();
    g2d.draw(pointerShape);
    g2d.fill(pointerShape);

    g2d.setColor(Color.BLACK);
    Shape followerShape = follower.getShape();
    g2d.draw(followerShape);
    g2d.fill(followerShape);
  }

  public static double[] readPIDVals(String fn) {
    double[] r = new double[3];
    try {
      String contents = "";
      File fObj = new File(fn);
      Scanner reader = new Scanner(fObj);
      while (reader.hasNextLine()) {
        contents += reader.nextLine();
      }
      reader.close();
      String[] split = contents.split(",");
      for (int i = 0; i < r.length; i++) {
        r[i] = Double.parseDouble(split[i]);
      }
    } catch (FileNotFoundException e) {
      System.out.println(String.format("File: %s cannot be found!", fn));
    }
    return r;
  }

  public static void main(String[] args) throws InterruptedException {

    PID pid = new PID();
    double[] arr;

    String fn = "pid.txt";
    if (args.length > 0) {
      fn = args[0];
    }

    System.out.printf("Reading PID values from file: %s", fn);

    // Start UI etc.
    JFrame window = new JFrame();
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setTitle(String.format("PID Simulator – reaing data from \"%s\"", fn));
    window.setBounds(30, 30, SCREEN_WIDTH, SCREEN_HEIGHT);

    Font serif = new Font("Times", Font.PLAIN, 18);
    Font sans = new Font("Helvetica", Font.PLAIN, 18);
    Font mono = new Font("Courier", Font.PLAIN, 18);

    MainCanvas canvas = new MainCanvas();
    canvas.setBorder(BorderFactory.createLineBorder(Color.black));
    canvas.setBounds(BOUND_X, BOUND_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
    window.addKeyListener(canvas);
    window.getContentPane().add(canvas);
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
    pointerPos.setFont(serif);
    JLabel followerPos = new JLabel("Initializing Positions...");
    followerPos.setHorizontalAlignment(JLabel.CENTER);
    followerPos.setFont(serif);
    output.add(pointerPos);
    output.add(followerPos);
    controlPanel.add(output);

    JPanel reqPosPane = new JPanel();
    reqPosPane.setLayout(new GridLayout(2, 0));

    JLabel label = new JLabel("Set Requested Positions (-180 < n < 180)");
    // make this set to wrap const maybe idk (M)
    label.setFont(serif);
    label.setHorizontalAlignment(JLabel.CENTER);
    reqPosPane.add(label);

    JPanel reqFormPane = new JPanel();
    reqFormPane.setLayout(new FlowLayout());

    JTextField reqPosF = new JTextField("");
    reqPosF.setColumns(10);

    reqPosF.setFont(mono);
    reqFormPane.add(reqPosF);

    JButton submit = new JButton("Change Position");
    submit.setFont(serif);
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
      arr = readPIDVals(fn);
      pid.setFromArry(arr);

      Thread.sleep(10);
      canvas.set();
      /* Start */

      if (rightPressed) {
        pointer.rot += pointIncrement;
      }
      if (leftPressed) {
        pointer.rot -= pointIncrement;
      }

      pointer.clampBetween(-180, 180);
      // follower.clampBetween(-180, 180);
      minChange = MinimumChange.getMinimumChange(follower.rot, pointer.rot);

      // pid.target = pointer.rot;
      pid.target = minChange;
      follower.rot = pid.update(follower.rot);

      /* End */
      canvas.setBounds(BOUND_X, BOUND_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
      window.getContentPane().add(canvas);

      pointerPos.setText(String.format("Pointer Pos θ: %03d°", (int) (pointer.rot)));
      followerPos.setText(String.format("Follower θ: %03d°", (int) (follower.rot)));

    }
  }
}

class Rect {
  public double rot = 0;
  public int cx = 0;
  public int cy = 0;
  public int w = 0;
  public int h = 0;

  public Rect(int cx, int cy, int w, int h) {
    this.cx = cx;
    this.cy = cx;
    this.w = w;
    this.h = h;
  }

  public Shape getShape() {
    AffineTransform rotation = new AffineTransform();
    rotation.rotate(Math.toRadians(rot));

    AffineTransform translation = new AffineTransform();
    translation.translate(cx, cy);

    Rectangle rect = new Rectangle(-(int) (w / 2), -(int) (h / 2), w, h);
    Shape shape = rotation.createTransformedShape(rect);
    shape = translation.createTransformedShape(shape);
    return shape;
  }

  public void clampBetween(int lower, int upper) {
    if (rot > upper) {
      rot = upper;
    }
    if (rot < lower) {
      rot = lower;
    }
  }
}

class PID {
  private double kp;
  private double ki;
  private double kd;

  private double p = 0;
  private double i = 0;
  private double d = 0;

  public double target;

  public double error = 0;
  public NDouble pError = new NDouble();

  public double time = 0;
  public NDouble pTime = new NDouble();

  public PID() {
  }

  public PID(double kp, double ki, double kd) {
    this.kp = kp;
    this.ki = ki;
    this.kd = kd;
  }

  public double update(double input) {
    time = System.currentTimeMillis();
    error = target - input;

    double timeDelta = 0;
    if (!pTime.nil) {
      timeDelta = time - pTime.val;
    }

    double errorDelta = 0;
    if (!pError.nil) {
      errorDelta = error - pError.val;
    }

    p = kp * error;
    // Kp(e)

    i = i + (ki * error);
    // Ki ∫e(Δt)

    d = kd * (errorDelta / (timeDelta / 1000));
    // Kd(Δe/Δt)

    pTime.val = time;
    pError.val = error;

    // Stupid way to check for NaN
    // NaN != NaN, but a number == a number
    // so (var != var) -> (val == NaN)
    if (p != p) {
      p = 0;
    }
    if (i != i) {
      i = 0;
    }
    if (d != d) {
      d = 0;
    }

    return p + i + d;
  }

  public void setFromArry(double[] arr) {
    kp = arr[0];
    ki = arr[1];
    kd = arr[2];
  }
}

// Prob going to get rid of this class
// and just use NaN as a no value state
class NDouble {
  public double val;
  public boolean nil = true;

  public NDouble() {
  }

  public NDouble(int n) {
    val = n;
    nil = false;
  }

  public void set(int n) {
    val = n;
    nil = false;
  }

  public double get() {
    return val;
  }

  public boolean isNil() {
    return nil;
  }
}

// All of this could be so completely fucking wrong but thats why im making it
// ...
// to see how wrong we r and then to cry about it
class MinimumChange {
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
    double dv_a = (Math.abs(-nonZeroSignum(valueFloored) * m - realFloored)); // d to breakoff point from real
    double dv_b = (Math.abs(nonZeroSignum(valueFloored) * m - valueFloored)); // d to breakoff point from value
    double dv = dv_a + dv_b;

    // This represents the "wrap in" diffrence
    double ds = valueFloored - realFloored;
    System.out.printf("(%f, %f) (%f, %f)\n", real, realFloored, ds, dv); // Jacks debug

    if (Math.abs(ds) < dv) {
      return real + ds;
    }
    return (nonZeroSignum(realFloored - valueFloored) * dv) + real;
  }

  public static double getFloored(double real, double value) {
    int a = (int) Math.floor(Math.abs((real / m)));
    int sign = (int) (nonZeroSignum(real));
    double remainder = Math.abs(value % m);

    if (sign == -1) {
      if (a % 2 == 0) {
        return -remainder;
      } else {
        return m - remainder;
      }
    } else {
      if (a % 2 == 1) {
        return -m - remainder;
      } else {
        return remainder;
      }
    }
  }

  public static double nonZeroSignum(double val) {
    if (val >= 0) {
      return 1;
    }
    return -1;
  }
}