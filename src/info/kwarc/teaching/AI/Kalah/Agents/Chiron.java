package info.kwarc.teaching.AI.Kalah.Agents;

import info.kwarc.teaching.AI.Kalah.Board;
import info.kwarc.teaching.AI.Kalah.util.Converter;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import java.util.LinkedList;

public class Chiron extends info.kwarc.teaching.AI.Kalah.Agents.Agent {
	private Board	b;
	private MyBoard	mb;

	@Override
	public String name() {
		return "Chiron";
	}

	@Override
	public void init(Board board, boolean playerOne) {
		b = board;
		mb = new MyBoard(b);
		// TODO: was macht playerOne ?
		// TODO: start thinking
	}

	@Override
	public int move() {
		// test_search(); // just for test
		// return -1;
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
			ArrayList<Integer> in_max = Converter.getMyHouses(board, true);
			ArrayList<Integer> in_min = Converter.getMyHouses(board, false);
			in_max.add(Converter.getMyStoreSeeds(board, true));
			in_min.add(Converter.getMyStoreSeeds(board, false));
			int n = in_max.size();
			max = new int[n];
			min = new int[n];
			for (int i = 0; i < n; i++) {
				max[i] = in_max.get(i).intValue();
				min[i] = in_min.get(i).intValue();
			}
			// System.out.println("My Turn - starting on ");
			// getState().print();
			// System.out.println();
		}

