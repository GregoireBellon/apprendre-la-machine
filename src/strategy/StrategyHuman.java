package strategy;

import agent.Snake;
import model.SnakeGame;
import utils.AgentAction;

public class StrategyHuman extends Strategy{



    @Override
    public AgentAction chooseAction(int idxSnake, SnakeGame snakeGame) {
        
        return snakeGame.getInputMoveHuman1();

    }

	@Override
	public void update(int idx, SnakeGame state,  AgentAction action, SnakeGame nextState, int reward, boolean isFinalState) {
		// TODO Auto-generated method stub
		
	}





}