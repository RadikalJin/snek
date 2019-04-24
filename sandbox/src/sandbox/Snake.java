package sandbox;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class Snake extends JFrame {

	public Snake() {
		initUI();
	}

	private void initUI() {
		add(new Board());
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

	private final int DOT_SIZE = 10;
	private final int WIDTH = 50, 
					  HEIGHT = 50;
	private final int DEFAULT_DELAY = 180;
	private final int FAST_DELAY = 1;
	private int currentDelay = DEFAULT_DELAY;
	private final int STARTING_SNAKE_LENGTH = 4;
	private final int STARTING_COORD = 5;
	private List<Coordinate> snake = new ArrayList<>();
	private boolean colourfulMode = false;
	private List<Color> snakeColors = new ArrayList<>();
	private GameState gameState = GameState.INIT;
	private Pathfinding pathfinding = Pathfinding.MANUAL;
	private final Random random = new Random();
	private Timer timer;
	private final Color headColour = Color.WHITE, 
			bodyColour = Color.GRAY,
			defaultFoodColour = Color.GREEN;
	private Color currentFoodColour = defaultFoodColour;
	private Coordinate foodCoordinate;
	private Direction currentDirection;

	public Board() {
		initBoard();
	}

	private void initBoard() {
		addKeyListener(new SnakeKeyInputAdapter());
		setBackground(Color.BLACK);
		setFocusable(true);
		setPreferredSize(new Dimension(WIDTH * DOT_SIZE, HEIGHT * DOT_SIZE));
		initGame();
	}

	private void initGame() {
		currentDirection = Direction.RIGHT;
		snake = new ArrayList<Coordinate>();
		snakeColors = new ArrayList<Color>();
		for (int i = 0; i <= STARTING_SNAKE_LENGTH; i++) {
			snake.add(new Coordinate(STARTING_COORD - i, STARTING_COORD));
			snakeColors.add(randomColour());				
		}

		relocateApple();

		timer = new Timer(currentDelay, this);
		timer.start();
	}
	
	private Color randomColour() {
		return new Color((int)(Math.random() * 0x1000000));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g, gameState, pathfinding, colourfulMode);
	}

	private void doDrawing(Graphics g, GameState gameState, Pathfinding pathfinding, boolean colourfulMode) {
		if (gameState == GameState.INIT) {
			showMessage(g, "Welcome to Snek! Press Space to play");
		} else {
			g.setColor(colourfulMode ? currentFoodColour : defaultFoodColour);				
			g.fillRect(foodCoordinate.x * DOT_SIZE, foodCoordinate.y * DOT_SIZE, DOT_SIZE, DOT_SIZE);

			drawSnake(g, snake);
			
			showStates(g, pathfinding);
			
			if (gameState == GameState.FAIL) {
				showMessage(g, "Game Over, Press Space to restart");
			} else if (gameState == GameState.PAUSED) {
				showMessage(g, "Paused");
				showStates(g, pathfinding);
				showOptionsMessage(g);
			}
			
			Toolkit.getDefaultToolkit().sync();
		}
	}
	
	private void drawSnake(Graphics g, List<Coordinate> snake) {
		for (int i = snake.size() - 1; i >= 0; i--) {
			//head
			if (i == 0) {
				g.setColor(headColour);
			//body
			} else {
				g.setColor(colourfulMode ? snakeColors.get(i) : bodyColour);						
			}
			g.fillRect(snake.get(i).x * DOT_SIZE, snake.get(i).y * DOT_SIZE, DOT_SIZE, DOT_SIZE);
		}
	}

	private void showMessage(Graphics g, String msg) {
		Font small = new Font("Helvetica", Font.BOLD, 14);
		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(msg, (WIDTH * DOT_SIZE - getFontMetrics(small).stringWidth(msg)) / 2, HEIGHT * DOT_SIZE / 3);
	}
	
	private void showOptionsMessage(Graphics g) {
		Font small = new Font("Helvetica", Font.BOLD, 14);
		g.setColor(Color.white);
		g.setFont(small);
		List<String> options = new ArrayList<>();
		options.add("Options:");
		options.add("F: Fast");
		options.add("C: Colourful mode");
		options.addAll(
				Arrays.asList(Pathfinding.values())
					.stream()
					.filter(e -> e != Pathfinding.MANUAL)
					.map(e -> KeyEvent.getKeyText(e.matchingKey()) + ": " + e.getFullDescription())
					.collect(Collectors.toList()));
		for (int i = 0; i < options.size(); i++) {
			g.drawString(
					options.get(i), 
					0, 
					HEIGHT * DOT_SIZE - (getFontMetrics(small).getHeight() * (options.size() - i)));			
		}
	}
	
	private void showStates(Graphics g, Pathfinding pathfinding) {
		g.setColor(Color.white);
		g.setFont(new Font("Helvetica", Font.BOLD, 14));
		String pathFindingMode = "";
		
		if (pathfinding != Pathfinding.MANUAL) {
			pathFindingMode = "AutoSnek: " + pathfinding.getStateName();			
		}

		List<String> states = new ArrayList<>();
		states.add(pathFindingMode);
		
		if (currentDelay == FAST_DELAY) {
			states.add("Fast");
		}
		
		if (colourfulMode) {
			states.add("Colourful");
		}		
		
		for (int i = 0; i < states.size(); i++) {
			g.drawString(states.get(i), 0, DOT_SIZE * (i + 1));			
		}
	}

	private void checkApple(Coordinate last) {
		if (snake.get(0).equals(foodCoordinate)) {
			snake.add(last);
			snakeColors.add(currentFoodColour);
			
			relocateApple();
		}
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

	private void checkCollision() {

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

	private void relocateApple() {
		currentFoodColour = randomColour();
		foodCoordinate = new Coordinate(
				random.nextInt(WIDTH),
				random.nextInt(HEIGHT));
		if (snake.contains(foodCoordinate)) {
			relocateApple();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		switch(pathfinding) {
			case BFS:
			case BFS_MANHATTAN:
			case DFS:
				if (pathToFollow.isEmpty()) {
					currentDirection = findNextAutoDirection(pathfinding);
				} else {
					currentDirection = pathToFollow.remove(0);					
				}
			default:
				break;
		}
		
		switch (gameState) {
			case LIVE:
				checkApple(move());
				checkCollision();
				break;
			case PAUSED:
			default:
				break;
		}

		repaint();
	}
	
	List<Direction> pathToFollow = new ArrayList<>();

	private Direction findNextAutoDirection(Pathfinding pathfinding) {
		visited = new LinkedList<Node>();
		
		Node startNode = new Node(new Coordinate(snake.get(0).x, snake.get(0).y));
		Node goalNode = new Node(new Coordinate(foodCoordinate.x, foodCoordinate.y));
		List<Node> search = search(startNode, goalNode);
		if (search != null) {
			System.out.println("Found!: " + search);	
		} else {
			System.out.println("None found");			
		}
		if (search==null || search.isEmpty()) {
			Optional<Coordinate> firstNeighbour = getNeighbouringCoordinates(snake.get(0).x, snake.get(0).y).stream().findFirst();
			if (! firstNeighbour.isPresent()) {
				return Direction.DOWN; // as in, you're going down, because game over
			} else {
				search = Collections.singletonList(new Node(firstNeighbour.get()));
			}
		}
		
		for (int i = 0; i < search.size() - 2; i++) {
			pathToFollow.add(search.get(i).findDirectionToAdjacentNode(search.get(i + 1)));
		}
			
		return startNode.findDirectionToAdjacentNode(search.get(0));
	}
	
	
	// list of visited nodes
  	LinkedList<Node> visited = new LinkedList<>();
	
	public List<Node> search(Node startNode, Node goalNode) {
		  
	  // list of nodes to visit (sorted)
	  LinkedList<Node> toVisit = new LinkedList<>();
	  toVisit.add(startNode);
	  startNode.pathParent = null;
	  
	  while (!toVisit.isEmpty()) {
		  Node node = null;
		  switch (pathfinding) {
		  	case DFS:
		  		node = (Node)toVisit.removeLast();
		  		break;
		  	case BFS: 
		  	case BFS_MANHATTAN: 
		  	default:
		  		node = (Node)toVisit.removeFirst();
		  		break;
		  }
	    if (node.equals(goalNode)) {
	      // path found!
	      return constructPath(node);
	      
	    } else {
	      visited.add(node);
	      
	      // add neighbours to the "to visit" list
	      for (Node neighborNode : getNodeNeighbors(node)) {
	    	
	        if (!visited.contains(neighborNode) && 
	        	!toVisit.contains(neighborNode)
	        ) {
	          neighborNode.pathParent = node;
	          toVisit.add(neighborNode);
	        }
	      }
	    }
	  }
	  
	  // no path found
	  return null;
	}
	
	public List<Node> getNodeNeighbors(Node node) {
		Set<Coordinate> neighborCoordinates = getNeighbouringCoordinates(node.coordinates.x, node.coordinates.y);
		
		switch (pathfinding) {
			case BFS:
			case DFS:
				neighborCoordinates.removeAll(snake);
				break;
			case BFS_MANHATTAN:
			default:
				Iterator<Coordinate> iterator = neighborCoordinates.iterator();
				while (iterator.hasNext()) {
					Coordinate coordinate = iterator.next();
					if (snake.contains(coordinate)) {
						int distanceFromSnakeTail = distanceFromSnakeTail(coordinate);
						int manhattanDistance = manhattanDistance(snake.get(0), coordinate);
						if (manhattanDistance < distanceFromSnakeTail) {
							iterator.remove();
						}			  				  
					}
				}			  
				break;
		}
		  
		List<Node> neighbours = new ArrayList<Node>();
		for (Coordinate coordinate : neighborCoordinates) {
			Node parent = new Node(coordinate);
			parent.pathParent = node;
			neighbours.add(parent);				  			  
		}
		  
		return neighbours;
	}
	
	private Set<Coordinate> getNeighbouringCoordinates(int x, int y) {
		Set<Coordinate> neighborCoordinates = new HashSet<Coordinate>();
		  
		//up
		  if (y + 1 < HEIGHT) {
			  neighborCoordinates.add(new Coordinate(x, y + 1));
		  }
		//down
		  if (y - 1 >= 0) {
			  neighborCoordinates.add(new Coordinate(x, y - 1));
		  }
		//left
		  if (x - 1 >= 0) {
			  neighborCoordinates.add(new Coordinate(x - 1, y));
		  }
		//right
		  if (x + 1 < WIDTH) {
			  neighborCoordinates.add(new Coordinate(x + 1, y));
		  }
		
		return neighborCoordinates;
	}
	
	
	private int distanceFromSnakeTail(Coordinate node) {
		if (snake.contains(node)) {
			return snake.size() - snake.indexOf(node); 
		} else {
			return 999;
		}
	}
	
	
	private int manhattanDistance(Coordinate first, Coordinate second) {
		return Math.abs(second.x - first.x) 
				+ Math.abs(second.y - first.y);
	}
	
	
	protected List<Node> constructPath(Node node) {
	  LinkedList<Node> path = new LinkedList<>();
	  while (node.pathParent != null) {
	    path.addFirst(node);
	    node = node.pathParent;
	  }
	  return path;
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
				colourfulMode = !colourfulMode;
				break;
				
			case KeyEvent.VK_F:
				toggleSpeed();
				break;
				
			default:
				break;
			}
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

	private void setCurrentDirectionIfNotAlready(Direction direction) {
		if (direction != currentDirection.opposite()) {
			currentDirection = direction;
		}
	}
}

enum GameState {
	LIVE, PAUSED, FAIL, INIT
}

enum Pathfinding implements Option {
	MANUAL {
		@Override
		public String getFullDescription() {
			return "Manual";
		}

		@Override
		public int matchingKey() {
			return 0;
		}

		@Override
		public String getStateName() {
			return "Manual";
		}		
	}, 
	BFS {
		@Override
		public String getFullDescription() {
			return "Breadth First Search";
		}

		@Override
		public int matchingKey() {
			return KeyEvent.VK_B;
		}

		@Override
		public String getStateName() {
			return "BFS";
		}
	}, 
	BFS_MANHATTAN {
		@Override
		public String getFullDescription() {
			return "Breadth First Search, with Manhattan distances, ignoring tail";
		}

		@Override
		public int matchingKey() {
			return KeyEvent.VK_M;
		}

		@Override
		public String getStateName() {
			return "BFS with Manhattan";
		}
	},
	DFS {

		@Override
		public String getFullDescription() {
			return "Depth First Search";
		}

		@Override
		public int matchingKey() {
			return KeyEvent.VK_D;
		}

		@Override
		public String getStateName() {
			return "DFS";
		}	
	};

}

interface Option {
	String getStateName();
	String getFullDescription();
	int matchingKey();
}

enum Direction {
	LEFT {
		@Override
		Direction opposite() {
			return RIGHT;
		}
	},

	RIGHT {
		@Override
		Direction opposite() {
			return LEFT;
		}
	},

	UP {
		@Override
		Direction opposite() {
			return DOWN;
		}
	},

	DOWN {
		@Override
		Direction opposite() {
			return UP;
		}
	};

	abstract Direction opposite();
}
