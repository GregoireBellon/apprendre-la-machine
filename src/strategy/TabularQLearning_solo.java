package strategy;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.SplittableRandom;

import item.Item;
import model.SnakeGame;
import utils.AgentAction;
import utils.Position;



public class TabularQLearning_solo extends Strategy {

	//	utilisation d'une map et non d'une hashmap afin d'assurer la généricité
	//	un tableau et non un vecteur car toujour 4 moves possible : certain mènent juste à la mort
	private Map<String, double[]> tab;
	private int nb_actions;

	public TabularQLearning_solo(int nbActions, double epsilon, double gamma, double alpha) {	
		super(nbActions, epsilon, gamma, alpha);

		this.tab = new HashMap<String, double[]>();
		this.nb_actions = nbActions;
	}





	@Override
	public synchronized AgentAction chooseAction(int idxSnake, SnakeGame snakeGame) {

//		System.out.println("In choose action : ");
		
		Random r = new Random();
		if(r.nextDouble() < this.epsilon) {
//			System.out.println("Epsiloned");
			return TabularQLearning_solo.randomAction();
		}
		
		String state = encodeState(idxSnake, snakeGame);
				
		if(this.tab.get(state) == null) 
			this.tab.put(state, new double[] {0,0,0,0});
		
		return AgentAction.values()[getMaxReward(this.tab.get(state))];
	}

//	Retourne l'index de l'action avec le meilleur reward
	public static int getMaxReward(double[] rewards) {
//		on vérifie si les probabilités sont toutes 0, afin de retourner une action aléatoire si c'est le cas
//		choisir une valeur random lorsque que tous les éléments de Q(s,a) = 0 
//		permet d'ajouter de la diversité dans les mouvements du snake

		boolean only_0 = true;
		int i_max = 0;
//		System.out.println("In max reward");
		
//		recherche de Qmax(s,a)
		for(int i = 0; i < rewards.length; i++) {
			
			if(rewards[i] > 0) {
				only_0 = false;
			}
			
			if(rewards[i] > rewards[i_max]) {
				i_max = i;
			}
		}
		
		if(only_0) {
			SplittableRandom r = new SplittableRandom();
			return r.nextInt(rewards.length);
		}
		
//		System.out.println("max q : " + rewards[i_max]); 
		
		return i_max;

	}

	@Override
	public synchronized void update(int idxSnake, SnakeGame state, AgentAction action, SnakeGame nextState, int reward, boolean isFinalState) {
		String old_state = encodeState(idxSnake, state);
		String new_state = encodeState(idxSnake, nextState);
		
		if(this.tab.get(new_state) == null) 
			this.tab.put(new_state, new double[] {0,0,0,0});
		
		if(this.tab.get(old_state) == null) 
			this.tab.put(old_state, new double[] {0,0,0,0});

		
		
		double[] new_state_Q = this.tab.get(new_state);
		double[] old_state_Q = this.tab.get(old_state);
		
		double new_state_maxQ = new_state_Q[getMaxReward(new_state_Q)];
		
		old_state_Q[action.ordinal()] = ((1-this.alpha) * old_state_Q[action.ordinal()]) + (this.alpha * (reward + this.gamma * new_state_maxQ));
		
//		tab.put(old_state, old_state_Q);

//		System.out.println("Taille de la table : " + tab.size());
//        this.QTab.get(encodeState(0, state))[a] = (1 - this.alpha) * this.QTab.get(encodeState(0, state))[a] + this.alpha*(reward + this.gamma * maxQtab_next_state);
	}
	
	//	Code utilisé :
	//	' ' : vide
	//	'S' : corps de snake joueur
	//  'J' : tête du snake joueur
	//  'A' : pomme
	//	'I' : invincibilité
	//	'S' : sickball
	//	'B' : box
	public String encodeState(int idxSnake, SnakeGame snake_game) {

		char[][] plateau = new char[snake_game.getSizeX()][snake_game.getSizeY()];


		ArrayList<Position> positions_snake = snake_game.getSnakes().get(idxSnake).getPositions();

		//		On set la tête de notre snake
		plateau[positions_snake.get(0).getX()][positions_snake.get(0).getY()] = 'J';


		//		On set le corps
		Iterator<Position> pos_iterator = positions_snake.listIterator(1);

				while(pos_iterator.hasNext()) {
					Position pos_corps = pos_iterator.next();
					plateau[pos_corps.getX()][pos_corps.getY()] = 'S';
				}

		//		On ajoute les items
		for(Item item : snake_game.getItems()) {
			char item_symbol = 'B';

			switch(item.getItemType()) {
			case APPLE : item_symbol = 'A'; break;
			case INVINCIBILITY_BALL : item_symbol = 'I'; break;
			case SICK_BALL : item_symbol = 's'; break;
			default : item_symbol = 'B'; break;
			}

			plateau[item.getX()][item.getY()] = item_symbol;
		}
		
		//		utilisation d'un string builder afin d'éviter les copies de String intempestives.
		StringBuilder builder = new StringBuilder(snake_game.getSizeX() * snake_game.getSizeY());
			
			for(int y = 0; y < snake_game.getSizeY(); y++) {
				for(int x = 0; x < snake_game.getSizeX(); x++) {
				if(plateau[x][y] == '\u0000') {
					builder.append(' ');
				}
				else {
					builder.append(plateau[x][y]);
				}
			}
		}
		
		return builder.toString();
	}

	
	public static AgentAction randomAction() {
		Random rng = new Random();
		return AgentAction.values()[rng.nextInt(AgentAction.values().length)];
	}
}