		private State getState() {
			State max_plays_this = new State(this);
			max_plays_this.player_max = false;
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
			re[0] = next.player_max ? MIN_V : MAX_V;
			re[1] = -3;

			if (depth == 0) { // Base case
				re[0] = next.eval();
				// System.out.println("===========> " + re[0]);
				re[1] = -33;
				return re;
			}

			Queue<State> children = next.getExtensions();
			State next_child = children.poll();
			while (next_child != null) {
				int[] result = deaper(next_child, depth - 1, alpha, beta);
				if (next.player_max) { // I am Max
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

	private class State {
		// TODO: refactor max, min to player and enemy
		// Ein Spieler
		private int[]		max;
		private int[]		min;
		private boolean		isFinal		= false;	// ob state schon zu Ende ist
		private int			ergebnis	= 2;		// -1 Min gewinnt, 0 Draw, 1 Max gewinnt, 2 sie spielen noch
		private final int	MAX_V;
		private final int	DRAWSTONES;
		// player_max == true => its max's turn
		// player_max == false => its mins turn
		private boolean		player_max;
		private int			len;
		int					prev_move	= -1;		// 1,2,... len
													// how to get there
													// -1 if starting state

		private State (MyBoard b) {
			MAX_V = b.MAX_V; // TODO: hier waere es vll sinnvoller, nicht fuer jeden State extra die ganzen
								// Variablen
								// anzulegen sondern einfach das Board auch in State als Attribut speichern

			DRAWSTONES = b.DRAWSTONES;
			len = b.max.length;

			max = Arrays.copyOf(b.max, len);
			min = Arrays.copyOf(b.min, len);

			if (max[len - 1] >= DRAWSTONES) { // berechnet isFinal und Ergebnis "moeglichst effizient"
				if (max[len - 1] == DRAWSTONES) {
					if (min[len - 1] == DRAWSTONES) {
						isFinal = true;
						ergebnis = 0;
					}
				} else {
					isFinal = true;
					ergebnis = 1;
				}
			} else if (min[len - 1] > DRAWSTONES) {
				isFinal = true;
				ergebnis = -1;
			}
		}

		private State (State s) {
			DRAWSTONES = s.DRAWSTONES;
			MAX_V = s.MAX_V;
			isFinal = s.isFinal;
			ergebnis = s.ergebnis;
			len = s.max.length;
			max = Arrays.copyOf(s.max, len);
			min = Arrays.copyOf(s.min, len);
		}

		private State (State s, int prev) {
			this(s);
			prev_move = prev;
		}

		// zur Kalibrierung der Heuristik
		int	maxHeu	= 1000;
		int	minHeu	= -maxHeu;

		/*
		 * TODO: eval => heuristik look up Board.max_V .min_V draw_V
		 */
		private int eval() { // wird noch optimiert, nur zum Testen bzgl Gewichtungen
			if (isFinal) {
				return ergebnis * MAX_V;
			}
			int[] weights = { 1, 1, 1, 1, 1, 1, 1 };
			return weights[0] * h0() + weights[1] * h1() + weights[2] * h2() + weights[3] * h3() + weights[4] * h4() + weights[5] * h5() + weights[6] * h6();
		}

		private int h0() {
			return 0;
		}

		private int h1() {
			return 0;
		}

		private int h2() {
			return 0;
		}

		private int h3() {
			return 0;
		}

		private int h4() {
			return 0;
		}

		private int h5() {
			return 0;
		}

		private int h6() { // differenz in Punkten
			return (max[len - 1] - min[len - 1]);
		}

		// TODO: make it a PrioQueue => we need a fast and easy eval for this (faster
		// then the one for our leaves)
		private Queue<State> getExtensions() {
			int[] player;
			if (player_max) {
				player = this.max;
			} else {
				player = this.min;
			}
			// System.out.println("====================GET ===================");
			// System.out.println();
			// print();
			// System.out.println();
			Queue<State> children = new LinkedList<State>();
			for (int i = 0; i < len - 1; i++) {
				if (player[i] != 0) {
					children.addAll(move(i));
				}
			}
			// System.out.println("====================End ===================");
			return children;

		}

		// Makes a move and returns the resulting State
		private Queue<State> move(int house) {
			State newState = new State(this, house);
			// newState.player_max = !this.player_max; // TODO: Will ich das so fr"uh haben => ich k"onnte ja wieder am
			// zug sein (?)
			int[] player;
			int[] enemy;
			if (this.player_max) {
				player = newState.max;
				enemy = newState.min;
			} else {
				player = newState.min;
				enemy = newState.max;
			}

			// =====================================================================================
			// Karols Vorschlag
			// take all stones and place them
			int curStones = player[house];
			player[house] = 0;
			int index = house;
			while (curStones > 0) {
				index = (index + 1) % (2 * len - 1);
				// else place a stone into next field
				curStones--; //
				// now actually place it
				if (index < len) {
					player[index]++;
				} else {
					enemy[index % len]++;
				}
			}
			if (index < len) {
				/*
				 * I ended up on my side => Therefore there are 2 special cases
				 * 
				 * a) I ended up in an prev. empty field (! not my freaking store)
				 * b) Am I next due to ending up in
				 */
				if (index == len - 1) { // b) => I am at my store
					// System.out.println("Moved i: " + index + " And i get to play again");
					// newState.print();
					// System.out.println("---------------------->");
					return newState.getExtensions();
				} else if (player[index] == 1 && enemy[enemy.length - 2 - index] > 0) {// a)
					// System.out.println("Wierd suff ?");
					// code reused
					int enemyStones = enemy[enemy.length - 2 - index];
					enemy[enemy.length - 2 - index] = 0;
					player[index] = 0;
					player[player.length - 1] += enemyStones + 1;
				}
			}
			Queue<State> move = new LinkedList<State>();
			newState.player_max = !this.player_max;
			move.add(newState);
			return move;

			/*
			 * State false
			 * Max:[0, 5, 0, 2]
			 * Min:[0, 5, 5, 1]
			 * 
			 * State true
			 * Max:[1, 6, 1, 2]
			 * Min:[0, 0, 6, 2]
			 * State true
			 * Max:[1, 6, 1, 3]
			 * Min:[0, 5, 0, 2]
			 */
			// Karols Vorschlag ENDE
			// =====================================================================================

			// Max:
			/*
			 * for (int i = house + 1; i < player.length; i++) {
			 * if (curStones > 0) {
			 * player[i]++;
			 * curStones--;
			 * // Stein kann in einem leeren Feld landen und ich bekomme alle Gegnerischen
			 * // Steine
			 * // letzeter Stein + nicht im Kalah + davor leeres Feld
			 * if ((curStones == 0) && (i != player.length - 1) && (player[i] == 1)) {
			 * int enemyStones = enemy[enemy.length - 2 - i];
			 * enemy[enemy.length - 2 - i] = 0;
			 * player[i] = 0;
			 * player[player.length - 1] += enemyStones + 1;
			 * } else if ((curStones == 0) && (i == player.length - 1)) {
			 * move.addAll(newState.getExtensions());
			 * }
			 * }
			 * }
			 * while (curStones > 0) {
			 * for (int i = enemy.length - 2; i >= 0; i--) {
			 * if (curStones > 0) {
			 * enemy[i]++;
			 * curStones--;
			 * }
			 * }
			 * if (curStones > 0) {
			 * enemy[newState.min.length - 1]++;
			 * curStones--;
			 * }
			 * for (int i = 0; i < player.length; i++) {
			 * if (curStones > 0) {
			 * player[i]++;
			 * curStones--;
			 * if ((curStones == 0) && (i != player.length - 1) && (player[i] == 1)) {
			 * int enemyStones = enemy[enemy.length - 2 - i];
			 * enemy[enemy.length - 2 - i] = 0;
			 * player[i] = 0;
			 * player[player.length - 1] += enemyStones + 1;
			 * } else if ((curStones == 0) && (i == player.length - 1)) {
			 * move.addAll(newState.getExtensions());
			 * }
			 * }
			 * }
			 * 
			 * }
			 * move.add(newState);
			 * return move;
			 */
		}

		// // Is a move possible with this house?
		// private boolean possibleMove(int house, State state) {
		//
		// return false;
		// }

		/*
		 * TODO: remember Alles sich zu merken, was einmal berechnet wurde, ist ein zu
		 * großer Overhead. Angenommen wir haben den fertig berechneten Baum und haben
		 * die jeweiligen Alpha (und Beta) werte für jeden Knoten => Merke dir zunächst
		 * 2 Ebenen Tief ausgehend vom Startknoten => Suche dir deinen besten aufgrund
		 * Berechnungen aus => merke die die Erweiterungen von jenem => warte auf den
		 * Zug des Gegners. => Matche was Gegner getan hat => Suche kann nun begonnen
		 * werden mit Alpha vorgerechnet => Mehr Teilbäume abschneiden da sie nicht
		 * schlechter als mein altes ergebnis sind (in der Regel variert die Güte eines
		 * Knoten nicht allzustark von dem seinem Teilbaum => altes Alpha ist relativ
		 * gut als untere Schranke für die neue Suche). => Dies kann wahrscheinlich in
		 * wenigen Situationen problematisch werden, falls heuristik schlecht ist ^^"
		 *
		 */

		private void print() {
			System.out.println("State " + player_max);
			System.out.println("Max:" + Arrays.toString(max));
			System.out.println("Min:" + Arrays.toString(min));
		}
	}

	private void test_getExtensions_skip_enemyStore() {
		System.out.println("====================Test ===================");
		State a = new State(mb);
		int[] ma = { 0, 5, 0, 2 };
		int[] mi = { 0, 5, 5, 1 };
		a.max = ma;
		a.min = mi;
		a.player_max = false;
		a.print();
		System.out.println();
		for (State s : a.move(2)) {
			s.print();
		}
		System.out.println("====================Test ===================");
	}

	private void test_search2() {
		System.out.println("====================Test ===================");
		int[] ma = { 6, 6, 6, 6, 6, 6, 0 };
		int[] mi = { 6, 6, 6, 6, 6, 6, 0 };
		MyBoard mb = new MyBoard(ma, mi);
		System.out.println(mb.search());
		System.out.println("====================Test ===================");
	}

	private void test_search() {
		System.out.println("====================Test ===================");
		int[] ma = { 1, 0, 9, 9, 8, 8, 2 };
		int[] mi = { 1, 0, 8, 8, 8, 8, 2 };
		MyBoard mb = new MyBoard(ma, mi);
		System.out.println(mb.search());
		System.out.println("====================Test ===================");
	}
}
