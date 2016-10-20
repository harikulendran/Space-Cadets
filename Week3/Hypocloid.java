import java.util.HashMap;

public class Hypocloid {
	double R;
	double r;
	double t;
	double O;
	int fidelity;
	HashMap <Integer, Coords> coordsNest = new HashMap<Integer, Coords>();

	public Hypocloid() {
		R = 75;
		r = 60;
		O = 30;
		fidelity = 1000;
	}
	public Hypocloid(double Radius, double radius, double offset, int fid) {
		R = Radius;
		r = radius;
		O = offset;
		fidelity = fid;
	}
	
	public HashMap<Integer,Coords> getCoordinates() {
		for (int i=0; i < fidelity; i++) {
			t = i;
			double x = (R+r)*Math.cos(t) - (r+O)*Math.cos(((R+r)/r)*t);
			double y = (R+r)*Math.sin(t) - (r+O)*Math.sin(((R+r)/r)*t);
			//System.out.println(x);
			coordsNest.put(i, new Coords(x+100,y+100));
		}
		return coordsNest;
	}
}
