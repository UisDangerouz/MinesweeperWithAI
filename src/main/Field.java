package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

public class Field {

	public int width;
	public int height;
	
	public Tile[][] field;
	
	public int tileSize = 30;
	public int marginSize = 4;
	public int mineMargin = 10;
	public int maxTileExposes = 200;
	
	public int effect3DSize = 10;
	
	private Random rand;
	
	public Color closedColor = new Color(112, 166, 255);
	public Color closedShadowColor = new Color(66, 129, 255);
	public Color markerColor = new Color(255, 120, 120);
	public Color markerShadowColor = new Color(255, 0, 0);
	
	public Color openedColor = new Color(189, 189, 189);
	public Color bombColor = Color.black;
	public Color backgroundColor = new Color(123, 123, 123);
	
	public Color textColor = Color.black;
	public Font textFont = new Font("Arial", Font.PLAIN, 20);
	
	public Field(int width, int height) {
		this.width = width;
		this.height = height;
		
		field = new Tile[width][height];
		
	}
	
	public void addMines(int percentage) {
		//100 = ALL TILES HAVE MINE
		//25 = 25% CHANCE OF SPAWNING MINE PER TILE
		rand = new Random();
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(rand.nextInt(100) <= percentage) {
					field[x][y] = new Tile(true);
				} else {
					field[x][y] = new Tile(false);
				}
			}
		}
		
		//COUNTS HOW MANY MINES NEARBY AND SAVES THAT INFORMATION TO THE MINE
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				field[x][y].minesNearby = countMinesNearby(x,y);
			}
		}
	}
	
	public int countMinesNearby(int locX, int locY) {	
		int mines = 0;
		for(int y = -1; y <= 1; y++) {
			for(int x = -1; x <= 1; x++) {
				if(insideMap(locX + x, locY + y)) {
					if(field[locX + x][locY + y].hasMine) {
						mines++;
					}
				}
			}
		}
		return mines;
	}
	
	public void showAllMines() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(field[x][y].hasMine) {
					field[x][y].discovered = true;
				}
			}
		}
	}
	
	public boolean gameWon() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				//IF THERE IS UNMARKED MINE OR UNDISCOVERED TILE WITHOUT MINE GAME NOT WON YET
				if(field[x][y].hasMine && field[x][y].marked == false || field[x][y].hasMine == false && field[x][y].discovered == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	//THIS FUNCTION "OPENS" THE EMPTY AREA AS FAR AS THERE ARE 0 MINES NEARBY
	public void exposeEmptyArea(int startingX, int startingY) {
		//IF STARTING TILE HAS MINES NEARBY DOESN'T EXPOSE
		if(field[startingX][startingY].minesNearby != 0) {
			field[startingX][startingY].discovered = true;
			return;
		}
		
		
		boolean[][] exposeTile = new boolean[width][height];
		exposeTile[startingX][startingY] = true;
		
		for(int i = 0; i < 100; i++) {
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					if(exposeTile[x][y]) {
						//EXPANDS TO ALL EMPTY DIRECTIONS
						field[x][y].discovered = true;	
						for(int y2 = y-1; y2 <= y+1; y2++) {
							for(int x2 = x-1; x2 <= x+1; x2++) {
								if(insideMap(x2, y2)) {
									if(field[x2][y2].minesNearby == 0) {
										exposeTile[x2][y2] = true;
									} else if(field[x2][y2].hasMine == false) {
										field[x2][y2].discovered = true;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public boolean insideMap(int locX, int locY) {
		if(locX >= 0 && locX < width && locY >= 0 && locY < height) {
			return true;
		} else {
			return false;
		}
	}
	
	public Graphics2D draw(Graphics2D g2d) {
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, 0,width * tileSize + marginSize, height * tileSize + marginSize);
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int tileX = x * tileSize + marginSize;
				int tileY = y * tileSize + marginSize;
				
				int size =  tileSize - marginSize;
				
				if(field[x][y].discovered) {
					g2d.setColor(openedColor);
					g2d.fillRect(tileX, tileY, size, size);
					
					if(field[x][y].hasMine) {
						//DRAWS MINE IN THE CENTER OF THE TILE
						g2d.setColor(bombColor);
						g2d.fillOval(tileX + mineMargin / 2, tileY + mineMargin / 2, size - mineMargin, size - mineMargin);
					} else {
						if(field[x][y].minesNearby > 0) {
							g2d.setColor(textColor);
							g2d.setFont(textFont);
							g2d.drawString(String.valueOf(field[x][y].minesNearby), tileX + size / 3, tileY + (int) (size / 1.3));
						}
					}
				} else {					
					if(field[x][y].marked) {
						g2d.setColor(markerColor);
					} else {
						g2d.setColor(closedColor);
					}
					g2d.fillRect(tileX, tileY, size, size);
					
					//CREATES "3D EFFECT IF THE TILE BELOW IS DISCOVERED
					if(insideMap(x, y+1) && field[x][y+1].discovered) {
						if(field[x][y].marked) {
							g2d.setColor(markerShadowColor);
						} else {
							g2d.setColor(closedShadowColor);
						}
						
						g2d.fillRect(tileX, tileY + size - effect3DSize, size, effect3DSize);
						
					}
				}
				
				//DRAWS PROBABILITY IF IT IS SET
				if(field[x][y].probability > 0) {
					g2d.setFont(new Font("Arial", Font.PLAIN, 10));
					g2d.setColor(Color.white);
					String ok = Double.toString(Math.round(field[x][y].probability * 10d) / 10d);
					g2d.drawString(ok, tileX + size / 10, tileY + size / 2);
				}
			}
		}
		return g2d;
	}
	
	public void hideProbabilities() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				field[x][y].probability = 0;
			}
		}
	}
	
	public void aiPlay(int maxMoves) {
		int moves = 0;
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(moves >= maxMoves) {
					return;
				} else if(field[x][y].discovered) {
					updateAITileNearbyInfo(x,y);
					//IF MINES NEARBY EQUALS UNDISCOVERED MINES NEARBY MARKS THEM ALL
					if(field[x][y].minesNearby == field[x][y].undiscoveredNearby && moves < maxMoves) {
						if(setMarkersToNearbyMines(x,y)) {
							moves++;
						}
					}
					updateAITileNearbyInfo(x,y);
					//IF MINES NEARBY EQUALS MARKED NEARBY OPENS THE UNMARKED TILES
					if(field[x][y].minesNearby == field[x][y].markersNearby && moves < maxMoves) {
						if(openUnmarkedTilesNearby(x,y)) {
							moves++;
						};
					}
				}
				
			}
		}
		
		//IF AI DID ZERO MOVES SHOWS PROBABILITY
		if(moves == 0) {
			calculateProbabilities();
			Game.gameMessage = "Mine probabilities shown becouse there isn't enought information";
		} else {
			hideProbabilities();
		}
		
	}
	
	private void updateAITileNearbyInfo(int locX, int locY) {
		int markersNearby = 0;
		int undiscoveredNearby = 0;
		for(int y = -1; y <= 1; y++) {
			for(int x = -1; x <= 1; x++) {
				if(insideMap(locX + x, locY + y) && !(locX == x && locY == y)) {
					if(field[locX+x][locY+y].marked) {
						markersNearby++;
					}
					if(field[locX+x][locY+y].discovered == false) {
						undiscoveredNearby++;
					}
				}
			}
		}
		
		field[locX][locY].markersNearby = markersNearby;
		field[locX][locY].undiscoveredNearby = undiscoveredNearby;
	}

	private boolean setMarkersToNearbyMines(int locX, int locY) {
		boolean successful = false;
		for(int y = -1; y <= 1; y++) {
			for(int x = -1; x <= 1; x++) {
				if(insideMap(locX + x, locY + y) && !(locX == x && locY == y) && field[locX+x][locY+y].marked == false && field[locX+x][locY+y].discovered == false) {
					field[locX+x][locY+y].marked = true;
					successful = true;
				}
			}
		}
		return successful;
	}
	
	private boolean openUnmarkedTilesNearby(int locX, int locY) {
		boolean successful = false;
		for(int y = -1; y <= 1; y++) {
			for(int x = -1; x <= 1; x++) {
				if(insideMap(locX + x, locY + y) && !(locX == x && locY == y) && field[locX+x][locY+y].marked == false && field[locX+x][locY+y].discovered == false) {
					exposeEmptyArea(locX+x, locY+y);
					successful = true;
				}
			}
		}
		return successful;
	}
	
	private void calculateProbabilities() {
		//RESETS PROBABILITIES
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(field[x][y].discovered) {
					field[x][y].probability = 0;
				} else {
					field[x][y].probability = 10;
				}
			}
		}
			
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(field[x][y].discovered) {
					updateAITileNearbyInfo(x, y);
					double probability = (double) (field[x][y].minesNearby - field[x][y].markersNearby) / (double) (field[x][y].undiscoveredNearby);
					addProbabilityToNearbyUndiscoveredUnMarkedMines(x, y, probability);
				}
			}
		}	
	}
	
	private void addProbabilityToNearbyUndiscoveredUnMarkedMines(int locX, int locY, double probability) {
		for(int y = -1; y <= 1; y++) {
			for(int x = -1; x <= 1; x++) {
				if(insideMap(locX + x, locY + y) && !(locX == x && locY == y) && field[locX+x][locY+y].discovered == false) {
					if(field[locX+x][locY+y].discovered) {
						field[locX + x][locY + y].probability = 0;
					} else if(field[locX+x][locY+y].marked) {
						field[locX + x][locY + y].probability = 10;
					} else {
						field[locX + x][locY + y].probability = probability;
					}
				}
			}
		}
	}
}
