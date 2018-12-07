package info.kwarc.teaching.AI.Kalah.Agents;

import info.kwarc.teaching.AI.Kalah.Board;
import info.kwarc.teaching.AI.Kalah.util.Converter;

import java.util.ArrayList;
import java.util.Queue;

public class Chiron extends info.kwarc.teaching.AI.Kalah.Agents.Agent {
	private Board	b;
	private MyBoard	mb;
	private boolean	playerOne;

	@Override
	public String name() {
		return "Chiron";
	}

	@Override
	public void init(Board board, boolean playerOne) {
		b = board;
		mb = new MyBoard(b);
		this.playerOne = playerOne;
		// TODO: start thinking
	}

	@Override
	public int move() {
		mb.update();
		return mb.search();
	}

	@Override
	public Iterable<String> students() {
		// TODO Auto-generated method stub
		return null;
	}

	private class MyBoard {
		// max = upper half ? // TODO
		// min = under half
		// store => .length - 1
		private int[]				max;										// int is faster then short in most
																				// cases
																				// .... fuck cpu nativ stuff
		private int[]				min;										// => short is slower on x86 -- I expect
																				// to
																				// play on an modern cpu ^^"
		// ref to board => update
		private Board				board;

		// Value of Max, Min wins or a draw
		private final static int	MAX_V	= 10000, MIN_V = -10000, DRAW_V = 0;
		private final int			DRAWSTONES;									// die Haelfte aller Steine im Spiel.
																				// Haben beide Spieler
																				// diese Anzahl ist es Unentschieden

		private final static int	UNUSED	= 42_666;

		// Konstruktor
		private MyBoard (Board board) {
			this.board = board;
			update();
			DRAWSTONES = this.board.houses() * this.board.initSeeds();
		}

		private MyBoard (int[] max, int[] min) { // just for testing
			this.max = max;
			this.min = min;
			DRAWSTONES = 36;
		}

		// update: uses this.board => init werte vom board
		private void update() {
			ArrayList<Integer> in_max = Converter.getMyHouses(board, playerOne);
			ArrayList<Integer> in_min = Converter.getMyHouses(board, playerOne);
			in_max.add(Converter.getMyStoreSeeds(board, playerOne));
			in_min.add(Converter.getMyStoreSeeds(board, playerOne));
			int n = in_max.size();
			max = new int[n];
			min = new int[n];
			for (int i = 0; i < n; i++) {
				max[i] = in_max.get(i).intValue();
				min[i] = in_min.get(i).intValue();
			}
			// System.out.println(" \n My Turn - starting on ");
			// getState().print();
			// System.out.println();
		}

		private State getState() {
			State max_plays_this = new State(max,min,DRAWSTONES);
			return max_plays_this;
		}

		// start iterativ depening
		private int search() {
			int re = 0;
			for (int i = 0; i < 1; i++) { // iterativ deepening //TODO: change 15 to a timeout based search
				re = deaper(getState(), 10, MIN_V, MAX_V)[1]; // TODO: tiefe sollte i werden
			}
			return re + 1; // +1 da scala mit 1 startet anstatt mit 0 -.-
		}

		// recusiv deepening
		// [0] ==> value of the best move
		// [1] ==> int of the move
		// TODO: parallelisieren
		// TODO: endrekusiviere
		// TODO: player_max needed ? => its in my State ....
		private int[] deaper(State next, int depth, int alpha, int beta) {
			int[] re = new int[2];
			re[0] = next.getCurrentPlayer_is_max() ? MIN_V : MAX_V;
			re[1] = -42;
			if (depth == 0) { // Base case
				re[0] = next.getValue();
				return re;
			}

			Queue<State> children = next.getExtensions();
			State next_child = children.poll();
			while (next_child != null) {
				int[] result = deaper(next_child, depth - 1, alpha, beta);
				if (next.getCurrentPlayer_is_max()) { // I am Max
					if (re[0] <= result[0]) {
						re[0] = result[0];
						re[1] = next_child.prev_move;
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
						re[1] = next_child.prev_move;
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

	}

}
