package info.kwarc.teaching.AI.Kalah.Agents;

import info.kwarc.teaching.AI.Kalah.Board;
import info.kwarc.teaching.AI.Kalah.util.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import java.util.LinkedList;

public class Chiron extends info.kwarc.teaching.AI.Kalah.Agents.Agent {
	private Board b;
	private MyBoard mb;

	public Chiron () {
	}

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
		// mb.update();
		// return mb.search(); //TODO: revert
		// TODO: do not forget to transfer bla into usefull    possible moves are
		// 1,2,3,..... (it does not start with index 0  -.- ) 
		return 1;
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
		private int[] max; // int is faster then short in most cases
							// .... fuck cpu nativ stuff
		private int[] min; // => short is slower on x86 -- I expect to
							// play on an modern cpu ^^"
		// ref to board => update
		private Board board;

		// Value of Max, Min wins or a draw
		private final static int MAX_V = 10000, MIN_V = -10000, DRAW_V = 0;
		private final static int UNUSED = 42_666;

		// Konstruktor
		private MyBoard(Board board) {
			this.board = board;
			update();
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
		}

		private State getState() {
			State max_plays_this = new State(this);
			max_plays_this.player_max = true;
			return max_plays_this;
		}

		// start iterativ depenig
		private int search() {
			int re = 0;
			for (int i = 0; i < 15; i++) { // iterativ deepening //TODO: change 15 to a timeout based search
				re = deaper(getState(), i, UNUSED, UNUSED)[0];
			}
			return re;
		}

		// recusiv deepening
		// int[0] value of the best move
		// TODO: parallelisieren
		// TODO: upgrade to alpha beta saerch => use int[] with idex alpha == 1 and beta
		// == 2
		// TODO: endrekusiviere
		// TODO: Queue in header needed ?
		// TODO: player_max needed ? => its in my State ....
		private int[] deaper(State next, int depth, int alpha, int beta) {
			if (next == null) { // should never happen //TODO: check wether it will never happen
				return null;
			}
			int[] re = new int[3]; // TODO: init => alpha and beta need to have set to a not possible Value (
									// \notin
			re[1] = re[2] = UNUSED; // [min_V,maxV])
			re[0] = next.player_max ? MIN_V : MAX_V;
			if (depth == 0) {
				re[0] = next.eval();
				return re;
			}
			Queue<State> children = next.getExtensions();
			State next_child = children.poll();

			// TODO: remember the best way and how to get there
			while (next_child != null) {
				int[] result = deaper(next_child, depth - 1, alpha, beta);
				next_child = children.poll();
				//

				// if (result != null && chooseNew(re[0], result[0], next.player_max)) {
				// re = result;
				// }
				if (result != null) { // TODO remove ASAP => deaper should never return null;
					if (next.player_max) { // I am Max
						if (re[0] < result[0]) {
							re = result;
						}
						if (re[0] > beta) { // TODO: beta
							return re;
						} else {
							beta = re[0];
						}
					} else { // I am Min
						if (result[0] < re[0]) {
							re = result;
						}
						if (re[0] < alpha) { // TODO: alpha
							return re;
						}
					}
				}

			}
			// laut compiler deadCode // TODO double check it
			// if (re == null) return null; // sollte nicht eintretten :S

			return re;
		}

		private boolean chooseNew(int alt, int neu, boolean player_max) {
			if (player_max) {
				return alt < neu;
			} else {
				return neu < alt;
			}
		}

	}

	private class State {
		private int[] max;
		private int[] min;
		
		// player_max == true => its max's turn
		// player_max == false => its mins turn
		private boolean player_max;
		private int len;

		private State(MyBoard b) {
			len = b.max.length;
			max = Arrays.copyOf(b.max, len);
			min = Arrays.copyOf(b.min, len);
		}

		private State(State s) {
			len = s.max.length;
			max = Arrays.copyOf(s.max, len);
			min = Arrays.copyOf(s.min, len);
		}

		/*
		 * TODO: eval => heuristik look up Board.max_V .min_V draw_V
		 */
		private int eval() {
			int[] weights = {2,2,4,10,4,6,0};
			return weights[0]*h0() + weights[1]*h1() + weights[2]*h2() + weights[3]*h3() + weights[4]*h4() + weights[5]*h5() + weights[6]*h6();
		}

		private int h0() { 
			return 1;
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

		private int h6() {
			return 0;
		}

		// TODO: build it 
		// TODO: make it a PrioQueue => we need a fast and easy eval for this (faster
		// then the one for our leaves)
		private Queue<State> getExtensions() {
			Queue<State> children = new LinkedList<State>();
			return children;
		}
		/*
		 * TODO: remember Alles sich zu merken, was einmal berechnet wurde, ist ein zu
		 * großer Overhead. Angenommen wir haben den fertig berechneten Baum und haben
		 * die jeweiligen Alpha (und Beta) werte für jeden Knoten => Merke dir
		 * zunächst 2 Ebenen Tief ausgehend vom Startknoten => Suche dir deinen besten
		 * aufgrund Berechnungen aus => merke die die Erweiterungen von jenem => warte
		 * auf den Zug des Gegners. => Matche was Gegner getan hat => Suche kann nun
		 * begonnen werden mit Alpha vorgerechnet => Mehr Teilbäume abschneiden da sie
		 * nicht schlechter als mein altes ergebnis sind (in der Regel variert die Güte
		 * eines Knoten nicht allzustark von dem seinem Teilbaum => altes Alpha ist
		 * relativ gut als untere Schranke für die neue Suche). => Dies kann
		 * wahrscheinlich in wenigen Situationen problematisch werden, falls heuristik
		 * schlecht ist ^^"
		 * 
		 */
	}
}
