import java.util.HashMap;

public class Normalize {
	int xDim;
	int yDim;
	int xOffset;
	int yOffset;


	public Normalize(int x, int y) {
		xDim = x-1;
		yDim = y-1;
		xOffset = (1280-xDim)/2;
		yOffset = (720-yDim)/2;
	}
	
	public int getXOff() {
		return xOffset;
	}
	public int getYOff() {
		return yOffset;
	}

	public HashMap<Integer,Coords> Range(HashMap<Integer,Coords> in) {
		double xMin = xDim;
		double yMin = yDim;
		double xMax = 0;
		double yMax = 0;
		for (int i=0; i<in.size(); i++) {
			double tempx = in.get(i).getX();
			double tempy = in.get(i).getY();
			if (tempx < xMin) {
				xMin = tempx;
			}
			if (tempy < yMin) {
				yMin = tempy;
			}
			if (tempx > xMax) {
				xMax = tempx;
			}
			if (tempy > yMax) {
				yMax = tempy;
			}
		}
		
		double xRatio = xDim/(xMax-xMin);
		double yRatio = yDim/(yMax-yMin);
		
		HashMap<Integer,Coords> output = new HashMap<Integer,Coords>();

		for (int i=0; i<in.size(); i++) {
			output.put(i, new Coords((in.get(i).getX() - xMin)*xRatio, (in.get(i).getY() - yMin)*yRatio));
			//System.out.println((in.get(i).getX() - xMin)*xRatio);
		}

		return output;
	}
}
