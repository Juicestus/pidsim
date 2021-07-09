import java.lang.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;

class MainCanvas extends JComponent implements KeyListener {

	final private double DEG2RAD = (Math.PI / 180);
	final private double RAD2DEG = (180 / Math.PI);

	private static int screenWidth = 1000;
	private static int screenHeight = 1000;

	private static boolean rightPressed = false;
	private static boolean leftPressed = false;

	private static int pointIncrement = 1;

	public static Rect follower = new Rect(500, 400, 50, 450);
	public static Rect pointer = new Rect(500, 400, 25, 600);
	//public static PID pid = new PID(.80, 0.1, 0.1);


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
			for (int i=0; i < r.length; i++) {
				r[i] = Double.parseDouble(split[i]);
			}
		} catch (FileNotFoundException e) {
			System.out.println(String.format(
				"File: %s cannot be found!", fn));
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

		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(30, 30, screenWidth, screenHeight);
		window.setTitle(String.format("PID Simulator – reaing data from \"%s\"", fn));

		JLabel data = new JLabel("Initializing...");
		data.setVerticalAlignment(JLabel.BOTTOM);

		MainCanvas canvas = new MainCanvas();
		window.addKeyListener(canvas);
		window.getContentPane().add(canvas);
		window.setVisible(true);

		while (true) {
			arr = readPIDVals(fn);
			pid.setFromArry(arr);

			Thread.sleep(5);
			canvas.set();
			/* Start */

			if (rightPressed) {
				pointer.rot += pointIncrement;
			}
			if (leftPressed) {
				pointer.rot -= pointIncrement;
			}

			pointer.clampBetween(-180, 180);
			follower.clampBetween(-180, 180);

			pid.target = pointer.rot;
			follower.rot = pid.update(follower.rot);

			/* End */
			window.getContentPane().add(canvas);
			data.setText(String.format(
				"  Pointer θ: %03d°    Follower θ: %03d°", 
				(int) (pointer.rot),
				(int) (follower.rot)
			));
			window.getContentPane().add(data);
			window.setVisible(true);
		}
	}
}

class Rect {
	public double rot = 0;
	public int cx = 0;
	public int cy = 0;
	public int w = 0;
	public int h = 0;

	public Rect (int cx, int cy, int w, int h) {
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

		Rectangle rect = new Rectangle(-(int)(w/2), -(int)(h/2), w, h);
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
