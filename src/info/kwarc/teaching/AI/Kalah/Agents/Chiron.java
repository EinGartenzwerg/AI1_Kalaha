package info.kwarc.teaching.AI.Kalah.Agents;

import info.kwarc.teaching.AI.Kalah.Board;

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
		mb = new MyBoard(b,playerOne);
		this.playerOne = playerOne;
		// TODO: start thinking
	}

	@Override
	public int move() {
		mb.update();
		return mb.search(12);
	}

	@Override
	public Iterable<String> students() {
		// TODO Auto-generated method stub
		return null;
	}

}
