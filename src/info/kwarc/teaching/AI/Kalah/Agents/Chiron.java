package info.kwarc.teaching.AI.Kalah.Agents;

import info.kwarc.teaching.AI.Kalah.Board;
import info.kwarc.teaching.AI.Kalah.util.Converter;
import java.util.ArrayList;
import java.util.Arrays;

public class Chiron extends info.kwarc.teaching.AI.Kalah.Agents.Agent {
	Board	b;
	MyBoard	mb;

	@Override
	public String name() {
		return "Smith";
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
		// TODO Auto-generated method stub
		return 0;
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
		private short[]	max;
		private short[]	min;
		// ref to board => update
		private Board	board;

		// Konstruktor
		private MyBoard (Board board) {
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
			max = new short[n];
			min = new short[n];
			for (int i = 0; i < n; i++) {
				max[i] = in_max.get(i).shortValue();
				min[i] = in_min.get(i).shortValue();
			}
		}

		private State getState() {
			return new State(this);
		}

		// T
	}

	private class State {
		private short[]	max;
		private short[]	min;
		// turn == true => its max's turn
		// turn == false => its mins turn
		private boolean	turn;
		private int		len;

		private State (MyBoard b) {
			len = b.max.length;
			max = Arrays.copyOf(b.max, len);
			min = Arrays.copyOf(b.min, len);
		}

		private State (State s) {
			len = s.max.length;
			max = Arrays.copyOf(s.max, len);
			min = Arrays.copyOf(s.min, len);
		}

		/*
		 * TODO:
		 * eval => heuristik
		 * eval => iterativeDeepening.
		 * 
		 */

		/*
		 * TODO: remember
		 * Alles sich zu merken, was einmal berechnet wurde, ist ein zu großer Overhead.
		 * Angenommen wir haben den fertig berechneten Baum und haben die jeweiligen Alpha (und Beta) werte für jeden
		 * Knoten
		 * => Merke dir zunächst 2 Ebenen Tief ausgehend vom Startknoten
		 * => Suche dir deinen besten aufgrund Berechnungen aus
		 * => merke die die Erweiterungen von jenem
		 * => warte auf den Zug des Gegners.
		 * => Matche was Gegner getan hat => Suche kann nun begonnen werden mit Alpha vorgerechnet
		 * => Mehr Teilbäume abschneiden da sie nicht schlechter als mein altes ergebnis sind
		 * (in der Regel variert die Güte eines Knoten nicht allzustark von dem seinem Teilbaum
		 * => altes Alpha ist relativ gut als untere Schranke für die neue Suche).
		 * => Dies kann wahrscheinlich in wenigen Situationen problematisch werden, falls heuristik schlecht ist ^^"
		 * 
		 */
	}
}
