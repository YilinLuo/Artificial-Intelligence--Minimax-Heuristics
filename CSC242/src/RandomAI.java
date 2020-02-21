import java.util.List;
import java.util.Random;

public class RandomAI extends Player implements AI {
	public RandomAI(String name, Side s) {
		super(name, s);
	}

	public RandomAI(Side s) {
		super("Random", s);
	}

	public Board.Decision movement(Board board) {
		Random ran = new Random();
		List<Move> moves = board.valid_moves(get_side());
		if (moves.size() == 0)
			return Board.Decision.game_over;
		Move m = moves.get(ran.nextInt(moves.size()));

		return board.movement(m, get_side());
	}

}
