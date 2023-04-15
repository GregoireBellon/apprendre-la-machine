package strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;

import agent.Snake;
import item.Item;
import model.SnakeGame;

import utils.AgentAction;
import utils.Feature;
import utils.GameHelpers;
import utils.ItemType;
import utils.Position;


public class ApproximateQLearning_solo extends Strategy{


	//	Features : 
	//	Manathan distance de la pomme la plus proche
	//	Manathan distance du mur le plus proche
	//	L'action va elle nous tuer ?

	private double w[];
	private Feature[] features;
	private double total_reward;
	private int tour_cpt;
	double total_guess;

	private  Feature pommeProche = (int idxSnake, SnakeGame game, Position next_pos) -> {
		double ret = GameHelpers.closestApple(next_pos, game);
		
		return ret - game.getSizeX() + game.getSizeY();
		
		//		System.out.println("Pomme la plus proche : " + ret);
//		return -ret ;
	};

	private Feature actionMortelle = (int idxSnake, SnakeGame game, Position next_pos) -> {
		double ret =  GameHelpers.deadPos(next_pos, game) ? 1 : 0;
//		System.out.println("Action mortelle ? : " + ret);
		return ret;
	};

	private Feature murProche = (int idxSnake, SnakeGame game, Position next_pos) -> {
		double ret = GameHelpers.closestWall(next_pos, game);
//				System.out.println("Mur le plus proche : " + ret);
		return -ret;
	};


	public ApproximateQLearning_solo(int nbActions, double epsilon, double gamma, double alpha) {	
		super(nbActions, epsilon, gamma, alpha);

		this.features = new Feature[] {Feature.emptyFeature, pommeProche, actionMortelle};

		this.total_reward = 0;
		this.total_guess = 0;
		this.tour_cpt = 0;

		Random r = new Random();

		this.w = new double[this.features.length];
		for(int i = 0; i < this.w.length; i++) {this.w[i] = r.nextGaussian();}
		
//		this.w = new double[] {-0.28661643707056117, 0.23784230344219542, -10.2147240287572967};

	}



	@Override
	public synchronized AgentAction chooseAction(int idxSnake, SnakeGame snakeGame) {

		
//		if()
		
		Position current_pos = snakeGame.getSnakes().get(idxSnake).getPositions().get(0);
//		System.out.println("Current pos : " + current_pos);		
		
		
		Random r = new Random();
		AgentAction best_action = AgentAction.values()[r.nextInt(AgentAction.values().length )]; 

		if(r.nextDouble() < this.epsilon) {
//			System.out.println("epsiloned");
			return best_action;
		}


		best_action = getMaxScore(current_pos, best_action, snakeGame, idxSnake).getKey();

		
//		System.out.println("chosed " + best_action);
		
		return best_action;

	}

	public void updateW(int idx, SnakeGame former_game, double target_q, AgentAction action_chosed) {

		Position former_pos = former_game.getSnakes().get(idx).getPositions().get(0);
		Position chosed_position = GameHelpers.calcNextPos(former_pos, action_chosed, former_game.getSizeX(), former_game.getSizeY());

		double guessed_score = computeScore(chosed_position, idx, former_game);

		
//		ERROR OK
		double error = guessed_score - target_q;

//				System.out.println("guessed_score : " + guessed_score);
//				System.out.println("error : " + error);

		for(int i = 0; i < this.w.length; i++) {

//			System.out.println(this.w[i] + " - " + (2 * this.alpha * this.features[i].feature(idx, former_game, chosed_position) * error));

			this.w[i] = this.w[i] - 2 * this.alpha * this.features[i].feature(idx, former_game, chosed_position)* error;

			//			if( Double.isNaN(this.w[i])) {
			//				System.out.println("STOP ");
			//				System.out.println("chosen position : " + chosed_position);
			//				System.out.println("features : ");
			//				
			//				for(int j = 0; j < this.features.length; j++) {
			//					System.out.println(
			//					this.features[j].feature(idx, game, chosed_position));
			//				}
			//				System.out.println("error : "+ error);
			//				System.out.println("guessed_score : "+ guessed_score);
			//				System.out.println("target_q : "+ target_q);
			//				System.out.println("this.w.length : "+ this.w.length);
			//												
			//				System.exit(1);
			//			}
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

//				for(ImmutablePair<AgentAction, Double> score : all) {
//					System.out.println(score.getKey() + " : " + score.getValue());
//				}
//
//
//				System.out.println("selected : " + all.get(all.size()-1).getValue());
				
				return all.get(all.size()-1);
	}

	private double computeScore(Position pos, int idx, SnakeGame game) {

//		System.out.println("Computing score for " + pos);

		double sum = 0;
		for(int i = 0; i < this.w.length; i++) {
			sum += this.w[i] * this.features[i].feature(idx, game, pos);
		}
		//		System.out.println("Result : " +sum);

		return sum;

	}


	@Override
	public synchronized void update(int idx, SnakeGame state, AgentAction action, SnakeGame nextState, int reward, boolean isFinalState) {

//		this.total_reward += reward;


//		System.out.println("Final State");
		//		}

		//		if(!(this.tour_cpt % 16 == 0)) {
		//			return;
		//		}


		//		else {
		//			reward += 1;
		//		}

		//		System.out.println("total reward : " + total_reward);
		//		System.out.println("reward : " + reward);

		Random r = new Random();
		AgentAction default_action = AgentAction.values()[r.nextInt(AgentAction.values().length)];
//		System.out.println("default action" + default_action );
		
		
		Position current_pos = nextState.getSnakes().get(idx).getPositions().get(0);
		
		ImmutablePair<AgentAction, Double> max_score = getMaxScore(current_pos, default_action, nextState, idx);		

//		System.out.println("total_reward "+ total_reward);
		//		System.out.println("gamma " + this.gamma);
//		System.out.println("max_score " + max_score.getValue() + " with action " + max_score.getKey());

		
//		OK
		
		double targetQ = reward;
		
		// Ca n'a pas de sens de chercher le meilleur score  lorsque le snake est mort
		if(!(isFinalState && nextState.getSnakes().get(idx).isDead())) {
		
		targetQ = (reward + this.gamma * max_score.getValue());
		
		}

//		System.out.println("targetQ : "+ targetQ);

		this.updateW(idx, state, targetQ, action);

		
//		String result = "";
//		for(double param : this.w) {
//			result += (param + ", ");
//		}
//
//		System.out.println("Final w : " + result);

//		this.total_reward = 0;

	}






}
