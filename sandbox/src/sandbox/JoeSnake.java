package sandbox;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Scanner;

public class JoeSnake {

	int[][] grid = new int[][] {
		{0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0}
	};
	
	enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
	
	public static void main(String[] args) {
		JoeSnake snake = new JoeSnake();
		snake.printGrid();
		snake.runGame();
	}
	
	public void runGame() {
	}
	
	public void keyTyped(KeyEvent event) {
		Direction dir = null;
		
	    if (event.getKeyCode() == KeyEvent.VK_UP) {
	        dir = Direction.UP;
	    }
	    if (event.getKeyCode() == KeyEvent.VK_DOWN) {
	        dir = Direction.DOWN;
	    }
	    if (event.getKeyCode() == KeyEvent.VK_LEFT) {
	        dir = Direction.LEFT;
	    }
	    if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
	        dir = Direction.RIGHT;
	    }
	    
	    System.out.println(dir);
	}
	
	private void printGrid() {
		for (int[] column : grid) {
			for (int row : column) {
				System.out.print(row + " ");
			}
			System.out.println();
		}
	}
}
