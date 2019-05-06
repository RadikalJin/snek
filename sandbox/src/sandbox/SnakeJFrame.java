package sandbox;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class SnakeJFrame extends JFrame implements ActionListener, GameSpeedHandler {

	private final int DEFAULT_DELAY = 180;
	private final int FAST_DELAY = 1;
	int currentDelay = DEFAULT_DELAY;
	
	View view;
	Board board;
	private Timer timer;
	
	public SnakeJFrame() {
		initUI();		
	}
	

	private void initUI() {
		timer = new Timer(currentDelay, this);

		view = new View();
		view.setBoard(board);
		view.setBackground(Color.BLACK);
		add(view);
		
		board = new Board(view, this);

		setResizable(false);
		setTitle("Snake");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(new KeyAdapterAwt(board));
		setFocusable(true);
		setPreferredSize(view.getPreferredSize());
		pack();
		
		timer.start();
	}
	
	public static void main(String[] args) {
		JFrame ex = new SnakeJFrame();
		ex.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		board.actionPerformed();
		repaint();
	}

	@Override
	public void gameStarted() {
		timer.start();
	}
	
	@Override
	public void gamePaused() {
		timer.stop();
	}

	@Override
	public void gameSpeedChanged() {
		if (currentDelay == DEFAULT_DELAY) {
			currentDelay = FAST_DELAY;
		} else if (currentDelay == FAST_DELAY) {
			currentDelay = DEFAULT_DELAY;
		}
		timer.setDelay(currentDelay);
	}

	@Override
	public boolean isFast() {
		return currentDelay == FAST_DELAY;
	}
}