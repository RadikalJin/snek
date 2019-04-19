package sandbox;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
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

class Board extends JPanel implements ActionListener {

	private final int DOT_SIZE = 10;
	private final int WIDTH = 50 * DOT_SIZE, 
					  HEIGHT = 50 * DOT_SIZE;
	private final int DEFAULT_DELAY = 180;
	private final int FAST_DELAY = 1;
	private int currentDelay = DEFAULT_DELAY;
	private final int STARTING_SNAKE_LENGTH = 4;
	private final int STARTING_COORD = 5 * DOT_SIZE;
	private List<Coordinate> snake = new ArrayList<>();
	private boolean maintainColours = false;
	private List<Color> snakeColors = new ArrayList<>();
	private GameState gameState = GameState.INIT;
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
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		initGame();
	}

	private void initGame() {
		currentDirection = Direction.RIGHT;
		snake = new ArrayList<Coordinate>();
		snakeColors = new ArrayList<Color>();
		for (int i = 0; i <= STARTING_SNAKE_LENGTH; i++) {
			snake.add(new Coordinate(STARTING_COORD - i * DOT_SIZE, STARTING_COORD));
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
		doDrawing(g);
	}

	private void doDrawing(Graphics g) {
		if (gameState == GameState.INIT) {
			showMessage(g, "Welcome to Snek! Press Space to play");
		} else {
			g.setColor(maintainColours ? currentFoodColour : defaultFoodColour);				
			g.fillRect(foodCoordinate.x, foodCoordinate.y, DOT_SIZE, DOT_SIZE);

			for (int i = snake.size() - 1; i >= 0; i--) {
				//head
				if (i == 0) {
					g.setColor(headColour);
				//body
				} else {
					g.setColor(maintainColours ? snakeColors.get(i) : bodyColour);						
				}
				g.fillRect(snake.get(i).x, snake.get(i).y, DOT_SIZE, DOT_SIZE);
			}

			if (gameState == GameState.BFS) {
				showType(g, "AutoSnek: BFS");
			} else if (gameState == GameState.BFS_MANHATTAN) {
				showType(g, "AutoSnek: BFS with Manhattan");
			} else if (gameState == GameState.FAIL) {
				showMessage(g, "Game Over, Press Space to restart");
			} else if (gameState == GameState.PAUSED) {
				showMessage(g, "Paused");
			}
			
			Toolkit.getDefaultToolkit().sync();
		}
	}

	private void showMessage(Graphics g, String msg) {
		Font small = new Font("Helvetica", Font.BOLD, 14);
		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(msg, (WIDTH - getFontMetrics(small).stringWidth(msg)) / 2, HEIGHT / 3);
	}
	
	private void showType(Graphics g, String msg) {
		g.setColor(Color.white);
		g.setFont(new Font("Helvetica", Font.BOLD, 14));
		g.drawString(msg, 0, DOT_SIZE);
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
			head = new Coordinate(head.x - DOT_SIZE, head.y);
			break;
			
		case RIGHT:
			head = new Coordinate(head.x + DOT_SIZE, head.y);
			break;
			
		case UP:
			head = new Coordinate(head.x, head.y - DOT_SIZE);
			break;
			
		case DOWN:
			head = new Coordinate(head.x, head.y + DOT_SIZE);
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
				random.nextInt(WIDTH / DOT_SIZE) * DOT_SIZE,
				random.nextInt(HEIGHT / DOT_SIZE) * DOT_SIZE);
		if (snake.contains(foodCoordinate)) {
			relocateApple();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (gameState) {
			case LIVE:
				checkApple(move());
				checkCollision();
				break;
			case BFS:
			case BFS_MANHATTAN:
				if (pathToFollow.isEmpty()) {
					currentDirection = findNextAutoDirection();
				} else {
					currentDirection = pathToFollow.remove(0);					
				}
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

	private Direction findNextAutoDirection() {
		visited = new LinkedList<Board.Node>();
		
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
	    Node node = (Node)toVisit.removeFirst();
	    if (node.equals(goalNode)) {
	      // path found!
	      return constructPath(node);
	      
	    } else {
	      visited.add(node);
	      
	      // add neighbours to the "to visit" list
	      for (Node neighborNode : node.getNodeNeighbors()) {
	    	
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
	
	public class Node {
	  Coordinate coordinates;	
	  Node pathParent;
	  
	  public List<Node> getNodeNeighbors() {
		  Set<Coordinate> neighborCoordinates = getNeighbouringCoordinates(coordinates.x, coordinates.y);
		  
		  List<Node> neighbours = new ArrayList<Node>();
		  for (Coordinate coordinate : neighborCoordinates) {
			  Node node = new Node(coordinate);
			  node.pathParent = this;
			  neighbours.add(node);				  			  
		  }
		  
		  return neighbours;
	  }
	  
	  public Direction findDirectionToAdjacentNode(Node target) {
		  if (target.coordinates.x > this.coordinates.x) {
				return Direction.RIGHT;			
			} else if (target.coordinates.x < this.coordinates.x) {
				return Direction.LEFT;
			} else if (target.coordinates.y > this.coordinates.y) {
				return Direction.DOWN;
			} else if (target.coordinates.y < this.coordinates.y) {
				return Direction.UP;
			}
		  return Direction.DOWN;
	  }

		public Node(Coordinate coordinates) {
			super();
			this.coordinates = coordinates;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (coordinates == null) {
				if (other.coordinates != null)
					return false;
			} else if (!coordinates.equals(other.coordinates))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return coordinates.x + " " + coordinates.y;
		}
	}
	
	private Set<Coordinate> getNeighbouringCoordinates(int x, int y) {
		Set<Coordinate> neighborCoordinates = new HashSet<Coordinate>();
		  
		//up
		  if (y + DOT_SIZE < HEIGHT) {
			  neighborCoordinates.add(new Coordinate(x, y + DOT_SIZE));
		  }
		//down
		  if (y - DOT_SIZE >= 0) {
			  neighborCoordinates.add(new Coordinate(x, y - DOT_SIZE));
		  }
		//left
		  if (x - DOT_SIZE >= 0) {
			  neighborCoordinates.add(new Coordinate(x - DOT_SIZE, y));
		  }
		//right
		  if (x + DOT_SIZE < WIDTH) {
			  neighborCoordinates.add(new Coordinate(x + DOT_SIZE, y));
		  }
		  
		  if (gameState == GameState.BFS) {
			  neighborCoordinates.removeAll(snake);
		  } else if (gameState == GameState.BFS_MANHATTAN) {
			  Iterator<Coordinate> iterator = neighborCoordinates.iterator();
			  while (iterator.hasNext()) {
				  Coordinate coordinate = iterator.next();
				  if (snake.contains(coordinate)) {
					  int distanceFromSnakeTail = distanceFromSnakeTail(coordinate) * DOT_SIZE;
					  int manhattanDistance = manhattanDistance(snake.get(0), coordinate);
					  if (manhattanDistance < distanceFromSnakeTail) {
						  iterator.remove();
					  }			  				  
				  }
			  }			  
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
				case BFS:
				case BFS_MANHATTAN:
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
				
				// Auto mode!
			case KeyEvent.VK_A:
				if (gameState == GameState.LIVE) {
					gameState = GameState.BFS;					
				} else if (gameState == GameState.BFS_MANHATTAN) {
						gameState = GameState.BFS;
				} else if (gameState == GameState.FAIL) {
					initGame();
					gameState = GameState.BFS;
				} else {
					gameState = GameState.LIVE;
				}
				break;
				
				// Auto mode!
			case KeyEvent.VK_B:
				if (gameState == GameState.LIVE) {
					gameState = GameState.BFS_MANHATTAN;					
				} else if (gameState == GameState.BFS) {
					gameState = GameState.BFS_MANHATTAN;	
				} else if (gameState == GameState.FAIL) {
					initGame();
					gameState = GameState.BFS_MANHATTAN;
				} else {
					gameState = GameState.LIVE;
				}
				break;
				
			case KeyEvent.VK_M:
				maintainColours = !maintainColours;
				break;
				
			case KeyEvent.VK_F:
				if (currentDelay == DEFAULT_DELAY) {
					currentDelay = FAST_DELAY;
				} else if (currentDelay == FAST_DELAY) {
					currentDelay = DEFAULT_DELAY;
				}
				timer.setDelay(currentDelay);
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

}

enum GameState {
	LIVE, PAUSED, FAIL, BFS, INIT, BFS_MANHATTAN
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
