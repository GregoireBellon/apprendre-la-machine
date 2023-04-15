package main_duel;


import java.util.ArrayList;

import controller.ControllerSnakeGame;
import model.Game;
import model.InputMap;
import model.SnakeGame;
import strategy.ApproximateQLearning_duel;
import strategy.ApproximateQLearning_solo;
import strategy.Strategy;
import strategy.StrategyAdvanced;
import strategy.StrategyHuman;
import strategy.StrategyRandom;
import strategy.TabularQLearning_duel;
import strategy.TabularQLearning_solo;
import utils.AgentAction;
import view.PanelSnakeGame;
import view.ViewCommand;
import view.ViewSnakeGame;



public class main_batchMode_duel {

	public static void main(String[] args) {
		
		double gamma = 0.95;
		double epsilon = 0.2;
		double alpha = 0.01;

		boolean randomFirstApple = true;
		
		String layoutName = "layouts/duel/smallArenaNoWall.lay";
		
		InputMap inputMap = null;
		
		try {
			inputMap = new InputMap(layoutName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Strategy[] arrayStrategies = new Strategy[inputMap.getStart_snakes().size()];
		
	    arrayStrategies[0] = new ApproximateQLearning_duel(AgentAction.values().length, epsilon, gamma, alpha); 
	    arrayStrategies[1] = new StrategyRandom();

		

		//Nombre de simulations séquentielles lancees pour calculer la recompense moyenne en mode train
		int Ntrain = 100;
		
		
		//Nombre de simulations parallèle lancees pour calculer la recompense moyenne en mode test
		int Ntest = 100;
		
		
		//Nombre max de tours d'une partie de snake
		int maxTurnSnakeGame = 200;
		
		

		for(int cpt = 0; cpt < 10000000; cpt++) {

			System.out.println("Compute score in test mode");
			launchParallelGames(Ntest, maxTurnSnakeGame, inputMap, arrayStrategies, false, randomFirstApple);
			
			if(cpt%10 == 0) {
				System.out.println("Visualization mode");
				vizualize(maxTurnSnakeGame, inputMap, arrayStrategies, false, randomFirstApple);
			}
			
			System.out.println("Play and collect examples - train mode");
			launchParallelGames(Ntrain, maxTurnSnakeGame, inputMap, arrayStrategies, true, randomFirstApple);
			
		}
		
	

	}
	
	
	
	

	
	public static void launchParallelGames(int nbGames, int maxTurnSnakeGame, InputMap inputMap, Strategy[] arrayStrats, boolean modeTrain, boolean randomFirstApple) {
		

		double[] scoreStrats = new double[arrayStrats.length];
		
		ArrayList<SnakeGame> snakeGames = new ArrayList<SnakeGame>();
		

		
		for(int i = 0; i < nbGames; i++ ) {

			for(int j =0; j < arrayStrats.length; j++) {
				arrayStrats[j].setModeTrain(modeTrain);
			}
			
			
			SnakeGame snakeGame = new SnakeGame(maxTurnSnakeGame, inputMap, randomFirstApple);
			snakeGame.setStrategies(arrayStrats);
			snakeGame.init();
			
			snakeGame.setTime(0);
			
			
			snakeGames.add(snakeGame);
				

		}
		
		
		for(int i = 0; i < nbGames; i++ ) {
		
			//System.out.println("launch game " + i);
			snakeGames.get(i).launch();
			
		}
		

		
		for(int i = 0; i < nbGames; i++ ) {
				
			try {
				((Game) snakeGames.get(i)).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			for(int j =0; j < arrayStrats.length; j++) {
				
				scoreStrats[j] += snakeGames.get(i).getTabTotalScoreSnakes()[j];
				
			}
			
		}
		
		
		
		for(int j =0; j < arrayStrats.length; j++) {
			
			if(modeTrain) {
				System.out.println("Train - agent " + j + " - strategy " + arrayStrats[j] + " average global score : " + scoreStrats[j]/nbGames);
			} else {
				System.out.println("Test - agent " + j + " - strategy " + arrayStrats[j] + " average global score : " + scoreStrats[j]/nbGames);
			}
			
		}
		
		
		
		


		
	}


	
	
	
	private static void vizualize(int maxTurnSnakeGame, InputMap inputMap, Strategy[] arrayStrats, boolean modeTrain, boolean randomFirstApple) {
		
		

		SnakeGame snakeGame = new SnakeGame(maxTurnSnakeGame, inputMap, randomFirstApple);
		
		for(int j =0; j < arrayStrats.length; j++) {
			
			arrayStrats[j].setModeTrain(modeTrain);

		}
		
		snakeGame.setStrategies(arrayStrats);
		
		snakeGame.init();
		snakeGame.setTime(10);

		
		
		ControllerSnakeGame controllerSnakeGame = new ControllerSnakeGame(snakeGame);
		

		
		
		
		PanelSnakeGame panelSnakeGame = new PanelSnakeGame(inputMap.getSizeX(), inputMap.getSizeY(), inputMap.get_walls(), inputMap.getStart_snakes(), inputMap.getStart_items());
		
		
		ViewSnakeGame viewSnakeGame = new ViewSnakeGame(controllerSnakeGame, snakeGame, panelSnakeGame);
		
		ViewCommand viewCommand = new ViewCommand(controllerSnakeGame, snakeGame);
		
		
		
		controllerSnakeGame.play();
		viewCommand.getState().clickPlay();
		

	}
	
	
}
