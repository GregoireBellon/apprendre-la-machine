package utils;

import model.SnakeGame;

@FunctionalInterface
public interface Feature {
	double feature(int idxSnake, SnakeGame game, Position next_pos);
	public static Feature  emptyFeature = (int a, SnakeGame b, Position c) ->{return 1D;};

}
