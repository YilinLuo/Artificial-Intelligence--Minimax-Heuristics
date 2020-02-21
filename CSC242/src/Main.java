import java.util.Scanner;

public class Main {
	public static int side_length, game_type, depth_limit, draw_turn, opponent_type, user_play; // variable for game
	public static double game = 1;
	public static boolean turn = true;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Checkers by Ruqin Chang, Yilin Luo\n" + "Choose your game:\n" + "1. Small 4x4 Checkers\n"
				+ "2. Standard 8x8 Checkers\n" + "Your choice?");

		game_type = scanner.nextInt();
		if (game_type == 1) { // initialize variables for 4x4 checker
			side_length = 4;
			depth_limit = 10; // no winner after 10 moves for 4x4 checker
			draw_turn = 10; // no winner after 10 moves for 4x4 checker
		} else if (game_type == 2) { // initialize variables for 8x8 checker
			side_length = 8;
			depth_limit = 50; // no winner after 50 moves for 8x8 checker
			draw_turn = 50; // no winner after 50 moves for 8x8 checker
		}

		System.out.println("Choose your opponent:\n" + "1. An agent that plays randomly\n"
				+ "2. An agent that uses MINIMAX\n" + "3. An agent that uses MINIMAX with alpha-beta pruning\n"
				+ "4. An agent that uses H-MINIMAX with a fixed depth cutoff\n" + "Your choice?");

		opponent_type = scanner.nextInt();
		if (opponent_type == 4) {
			System.out.println("Depth limit?");
			depth_limit = scanner.nextInt();
		}

		System.out.println("Do you want to play BLACK (1) or WHITE (2)?");
		int user_play = scanner.nextInt();
		Player player = null;
		Minimax ai = null;
		if (user_play == 1) {
			player = new Player("Player", Player.Side.Black);
			ai = new Minimax(Player.Side.White, depth_limit);
		} else if (user_play == 2) {
			ai = new Minimax(Player.Side.Black, depth_limit);
			player = new Player("Player", Player.Side.White);
		}
		scanner.nextLine();

		for (int t = 0; t < game; t++) {
			Board board = new Board();
			Player current = null;
			int turn_count = 0;
			if (user_play == 1) {
				current = player;
				if (!turn) {
					current = ai;
				}
			} else if (user_play == 2) {
				current = ai;
				if (!turn) {
					current = player;
				}
			}
			System.out.println(board.toString());

			while (turn_count <= depth_limit) { // While no more than 10/50 turns for 4x4/8x8 checker
				System.out.print(current.toString() + "'s turn[" + (turn_count + 1) + "]: ");
				Board.Decision decision = null;
				if (current instanceof AI) { // AI's turn
					System.out.print("\n    I am thinking...");
					decision = ((AI) current).movement(board);
				} else { // Player's turn
					String text = scanner.nextLine();
					String[] split = text.split(" ");
					Move move;
					if (split.length == 1) {
						move = new Move(Integer.parseInt(text.charAt(0) + ""), Integer.parseInt(text.charAt(1) + ""),
								Integer.parseInt(text.charAt(2) + ""), Integer.parseInt(text.charAt(3) + ""));
					} else {
						int[] s = new int[split.length];
						for (int i = 0; i < split.length; i++) {
							s[i] = Integer.parseInt(split[i]);
						}
						move = new Move(s[0], s[1], s[2], s[3]);
					}
					decision = current.movement(move, board);
				}

				if (decision == Board.Decision.invalid_destination || decision == Board.Decision.invalid_piece) {
					System.out.println("Move failed");
					turn_count--;
				} else if (decision == Board.Decision.success) {
					System.out.println(board);
					if (board.get_b() == 0) {
						System.out.println("White wins with " + board.get_w() + " pieces left");
						turn_count++;
						break;
					}
					if (board.get_w() == 0) {
						System.out.println("Winner: Black with " + board.get_b() + " pieces left");
						turn_count++;
						break;
					}
					if (user_play == 1) {
						if (turn) {
							current = ai;
						} else {
							current = player;
						}
					} else if (user_play == 2) {
						if (turn) {
							current = player;
						} else {
							current = ai;
						}
					}
					turn = !turn;
				} else if (decision == Board.Decision.multi_capture) {
					System.out.println("Mutiple Move");
					turn_count--;
				} else if (decision == Board.Decision.game_over) {
					if (current.get_side() == Player.Side.Black) {
						System.out.println("Winner: White with " + board.get_b() + " pieces left");
						turn_count++;
					} else {
						System.out.println("Winner: Black with " + board.get_b() + " pieces left");
						turn_count++;
					}
					break;
				}
				turn_count++;
			}
			if (turn_count > draw_turn) {
				System.out.println("No winner. The game is a draw.");
			}
			System.out.println("Game finished after: " + turn_count + " turns");
		}
	}

}
