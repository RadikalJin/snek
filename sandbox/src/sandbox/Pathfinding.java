package sandbox;

import java.awt.event.KeyEvent;

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
