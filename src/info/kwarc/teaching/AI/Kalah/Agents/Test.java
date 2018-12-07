package info.kwarc.teaching.AI.Kalah.Agents;

import java.util.Queue;

public class Test {

	public static void main(String[] args) {
		int[] min = { 3, 1, 2, 3 };
		int[] max = { 0, 1, 0, 9};
		State s = new State(max, min, 9);
		Queue<State> q = s.getExtensions();
		for (State child: q ){
		}
		System.out.println(Heuristik.evalState(s));
		System.out.println(s.isFinal());
	}
}
