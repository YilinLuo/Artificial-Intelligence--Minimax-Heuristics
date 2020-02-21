import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board {
	public Type[][] board; // 2D array that represent the board
	private int w_normal;
	private int w_king;
	private int b_normal;
	private int b_king;
	private int w_firstrow;
	private int b_lastrow;
	public int side_length = Main.side_length; // 4/8

	private enum Type { // piece's type
		empty, white, black, white_king, black_king;
	}

	public enum Decision {
		success, invalid_destination, invalid_piece, multi_capture, game_over;
	}

	public Board() {
		set_up_board(side_length);
//		set_up_test_board(side_length);
	}

	public Board(Type[][] board) {
		w_normal = 0;
		w_king = 0;
		b_normal = 0;
		b_king = 0;
		// Update the number of piece for each type
		for (int i = 0; i < side_length; i++) {
			for (int j = 0; j < side_length; j++) {
				Type piece = get_piece(i, j);
				if (piece == Type.black)
					b_normal++;
				else if (piece == Type.black_king)
					b_king++;
				else if (piece == Type.white)
					w_normal++;
				else if (piece == Type.white_king)
					w_king++;
			}
		}
	}

	public void set_up_board(int side_length) { // set up initial board
		if (side_length == 4) { // initialize variables for 4x4 checker
			w_normal = 2;
			w_king = 0;
			b_normal = 2;
			b_king = 0;
			b_lastrow = 0;
			w_firstrow = 3;
		} else if (side_length == 8) { // initialize variables for 8x8 checker
			w_normal = 12;
			w_king = 0;
			b_normal = 12;
			b_king = 0;
			b_lastrow = 2;
			w_firstrow = 5;
		}
		board = new Type[side_length][side_length]; // create a board

		for (int i = 0; i < board.length; i++) { // add black/white piece types
			int piece = 0; // variable that represent the column of the first piece at i row
			if (i % 2 == 0) {
				piece = 1;
			}
			Type type = Type.empty;
			if (i <= b_lastrow) {
				type = Type.black;
			} else if (i >= w_firstrow) {
				type = Type.white;
			}
			for (int j = piece; j < board[i].length; j += 2) { // add black/white piece types
				board[i][j] = type;
			}
		}

		for (int i = 0; i < board.length; i++) { // add empty piece types
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == null)
					board[i][j] = Type.empty;
			}
		}
	}

	public void set_up_test_board(int side_length) { // set up a board to test
		board = new Type[side_length][side_length];
		board[6][1] = Type.black;
		board[1][0] = Type.white;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == null)
					board[i][j] = Type.empty;
			}
		}
	}

	public List<Move> basic_moves(int row, int col, Player.Side side) { // find all valid basic moves at specific point
		Type t = board[row][col];
		Point p1 = new Point(row, col);
		List<Move> moves = new ArrayList<>(); // initial a list to store all valid basic moves

		if (t == Type.white || t == Type.black) { // if normal piece, 2 possible moves
			int row_change = t == Type.black ? 1 : -1; // black(white) normal piece can only move down(up)
			int row2 = row + row_change;
			if (row2 >= 0 && row2 < side_length) {
				int col2 = col + 1; // piece moves right
				if (col2 < side_length && get_piece(row2, col2) == Type.empty) // if there is no piece at diagonally
																				// adjacent square
					moves.add(new Move(p1, new Point(row2, col2))); // add valid basic moves
				col2 = col - 1; // piece moves lest
				if (col2 >= 0 && get_piece(row2, col2) == Type.empty) // if there is no piece at diagonally adjacent
																		// square
					moves.add(new Move(p1, new Point(row2, col2))); // add valid basic moves
			}
		} else if (t == Type.white_king || t == Type.black_king) { // if king, 4 possible moves
			int row2 = row + 1; // king moves down
			if (row2 < side_length) {
				int col2 = col + 1; // king moves right
				if (col2 < side_length && get_piece(row2, col2) == Type.empty)
					moves.add(new Move(p1, new Point(row2, col2)));
				col2 = col - 1; // king moves left
				if (col2 >= 0 && get_piece(row2, row2) == Type.empty)
					moves.add(new Move(p1, new Point(row2, col2)));
			}
			row2 = row - 1; // king moves up
			if (row2 >= 0) {
				int col2 = col + 1; // king moves right
				if (col2 < side_length && get_piece(row2, col2) == Type.empty)
					moves.add(new Move(p1, new Point(row2, col2)));
				col2 = col - 1; // king moves left
				if (col2 >= 0 && get_piece(row2, col2) == Type.empty)
					moves.add(new Move(p1, new Point(row2, col2)));
			}
		}
		return moves; // return a list of valid basic moves
	}

	public List<Move> capture_moves(int row, int col, Player.Side side) { // find all valid capture moves at specific
																			// point
		Point p1 = new Point(row, col);
		List<Move> moves = new ArrayList<>(); // initialize list to store all valid capture moves
		List<Point> p2 = new ArrayList<>(); // initialize list to store all possible destination for a capture move

		if (side == Player.Side.White && get_piece(row, col) == Type.white) { // if side is white and current point has
																				// white normal piece, add 2 possible
																				// destinations
			p2.add(new Point(row - 2, col + 2));
			p2.add(new Point(row - 2, col - 2));
		} else if (side == Player.Side.Black && get_piece(row, col) == Type.black) { // if side is white and current
																						// point has white normal piece,
																						// add 2 possible destinations
			p2.add(new Point(row + 2, col + 2));
			p2.add(new Point(row + 2, col - 2));
		} else if (get_piece(row, col) == Type.black_king || get_piece(row, col) == Type.white_king) { // current point
																										// is
																										// white/black
																										// king, add 4
																										// possible
																										// destinations
			p2.add(new Point(row + 2, col + 2));
			p2.add(new Point(row + 2, col - 2));
			p2.add(new Point(row - 2, col + 2));
			p2.add(new Point(row - 2, col - 2));
		}

		for (int i = 0; i < p2.size(); i++) { // for all the possibilities, check validity
			Point p = p2.get(i);
			Move m = new Move(p1, p);
			if (p.x < side_length && p.x >= 0 && p.y < side_length && p.y >= 0 // if point is in the board
					&& get_piece(p.x, p.y) == Type.empty && opponent_piece(side, get_piece(mid_square(m)))) { // if mid
																												// square
																												// has
																												// an
																												// opponent
																												// piece
																												// and
																												// destination
																												// is
																												// empty
				moves.add(m); // add valid capture move
			}
		}
		return moves; // return a list of valid capture moves
	}

	public List<Move> valid_moves(int row, int col, Player.Side side) { // find all valid moves at specific point
		List<Move> moves = new ArrayList<>(); // initialize a list to store all valid moves at specific point
		if (own_piece(row, col, side)) { // if point has own piece
			moves.addAll(basic_moves(row, col, side)); // add valid basic moves
			moves.addAll(capture_moves(row, col, side)); // add valid capture moves
		}
		return moves;
	}

	public List<Move> valid_moves(Player.Side side) { // find all valid moves for a state
		List<Move> moves = new ArrayList<>(); // initialize a list to store all valid moves
		for (int i = 0; i < side_length; i++) {
			for (int j = 0; j < side_length; j++) {
				if (own_piece(i, j, side)) { // if point has own piece
					moves.addAll(valid_moves(i, j, side)); // add valid moves
				}
			}
		}
		return moves;
	}

	public Point mid_square(Move m) { // find mid square for a specific capture move
		Point mid = new Point((m.get_p1().x + m.get_p2().x) / 2, (m.get_p1().y + m.get_p2().y) / 2);
		return mid;
	}

	public boolean opponent_piece(Player.Side side, Type t) { // return true if the piece is opponent's piece
		if (side == Player.Side.Black && (t == Type.white || t == Type.white_king))
			return true;
		if (side == Player.Side.White && (t == Type.black || t == Type.black_king))
			return true;
		return false;
	}

	public boolean own_piece(int row, int col, Player.Side side) { // return true if the piece is own piece
		Type t = get_piece(row, col);
		if (side == Player.Side.Black && t != Type.black && t != Type.black_king)
			return false;
		else if (side == Player.Side.White && t != Type.white && t != Type.white_king)
			return false;
		return true;
	}

	public Decision movement(Move move, Player.Side side) { // return Decision for a specific movement
		if (move == null) { // if there is no legal move
			return Decision.game_over; // the game ends
		}

		Point p1 = move.get_p1();
		Point p2 = move.get_p2();
		Type t1 = get_piece(p1);
		int row1 = p1.x;
		int row2 = p2.x;
		int col1 = p1.y;
		int col2 = p2.y;
		boolean capture_move = false; // return true if player make a capture move
		List<Move> valid_moves = valid_moves(row1, col1, side); // get a list of valid moves at initial position

		if (!own_piece(row1, col1, side)) { // if player is not moving their own piece
			return Decision.invalid_piece; // return invalid_piece Decision
		}

		// return Decision and update piece type when player make a normal/capture move
		if (valid_moves.contains(move)) { // if move is valid
			if (row1 + 1 == row2 || row1 - 1 == row2) { // if player make a basic move
				board[row1][col1] = Type.empty; // update piece type at p1
				board[row2][col2] = t1; // update piece type at p2
			} else { // if player make a capture move
				capture_move = true;
				board[row1][col1] = Type.empty; // update piece type at p1
				board[row2][col2] = t1; // update piece type at p2
				Point mid = mid_square(move);
				Type mid_type = get_piece(mid);
				if (mid_type == Type.black) { // update number of pieces
					b_normal--;
				} else if (mid_type == Type.black_king) {
					b_king--;
				} else if (mid_type == Type.white) {
					w_normal--;
				} else if (mid_type == Type.white_king) {
					w_king--;
				}
				board[mid.x][mid.y] = Type.empty; // update piece type at mid point
			}
			if (side_length == 4) { // for 4x4 board
				if (row2 == 3 & side == Player.Side.Black) { // if black piece reaches the farthest row forward
					board[row2][col2] = Type.black_king; // update piece type to black king
					b_normal--; // update number of pieces
					b_king++;
				} else if (row2 == 0 & side == Player.Side.White) { // if white piece reaches the farthest row forward
					board[row2][col2] = Type.white_king; // update piece type to white king
					w_normal--; // update number of pieces
					w_king++;
				}
			} else if (side_length == 8) { // for 8x8 board
				if (row2 == 7 & side == Player.Side.Black) { // if black piece reaches the farthest row forward
					board[row2][col2] = Type.black_king; // update piece type to black king
					b_normal--; // update number of pieces
					b_king++;
				} else if (row2 == 0 & side == Player.Side.White) { // if white piece reaches the farthest row forward
					board[row2][col2] = Type.white_king; // update piece type to white king
					w_normal--; // update number of pieces
					w_king++;
				}
			}
			if (capture_move) { // player must capture the maximum possible number of their opponent's piece in
								// any run
				List<Move> multi_capture = capture_moves(row2, col2, side); // get a list of valid capture moves at p2
				if (multi_capture.isEmpty()) { // if no additional capture available
					return Decision.success; // return success Decision
				}
				return Decision.multi_capture; // else return multiple capture Decision
			}
			return Decision.success; // return success Decision
		} else { // There is no valid move to this destination
			return Decision.invalid_destination; // return invalid_destination Decision
		}
	}

	public String toString() { // print out the board
		StringBuilder builder = new StringBuilder();
		builder.append("  ");
		for (int i = 0; i < board.length; i++) {
			builder.append(i + " ");
		}
		builder.append("\n");
		for (int i = 0; i < board.length; i++) {
			for (int j = -1; j < board[i].length; j++) {
				String a = "";
				if (j == -1)
					a = i + "";
				else if (board[i][j] == Type.white)
					a = "w";
				else if (board[i][j] == Type.black)
					a = "b";
				else if (board[i][j] == Type.white_king)
					a = "W";
				else if (board[i][j] == Type.black_king)
					a = "B";
				else
					a = "_";

				builder.append(a);
				builder.append(" ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	public Board clone() { // copy current board
		Type[][] board = new Type[side_length][side_length];
		for (int i = 0; i < side_length; i++) {
			for (int j = 0; j < side_length; j++) {
				board[i][j] = board[i][j];
			}
		}
		Board b = new Board();
		return b;
	}

	public Type get_piece(int row, int col) { // get piece at specific row and column
		return board[row][col];
	}

	public Type get_piece(Point p) { // get piece at specific point
		return board[p.x][p.y];
	}

	public Type[][] get_board() { // get board
		return board;
	}

	public int get_w() { // get number of white pieces
		return w_normal + w_king;
	}

	public int get_b() { // get number of black pieces
		return b_normal + b_king;
	}

	public int get_w_normal() { // get number of white normal pieces
		return w_normal;
	}

	public int get_b_normal() { // get number of black normal pieces
		return b_normal;
	}

	public int get_w_king() { // get number of white king pieces
		return w_king;
	}

	public int get_b_king() { // get number of black king pieces
		return b_king;
	}
}
