package info.kwarc.teaching.AI.Kalah.Agents;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;;

public class State implements Comparable<State> {
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
	// how to get there
	// -1 if starting state

	public State (int[] max, int[] min, int drawseeds) {
		len = max.length;
		this.max = Arrays.copyOf(max, len);
		this.min = Arrays.copyOf(min, len);

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
		return "" + getMyStore();
	}

	// ====================================================================================================================================
	// getter and setter
	public boolean isFinal() {
		return isFinal;
	}

	public void calcIsFinal() {
		int drawseeds = MyBoard.DRAWSTONES;
		if (drawseeds == -1) {
			return;
		}

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
		return spielStand();
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
