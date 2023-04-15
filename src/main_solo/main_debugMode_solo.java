package main_solo;
import controller.ControllerSnakeGame;
import model.InputMap;
import model.SnakeGame;
import strategy.ApproximateQLearning_solo;
import strategy.Strategy;
import strategy.TabularQLearning_solo;
import utils.AgentAction;
import view.PanelSnakeGame;
import view.ViewCommand;
import view.ViewSnakeGame;

public class main_debugMode_solo {

	public static void main(String[] args) {
		
		double gamma = 0.95;
		double epsilon = 0.2;
		double alpha = 0.01;

		boolean randomFirstApple = true;
				
		String layoutName = "layouts/alone/small_alone.lay";
		
		InputMap inputMap = null;
		
		try {
			inputMap = new InputMap(layoutName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Strategy[] arrayStrategies = new Strategy[inputMap.getStart_snakes().size()];
		
//		arrayStrategies[0] = new TabularQLearning_solo(AgentAction.values().length, epsilon, gamma, alpha);
		arrayStrategies[0] = new ApproximateQLearning_solo(AgentAction.values().length, epsilon, gamma, alpha);
		
		
		for(int j =0; j < arrayStrategies.length; j++) {
			
			arrayStrategies[j].setModeTrain(true);

		}
		
		
		SnakeGame snakeGame = new SnakeGame(200, inputMap, randomFirstApple);
		snakeGame.setStrategies(arrayStrategies);
		
		snakeGame.init();


		
		ControllerSnakeGame controllerSnakeGame = new ControllerSnakeGame(snakeGame);
				
		PanelSnakeGame panelSnakeGame = new PanelSnakeGame(inputMap.getSizeX(), inputMap.getSizeY(), inputMap.get_walls(), inputMap.getStart_snakes(), inputMap.getStart_items());
		
		ViewSnakeGame viewSnakeGame = new ViewSnakeGame(controllerSnakeGame, snakeGame, panelSnakeGame);
		
		ViewCommand viewCommand = new ViewCommand(controllerSnakeGame, snakeGame);

	}
	
	



}
