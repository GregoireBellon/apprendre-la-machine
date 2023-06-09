package utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.OptionalInt;

import agent.Snake;
import model.SnakeGame;

public class GameHelpers {

	public static Position calcNextPos(Position current_position, AgentAction action, int size_x, int size_y) {
			
		Position ret = current_position;
		
//		System.out.println("Current pos : " + ret.getX() + ", " + ret.getY());
		
//		System.out.println("Agent action : " + action);
		
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

//		System.out.println("so nex pos : " + ret.getX() + ", " + ret.getY());

		return ret;
	}
	
	public static boolean deadPos(Position position, SnakeGame game) {
		
		if(game.getWalls()[position.getX()][position.getY()]) {
			return true;
		}
		
		for(Snake snake : game.getSnakes()) {
						
			for(Position pos : snake.getPositions()) {
				if(pos.equals(position)) {
					return true;
				}
			}
		}
		
		return false; 
	}
	
	
	public static int freeSpaceAround(Position position, SnakeGame game) {
		
		Map<Position, Boolean> checked_positions = new HashMap<Position, Boolean>();
		LinkedList<Position> unchecked_positions = new LinkedList<Position>();
		
		int free_space = 0;
		
		unchecked_positions.add(position);
		
		while(unchecked_positions.size()>0) {
			
			Position checking = unchecked_positions.remove();
			
			if(checked_positions.getOrDefault(checking, false)) {
				continue;
			}

			
			if(!GameHelpers.deadPos(checking, game)) {
				
				free_space++;
				
				for(AgentAction a : AgentAction.values()) {
					
					Position next_pos = calcNextPos(checking, a, game.getSizeX(), game.getSizeY());
					unchecked_positions.add(next_pos);
				}
			}
			
			checked_positions.put(checking, true);		
		}
		
		return free_space;
		
	}

	
	public static int closestWall(Position pos, SnakeGame game){
		
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
	
	public static int closestApple(Position pos, SnakeGame game) {
		
		OptionalInt res = game.getItems().stream().mapToInt(item -> manhattanDistance(pos, new Position(item.getX(), item.getY()))).min();
				
		return res.isPresent() ? res.getAsInt() : (game.getSizeX() + game.getSizeY());

	}
	
	public static int manhattanDistance(Position pos1, Position pos2) {
		return (Math.abs(pos2.getX()-pos1.getX()) + Math.abs(pos2.getY() - pos1.getY()));
	}
	
}
