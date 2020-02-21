import java.util.List;
import java.util.Random;

public class Player {
	private Side side;
	private String name;

	enum Side {
		Black, White;
	}

	public Player() {

	}

	public Player(String name, Side side) {
		this.name = name;
		this.side = side;
	}

	public Player(Side side) {
		this.name = side.toString();
		this.side = side;
	}

	public Side get_side() {
		return side;
	}

	public String toString() {
		return name + "/" + side;
	}

	public Board.Decision movement(Move m, Board b) {
		return b.movement(m, side);
	}

	public Board.Decision random_movement(Board b) {
		List<Move> moves = b.valid_moves(side);
		Random ran = new Random();
		Move random_move = moves.get(ran.nextInt(moves.size()));
		return b.movement(random_move, side);
	}
}
