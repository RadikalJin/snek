package sandbox;

public interface GameSpeedHandler {

	void gameStarted();
	void gamePaused();
	void gameSpeedChanged();
	boolean isFast();
}
