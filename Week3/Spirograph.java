import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.HashMap;

public class Spirograph extends JApplet {
	@Override
	public void init() {
		mainFrame window = new mainFrame();
		add(window);
	}
}

class mainFrame extends JPanel {
	Timer time;
	Boolean running = true;

	Hypocloid h = new Hypocloid(80,35,60,10000);
	HashMap<Integer,Coords> hc;
	HashMap<Integer,Coords> hcNorm;
	Normalize norm = new Normalize(501,501);

	BufferedImage image = new BufferedImage(501,501,BufferedImage.TYPE_INT_ARGB);
	Color red = new Color(0,0,0);
	int col = red.getRGB();
	Color white = new Color(255,255,255);
	int clearCol = white.getRGB();

	String errorText;
	Boolean error = false;
	
	JTextField Rtf = new JTextField("Outer Radius");
	JTextField rtf = new JTextField("Inner Radius");
	JTextField otf = new JTextField("Offset");

	JButton draw = new JButton("Draw");
	JButton animate = new JButton("Animate");

	public final static int INTERVAL = 2;
	int step=0;
	
	public void validate(String R, String r, String o) {
		int Ri;
		int ri;
		int oi;
		try {
			Ri = Integer.parseInt(R);
			ri = Integer.parseInt(r);
			oi = Integer.parseInt(o);
			h = new Hypocloid(Ri,ri,oi,10000);
		} catch (Exception e) {
			System.err.println("Input Error");
			error = true;
			errorText = "There is an error with your input";
			h = new Hypocloid();
		}
	}
	
	public void clearImage() {
		for (int j=0; j < 501; j++) {
			for (int i=0; i<501; i++) {
				image.setRGB(i,j,clearCol);
			}
		}
	}

	public void createImage() {
		calculateImage();
		for (int i=0; i < hcNorm.size(); i++) {
			Coords c = hcNorm.get(i);
			image.setRGB((int)c.getX(),(int)c.getY(),col);
		}
		repaint();
	}
	
	public void animateCreateImage() {
		Coords c = hcNorm.get(step);
		step++;
		image.setRGB((int)c.getX(),(int)c.getY(),col);
		repaint();
		if (step >= hcNorm.size()) {
			running = false;
			step = 0;
			System.out.println("DONE");
		}
	}

	public void calculateImage() {
		clearImage();
		hc = h.getCoordinates();
		hcNorm = norm.Range(hc);
	}
	

	//paint component
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.BLACK);
		if (error) {
			g.drawString(errorText,0,710);
			error = false;
		} else {
			g.drawImage(image,norm.getXOff(),norm.getYOff(),null);
		}
	}

	public mainFrame() {
		//input fields
		add(Rtf);
		add(rtf);
		add(otf);
		
		//draw button
		draw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				running = false;
				String R = Rtf.getText();
				String r = rtf.getText();
				String o = otf.getText();
				validate(R,r,o);
				calculateImage();
				createImage();
			}
		});
		
		add(draw);
		
		//animate button
		animate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String R = Rtf.getText();
				String r = rtf.getText();
				String o = otf.getText();
				validate(R,r,o);
				calculateImage();
				running = true;
				time.start();
			}
		});

		add(animate);
		

		//Timer from animation
		time = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animateCreateImage();
				if (!running) {
					time.stop();
				}
			}
		});
	}
}
