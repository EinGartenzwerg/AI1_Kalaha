package info.kwarc.teaching.AI.Kalah.Agents;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

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
		
		return re;
		// debugging();
		// return -3;
	}

	@Override
	public Iterable<String> students() {
		// TODO Auto-generated method stub
		return null;
	}


	private class SearchThread extends Thread {
		private MyBoard			mb;
		private AtomicInteger	bestMove;
		private AtomicBoolean	poisionPill;
		private int				index;

		public SearchThread (MyBoard b) {
			mb = b;
			bestMove = new AtomicInteger();
			poisionPill = new AtomicBoolean();
			poisionPill.set(true);
			index = 0;
		}

		@Override
		public void run() {
			int[] re;
			while (!poisionPill.get()) {
				index++;
				re = mb.search(index);
				bestMove.set(re[1] + 1);// +1 da scala mit 1 startet anstatt mit 0 -.-
				// System.out.println("[AI_THINKS]: Depth: " + d + " Move " + re[1] + " Value " + re[0]);
				if (!poisionPill.get()) {
					System.out.println("\n 												[AI_THINKS]:		Depth: " + index + "	Move " + re[1] + "		Value " + re[0]);
				}
			}
		}

		public void kill() {
			poisionPill.set(true);
		}
	}
}
