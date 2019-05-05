package sandbox;

public class Coordinate {

	int x;
	int y;
	public Coordinate(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public Direction findDirectionToAdjacentCoordinate(Coordinate target) {
		  if (target.x > this.x) {
				return Direction.RIGHT;			
			} else if (target.x < this.x) {
				return Direction.LEFT;
			} else if (target.y > this.y) {
				return Direction.DOWN;
			} else if (target.y < this.y) {
				return Direction.UP;
			}
		  return Direction.DOWN;
	}
	
	public Coordinate getCoordinateInDirection(Direction direction) {
		switch (direction) {
			case RIGHT:			
				return new Coordinate(this.x + 1, this.y);
			case LEFT:			
				return new Coordinate(this.x - 1, this.y);
			case UP:			
				return new Coordinate(this.x, this.y - 1);
			case DOWN:			
				return new Coordinate(this.x, this.y + 1);
		}
		return this;					
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Coordinate other = (Coordinate) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
}
