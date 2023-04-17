package strategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;

import agent.Snake;
import model.SnakeGame;

import utils.AgentAction;
import utils.GameHelpers;
import utils.Position;

public class ChallengeQLearning extends Strategy{


	//	Features : 
	//	Manathan distance de la pomme la plus proche
	//	Manathan distance du mur le plus proche
	//	L'action va elle nous tuer ?
	//  Quel espace espace du terrain est accessible après ce move ? (permet d'éviter de s'enfermer)
	
	private double w[];

	private double pommeProche(int idxSnake, SnakeGame game, Position next_pos){

		int res = game.getItems().stream()
				.mapToInt(item -> manhattanDistance(next_pos, new Position(item.getX(), item.getY())))
				.min().orElse((game.getSizeX() + game.getSizeY()));

		return res;

	}

	private double actionMortelle(int idxSnake, SnakeGame game, Position next_pos){

		Position position = next_pos;

		if(game.getWalls()[position.getX()][position.getY()]) {
			return -1;
		}

		for(Snake snake : game.getSnakes()) {

			for(Position pos : snake.getPositions()) {
				if(pos.equals(position)) {
					return -1;
				}
			}
		}
		return 1; 

	}

	private double murProche (int idxSnake, SnakeGame game, Position next_pos){

		Position pos = next_pos;

		int closest = (game.getSizeX() + game.getSizeY());

		for(int x = 0; x < game.getSizeX(); x++) {
			for(int y = 0; y < game.getSizeY(); y++) {

				if(game.getWalls()[x][y]) {
					int distance = manhattanDistance(pos, new Position(x,y));
					if(distance < closest) {
						closest = distance;
					}					
				}
			}
		}

		return closest;
	}
	
	
	
	private double spaceAround(int idxSnake, SnakeGame game, Position next_pos){
		
		Position position = next_pos;
		
		Map<Integer, Boolean> checked_positions = new HashMap<Integer, Boolean>();
		LinkedList<Position> unchecked_positions = new LinkedList<Position>();
		
		int free_space = 0;
		
		unchecked_positions.add(position);
		
		while(unchecked_positions.size()>0) {
			
			Position checking = unchecked_positions.remove();
			int checking_hash = (checking.getX()+":"+checking.getY()).hashCode();

			if(checked_positions.getOrDefault(checking_hash, false)) {
				continue;
			}

			
			if(!GameHelpers.deadPos(checking, game)) {
				
				free_space++;
				
				for(AgentAction a : AgentAction.values()) {
					
					Position to_check = calcNextPos(checking, a, game.getSizeX(), game.getSizeY());
					unchecked_positions.add(to_check);
				}
			}
			checked_positions.put(checking_hash, true);		
		}
				
//		System.out.println("Free space : " + free_space);
		
		return free_space / (game.getSizeX() * game.getSizeY());
				
	}



	public ChallengeQLearning(int nbActions, double epsilon, double gamma, double alpha) {	
		super(nbActions, epsilon, gamma, alpha);

		Random r = new Random();

		this.w = new double[]{-0.6948440722218792, -0.24112522940115177, 0.4522513931693198, -0.6701793444282204, -0.01665073837898068};

//		for(int i = 0; i < this.w.length; i++) {this.w[i] = r.nextGaussian();}
		
	}



	@Override
	public synchronized AgentAction chooseAction(int idxSnake, SnakeGame snakeGame) {

		Position current_pos = snakeGame.getSnakes().get(idxSnake).getPositions().get(0);
		//		System.out.println("Current pos : " + current_pos);		


		Random r = new Random();
		AgentAction best_action = AgentAction.values()[r.nextInt(AgentAction.values().length )]; 

		if(r.nextDouble() < this.epsilon) {
			//			System.out.println("epsiloned");
			//			Dans ce cas, best_action sera random
			return best_action;
		}


		best_action = getMaxScore(current_pos, best_action, snakeGame, idxSnake).getKey();


		return best_action;
	}

