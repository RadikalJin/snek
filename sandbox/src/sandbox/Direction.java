package sandbox;

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