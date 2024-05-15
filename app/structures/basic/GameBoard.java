package structures.basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameController;
import utils.BasicObjectBuilders;
import structures.basic.*;
import structures.basic.units.Unit;

/*This class stores the array of tiles representing the Game Board.
 * It contains a lot of helper methods to allow other classes to analyse the board conditions.
 * I.e. check valid move/attack range, find nearby enemies etc. 
 * It also handles all highlighting and redrawing of the board on the screen */


public class GameBoard {

	private int xaxis = 9;
	private int yaxis = 5;
	private ActorRef out;

	private Tile[][] board;
	private ArrayList<Tile> currentHighlightedTiles;
	private ArrayList<Tile> currentHighlightedEnemies;

	private GameController controller;

	public GameBoard(ActorRef ref, GameController control) {
		out = ref;
		controller = control;
		currentHighlightedTiles = new ArrayList<>();
		currentHighlightedEnemies = new ArrayList<>();
	}

	public void initialise() {
		createBoard();
		drawEmptyBoard();

	}

	private void createBoard() {
		board = new Tile[yaxis][xaxis];
		for (int y = 0; y < yaxis; y++) {

			for (int x = 0; x < xaxis; x++) {
				board[y][x] = BasicObjectBuilders.loadTile(x, y);
			}
		}
	}

	private void drawEmptyBoard() {
		for (int y = 0; y < yaxis; y++) {

			for (int x = 0; x < xaxis; x++) {
				BasicCommands.drawTile(out, board[y][x], 0);
			}
		}
	}
	
	public Tile getTileAtXY(int x, int y) {
		return board[y][x];
	}
	
//METHODS FOR CLEARING CURRENT HIGHLIGHTED TILES
	/*
	 * Used when moving from one unit to another where the highlighted radius overlaps. Will
	 * only redraw tiles that are not to be newly highlighted. This reduces the
	 * number of drawTile calls to help prevent buffer overflow.
	 */
	private void refreshHighlightedTiles(ArrayList<Tile> newTiles) {
		for (Tile tile : currentHighlightedTiles) {
			if (!newTiles.contains(tile)) {
				BasicCommands.drawTile(out, tile, 0);
			}
		}
		currentHighlightedTiles.removeIf(i -> !newTiles.contains(i));
	}

	public void refreshAllTiles() {
		unhighlightAllEnemies();
		for (Tile tile : currentHighlightedTiles) {
			BasicCommands.drawTile(out, tile, 0);
		}
		currentHighlightedTiles.clear();
		currentHighlightedEnemies.clear();
	}

	public void refreshAllMoveTiles() {
		for (Tile tile : currentHighlightedTiles) {
			BasicCommands.drawTile(out, tile, 0);
		}
		currentHighlightedTiles.clear();
	}
	
	private void unhighlightSomeEnemies(List<Unit> highlightsToKeep) {
		for (Tile tile : currentHighlightedEnemies) {
			if (!highlightsToKeep.contains(tile.getUnit())) {
				BasicCommands.drawTile(out, tile, 0);
			}
		}
		currentHighlightedEnemies.clear();
	}

	public void unhighlightAllEnemies() {
		for (Tile tile : currentHighlightedEnemies) {
			BasicCommands.drawTile(out, tile, 0);
		}
		currentHighlightedEnemies.clear();
	}
	
//METHODS FOR HIGHLIGHTING THE TILES
	
	public void highlightMovement(Unit unit, Player player) {
		ArrayList<Tile> movementRadius = getMovementRange(unit);
		highlightTiles(movementRadius);
		List<Unit> enemies = new ArrayList<>();; 
			for(Tile t : movementRadius) {
				enemies.addAll(getEnemiesInTileRange(t, player));
			}
		highlightEnemies(enemies);
	}
	
	// Highlights all tiles in given list
	public void highlightTiles(ArrayList<Tile> highlightedTiles) {

			refreshHighlightedTiles(highlightedTiles);
			// only highlight if not already highlighted
			for (Tile tile : highlightedTiles) {
				if (!currentHighlightedTiles.contains(tile)) {
					BasicCommands.drawTile(out, tile, 1);
					currentHighlightedTiles.add(tile);
				}
			}
		}
//Highlights all enemies in the given list
	public void highlightEnemies(List<Unit> enemiesInRange) {
			unhighlightSomeEnemies(enemiesInRange);
			for (Unit unit : enemiesInRange) {
				BasicCommands.drawTile(out, unit.getCurrentTile(), 2);
				currentHighlightedEnemies.add(unit.getCurrentTile());
			}
		}
	//highlights single tile but makes sure to unhighlight others	
	public void highlightSingleTile(Tile tile) {
			
			if(!currentHighlightedTiles.contains(tile)) {
				for(Tile t : currentHighlightedTiles) {
					
					BasicCommands.drawTile(out, t, 0);						
			}	
				currentHighlightedTiles.clear();
				BasicCommands.drawTile(out, tile, 1);
				currentHighlightedTiles.add(tile);
			}
			else {
					for(Tile t : currentHighlightedTiles) {	
						if(t != tile) {
							BasicCommands.drawTile(out, t, 0);	
						}								
			}	
				currentHighlightedTiles.clear();
				BasicCommands.drawTile(out, tile, 1);
				currentHighlightedTiles.add(tile);
		}					
	}

//Highlights attack radius of the given unit		
	public void highlightAttack(Unit unit) {
		refreshAllMoveTiles();
			ArrayList<Unit> enemyRadius = getEnemiesInAttackRange(unit);
			highlightEnemies(enemyRadius);
		}

