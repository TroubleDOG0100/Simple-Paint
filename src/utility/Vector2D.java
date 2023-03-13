package utility;

public class Vector2D {

	public double x;
	public double y;

	public Vector2D() {
		this.setTo(0, 0);
	}
	
	public Vector2D(double x, double y) {
		this.setTo(x, y);
	}

	public void setTo(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	// Returns the distance between the two vectors.
	public double distanceTo(Vector2D vec2) {
		return Math.sqrt(Math.pow((vec2.x - x), 2) + Math.pow((vec2.y - y), 2)); 
	}

	
	public double distanceTo(double x, double y) {
		return distanceTo(new Vector2D(x, y));
	}
}
