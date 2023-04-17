package strategy;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;

import model.SnakeGame;

import utils.AgentAction;
import utils.Feature;
import utils.GameHelpers;
import utils.Position;


public class ApproximateQLearning_duel extends Strategy{
	
	//	Features : 
	//	Manathan distance de la pomme la plus proche
	//	Manathan distance du mur le plus proche
	//	L'action va elle nous tuer ?
	//	Manathan distance du corps ennemi le plus proche (tête inclue)
	//	Manathan distance de la tête ennemi la plus proche
	//  Quel espace espace du terrain est accessible après ce move ? (permet d'éviter de s'enfermer)

	private double w[];
	private Feature[] features;
	
	private  Feature pommeProche = (int idxSnake, SnakeGame game, Position next_pos) -> {
		double ret = GameHelpers.closestApple(next_pos, game);
		
		return ret;
		};

	private Feature actionMortelle = (int idxSnake, SnakeGame game, Position next_pos) -> {
		double ret =  GameHelpers.deadPos(next_pos, game) ? -1 : 1;
//		System.out.println("Action mortelle ? : " + ret);
		return ret;
	};

	private Feature murProche = (int idxSnake, SnakeGame game, Position next_pos) -> {
		double ret = GameHelpers.closestWall(next_pos, game);
//				System.out.println("Mur le plus proche : " + ret);
		return ret;
	};
	
	private Feature teteProche = (int idxSnake, SnakeGame game, Position next_pos) -> {
		
		int max_value = game.getSizeX() + game.getSizeY();

		
		int head_dist = game.getSnakes().stream().map(s -> {
			if(s.getId() == idxSnake) {
				return max_value;
			}
			
			return GameHelpers.manhattanDistance(next_pos, s.getPositions().get(0));
			
		}).min((i1,i2) -> i1.compareTo(i2)).orElse(max_value);
		
//		System.out.println("Closest head : " + head_dist);
		
		return head_dist;
		
	};
	
	private Feature corpsProche = (int idxSnake, SnakeGame game, Position next_pos) -> {
		
		int max_value = game.getSizeX() + game.getSizeY();
		
		int body_dist = game.getSnakes().stream().map(s -> {
			
			if(s.getId() == idxSnake) {
				return max_value;
			}
			
			return s.getPositions().stream().map(p -> GameHelpers.manhattanDistance(p, next_pos)).min((i1,i2)->i1.compareTo(i2)).orElse(max_value);

		}).min((i1,i2)->i1.compareTo(i2)).orElse(max_value);

//		System.out.println("Closest body : " + body_dist);
		
		return body_dist;
	};

	private Feature spaceAround = (int idxSnake, SnakeGame game, Position next_pos) -> {
		
		double ret = GameHelpers.freeSpaceAround(next_pos, game);

//		System.out.println("Space around : " + ret);
		
		return ret / (game.getSizeX() * game.getSizeY());
		
		
	};
	

    public ApproximateQLearning_duel(int nbActions, double epsilon, double gamma, double alpha) {	
		super(nbActions, epsilon, gamma, alpha);

		this.features = new Feature[] {Feature.emptyFeature, pommeProche, 
				actionMortelle, spaceAround};

		Random r = new Random();

		this.w = new double[this.features.length];
		
		for(int i = 0; i < this.w.length; i++) {this.w[i] = r.nextGaussian();}//		this.features = new double{}
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

				double error = guessed_score - target_q;

		for(int i = 0; i < this.w.length; i++) {

			this.w[i] = this.w[i] - 2 * this.alpha * this.features[i].feature(idx, former_game, chosed_position)* error;
		}

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

		double sum = 0;
		for(int i = 0; i < this.w.length; i++) {
			sum += this.w[i] * this.features[i].feature(idx, game, pos);
		}

		return sum;

	}

	@Override
	public synchronized void update(int idx, SnakeGame state, AgentAction action, SnakeGame nextState, int reward,
			boolean isFinalState) {
		
		
		Random r = new Random();
		AgentAction default_action = AgentAction.values()[r.nextInt(AgentAction.values().length)];		
		
		Position current_pos = nextState.getSnakes().get(idx).getPositions().get(0);
		
		ImmutablePair<AgentAction, Double> max_score = getMaxScore(current_pos, default_action, nextState, idx);		
		
		double targetQ = reward;
		
		// Ca n'a pas de sens de chercher le meilleur score  lorsque le snake est mort
		if(!(isFinalState && nextState.getSnakes().get(idx).isDead())) {
		
		targetQ = (reward + this.gamma * max_score.getValue());
		
		}

		this.updateW(idx, state, targetQ, action);
		
		
//		if(isFinalState) {
//
//			String result = "";
//			for(double param : this.w) {
//				result += (param + ", ");
//			}
//
//			System.out.println("Final w : " + result);
//		}
//
	}
	
	
}
