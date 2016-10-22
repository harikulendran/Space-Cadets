import java.util.HashMap;
import java.awt.Color;

public class Mandelbrot {
	ComplexNumber Z;
	double c;

	public Mandelbrot (ComplexNumber Zin) {
		Z = Zin;
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
		double x = -2;
		double y = -2;
		for (int j=0; j<size; j++) {
			x = -2;
			for (int i=0; i<size; i++) {
				output.put(size*j+i,checkBounds(new ComplexNumber(x,y)));
				x += (double)4/((double)size-1);
			}
			y += (double)4/((double)size-1);
		}
		return output;
	}
}
