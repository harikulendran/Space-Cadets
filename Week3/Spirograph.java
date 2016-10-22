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
	Boolean spiro = true;

	Hypocloid h = new Hypocloid(80,35,60,10000);
	Mandelbrot mSet;
	HashMap<Integer,Coords> hc;
	HashMap<Integer,Coords> hcNorm;
	Normalize norm = new Normalize(501,501);

	HashMap<Integer,Integer> mand;

	BufferedImage image = new BufferedImage(501,501,BufferedImage.TYPE_INT_ARGB);
	Color red = new Color(0,0,0);
	int col = red.getRGB();
	Color white = new Color(255,255,255);
	int clearCol = white.getRGB();

	String errorText;
	Boolean error = false;
	String alertText;
	Boolean alert = false;
	
	JTextField Rtf = new JTextField("Outer Radius");
	JTextField rtf = new JTextField("Inner Radius");
	JTextField otf = new JTextField("Offset");

	Checkbox Rbox = new Checkbox("Sweep R",false);
	Checkbox rbox = new Checkbox("Sweep r",false);
	Checkbox obox = new Checkbox("Sweep o",false);

	JButton draw = new JButton("Draw");
	JButton animate = new JButton("Animate");
	JButton sweep = new JButton("Sweep");
	JButton swap = new JButton("Mandelbrot Set");
	
	int animOption = 0;

	//Sweep vars very messy
	Boolean sweepR = false;
	Boolean sweepr = false;
	Boolean sweepo = false;

	int Rhs;
	int rhs;
	int ohs;

	int Ris;
	int ris;
	int ois;

	int Rmod = 1;
	int rmod = 1;
	int omod = 1;



	public final static int INTERVAL = 1;
	int step=0;
	
	public void validate(String R, String r, String o) {
		int Ri;
		int ri;
		int oi;
		try {
			Ri = Integer.parseInt(R);
			ri = Integer.parseInt(r);
			oi = Integer.parseInt(o);
			h = new Hypocloid(Ri,ri,oi,20000);
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
	
	public void drawMandelbrot(int size) {
		clearImage();
		for (int j=0; j<size; j++) {
			for (int i=0; i<size; i++) {
				image.setRGB(i,j,mand.get(size*j+i));
			}
		}
		repaint();
	}
	
	public void animateCreateImage() {
		alertText = step*(100/(double)hcNorm.size()) + "%";
		Coords c = hcNorm.get(step);
		step++;
		image.setRGB((int)c.getX(),(int)c.getY(),col);
		repaint();
		if (step >= hcNorm.size()) {
			running = false;
			step = 0;
			alertText = "DONE!";
		}
	}

	public void animateSweepImage() {
		String alertHolder = "";

		if (sweepR) {
			if (Ris > Rhs + 50) {
				Rmod = -1;
			} 
			if (Ris < Rhs - 50) {
				Rmod = 1;
			}
			Ris += Rmod;
			alertHolder += "R Value: " + Integer.toString(Ris);
		}
		if (sweepr) {
			if (ris > rhs + 50) {
				rmod = -1;
			} 
			if (ris < rhs - 50) {
				rmod = 1;
			}
			ris += rmod;
			alertHolder += '\n' + "r  Value: " + Integer.toString(ris);
		}
		if (sweepo) {
			if (ois > ohs + 100) {
				omod = -1;
			} 
			if (ois < ohs - 100) {
				omod = 1;
			}
			ois += omod;
			alertHolder += '\n' + "o Value: " + Integer.toString(ois);
		}
		
		alertText = alertHolder;
		validate(Integer.toString(Ris),Integer.toString(ris),Integer.toString(ois));
		createImage();
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

		if (alert) {
			int height = 675;
			for (String splitAlert : alertText.split("\n")) {
				g.drawString(splitAlert,1200,height += 15);
			}
		}
	}

	public mainFrame() {
		//input fieldsanima
		add(Rtf);
		add(rtf);
		add(otf);

		add(Rbox);
		add(rbox);
		add(obox);
		
		//draw button
		draw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				running = false;
				alert = false;
				if (spiro) {
					String R = Rtf.getText();
					String r = rtf.getText();
					String o = otf.getText();
					validate(R,r,o);
					calculateImage();
					createImage();
				} else {
					try {
						double manI = Double.parseDouble(Rtf.getText());
						double manJ = Double.parseDouble(rtf.getText());
						double range = Double.parseDouble(otf.getText());
						mSet = new Mandelbrot(new ComplexNumber(0,0),manI,manJ,range);
						mand = mSet.plotMandelbrot(500);
						drawMandelbrot(500);
					} catch (Exception ex) {
						System.err.println(ex);
						error = true;
						errorText = "There was an error with your input, 0 + 0i Mandelbrot drawn";
					}
				}
			}
		});
		
		add(draw);
		
		//animate button
		animate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (spiro) {
					String R = Rtf.getText();
					String r = rtf.getText();
					String o = otf.getText();
					validate(R,r,o);
					calculateImage();
					animOption = 0;
					running = true;
					alert = true;
					time.start();
				} else {
					error = true;
					errorText = "Cannot animate Mandelbrot set";
					repaint();
				}
			}
		});

		add(animate);
		
		sweep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (spiro) {
					String R = Rtf.getText();
					String r = rtf.getText();
					String o = otf.getText();
					validate(R,r,o);
					Ris = Integer.parseInt(R);
					ris = Integer.parseInt(r);
					ois = Integer.parseInt(o);
					Rhs = Ris;
					rhs = ris;
					ohs = ois;
					sweepR = Rbox.getState();
					sweepr = rbox.getState();
					sweepo = obox.getState();
					animOption = 1;
					running = true;
					alert = true;
					time.start();
				} else {
					error = true;
					errorText = "Cannot sweep Mandelbrot set";
					repaint();
				}
			}
		});

		add(sweep);

		swap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (spiro) {
					spiro = false;
					Rtf.setText("X min");
					rtf.setText("Y min");
					otf.setText("Range");
					swap.setText("Spirographs");
				} else {
					spiro = true;
					Rtf.setText("Outer Radius");
					rtf.setText("Inner Radius");
					swap.setText("Mandelbrot Set");
				}
			}
		});

		add(swap);

		//Timer from animation
		time = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (animOption == 0) {
					animateCreateImage();
				} else if (animOption == 1) {
					animateSweepImage();
				}
				if (!running) {
					time.stop();
				}
			}
		});
	}
}
