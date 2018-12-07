package info.kwarc.teaching.AI.Kalah.Agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import info.kwarc.teaching.AI.Kalah.Board;
import info.kwarc.teaching.AI.Kalah.util.Converter;

public class MyBoard {
	public final static int		MAX_V		= 10000, MIN_V = -10000, DRAW_V = 0;// Value of Max, Min wins or a draw
	// die Haelfte aller Steine im Spiel.
	// Haben beide Spieler
	// diese Anzahl ist es Unentschieden
	public static int			DRAWSTONES	= -1;
	// store => .length - 1
	private int[]				player;
	private int[]				enemy;
	private Board				board;											// ref to board => update
	private boolean				playerOne;

	private final static int	UNUSED		= 42_666;

	// Konstruktor
	public MyBoard (Board board, boolean playerOne) {
		this.board = board;
		this.playerOne = playerOne;
		update();
		DRAWSTONES = this.board.houses() * this.board.initSeeds();
	}

	public MyBoard (int[] max, int[] min) { // just for testing
		this.player = max;
		this.enemy = min;
		DRAWSTONES = 36;
	}

	// update: uses this.board => init werte vom board
	public void update() {
		ArrayList<Integer> in_player = Converter.getMyHouses(board, playerOne);
		ArrayList<Integer> in_enemy = Converter.getMyHouses(board, playerOne);
		in_player.add(Converter.getMyStoreSeeds(board, playerOne));
		in_enemy.add(Converter.getMyStoreSeeds(board, playerOne));
		int n = in_player.size();
		player = new int[n];
		enemy = new int[n];
		for (int i = 0; i < n; i++) { // TODO: ArrayList to Array
			player[i] = in_player.get(i).intValue();
			enemy[i] = in_enemy.get(i).intValue();
		}
	}

	private State getState() {
		return new State(player, enemy, DRAWSTONES);
	}

	// start einfache suche
	public int search(int d) {
		int[] re = deaper(getState(), d, MIN_V, MAX_V);
		re[1]++;// +1 da scala mit 1 startet anstatt mit 0 -.-
		// System.out.println("[AI_THINKS]: Depth: " + d + " Move " + re[1] + " Value " + re[0]);
		System.out.println("\n 												[AI_THINKS]:		Depth: " + d + "	Move " + re[1] + "		Value " + re[0]);
		return re[1];
	}

	// recusiv deepening
	// [0] ==> value of the best move
	// [1] ==> int of the move
	// TODO: parallelisieren
	// TODO: endrekusiviere
	private int[] deaper(State next, int depth, int alpha, int beta) {
		// System.out.println("[AI_DEEP] (d,a,b): (" + depth + " " + alpha + " " + beta + ")" + " Turn of max: " +
		// next.getCurrentPlayer_is_max() + " histo " + next.prev_move
		// + " Max: " + Arrays.toString(next.getMax()) + " Min: " + Arrays.toString(next.getMin()));
		boolean current_player_max = next.getCurrentPlayer_is_max();
		int[] re = new int[2];
		re[0] = current_player_max ? MIN_V : MAX_V;
		re[1] = -42;
		if (depth == 0) { // Base case
			re[0] = next.getValue();
			return re;
		}

		Queue<State> children = next.getExtensions();
		State next_child = children.poll();
		while (next_child != null) {
			int[] result = deaper(next_child, depth - 1, alpha, beta);
			if (current_player_max) { // I am Max
				if (re[0] <= result[0]) {
					re[0] = result[0];
					re[1] = next_child.getPapa();
				}
				if (re[0] > beta) {
					return re;
				}
				if (re[0] > alpha) {
					alpha = re[0];
				}
			} else { // I am Min
				if (re[0] >= result[0]) {
					re[0] = result[0];
					re[1] = next_child.getPapa();
				}
				if (re[0] < alpha) {
					return re;
				}
				if (re[0] < beta) {
					beta = re[0];
				}
			}
			next_child = children.poll();
		}
		return re;
	}

	// Wikipedia algo ---- aber er funktioniert nicht xD
	// [0] == value
	// [1] == how to get to me
	// private int[] deaper(State next, int depth, int alpha, int beta) {
	// System.out.println("[AI_DEEP] (d,a,b): (" + depth + " " + alpha + " " + beta + ")" + " Turn of max: " +
	// next.getCurrentPlayer_is_max() + " histo " + next .getPapa() + " Max: "
	// + Arrays.toString(next.getMax()) + " Min: " + Arrays.toString(next.getMin()));
	//
	// int[] re = new int[2];
	// if (depth == 0 || next.isFinal()) {
	// re[0] = next.getValue();
	// re[1] = next.getPapa();
	// return re;
	// }
	// boolean maximizing = next.getCurrentPlayer_is_max();
	// int value = maximizing ? MIN_V : MAX_V;
	// Queue<State> children = next.getExtensions();
	// int[] c_result;
	// f:
	// for (State child : children) {
	// c_result = deaper(child, depth - 1, alpha, beta);
	// if (maximizing) {
	// if (c_result[0] > re[0]) {
	// re[0] = c_result[0];
	// re[1] = c_result[1];
	// }
	// alpha = re[0] > alpha ? re[0] : alpha;
	// if (alpha >= beta) break f;
	// } else {
	// if (c_result[0] < re[0]) {
	// re[0] = c_result[0];
	// re[1] = c_result[1];
	// }
	// beta = re[0] < beta ? re[0] : beta;
	// if (alpha >= beta) break f;
	// }
	// }
	// return re;
	// }
}
