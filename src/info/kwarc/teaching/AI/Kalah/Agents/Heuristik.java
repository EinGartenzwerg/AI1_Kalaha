package info.kwarc.teaching.AI.Kalah.Agents;

public class Heuristik {
	private State state;
	private int len;
	// zur Kalibrierung der Heuristik
	int				maxHeu		= 1000;
	int				minHeu		= -maxHeu;
	
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
		return (state.getMax()[len - 1] - state.getMin()[len - 1]);
	}
}
