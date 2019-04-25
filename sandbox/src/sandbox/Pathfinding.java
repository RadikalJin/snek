package sandbox;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

		@Override
		List<Direction> find(List<Coordinate> snake, Coordinate foodCoordinate, int width, int height) {
			return new ArrayList<>();
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

		@Override
		List<Direction> find(List<Coordinate> snake, Coordinate foodCoordinate, int width, int height) {
			return super.findPath(snake, foodCoordinate, width, height);
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

		@Override
		List<Direction> find(List<Coordinate> snake, Coordinate foodCoordinate, int width, int height) {
			return super.findPath(snake, foodCoordinate, width, height);
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

		@Override
		List<Direction> find(List<Coordinate> snake, Coordinate foodCoordinate, int width, int height) {
			return super.findPath(snake, foodCoordinate, width, height);
		}	
	};

	abstract List<Direction> find(List<Coordinate> snake, Coordinate foodCoordinate, int width, int height);
	
	private List<Direction> findPath(List<Coordinate> snake, Coordinate foodCoordinate, int width, int height) {
		List<Direction> pathToFollow = new ArrayList<>();
		LinkedList<Node> visited = new LinkedList<Node>();
		
		Node startNode = new Node(new Coordinate(snake.get(0).x, snake.get(0).y));
		Node goalNode = new Node(new Coordinate(foodCoordinate.x, foodCoordinate.y));
		List<Node> search = search(startNode, goalNode, snake, visited, width, height);
		if (search != null) {
			System.out.println("Found!: " + search);	
		} else {
			System.out.println("None found");			
		}
		if (search==null || search.isEmpty()) {
			Optional<Coordinate> firstNeighbour = getNeighbouringCoordinates(snake.get(0).x, snake.get(0).y, width, height).stream().findFirst();
			if (! firstNeighbour.isPresent()) {
				return new ArrayList<>(); // as in, you're going down, because game over
			} else {
				search = Collections.singletonList(new Node(firstNeighbour.get()));
			}
		}
		
		pathToFollow.add(startNode.findDirectionToAdjacentNode(search.get(0)));
		for (int i = 0; i < search.size() - 2; i++) {
			pathToFollow.add(search.get(i).findDirectionToAdjacentNode(search.get(i + 1)));
		}
			
		return pathToFollow;
	}
	
	
	private List<Node> search(Node startNode, Node goalNode, List<Coordinate> snake, LinkedList<Node> visited, int width, int height) {
		  
	  // list of nodes to visit (sorted)
	  LinkedList<Node> toVisit = new LinkedList<>();
	  toVisit.add(startNode);
	  startNode.pathParent = null;
	  
	  while (!toVisit.isEmpty()) {
		  Node node = null;
		  switch (this) {
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
	      for (Node neighborNode : getNodeNeighbors(node, snake, width, height)) {
	    	
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
	
	private List<Node> getNodeNeighbors(Node node, List<Coordinate> snake, int width, int height) {
		Set<Coordinate> neighborCoordinates = getNeighbouringCoordinates(node.coordinates.x, node.coordinates.y, width, height);
		
		switch (this) {
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
						int distanceFromSnakeTail = distanceFromSnakeTail(coordinate, snake);
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
	
	private Set<Coordinate> getNeighbouringCoordinates(int x, int y, int width, int height) {
		Set<Coordinate> neighborCoordinates = new HashSet<Coordinate>();
		  
		//up
		  if (y + 1 < height) {
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
		  if (x + 1 < width) {
			  neighborCoordinates.add(new Coordinate(x + 1, y));
		  }
		
		return neighborCoordinates;
	}
	
	
	private int distanceFromSnakeTail(Coordinate node, List<Coordinate> snake) {
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
	
	
	private List<Node> constructPath(Node node) {
	  LinkedList<Node> path = new LinkedList<>();
	  while (node.pathParent != null) {
	    path.addFirst(node);
	    node = node.pathParent;
	  }
	  return path;
	}
}