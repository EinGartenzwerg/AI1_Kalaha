package info.kwarc.teaching.AI.Kalah.Agents;

import info.kwarc.teaching.AI.Kalah.Board;
import info.kwarc.teaching.AI.Kalah.util.Converter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Arrays;

public class Chiron extends info.kwarc.teaching.AI.Kalah.Agents.Agent {
	private final long		move_time	= 4900;

	private Board			b;
	private MyBoard			mb;
	private AtomicInteger	bestMove	= new AtomicInteger();
	private AtomicBoolean	poisionPill	= new AtomicBoolean();

	@Override
	public String name() {
		return "Chiron";
	}

	@Override
	public void init(Board board, boolean playerOne) {
		b = board;
		mb = new MyBoard(b, playerOne);
		// TODO: start thinking
	}

	@Override
	public int move() {
		long time_start = System.currentTimeMillis();
		mb.update();
		SearchThread t = new SearchThread(mb);
		t.start();
		long delta = System.currentTimeMillis() - time_start;
		while (delta < move_time) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {}
			delta = System.currentTimeMillis() - time_start;
		}
		t.kill();
		return bestMove.get();

	}

	private class SearchThread extends Thread {
		private MyBoard	mb;
		private int		index;

		public SearchThread (MyBoard b) {
			mb = b;
			poisionPill.set(false);
			index = 0;
		}

		@Override
		public void run() {
			int[] re = new int[2];
			while (!poisionPill.get()) {
				index++;
				re = mb.search(index);
				bestMove.set(re[1] + 1);// +1 da scala mit 1 startet anstatt mit 0 -.-
				// System.out.println("[AI_THINKS]: Depth: " + d + " Move " + re[1] + " Value " + re[0]);
				// if (!poisionPill.get()) {
				// }
			}
			//System.out.println("\n 												[AI_THINKS]:		Depth: " + index + "	Move " + re[1] + "		Value " + re[0]);

		}

		public void kill() {
			poisionPill.set(true);
		}

		public int getDepth() {
			return index;
		}
	}

	@Override
	public Iterable<String> students() {
		// TODO Auto-generated method stub
		LinkedList<String> studs = new LinkedList<String>();
		studs.add("Karol Athanasios Bakas");
		studs.add("Maximilian Harl");
		studs.add("Johannes Rieder");
		return studs;
	}
}

class State implements Comparable<State> {
	// TODO: refactor max, min to player and enemy
	// Ein Spieler
	private int[]	max;
	private int[]	min;
	private boolean	isFinal		= false;	// ob state schon zu Ende ist
	private int		ergebnis	= 2;		// -1 Min gewinnt, 0 Draw, 1 Max gewinnt, 2 sie spielen noch
	// player_max == true => its max's turn
	// player_max == false => its mins turn
	private boolean	currentPlayer_is_max;
	private int		len;
	private boolean	prev_player;
	private int		prev_move	= -1;		// 1,2,... len
	private int		drawseeds;
	// how to get there
	// -1 if starting state

	public State (int[] max, int[] min, int drawseeds) {
		len = max.length;
		this.max = Arrays.copyOf(max, len);
		this.min = Arrays.copyOf(min, len);
		this.drawseeds = drawseeds;
		calcIsFinal();

	}
	public State (int[] max, int[] min, int drawseeds, boolean b) {
		len = max.length;
		this.max = Arrays.copyOf(max, len);
		this.min = Arrays.copyOf(min, len);
		this.drawseeds = drawseeds;
		currentPlayer_is_max = b;
		calcIsFinal();

	}

	public State (State s) {
		isFinal = s.isFinal;
		ergebnis = s.ergebnis;
		len = s.max.length;
		max = Arrays.copyOf(s.max, len);
		min = Arrays.copyOf(s.min, len);
		prev_player = s.currentPlayer_is_max;
	}

	public State (State s, int prev) {
		this(s);
		prev_move = prev;
	}

	// TODO: make it a PrioQueue => we need a fast and easy eval for this (faster
	// then the one for our leaves)
	public Queue<State> getExtensions() {
		int[] player;
		if (currentPlayer_is_max) {
			player = this.max;
		} else {
			player = this.min;
		}
		Queue<State> children = new PriorityQueue<State>();
		for (int i = 0; i < len - 1; i++) {
			if (player[i] != 0) {
				children.add(move(i));
			}
		}
		// System.out.println("\n " + Arrays.toString(children.toArray()) + "\n\n\n");
		return children;

	}

