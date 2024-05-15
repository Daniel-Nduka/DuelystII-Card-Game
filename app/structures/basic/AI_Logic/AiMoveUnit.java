package structures.basic.AI_Logic;

import structures.GameController;
import structures.basic.Tile;
import structures.basic.units.Unit;

public class AiMoveUnit extends AiAction{
	
	Tile tile;
	Unit unit;
	GameController controller;

	public void action() {
		
		if(unit.getIsDestroyed() || unit.getIsProvoked()) return;
		
		//needs to tell the controller directly rather than wait for the browser.
		controller.setUnitMoving(true);	
		unit.move(tile);	

	}
	
	public AiMoveUnit(Tile tile, Unit unit, GameController control)
	{
		this.tile = tile;
		this.unit = unit;
		this.controller = control;		
		this.unit.setPreviousTile();//stores its current tile as its previous tile
		
		//need to set the new tile as occupied so that the ai wont send any more units to it while the action is queued
		unit.setPositionByTile(tile);
		unit.setNotYetMoved(true);
		unit.setOutOfMovement(true);
	}
}
