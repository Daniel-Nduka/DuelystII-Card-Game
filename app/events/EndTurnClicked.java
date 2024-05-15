package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameController;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	GameController controller;
	public void setGameController(GameController control)	{controller = control;	}
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {	
		if(controller.isGameOver()) return;

		controller.endTurnClickedEvent();		
	}

}
