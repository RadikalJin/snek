package sandbox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class View extends JPanel {

	private Dimension boardDimensions;
	private final int DOT_SIZE = 10;
	private final Color headColour = Color.WHITE, 
			bodyColour = Color.GRAY,
			defaultFoodColour = Color.GREEN;
	private Color currentFoodColour = defaultFoodColour;
	private List<Color> snakeColors = new ArrayList<>();
	private boolean colourfulMode = false;
	
	private Board board;
	
	void setBoard(Board board) {
		this.board = board;
	}
	
	public void init(int startingSnakeLength, int width, int height) {
		boardDimensions = new Dimension(width * DOT_SIZE, height * DOT_SIZE);
		snakeColors = new ArrayList<Color>();
		for (int i = 0; i <= startingSnakeLength; i++) {
			snakeColors.add(randomColour());				
		}
	}
	
	public Dimension getPreferredSize() {
		return boardDimensions;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// TODO
		doDrawing(g, board.getGameState(), board.getFoodCoordinate(), board.getSnake(), board.getStates());
	}
	
	public void doDrawing(
			Graphics g, 
			GameState gameState, 
			Coordinate foodCoordinate,
			List<Coordinate> snake,
			List<String> states
	) {
		if (gameState == GameState.INIT) {
			showMessage(g, "Welcome to Snek! Press Space to play");
		} else {
			g.setColor(colourfulMode ? currentFoodColour : defaultFoodColour);				
			g.fillRect(foodCoordinate.x * DOT_SIZE, foodCoordinate.y * DOT_SIZE, DOT_SIZE, DOT_SIZE);

			drawSnake(g, snake);
			
			showStates(g, states);
			
			if (gameState == GameState.FAIL) {
				showMessage(g, "Game Over, Press Space to restart");
			} else if (gameState == GameState.PAUSED) {
				showMessage(g, "Paused");
				showStates(g, states);
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
		g.drawString(msg, 
				(boardDimensions.width - g.getFontMetrics().stringWidth(msg)) / 2, 
				boardDimensions.height / 3);
	}
	
	private void showOptionsMessage(Graphics g) {
		Font small = new Font("Helvetica", Font.BOLD, 14);
		g.setColor(Color.white);
		g.setFont(small);
		List<String> options = new ArrayList<>();
		options.add("Options:");
		options.add("F: Fast");
		options.add("C: Colourful mode");
		
		Arrays.asList(Pathfinding.values())
			.stream()
			.filter(e -> e != Pathfinding.MANUAL)
			.map(e -> KeyEvent.getKeyText(e.matchingKey()) + ": " + e.getFullDescription())
			.sequential()
			.collect(Collectors.toCollection(() -> options));
		
		for (int i = 0; i < options.size(); i++) {
			g.drawString(
					options.get(i), 
					0, 
					boardDimensions.height - (g.getFontMetrics().getHeight() * (options.size() - i)));			
		}
	}
	
	private void showStates(Graphics g, List<String> states) {
		g.setColor(Color.white);
		g.setFont(new Font("Helvetica", Font.BOLD, 14));
		
		if (colourfulMode && !states.contains("Colourful")) {
			states.add("Colourful");
		}
		
		for (int i = 0; i < states.size(); i++) {
			g.drawString(states.get(i), 0, DOT_SIZE * (i + 1));			
		}
	}
	
	public void foodEaten() {
		snakeColors.add(currentFoodColour);
		currentFoodColour = randomColour();
	}
	
	private Color randomColour() {
		return new Color((int)(Math.random() * 0x1000000));
	}
	
	public void toggleColourfulMode() {
		colourfulMode = !colourfulMode;
	}
}
