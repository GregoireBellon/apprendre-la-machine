package utils;

import java.io.Serializable;

public class Position implements Serializable{

	private int x;
	private int y;


	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return (this.x+":"+this.y).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {

		if(!(obj instanceof Position)) {
			return false;
		}
		
		Position converted = (Position) obj;
		return converted.x == this.x && converted.y == this.y;
		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "(" + this.getX() + ", " + this.getY() + ")";
	}
}