			public void highlightSingleEnemy(Unit enemy) {
				refreshAllMoveTiles();
				
				if(currentHighlightedEnemies.contains(enemy.getCurrentTile())) {
					for(Tile t : currentHighlightedEnemies) {
						if(t != enemy.getCurrentTile()) {
							BasicCommands.drawTile(out, t, 0);
						}
					}
				}
				else {
					unhighlightAllEnemies();
				}
				currentHighlightedEnemies.clear();
				currentHighlightedEnemies.add(enemy.getCurrentTile());
				BasicCommands.drawTile(out, enemy.getCurrentTile(), 2);
			}

//METHODS FOR RETURNING UNIT'S MOVE, SPAWN AND ATTACK RADIUS
	
//gets valid spawn radius (1 unoccupied tile surrounding) for all units in the passed list
	public ArrayList<Tile> getAllUnitSpawnRadius(ArrayList<Unit> units) {
		Set<Tile> allUnitsRadiusSet = new HashSet<>();
		
		for (Unit unit : units) {
			allUnitsRadiusSet.addAll(getUnitSpawnRadius(unit));
		}
		ArrayList<Tile> allUnitsRadiusList = new ArrayList<>(allUnitsRadiusSet);
		return allUnitsRadiusList;
	}


// get spawn radius for one unit
	public ArrayList<Tile> getUnitSpawnRadius(Unit unit) {
		ArrayList<Tile> radius = new ArrayList<Tile>();
		int currentX = unit.getCurrentTile().getTilex();
		int currentY = unit.getCurrentTile().getTiley();
		int newX;
		int newY;

		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				newX = currentX + x;
				newY = currentY + y;

				// don't include current position
				if (newX == currentX && newY == currentY) {
					continue;
				}

				// check if in bounds of board & unoccupied : add to return list if so
				if (validateTileForMove(newX, newY)) {
					radius.add(getTileAtXY(newX, newY));
			}
		}
		}
		return radius;
	}

	//Used to just get the enemies in adjascent tiles to the unit
	public ArrayList<Unit> getEnemiesInAttackRange(Unit unit){
		Tile tile = unit.getCurrentTile();
		return  new ArrayList<>(getEnemiesInTileRange(tile, unit.getOwner()));
	}
	
	
	//Used by ai player to get all human units adjascent to its units for targeting spells
	public Set<Unit> getAllEnemiesInAttackRange(List<Unit> units){
			Set<Unit> returnlist = new HashSet<>();
			for(Unit u : units) {
				returnlist.addAll(getEnemiesInAttackRange(u));	
			}
			return returnlist;
		}

//used to get enemies adjascent to the given tile 
	public Set<Unit> getEnemiesInTileRange(Tile tile, Player player) {
		Set<Unit> enemies = new HashSet<>();
		int currentX = tile.getTilex();
		int currentY = tile.getTiley();
		int newX;
		int newY;
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				newX = currentX + x;
				newY = currentY + y;

				if (checkTileForEnemy(newX, newY, player)) {
					enemies.add(getTileAtXY(newX, newY).getUnit());
				}
			}
		}
		return enemies;
	}
//Used by ai player to target spells
	public ArrayList<Unit> getFriedlyUnitsInRange(Unit unit) {
		ArrayList<Unit> allies = new ArrayList<>();
		int currentX = unit.getCurrentTile().getTilex();
		int currentY = unit.getCurrentTile().getTiley();
		int newX;
		int newY;
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				newX = currentX + x;
				newY = currentY + y;				
				
				if (checkTileForFriendly(newX, newY, unit.getOwner())) {
					allies.add(getTileAtXY(newX, newY).getUnit());
				}
			}
		}
		return allies;
	}

