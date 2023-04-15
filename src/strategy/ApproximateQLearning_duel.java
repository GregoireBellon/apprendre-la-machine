package strategy;

import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Consumer;

import agent.Snake;
import item.Item;
import model.SnakeGame;

import utils.AgentAction;
import utils.Feature;
import utils.ItemType;
import utils.Position;


public class ApproximateQLearning_duel extends Strategy{
	
	private int nb_actions;
	private double epsilon;
	private double gamma;
	private double alpha;
	
	private double w[];
	private Feature[] features;
// manhattan distance pomme la plus proche
// cette action nous tue elle ?
	
	private Feature PommeProche = (int idxSnake, SnakeGame game, Position pos) -> {
	Position snake_head = game.getSnakes().get(idxSnake).getPositions().get(0);
		OptionalInt res = game.getItems().stream().mapToInt(item -> Math.abs(item.getX() - snake_head.getX()) + Math.abs(item.getX() - snake_head.getX())).min();
		return res.isPresent() ? res.getAsInt():0;
	};
	
	private Feature actionMortelle = (int idxSnake, SnakeGame game, Position pos) -> {
	Position snake_head = game.getSnakes().get(idxSnake).getPositions().get(0);
		OptionalInt res = game.getItems().stream().mapToInt(item -> Math.abs(item.getX() - snake_head.getX()) + Math.abs(item.getX() - snake_head.getX())).min();
		return res.isPresent() ? res.getAsInt():0;
	};

	

    public ApproximateQLearning_duel(int nbActions, double epsilon, double gamma, double alpha) {	
        super(nbActions, epsilon, gamma, alpha);

		this.features = new Feature[] {PommeProche, actionMortelle};
		
		Random rand = new Random();
		this.w = new double[] {rand.nextDouble(), rand.nextDouble()};
//		this.features = new double{}
    }
    

    
	@Override
	public synchronized AgentAction chooseAction(int idxSnake, SnakeGame snakeGame) {
		
		
		return AgentAction.MOVE_DOWN;

       
    }
	
	

	@Override
	public synchronized void update(int idx, SnakeGame state, AgentAction action, SnakeGame nextState, int reward,
			boolean isFinalState) {
		
		
	
		
	}
	
	
}
