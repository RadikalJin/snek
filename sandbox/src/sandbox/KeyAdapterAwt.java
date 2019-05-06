package sandbox;

import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_SPACE;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;

class KeyAdapterAwt extends KeyAdapter {

	Board board;
	public KeyAdapterAwt(Board board) {
		this.board = board;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		switch (key) {
			// Directional input
			case VK_LEFT:
				board.directionPressed(Direction.LEFT);
				break;

			case VK_RIGHT:
				board.directionPressed(Direction.RIGHT);
				break;

			case VK_UP:
				board.directionPressed(Direction.UP);
				break;

			case VK_DOWN:
				board.directionPressed(Direction.DOWN);
				break;

				// Pause
			case VK_SPACE:
				board.togglePause();
				break;
				
			case KeyEvent.VK_C:
				board.toggleColorfulMode();
				break;
				
			case KeyEvent.VK_F:
				board.toggleSpeed();
				break;
				
			default:
				break;
		}
		
		Optional<Pathfinding> pathfinding = Pathfinding.forKey(key);
		if (pathfinding.isPresent()) {
			board.togglePathfinding(pathfinding.get());
		}
	}
}