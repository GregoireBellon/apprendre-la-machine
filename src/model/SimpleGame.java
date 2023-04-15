package model;

public class SimpleGame extends Game{

	public SimpleGame(int maxTurn) {
		super(maxTurn);

	}

	@Override
	public void initializeGame() {
		System.out.println("Init game");
		
	}

	@Override
	public void takeTurn() {
		System.out.println("Turn : " + this.turn);
		
	}

	@Override
	public boolean gameContinue() {
		
		return true;
	}

	@Override
	public void gameOver() {
		System.out.println("Game over");
		
	}

}
