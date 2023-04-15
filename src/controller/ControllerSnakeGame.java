package controller;

import java.util.ArrayList;

import agent.Snake;
import item.Item;
import model.SnakeGame;
import model.InputMap;
import utils.AgentAction;
import view.PanelSnakeGame;
import view.ViewSnakeGame;
import view.ViewCommand;



public class ControllerSnakeGame extends AbstractController {

	

	
	
	public ControllerSnakeGame(SnakeGame snakeGame) {
		
		this.game = snakeGame;
	}
	
	public ControllerSnakeGame() {
		

		String layoutName = "layouts/smallArena.lay";
		
		
		InputMap inputMap = null;
		
		try {
			inputMap = new InputMap(layoutName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		this.game = new SnakeGame(10000, inputMap, false);
		this.game.init();


		
		PanelSnakeGame panelSnakeGame = new PanelSnakeGame(inputMap.getSizeX(), inputMap.getSizeY(), inputMap.get_walls(), inputMap.getStart_snakes(), inputMap.getStart_items());
		
		
		ViewSnakeGame viewSnakeGame = new ViewSnakeGame(this, this.game, panelSnakeGame);
		
		ViewCommand viewCommand = new ViewCommand(this, this.game);
		
		
	}
	

	public void goUp(){
        ((SnakeGame)this.game).setInputMoveHuman1(AgentAction.MOVE_UP);
	}
	
	public void goDown(){
		((SnakeGame)this.game).setInputMoveHuman1(AgentAction.MOVE_DOWN);
	}	
	
	public void goLeft(){
		((SnakeGame)this.game).setInputMoveHuman1(AgentAction.MOVE_LEFT);
	}	
	
	public void goRight(){
		((SnakeGame)this.game).setInputMoveHuman1(AgentAction.MOVE_RIGHT);
	}	



}
