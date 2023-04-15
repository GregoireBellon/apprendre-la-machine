package controller;
import model.SimpleGame;
import view.ViewCommand;
import view.ViewSimpleGame;

public class ControllerSimpleGame extends AbstractController {


	public ControllerSimpleGame() {
		
		super();
		
		this.game = new SimpleGame(100);
		this.game.init();
		
		ViewSimpleGame viewSimpleGame = new ViewSimpleGame(this.game);
		
		ViewCommand viewCommand = new ViewCommand(this, this.game);
	}



}