package sandbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Board {

	private final int WIDTH = 50, 
					  HEIGHT = 50;
	
	private final int STARTING_SNAKE_LENGTH = 4;
	private final int STARTING_COORD = 5;
	private List<Coordinate> snake = new ArrayList<>();
	private GameState gameState = GameState.INIT;
	private Pathfinding pathfinding = Pathfinding.MANUAL;
	private final Random random = new Random();
	private View view;
	private GameSpeedHandler speedHandler;
	private Coordinate foodCoordinate;
	private Direction currentDirection;

	public Board(View view, GameSpeedHandler speedHandler) {
		this.view = view;
		this.speedHandler = speedHandler;
		view.init(STARTING_SNAKE_LENGTH, WIDTH, HEIGHT);
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
		speedHandler.gameStarted();
	}

	public GameState getGameState() {
		return gameState;
	}
	
	public List<String> getStates() {
		List<String> states = new ArrayList<>();
		
		if (pathfinding != Pathfinding.MANUAL) {
			states.add("AutoSnek: " + pathfinding.getStateName());			
		}
		
		if (speedHandler.isFast()) {
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

	
	public void actionPerformed() {
				
		switch (gameState) {
			case LIVE:
				switch(pathfinding) {
				case MANUAL:
					break;
				default:
					if (pathToFollow.isEmpty()) {
						pathToFollow = pathfinding.find(snake, foodCoordinate, WIDTH, HEIGHT);
					} 
					if (!pathToFollow.isEmpty()) {
						currentDirection = pathToFollow.remove(0);					
					}
					break;
				}
				checkFood(snake, foodCoordinate, move());
				checkCollision(snake);
				break;
			case PAUSED:
			default:
				break;
		}
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
			speedHandler.gamePaused();
		}
	}
	
	
	public void directionPressed(Direction direction) {
		setCurrentDirectionIfNotAlready(Direction.DOWN);
	}
	
	private void setCurrentDirectionIfNotAlready(Direction direction) {
		if (direction != currentDirection.opposite()) {
			currentDirection = direction;
		}
	}
	
	public void togglePathfinding(Pathfinding selected) {
		if (pathfinding == selected) {
			pathfinding = Pathfinding.MANUAL;					
		} else {
			pathfinding = selected;
		}
	}
	
	public void toggleSpeed() {
		speedHandler.gameSpeedChanged();
	}
	
	public void toggleColorfulMode() {
		view.toggleColourfulMode();
	}


	public void togglePause() {
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
	}


	public Coordinate getFoodCoordinate() {
		return foodCoordinate;
	}


	public List<Coordinate> getSnake() {
		return snake;
	}
}

