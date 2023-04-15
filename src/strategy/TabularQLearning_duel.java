package strategy;


import java.util.ArrayList;
import java.util.HashMap;

import java.util.Random;

import agent.Snake;
import model.SnakeGame;
import utils.AgentAction;
import utils.ItemType;



public class TabularQLearning_duel extends Strategy {


    

    public TabularQLearning_duel(int nbActions, double epsilon, double gamma, double alpha) {	
        super(nbActions, epsilon, gamma, alpha);

        
    }

    

	@Override
	public synchronized AgentAction chooseAction(int idxSnake, SnakeGame snakeGame) {

		return AgentAction.MOVE_DOWN;

        
    }


	@Override
	public synchronized void update(int idxSnake, SnakeGame state, AgentAction action, SnakeGame nextState, int reward, boolean isFinalState) {
		
		
	}
	
	


}
