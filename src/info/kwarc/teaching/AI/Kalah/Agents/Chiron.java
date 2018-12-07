package info.kwarc.teaching.AI.Kalah.Agents;

import java.util.Queue;

import info.kwarc.teaching.AI.Kalah.Board;

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
		mb = new MyBoard(b, playerOne);
		// TODO: start thinking
	}

	@Override
	public int move() {
		mb.update();
		int re = mb.search(10);
		return re;
		//debugging();
		//return -3;
	}

	@Override
	public Iterable<String> students() {
		// TODO Auto-generated method stub
		return null;
	}

	private void testPrioQueue() {
		testPrioQueue_1();
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		testPrioQueue_2();
	}

	private void testPrioQueue_1() {
		int[] min = { 3, 1, 2, 0 };
		int[] max = { 0, 1, 0, 0 };
		State s = new State(max, min, 9);
		boolean t = false;
		s.setCurrentPlayer_is_max(t);
		System.out.println("====================================>>>>>>>>>");
		s.print();
		System.out.println("~~~~~~~~~~");
		Queue<State> children = s.getExtensions();
		for (State child : children) {
			System.out.println("									Value: " + child.getStore(t));
			child.print();

		}
		System.out.println("====================================<<<<<<<<<");

	}

	private void testPrioQueue_2() {
		int[] min = { 3, 1, 2, 0 };
		int[] max = { 3, 1, 0, 0 };
		boolean t = true;
		State s = new State(max, min, 9);
		s.setCurrentPlayer_is_max(t);
		System.out.println("====================================>>>>>>>>>");
		s.print();
		System.out.println("~~~~~~~~~~");
		Queue<State> children = s.getExtensions();
		for (State child : children) {
			System.out.println("									Value: " + child.getStore(t));
			child.print();

		}
		System.out.println("====================================<<<<<<<<<");

	}

	private void debugging() {
		int[] min = { 2, 0, 17, 0, 14, 7, 5 };
		int[] max = { 3, 2, 0, 0, 15, 0, 7 };
		System.out.println("====================================>>>>>>>>>");
		MyBoard b = new MyBoard(max, min);
		b.getState().print();
		System.out.println(b.search(10));

		System.out.println("====================================<<<<<<<<<");

	}

}