	// Makes a move and returns the resulting State
	protected State move(int house) {
		State newState = new State(this, house);
		int[] player;
		int[] enemy;
		if (this.currentPlayer_is_max) {
			player = newState.max;
			enemy = newState.min;
		} else {
			player = newState.min;
			enemy = newState.max;
		}

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

				newState.currentPlayer_is_max = this.currentPlayer_is_max;
				return newState;
			} else if (player[index] == 1 && enemy[enemy.length - 2 - index] > 0) {// a)
				// code reused
				int enemyStones = enemy[enemy.length - 2 - index];
				enemy[enemy.length - 2 - index] = 0;
				player[index] = 0;
				player[player.length - 1] += enemyStones + 1;
			}
		}
		newState.currentPlayer_is_max = !this.currentPlayer_is_max;
		return newState;

	}

	public void print() {
		System.out.println("State " + currentPlayer_is_max + "		Papa: " + prev_move);
		System.out.println("Max:" + Arrays.toString(max));
		System.out.println("Min:" + Arrays.toString(min));
	}

	@Override
	public int compareTo(State o) { // TODO: make this a usefull heuristik
		int theirStore = o.getStore(prev_player);
		int myStore = getStore(prev_player);
		return myStore - theirStore;
	}

	@Override
	public String toString() { // TODO: make this a usefull heuristik
		return "" + prev_move;
	}

	// ====================================================================================================================================
	// getter and setter
	public boolean isFinal() {
		return isFinal;
	}

	public void calcIsFinal() {
//		int drawseeds = MyBoard.DRAWSTONES;
//		if (drawseeds == -1) {
//			return;
//		}

		if (max[len - 1] >= drawseeds) { // berechnet isFinal und Ergebnis "moeglichst effizient"
			if (max[len - 1] == drawseeds) {
				if (min[len - 1] == drawseeds) {
					isFinal = true;
					ergebnis = 0;
				}
			} else {
				isFinal = true;
				ergebnis = 1;
			}
		} else if (min[len - 1] > drawseeds) {
			isFinal = true;
			ergebnis = -1;
		}
	}

	public int spielStand() {
		return ergebnis;
	}

	public int[] getMax() {
		return max;
	}

	public void setMax(int[] m) {
		max = m;
	}

	public int[] getMin() {
		return min;
	}

	public void setMin(int[] m) {
		min = m;
	}

	public boolean getCurrentPlayer_is_max() {
		return currentPlayer_is_max;
	}

	public void setCurrentPlayer_is_max(boolean b) {
		currentPlayer_is_max = b;
	}

	public int getValue() {
		return Heuristik.evalState(this);
	}

	public int getPapa() {
		return prev_move;
	}

	public int getStore(boolean maxing) {
		return maxing ? max[len - 1] : min[len - 1];
	}

	public int getMyStore() {
		return currentPlayer_is_max ? max[len - 1] : min[len - 1];
	}
}


class MyBoard {
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
		playerOne = false;
	}

	// update: uses this.board => init werte vom board
	public void update() {
		ArrayList<Integer> in_player = Converter.getMyHouses(board, playerOne);
		ArrayList<Integer> in_enemy = Converter.getEnemyHouses(board, playerOne);
		in_player.add(Converter.getMyStoreSeeds(board, playerOne));
		in_enemy.add(Converter.getEnemyStoreSeeds(board, playerOne));
		int n = in_player.size();
		player = new int[n];
		enemy = new int[n];
		for (int i = 0; i < n; i++) { // TODO: ArrayList to Array
			player[i] = in_player.get(i).intValue();
			enemy[i] = in_enemy.get(i).intValue();
		}
	}

	public State getState() {
		return new State(player, enemy, DRAWSTONES, true);
	}

	// start einfache suche
	public int[] search(int d) {
		return deaper(getState(), d, MIN_V, MAX_V);
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
}

class Heuristik {
	private State state;
	private int len;
	
	public static int evalState(State s) {
		Heuristik h = new Heuristik(s);
		return h.eval();
	}
	public Heuristik (State s) {
		state = s;
		len = s.getMax().length;
	}
	
	public int eval() { // wird noch optimiert, nur zum Testen bzgl Gewichtungen
		if (state.isFinal()) {
			return state.spielStand() * MyBoard.MAX_V;
		}
		return (state.getMax()[len - 1] - state.getMin()[len - 1]);
	}
}
