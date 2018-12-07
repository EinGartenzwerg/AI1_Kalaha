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
			// System.out.println("max_player " + next.player_max + " depth: " + depth + " a: " + alpha + " b: " +
			// beta);
			// next.print();
			// System.out.println("------------------------------------");
			int[] re = new int[2];
			re[0] = next.getCurrentPlayer_is_max() ? MIN_V : MAX_V;
			re[1] = -42;
			if (depth == 0) { // Base case
				re[0] = next.getValue();
				// System.out.println("===========> " + re[0]);
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
						// System.out.println("GET out (beta) re: " +re[0] + " " +"max_player "+ next.player_max + "
						// depth: " + depth + " a: "+ alpha + " b: " +beta );
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
						// System.out.println("GET out (alpha) re: " +re[0] + " " +"max_player "+ next.player_max + "
						// depth: " + depth + " a: "+ alpha + " b: " +beta );
						return re;
					}
					if (re[0] < beta) {
						beta = re[0];
					}
				}
				next_child = children.poll();
			}
			// System.out.println();
			// System.out.println( "<<<<<<<<<<<<");
			// System.out.println();

			return re;
		}

	}

//	private class State {
//		// TODO: refactor max, min to player and enemy
//		// Ein Spieler
//		private int[]		max;
//		private int[]		min;
//		private boolean		isFinal		= false;	// ob state schon zu Ende ist
//		private int			ergebnis	= 2;		// -1 Min gewinnt, 0 Draw, 1 Max gewinnt, 2 sie spielen noch
//		private final int	MAX_V;
//		private final int	DRAWSTONES;
//		// player_max == true => its max's turn
//		// player_max == false => its mins turn
//		private boolean		player_max;
//		private int			len;
//		int					prev_move	= -1;		// 1,2,... len
//													// how to get there
//													// -1 if starting state
////
//		private State (MyBoard b) {
//			MAX_V = b.MAX_V; // TODO: hier waere es vll sinnvoller, nicht fuer jeden State extra die ganzen
//								// Variablen
//								// anzulegen sondern einfach das Board auch in State als Attribut speichern
//
//			DRAWSTONES = b.DRAWSTONES;
//			len = b.max.length;
//
//			max = Arrays.copyOf(b.max, len);
//			min = Arrays.copyOf(b.min, len);
//
//			if (max[len - 1] >= DRAWSTONES) { // berechnet isFinal und Ergebnis "moeglichst effizient"
//				if (max[len - 1] == DRAWSTONES) {
//					if (min[len - 1] == DRAWSTONES) {
//						isFinal = true;
//						ergebnis = 0;
//					}
//				} else {
//					isFinal = true;
//					ergebnis = 1;
//				}
//			} else if (min[len - 1] > DRAWSTONES) {
//				isFinal = true;
//				ergebnis = -1;
//			}
//		}
//
//		private State (State s) {
//			DRAWSTONES = s.DRAWSTONES;
//			MAX_V = s.MAX_V;
//			isFinal = s.isFinal;
//			ergebnis = s.ergebnis;
//			len = s.max.length;
//			max = Arrays.copyOf(s.max, len);
//			min = Arrays.copyOf(s.min, len);
//		}
//
//		private State (State s, int prev) {
//			this(s);
//			prev_move = prev;
//		}
//
//		// zur Kalibrierung der Heuristik
//		int	maxHeu	= 1000;
//		int	minHeu	= -maxHeu;
//
//		/*
//		 * TODO: eval => heuristik look up Board.max_V .min_V draw_V
//		 */
//		private int eval() { // wird noch optimiert, nur zum Testen bzgl Gewichtungen
//			if (isFinal) {
//				return ergebnis * MAX_V;
//			}
//			int[] weights = { 1, 1, 1, 1, 1, 1, 1 };
//			return weights[0] * h0() + weights[1] * h1() + weights[2] * h2() + weights[3] * h3() + weights[4] * h4() + weights[5] * h5() + weights[6] * h6();
//		}
//
//		private int h0() {
//			return 0;
//		}
//
//		private int h1() {
//			return 0;
//		}
//
//		private int h2() {
//			return 0;
//		}
//
//		private int h3() {
//			return 0;
//		}
//
//		private int h4() {
//			return 0;
//		}
//
//		private int h5() {
//			return 0;
//		}
//
//		private int h6() { // differenz in Punkten
//			return (max[len - 1] - min[len - 1]);
//		}
//
//		// TODO: make it a PrioQueue => we need a fast and easy eval for this (faster
//		// then the one for our leaves)
//		private Queue<State> getExtensions() {
//			int[] player;
//			if (player_max) {
//				player = this.max;
//			} else {
//				player = this.min;
//			}
//		
//			Queue<State> children = new LinkedList<State>();
//			for (int i = 0; i < len - 1; i++) {
//				if (player[i] != 0) {
//					children.add(move(i));
//				}
//			}
//			return children;
//
//		}
//
//		// Makes a move and returns the resulting State
//		private State move(int house) {
//			State newState = new State(this, house);
//			int[] player;
//			int[] enemy;
//			if (this.player_max) {
//				player = newState.max;
//				enemy = newState.min;
//			} else {
//				player = newState.min;
//				enemy = newState.max;
//			}
//
//			int curStones = player[house];
//			player[house] = 0;
//			int index = house;
//			while (curStones > 0) {
//				index = (index + 1) % (2 * len - 1);
//				// else place a stone into next field
//				curStones--; //
//				// now actually place it
//				if (index < len) {
//					player[index]++;
//				} else {
//					enemy[index % len]++;
//				}
//			}
//			if (index < len) {
//				/*
//				 * I ended up on my side => Therefore there are 2 special cases
//				 * 
//				 * a) I ended up in an prev. empty field (! not my freaking store)
//				 * b) Am I next due to ending up in
//				 */
//				if (index == len - 1) { // b) => I am at my store
//
//					newState.player_max = this.player_max;
//					return newState;
//				} else if (player[index] == 1 && enemy[enemy.length - 2 - index] > 0) {// a)
//					// code reused
//					int enemyStones = enemy[enemy.length - 2 - index];
//					enemy[enemy.length - 2 - index] = 0;
//					player[index] = 0;
//					player[player.length - 1] += enemyStones + 1;
//				}
//			}
//			newState.player_max = !this.player_max;
//			return newState;
//		}
//
//		private void print() {
//			System.out.println("State " + player_max);
//			System.out.println("Max:" + Arrays.toString(max));
//			System.out.println("Min:" + Arrays.toString(min));
//		}
//	}
}
