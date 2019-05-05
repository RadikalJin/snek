package sandbox;

public class Node {
	  Coordinate coordinates;	
	  Node pathParent;

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