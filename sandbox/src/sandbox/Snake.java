package sandbox;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class Snake extends JFrame {

	public Snake() {
		initUI();
	}

	private void initUI() {
		add(new Board(new View()));
		setResizable(false);
		pack();
		setTitle("Snake");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		JFrame ex = new Snake();
		ex.setVisible(true);
	}

}

@SuppressWarnings("serial")
class Board extends JPanel implements ActionListener {

	private final int WIDTH = 50, 
					  HEIGHT = 50;
	private final int DEFAULT_DELAY = 180;
	private final int FAST_DELAY = 1;
	private int currentDelay = DEFAULT_DELAY;
	private final int STARTING_SNAKE_LENGTH = 4;
	private final int STARTING_COORD = 5;
	private List<Coordinate> snake = new ArrayList<>();
	private GameState gameState = GameState.INIT;
	private Pathfinding pathfinding = Pathfinding.MANUAL;
	private final Random random = new Random();
	private Timer timer;
	private View view;
	private Coordinate foodCoordinate;
	private Direction currentDirection;

	public Board(View view) {
		this.view = view;
		view.init(STARTING_SNAKE_LENGTH, WIDTH, HEIGHT);
		initBoard();
	}

	private void initBoard() {
		addKeyListener(new SnakeKeyInputAdapter());
		setBackground(Color.BLACK);
		setFocusable(true);
		setPreferredSize(view.getPreferredSize());
		initGame();
	}

	private void initGame() {
		currentDirection = Direction.RIGHT;
		pathToFollow = new ArrayList<>();
		
		snake = new ArrayList<Coordinate>();
		for (int i = 0; i <= STARTING_SNAKE_LENGTH; i++) {
			snake.add(new Coordinate(STARTING_COORD - i, STARTING_COORD));
		}
		relocateFood();
		
		timer = new Timer(currentDelay, this);
		timer.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		view.doDrawing(g, gameState, foodCoordinate, snake, getStates());
	}
	
	private List<String> getStates() {
		List<String> states = new ArrayList<>();
		
		if (pathfinding != Pathfinding.MANUAL) {
			states.add("AutoSnek: " + pathfinding.getStateName());			
		}
		
		if (currentDelay == FAST_DELAY) {
			states.add("Fast");
		}
		return states;		
	}

	private Coordinate move() {
		Coordinate head = snake.get(0);
		switch (currentDirection) {
		case LEFT:
			head = new Coordinate(head.x - 1, head.y);
			break;
			
		case RIGHT:
			head = new Coordinate(head.x + 1, head.y);
			break;
			
		case UP:
			head = new Coordinate(head.x, head.y - 1);
			break;
			
		case DOWN:
			head = new Coordinate(head.x, head.y + 1);
			break;
		}
		
		for (int i = snake.size() -1; i < 0; i--) {
			//if head
			snake.set(i, snake.get(i -1));			
		}
		
		snake.add(0, head);
		
		// return last
		return snake.remove(snake.size() - 1);
	}

	private void relocateFood() {
		foodCoordinate = new Coordinate(
				random.nextInt(WIDTH),
				random.nextInt(HEIGHT));
		if (snake.contains(foodCoordinate)) {
			relocateFood();
		}
	}

	List<Direction> pathToFollow = new ArrayList<>();

	@Override
	public void actionPerformed(ActionEvent e) {
		
		switch(pathfinding) {
			case BFS:
			case BFS_MANHATTAN:
			case DFS:
				if (pathToFollow.isEmpty()) {
					pathToFollow = pathfinding.find(snake, foodCoordinate, WIDTH, HEIGHT);
				} 
				if (!pathToFollow.isEmpty()) {
					currentDirection = pathToFollow.remove(0);					
				}
			default:
				break;
		}
		
		switch (gameState) {
			case LIVE:
				checkFood(snake, foodCoordinate, move());
				checkCollision(snake);
				break;
			case PAUSED:
			default:
				break;
		}

		repaint();
	}
	
	
	private void checkFood(List<Coordinate> snake, Coordinate foodCoordinate, Coordinate last) {
		if (snake.get(0).equals(foodCoordinate)) {
			snake.add(last);
			view.foodEaten();
			
			relocateFood();
		}
	}
	
	
	private void checkCollision(List<Coordinate> snake) {

		Coordinate snakeHead = null;
		for (Coordinate snakeSegment : snake) {
			if (snakeHead == null) {
				snakeHead = snake.get(0);

			} else {
				// if head overlaps any of body
				if (snakeHead.x == snakeSegment.x && snakeHead.y == snakeSegment.y) {
					gameState = GameState.FAIL;
				}
			}
		}

		// if outside of grid
		if (snake.get(0).y >= HEIGHT || snake.get(0).y < 0 || snake.get(0).x >= WIDTH
				|| snake.get(0).x < 0) {
			gameState = GameState.FAIL;
		}

		if (gameState == GameState.PAUSED || gameState == GameState.FAIL) {
			timer.stop();
		}
	}
	

	private class SnakeKeyInputAdapter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();

			switch (key) {
				// Directional input
			case VK_LEFT:
				setCurrentDirectionIfNotAlready(Direction.LEFT);
				break;

			case VK_RIGHT:
				setCurrentDirectionIfNotAlready(Direction.RIGHT);
				break;

			case VK_UP:
				setCurrentDirectionIfNotAlready(Direction.UP);
				break;

			case VK_DOWN:
				setCurrentDirectionIfNotAlready(Direction.DOWN);
				break;

				// Pause
			case VK_SPACE:
				switch (gameState) {
				case LIVE:
					gameState = GameState.PAUSED;
					break;

				case PAUSED:
					gameState = GameState.LIVE;
					break;
					
				default:
					initGame();
					gameState = GameState.LIVE;
					break;
				}
				break;
				
				// BFS
			case KeyEvent.VK_B:
				togglePathfinding(Pathfinding.BFS);
				break;
				
				// Manhattan distances (ignore tail segments that will be gone by time we reach them)
			case KeyEvent.VK_M:
				togglePathfinding(Pathfinding.BFS_MANHATTAN);
				break;

				// DFS
			case KeyEvent.VK_D:
				togglePathfinding(Pathfinding.DFS);
				break;
				
			case KeyEvent.VK_C:
				view.toggleColourfulMode();
				break;
				
			case KeyEvent.VK_F:
				toggleSpeed();
				break;
				
			default:
				break;
			}
		}
	}
	
	private void setCurrentDirectionIfNotAlready(Direction direction) {
		if (direction != currentDirection.opposite()) {
			currentDirection = direction;
		}
	}
	
	private void togglePathfinding(Pathfinding selected) {
		if (pathfinding == selected) {
			pathfinding = Pathfinding.MANUAL;					
		} else {
			pathfinding = selected;
		}
	}
	
	private void toggleSpeed() {
		if (currentDelay == DEFAULT_DELAY) {
			currentDelay = FAST_DELAY;
		} else if (currentDelay == FAST_DELAY) {
			currentDelay = DEFAULT_DELAY;
		}
		timer.setDelay(currentDelay);
	}
}

