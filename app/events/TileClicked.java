package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameController;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.units.Unit;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	GameController controller;
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if(controller.isGameOver()) return;


		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		Tile tileClicked = controller.getBoard().getTileAtXY(tilex, tiley);
		
		if (gameState.something == true) {
			// do some logic
			
			if(tileClicked.getIsOccupied())
			{
				controller.unitSelected(tileClicked.getUnit());
			}
			else
			{
				controller.tileSelected(tileClicked);
			}			
		}	
	}	
	
	public void setGameController(GameController control)
	{
		controller = control;
	}

}
