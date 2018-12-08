package info.kwarc.teaching.AI.Kalah.Agents;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import info.kwarc.teaching.AI.Kalah.Board;

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

	@Override
	public Iterable<String> students() {
		// TODO Auto-generated method stub
		return null;
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
			System.out.println("\n 												[AI_THINKS]:		Depth: " + index + "	Move " + re[1] + "		Value " + re[0]);

		}

		public void kill() {
			poisionPill.set(true);
		}

		public int getDepth() {
			return index;
		}
	}
}
