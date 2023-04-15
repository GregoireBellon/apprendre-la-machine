package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;


import agent.Snake;
import controller.ControllerSnakeGame;
import item.Item;
import model.SnakeGame;
import model.Game;
import utils.AgentAction;
import utils.ColorSnake;
import utils.FeaturesItem;
import utils.FeaturesSnake;
import utils.Position;


public class ViewSnakeGame implements Observer{

	JFrame jFrame;

	PanelSnakeGame panelBomberman;

	public ViewSnakeGame(ControllerSnakeGame controller, Observable obs, PanelSnakeGame panelBomberman) {

		obs.addObserver(this);


		jFrame= new MainFrame(controller);
		
		
		
		
		jFrame.setTitle("Game");
		
		
		jFrame.setSize(new Dimension(panelBomberman.getSizeX()*45, panelBomberman.getSizeY()*45));
		Dimension windowSize=jFrame.getSize();
		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint=ge.getCenterPoint();
		int dx=centerPoint.x - windowSize.width/ 2 ;
		int dy=centerPoint.y - windowSize.height/ 2 - 350;
		jFrame.setLocation(dx,dy);
		

	
		
//		jFrame.setLayout(new BorderLayout());

		this.panelBomberman = panelBomberman;
		
		jFrame.add("Center",panelBomberman);


	

		jFrame.setVisible(true);

		


	}

	@Override
	public void update(Observable o, Object arg) {

		SnakeGame snakeGame = (SnakeGame)o;


		
		ArrayList<FeaturesSnake>  featuresSnakes = new ArrayList<FeaturesSnake>();
	
		for(Snake snake : snakeGame.getSnakes()) {	
			
			boolean isInvincible;
			if(snake.getInvincibleTimer() > 0) {
				isInvincible = true;
			} else {
				isInvincible = false;
			}
			
			boolean isSick;
			if(snake.getSickTimer() > 0) {
				isSick = true;
			} else {
				isSick = false;
			}
			
			featuresSnakes.add(new FeaturesSnake(snake.getPositions(), snake.getLastMove(), snake.getColorSnake(), isInvincible, isSick, snake.isDead()));
		}
		
		ArrayList<FeaturesItem> featuresItem = new ArrayList<FeaturesItem>();
		
		for(Item item : snakeGame.getItems()) {		
			featuresItem.add(new FeaturesItem(item.getX(), item.getY(), item.getItemType()));
		}

		
		panelBomberman.updateInfoGame(featuresSnakes , featuresItem);


		panelBomberman.repaint();

		

	}


}
