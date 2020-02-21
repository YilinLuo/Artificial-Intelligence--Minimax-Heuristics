import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Minimax extends Player implements AI {
	private int depth;
	private long total_AI_time;
	private Point capture_point;
	private static int state_counter = 0;

	public Minimax(String name, Side side) {
		super(name, side);
	}

	public Minimax(Side side, int depth) {
		super("AI", side);
		this.depth = depth;
		this.total_AI_time = 0;
	}

	public Board.Decision movement(Board b) {
		Move m = null;
		long time1 = System.currentTimeMillis();
		if (Main.opponent_type == 1) { // Random AI
			Random ran = new Random();
			List<Move> moves = b.valid_moves(get_side());
			if (moves.size() == 0)
				return Board.Decision.game_over;
			m = moves.get(ran.nextInt(moves.size()));
		} else if (Main.opponent_type == 2) {
			m = minimax(b, depth, get_side(), true); // Minimax
		} else if (Main.opponent_type == 3) {
			m = abpruning(b, depth, get_side(), true); // Alpha beta pruning
		} else if (Main.opponent_type == 4) {
			m = h_minimax(b, depth, get_side(), true); // Heuristic MINIMAX with alpha-beta pruning
		}
		long time2 = System.currentTimeMillis();
		double elapsed_time = (time2 - time1) / 1000.0000;
		Board.Decision move = b.movement(m, get_side());
		if (move == Board.Decision.multi_capture)
			capture_point = m.get_p2();
		System.out.println(String.format("    Elapsed time: %.4f secs", elapsed_time));
		System.out.println();
		total_AI_time += elapsed_time;
		return move;
	}

	private Move minimax(Board b, int depth, Side side, boolean max_player) {
		this.state_counter = 0;
		Random ran = new Random();
		List<Move> valid_moves;
		List<Double> utility = new ArrayList<>();

		if (capture_point == null) {
			valid_moves = b.valid_moves(side);
		} else { // if there is a capture move
			valid_moves = b.capture_moves(capture_point.x, capture_point.y, side); // consider all valid moves at p2
			capture_point = null;
		}

		if (valid_moves.isEmpty())
			return null;
		Board temp = null;
		for (int i = 0; i < valid_moves.size(); i++) {
			temp = b.clone();
			temp.movement(valid_moves.get(i), side);
			utility.add(find_minimax(temp, depth - 1, opponent(side), !max_player));
		}
		System.out.println("\nMinimax at depth: " + depth + "\n" + utility);

		double max = Double.NEGATIVE_INFINITY;
		for (int i = utility.size()-1; i>=0;i--) {
			if (utility.get(i) >= max) {
				max = utility.get(i);
			}
		}
		System.out.println("\nUnflitered utility: " + utility);
		for (int i = 0; i < utility.size(); i++) {
			if (utility.get(i) < max) {
				utility.remove(i);
				valid_moves.remove(i);
				i--;
			}
		}
		System.out.println("Flitered utility: " + utility);
		Move move = valid_moves.get(ran.nextInt(valid_moves.size()));
		System.out.println("\n    Best move: " + move.toString());
		System.out.println("    Value: " + max);
		System.out.println("    Visited: " + state_counter);
		return move;
	}

	private Move abpruning(Board b, int depth, Side side, boolean max_player) {
		this.state_counter = 0;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		Random ran = new Random();
		List<Move> valid_moves;
		List<Double> utility = new ArrayList<>();

		if (capture_point == null) {
			valid_moves = b.valid_moves(side);
		} else {
			valid_moves = b.capture_moves(capture_point.x, capture_point.y, side);
			capture_point = null;
		}

		if (valid_moves.isEmpty())
			return null;
		Board temp = null;
		for (int i = 0; i < valid_moves.size(); i++) {
			temp = b.clone();
			temp.movement(valid_moves.get(i), side);
			utility.add(find_abpruning(temp, depth - 1, opponent(side), !max_player, alpha, beta));
		}
		double max = Double.NEGATIVE_INFINITY;
		for (int i = utility.size()-1; i>=0;i--) {
			if (utility.get(i) >= max) {
				max = utility.get(i);
			}
		}
		System.out.println("\nUnflitered utility: " + utility);
		for (int i = 0; i < utility.size(); i++) {
			if (utility.get(i) < max) {
				utility.remove(i);
				valid_moves.remove(i);
				i--;
			}
		}
		System.out.println("Flitered utility: " + utility);
		Move move = valid_moves.get(ran.nextInt(valid_moves.size()));
		System.out.println("\n    Best move: " + move.toString());
		System.out.println("    Value: " + max);
		System.out.println("    Visited: " + state_counter);
		return move;
	}

	private Move h_minimax(Board b, int depth, Side side, boolean max_player) {
		this.state_counter = 0;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		Random ran = new Random();
		List<Move> valid_moves;
		List<Double> heuristic = new ArrayList<>();

		if (capture_point == null) {
			valid_moves = b.valid_moves(side);
		} else {
			valid_moves = b.capture_moves(capture_point.x, capture_point.y, side);
			capture_point = null;
		}

		if (valid_moves.isEmpty())
			return null;
		Board temp = null;
		for (int i = 0; i < valid_moves.size(); i++) {
			temp = b.clone();
		    temp.movement(valid_moves.get(i), side);
			heuristic.add(find_heuristic(temp, depth - 1, opponent(side), !max_player, alpha, beta));
			
			/*
			b.movement(valid_moves.get(i), side);
			heuristic.add(find_heuristic(b, depth - 1, opponent(side), !max_player, alpha, beta));
			*/
		}
		double max = Double.NEGATIVE_INFINITY;
		for (int i = heuristic.size()-1; i>=0;i--) {
			if (heuristic.get(i) >= max) {
				max = heuristic.get(i);
			}
		}
		System.out.println("\nUnfiltered heuristics: " + heuristic);
		for(int i =0; i<heuristic.size(); i++) {
			if(heuristic.get(i)<max) {
			heuristic.remove(i);
			valid_moves.remove(i);
			i--;
			}
		}
	    System.out.println("Filtered/max heuristics: " + heuristic);
		Move move = valid_moves.get(ran.nextInt(valid_moves.size()));
		System.out.println("\n    Best move: " + move.toString());
		System.out.println("    Value: " + max);
		System.out.println("    Visited: " + state_counter);
		return move;
	}

	private double find_minimax(Board b, int depth, Side side, boolean max_player) {
		state_counter++;
		if (isTerminated(b)) // if the game is over
			return utility(b);
		if (depth == 0) // if after 10/50 moves, the game is draw
			return utility(b);
		List<Move> valid_moves = b.valid_moves(side);
		Board temp = null;
		double initial;
		if (max_player) {
			initial = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < valid_moves.size(); i++) {
				temp = b.clone();
				temp.movement(valid_moves.get(i), side);
				double result = find_minimax(temp, depth - 1, opponent(side), !max_player);
				initial = Math.max(initial, result);
			}
		} else {
			initial = Double.POSITIVE_INFINITY;
			for (int i = 0; i < valid_moves.size(); i++) {
				temp = b.clone();
				temp.movement(valid_moves.get(i), side);
				double result = find_minimax(temp, depth - 1, opponent(side), !max_player);
				initial = Math.min(initial, result);
			}
		}
		return initial;
	}

	private double find_abpruning(Board b, int depth, Side side, boolean max_player, double alpha, double beta) {
		state_counter++;
		if (isTerminated(b))
			return utility(b);
		if (depth == 0)
			return utility(b);
		List<Move> valid_moves = b.valid_moves(side);
		Board temp = null;
		double initial;
		if (max_player) {
			initial = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < valid_moves.size(); i++) {
				temp = b.clone();
				temp.movement(valid_moves.get(i), side);
				double result = find_abpruning(temp, depth - 1, opponent(side), !max_player, alpha, beta);
				initial = Math.max(initial, result);
				alpha = Math.max(alpha, initial);
				if (alpha >= beta)
					break;
			}
		} else {
			initial = Double.POSITIVE_INFINITY;
			for (int i = 0; i < valid_moves.size(); i++) {
				temp = b.clone();
				temp.movement(valid_moves.get(i), side);
				double result = find_abpruning(temp, depth - 1, opponent(side), !max_player, alpha, beta);
				initial = Math.min(initial, result);
				alpha = Math.min(alpha, initial);
				if (alpha >= beta)
					break;
			}
		}
		return initial;
	}

	private double find_heuristic(Board b, int depth, Side side, boolean max_player, double alpha, double beta) {
		state_counter++;
		if (isTerminated(b)) {
			return heuristic(b);
		}
		if (depth == 0)
			return heuristic(b);
		List<Move> valid_moves = b.valid_moves(side);
		Board temp = null;
		double initial;
		if (max_player) {
			initial = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < valid_moves.size(); i++) {
				temp = b.clone();
				temp.movement(valid_moves.get(i), side);
				double result = find_heuristic(temp, depth - 1, opponent(side), !max_player, alpha, beta);
				initial = Math.max(initial, result);
				alpha = Math.max(alpha, initial);
				if (alpha >= beta)
					break;
			}
		} else {
			initial = Double.POSITIVE_INFINITY;
			for (int i = 0; i < valid_moves.size(); i++) {
				temp = b.clone();
				temp.movement(valid_moves.get(i), side);
				double result = find_heuristic(temp, depth - 1, opponent(side), !max_player, alpha, beta);
				initial = Math.min(initial, result);
				alpha = Math.min(alpha, initial);
				if (alpha >= beta)
					break;
			}
		}
		return initial;
	}

	private double utility(Board b) {
		if (get_side() == Side.Black) { // black wins: utility=1, loses: utility=-1
			if (b.get_w() == 0)
				return 1;
			else if (b.get_b() == 0)
				return -1;
		} else if (get_side() == Side.White) { // white wins: utility=1, loses: utility=-1
			if (b.get_b() == 0)
				return 1;
			else if (b.get_w() == 0)
				return -1;
		}
		return 0;
	}

	private double heuristic(Board b) {
		double king_weight = 1.2;
		double heuristic = 0;
		if (get_side() == Side.Black)
			heuristic = b.get_b_king() * king_weight + b.get_b_normal() - b.get_w_normal()
					- king_weight * b.get_w_king();
		else if (get_side() == Side.White)
			heuristic = b.get_w_king() * king_weight + b.get_w_normal() - b.get_b_normal()
					- king_weight * b.get_b_king();
		return heuristic;
	}

	private boolean isTerminated(Board b) {
		if (get_side() == Side.Black) { // black wins: utility=1, loses: utility=-1
			if (b.get_w() == 0)
				return true;
		} else if (get_side() == Side.White) { // white wins: utility=1, loses: utility=-1
			if (b.get_b() == 0)
				return true;
		}
		return false;
	}

	private Side opponent(Side side) {
		if (side == Side.Black)
			return side.White;
		return side.Black;
	}
}
