import java.util.HashMap;
import java.awt.Color;

public class Mandelbrot {
	ComplexNumber Z;
	double c;
	double xMin;
	double yMin;
	double range;

	public Mandelbrot (ComplexNumber Zin,double xmin,double ymin,double zoom) {
		Z = Zin;
		xMin = xmin;
		yMin = ymin;
		range = zoom;
	}
	
	public ComplexNumber iterate(ComplexNumber z, ComplexNumber c) {
		z.multiply(z);
		z.add(c);
		return z;
	}

	public int checkBounds(ComplexNumber c) {
		ComplexNumber it = new ComplexNumber(Z.getReal(),Z.getImag());
		for (int i=0; i<255; i++) {
			it = iterate(it,c);
			if (it.absolute() >= 4) {
				return (new Color(0,i,0)).getRGB();
			}
		}
		return (new Color(0,0,125)).getRGB();
	}

	public HashMap<Integer,Integer> plotMandelbrot(int size) {
		HashMap<Integer,Integer> output = new HashMap<Integer,Integer>();
		double x = xMin;
		double y = yMin;
		for (int j=0; j<size; j++) {
			x = xMin;
			for (int i=0; i<size; i++) {
				output.put(size*j+i,checkBounds(new ComplexNumber(x,y)));
				x += Math.abs(range)/((double)size-1);
			}
			y += Math.abs(range)/((double)size-1);
		}
		return output;
	}
}
