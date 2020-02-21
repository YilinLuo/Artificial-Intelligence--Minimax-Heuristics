import java.awt.Point;

public class Move {

	private Point p1;
	private Point p2;

	public Move(int p1_x, int p1_y, int p2_x, int p2_y) {
		p1 = new Point(p1_x, p1_y);
		p2 = new Point(p2_x, p2_y);
	}

	public Move(Point p1, Point p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public Point get_p1() {
		return p1;
	}

	public Point get_p2() {
		return p2;
	}

	public String toString() {
		return p1.x + " " + p1.y + " " + p2.x + " " + p2.y;
	}

	public boolean equals(Object m) {
		if (!(m instanceof Move))
			return false;
		Move move = (Move) m;
		if (this.get_p1().equals(move.get_p1()) && this.get_p2().equals(move.get_p2()))
			return true;
		return false;
	}
}
