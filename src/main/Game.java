package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Timer;

public class Game {

	public static Gui window;
	public static int height = 600;
	public static int targetFps = 60;
	
	public static boolean renderField = false;
	public static Field mineField;
	
	public static int difficulty = 5; //1-9
	
	public static int gameState;
	public static final int paused = 0;
	public static final int gameOn = 1;
	public static final int gameOver = 2;
	
	public static Font defaultFont = new Font("Arial", Font.BOLD, 20);
	public static Color fontColor = Color.BLACK;
	
	public static String gameMessage = "";
	
	public static void main(String[] args) {
		window = new Gui("Minesweeper With AI By Uranium", (int) (height * 1.7), height);
		newGame();
	}
	
	public static void newGame() {
		gameState = paused;
		mineField = new Field(32, 16);
		mineField.addMines(difficulty * 2);
		renderField = true;
		
		updateGame.setDelay(1000 / targetFps);
		updateGame.start();
		gameState = gameOn;
		gameMessage = "";
	}
	
	//GAME LOOP
	static Timer updateGame = new Timer(0, new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
	    	window.repaint();
	    	
	    	if(mineField.gameWon() && gameState == gameOn) {
	    		gameState = gameOver;
	    		gameMessage = "You Won!";
	    	}
	    	
	    	//CONTROLS
	    	checkKeyboard();
	    	checkMouse();
	    }
	});
	
	public static int[] searchTileByCoordinate(int locX, int locY) {
		for(int y = 0; y <  mineField.height; y++) {
			for(int x = 0; x <  mineField.width; x++) {
    			if(locX > x * mineField.tileSize && locX < x * mineField.tileSize + mineField.tileSize && locY > y * mineField.tileSize && locY < y * mineField.tileSize + mineField.tileSize) {
    				return new int[]{x,y};
    			}
    		}
		}
		return null;
	}
	
	public static Graphics2D draw(Graphics2D g2d) {
		g2d.setColor(new Color(189, 189, 189));
		g2d.fillRect(0, 0, (int) (height * 1.7), height);
		
		//FIELD
		if(renderField) {
			g2d = mineField.draw(g2d);
		}
		
		//INFO
		g2d.setFont(defaultFont);
		g2d.setColor(fontColor);
		g2d.drawString("Difficulty:" + difficulty + " [8] Decrease [9] Increase", 10, height - 80);
		g2d.drawString("[1] New Game [2] Let AI Help" + ((gameMessage != "") ? " | " + gameMessage : "" ), 10, height - 50);
		
		return g2d;
	}
	
	public static void checkMouse() {
		//IF GAME IS OVER RESETS CONTROLS
		if(gameState == gameOver) {
			window.leftClicked = false;
			window.rightClicked = false;
		}
		
		if(window.leftClicked) {
    		window.leftClicked = false;
    		int[] tileLoc = searchTileByCoordinate(window.lastMouseClickLocX, window.lastMouseClickLocY);
    		if(tileLoc != null) {
    			
    			if(mineField.field[tileLoc[0]][tileLoc[1]].marked) {
    				//IF MARKED REMOVES MARK
    				mineField.field[tileLoc[0]][tileLoc[1]].marked = false;
    			} else if(mineField.field[tileLoc[0]][tileLoc[1]].hasMine) {
    				//IF CLICKS MINE
        			gameState = gameOver;
        			mineField.showAllMines();
        			gameMessage = "You lost!";
    			} else {
    				mineField.exposeEmptyArea(tileLoc[0], tileLoc[1]);
    				mineField.hideProbabilities();
    				gameMessage = "";
    			}
    		}
    	} else if(window.rightClicked) {
    		window.rightClicked = false;
    		int[] tileLoc = searchTileByCoordinate(window.lastMouseClickLocX, window.lastMouseClickLocY);
    		if(tileLoc != null) {
    			if(!(mineField.field[tileLoc[0]][tileLoc[1]].discovered)) {
    				mineField.field[tileLoc[0]][tileLoc[1]].marked = true;
    			}
    		}
    	}
	}
	
	public static void checkKeyboard() {
		if(window.keyPressed[KeyEvent.VK_1]) {
			window.keyPressed[KeyEvent.VK_1] = false;
			newGame();
		} else if(window.keyPressed[KeyEvent.VK_2]) {
			window.keyPressed[KeyEvent.VK_2] = false;
			if(gameState == gameOn) {
				mineField.aiPlay(1);
			}	
		} else if(window.keyPressed[KeyEvent.VK_8]) {
			window.keyPressed[KeyEvent.VK_8] = false;
			if(difficulty > 1) {
				difficulty--;
			}
		} else if(window.keyPressed[KeyEvent.VK_9]) {
			window.keyPressed[KeyEvent.VK_9] = false;
			if(difficulty < 9) {
				difficulty++;
			}
		}
	}
	
}