//Returns the valid movement range of the given unit
	public ArrayList<Tile> getMovementRange(Unit unit) {
		ArrayList<Tile> radius = new ArrayList<Tile>();
		int currentX = unit.getCurrentTile().getTilex();
		int currentY = unit.getCurrentTile().getTiley();
		int newX;
		int newY;

		// if unit is flying : range = all tiles that are not occupied
		if (unit.getIsFlying()) {
			for (int y = 0; y < yaxis; y++) {
				for (int x = 0; x < xaxis; x++) {
					if (validateTileForMove(x, y)) {
						radius.add(getTileAtXY(x, y));
					}
				}
			}
			return radius;
		}
		// all other units range = 1 tile NSEW + diagonal + 2 * {X-1, X+1, Y+1, Y-1}
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				newX = currentX + x;
				newY = currentY + y;

				// don't highlight current position
				if (newX == currentX && newY == currentY)
					continue;

				// check if Tile in bounds of board & unoccupied
				if (validateTileForMove(newX, newY)) {
					
					if(x != 0 && y != 0 )// if the tile is diagonal
					{
						// check that  N&E|| N&W || S&E || S&W  tile are not both occupied
						if(checkDiagnoalTiles(x,y, newX, newY)) {
							radius.add(getTileAtXY(newX, newY));
						}
						else {
							continue;
						}
						
					}
					else {
						radius.add(getTileAtXY(newX, newY));

					}			

					// add tile in +-2 y position when x is 0 if y +- 1 is unoccupied
					if (x == 0) {
						int yPlus2 = currentY + (y * 2);
						if (validateTileForMove(currentX, yPlus2)) {
							radius.add(getTileAtXY(currentX, yPlus2));
						}

					}
					// add tile in +-2 x position when y is 0 : if x +- 1 is unoccupied
					if (y == 0) {
						int xPlus2 = currentX + (x * 2);
						if (validateTileForMove(xPlus2, currentY)) {
							radius.add(getTileAtXY(xPlus2, currentY));
						}
					}
				}
			}
		}
		return radius;
	}
	
//Used by ai to assess the range of nearby human player units	
	public List<Tile> getAllUnitsMovementRange(List<Unit> units){
			Set<Tile> allUnitsRangeSet = new HashSet<>();
		
		for (Unit unit : units) {
			allUnitsRangeSet.addAll(getMovementRange(unit));
		}
		ArrayList<Tile> allUnitsRangeList = new ArrayList<>(allUnitsRangeSet);
		return allUnitsRangeList;
	}

	
//this returns the units in movement range of a given enemy	
	public List<Unit> getOneUnitsEnemiesInMovementRange(Unit unit, Player player) {
		List<Unit> enemies = new ArrayList<>();
			for(Tile t : getMovementRange(unit)) {
				enemies.addAll(getEnemiesInTileRange(t, player));
			}
			return enemies;
	}
	
//used for assessing best location to move a unit for later attack	
	public Tile getNearestTileToEnemy(Unit attacker, Unit target){
		
		//for each tile in the attackers' movement range ; if it is also adjascent to the enemy
		//add it to adjascent tiles
		
		List<Tile> adjascentTiles = new ArrayList<>();
		for(Tile at : getMovementRange(attacker)) {
			if(getUnitSpawnRadius(target).contains(at)) {
				adjascentTiles.add(at);
			}
		}
		if(adjascentTiles.size() == 1) {// if there is only 1 just return it
			return adjascentTiles.get(0);
		}
		else {// return the tile that is only 1 move away from the attacker
			for(Tile t : adjascentTiles) {
				int newX = t.getTilex() - attacker.getCurrentTile().getTilex();
				int newY = t.getTiley() - attacker.getCurrentTile().getTiley();
				if(newX == 0) {
					if(newY == -1 || newY == 1) {
						return t;
					}
				}
				else if(newY == 0) {
					if(newX == 1 || newX == -1) {
						return t;
					}
				}
			}
		}
		//if all else just return the first tile in the list
		return adjascentTiles.get(0);
		
	}	
	
//HELPER METHODS FOR CHECKING OCCUPIED STATUS OF TILES
	
	public Boolean checkIfenemyInRadius(Unit attacker, Unit attacked) {
		ArrayList<Unit> enemiesInRange = getEnemiesInAttackRange(attacker);
		if (enemiesInRange.contains(attacked)) {
			return true;
		} else
			return false;
	}		
	
	
/*For getting movement range of units; if tile is diagonal to the unit	
 * this will check that both adjascent tiles are not occupied
 */
	private Boolean checkDiagnoalTiles(int x, int y, int tX, int tY) {
		
		Tile tileNS = getTileAtXY(tX - x, tY);
		Tile tileEW = getTileAtXY(tX, tY - y);
		
		if(tileNS.getIsOccupied() && tileEW.getIsOccupied()) {
			return false;
		}
		else {
			return true;
		}
	}

// checks if the position is within the board and is not occupied by a unit
	public Boolean validateTileForMove(int x, int y) {

		if (x < 0 || x > 8 || y < 0 || y > 4) {
			return false;
		} else if (getTileAtXY(x, y).getIsOccupied()) {
			return false;
		}
		return true;
	}



	//checks based on whether the unit belongs to the given players deck ; takes in the owner of the unit
	public Boolean checkTileForFriendly(int x, int y, Player player) {

		if (x < 0 || x > 8 || y < 0 || y > 4)
			return false;

		Tile tile = getTileAtXY(x, y);
		if (tile.getIsOccupied()) {
			Unit unit = tile.getUnit();
			if(player.getPlayerUnitsOnBoard().contains(unit)) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean checkTileForEnemy(int x, int y, Player player) {

		if (x < 0 || x > 8 || y < 0 || y > 4)
			return false;
		if(player == null) {
			BasicCommands.addPlayer1Notification(out, "null", 5);
		}		
		Tile tile = getTileAtXY(x, y);
		if (tile.getIsOccupied()) {
			Unit unit = tile.getUnit();
			if(!player.getPlayerUnitsOnBoard().contains(unit)) {
				return true;
			}
		}
		return false;
	}

}
