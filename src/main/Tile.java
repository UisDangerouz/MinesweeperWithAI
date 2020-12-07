package main;

public class Tile {

	boolean hasMine;
	boolean marked;
	
	boolean discovered;
	int minesNearby;
	
	//EMPTY HERE MEANS THAT THERE IS ATLEAST ONE TILE NEARBY THAT HAS minesNearby VALUE OF 0
	int emptyTileNearby;
	
	//AI USES THESE :D
	double probability;
	int markersNearby;
	int undiscoveredNearby;
	
	
	public Tile(boolean hasMine) {
		this.hasMine = hasMine;
	}
}