	public void updateW(int idx, SnakeGame former_game, double target_q, AgentAction action_chosed) {

		Position former_pos = former_game.getSnakes().get(idx).getPositions().get(0);
		Position chosed_position = GameHelpers.calcNextPos(former_pos, action_chosed, former_game.getSizeX(), former_game.getSizeY());

		double guessed_score = computeScore(chosed_position, idx, former_game);


		//		ERROR OK
		double error = guessed_score - target_q;

		this.w[0] = this.w[0] - 2 * this.alpha * 1 * error;
		this.w[1] = this.w[1] - 2 * this.alpha * pommeProche(idx, former_game, chosed_position) * error;
		this.w[2] = this.w[2] - 2 * this.alpha * actionMortelle(idx, former_game, chosed_position) * error;
		this.w[3] = this.w[3] - 2 * this.alpha * murProche(idx, former_game, chosed_position) * error;				
		this.w[4] = this.w[4] - 2 * this.alpha * spaceAround(idx, former_game, chosed_position) * error;
	}


	private ImmutablePair<AgentAction, Double> getMaxScore(Position current_pos, AgentAction default_action, SnakeGame snakeGame, int idxSnake){

		List<ImmutablePair<AgentAction, Double>> all =  Arrays.stream(AgentAction.values())
				.map(a -> {
					//					System.out.println("action " + a);
					Position next_pos = GameHelpers.calcNextPos(current_pos, a, snakeGame.getSizeX(), snakeGame.getSizeY());
					return new ImmutablePair<AgentAction, Double>(a, computeScore(next_pos, idxSnake, snakeGame));					
				})

				.sorted((pair1, pair2) -> (int) (pair1.getValue().compareTo(pair2.getValue()))).collect(Collectors.toList());

		return all.get(all.size()-1);
	}

	private double computeScore(Position pos, int idx, SnakeGame game) {

		//		System.out.println("Computing score for " + pos);

		double sum = 0;
		//		for(int i = 0; i < this.w.length; i++) {
		//			sum += this.w[i] * this.features[i].feature(idx, game, pos);
		//		}
		//		System.out.println("Result : " +sum);

		sum += this.w[0];
		sum += this.w[1] * this.pommeProche(idx, game, pos);
		sum += this.w[2] * this.actionMortelle(idx, game, pos);
		sum += this.w[3] * this.murProche(idx, game, pos);
		sum += this.w[4] * this.spaceAround(idx, game, pos);

		return sum;

	}


	@Override
	public synchronized void update(int idx, SnakeGame state, AgentAction action, SnakeGame nextState, int reward, boolean isFinalState) {

		Random r = new Random();
		AgentAction default_action = AgentAction.values()[r.nextInt(AgentAction.values().length)];


		Position current_pos = nextState.getSnakes().get(idx).getPositions().get(0);

		ImmutablePair<AgentAction, Double> max_score = getMaxScore(current_pos, default_action, nextState, idx);		

		double targetQ = reward;

		// Ca n'a pas de sens de chercher le meilleur score  lorsque le snake est mort
		if(!(isFinalState && nextState.getSnakes().get(idx).isDead())) {

			targetQ = (reward + this.gamma * max_score.getValue());

		}

		//		System.out.println("targetQ : "+ targetQ);

		this.updateW(idx, state, targetQ, action);

		if(isFinalState) {

			String result = "";
			for(double param : this.w) {
				result += (param + ", ");
			}

			System.out.println("Snake id : " + idx +" Final w : " + result);
		}

		//	this.total_reward = 0;

	}


	public static int manhattanDistance(Position pos1, Position pos2) {
		return (Math.abs(pos2.getX()-pos1.getX()) + Math.abs(pos2.getY() - pos1.getY()));
	}


	public static Position calcNextPos(Position current_position, AgentAction action, int size_x, int size_y) {

		Position ret = current_position;

		switch(action) {
		case MOVE_UP : ret = new Position(current_position.getX(), (current_position.getY()-1) ); break;
		case MOVE_DOWN : ret = new Position(current_position.getX(), (current_position.getY()+1) % size_y); break;
		case MOVE_LEFT : ret = new Position(current_position.getX()-1, current_position.getY()); break;
		case MOVE_RIGHT : ret = new Position((current_position.getX()+1) % size_x, current_position.getY()); break;
		}

		if(ret.getX() < 0) {
			ret.setX(size_x-1);
		}

		if(ret.getY() < 0) {
			ret.setY(size_y-1);
		}

		return ret;
	}
}
