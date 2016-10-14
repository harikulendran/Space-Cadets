import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class AppletChallenge extends JApplet {
	
	//initialise the JPanel
	@Override
	public void init() {
		animationFrame window = new animationFrame();
		add(window);
	}
}

class animationFrame extends JPanel {
       		
	//create the timer to control the frame refresh rate
	Timer time;
	JButton startJB;
	JButton stopJB;
	JComboBox<String> options;
	Boolean running = true;
	String selected = "Please select an option";
	int xPos = 0;
	int yPos = 0;
	int size = 10;
	int mod = 1;
	int xM = 1;
	int yM = 1;
	int maxHeight = 100;
	Boolean animate = false;
	Random rand = new Random();

	//set the interval for the refresh rate
	public final static int INTERVAL = 2;

	public animationFrame() {
		//create and implement the start button
		startJB = new JButton("Start");
		startJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Do Something
				reset();
				animate = true;
			}
		});
		
		stopJB = new JButton("Stop");
		stopJB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Do Something
				reset();
				animate = false;
			}
		});


		options = new JComboBox<String>();
		options.addItem("Please select an option");
		options.addItem("Zoom");
		options.addItem("Wander");
		options.addItem("Bounce");
		options.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected = (String)((JComboBox)e.getSource()).getSelectedItem();
				reset();
			}
		});

		add(startJB);
		add(stopJB);
		add(options);

		//create and implement the timer that contols the running of the program
		time = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action();
				if (!running) {
					time.stop();
				}
			}
		});
		time.start();
	}
	
	public void reset() {
		xPos = 640;
		yPos = 360;
		xM = 1;
		yM = 1;
		size = 50;
		maxHeight = 100;
	}

	public void action() {
		if (animate) {
			if (selected.contains("Please")) {
				xPos = 640;
				yPos = 360;
				size = 50;
			} else if (selected.contains("Zoom")) {
				if (size > 200) {mod = -1;}
				else if (size < 10) {mod = 1;}
				size += mod;
				xPos -= 5*mod;
			} else if (selected.contains("Wander")) {
				size = 20;
				if (xPos < 0) {xM = rand.nextInt(4) + 1;}
				else if (xPos > 1100) {xM = -(rand.nextInt(4) + 1);}
				if (yPos < 10) {yM = rand.nextInt(4) + 1;}
				else if (yPos > 720) {yM = -(rand.nextInt(4) + 1);}
				
				xPos += xM;
				yPos += yM;
			} else if (selected.contains("Bounce")) {
				size = 50;
				xPos = 500;
				if (yPos > 710) {mod = -1; maxHeight += 50;}
			        else if (yPos < maxHeight) {mod = 1;}
				yM = mod*(int)(3 * (double)yPos/(double)maxHeight);
				yPos += yM;
			}
			repaint();
		}
	}
	
	//The paint method that handles the drawing of the graphics
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		g.setColor(Color.RED);
		g.setFont(new Font("Courier New", Font.BOLD, size));
		g.drawString("Hello World!",xPos,yPos);
	}

}
