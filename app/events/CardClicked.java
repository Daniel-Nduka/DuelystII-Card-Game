package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameController;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{
	
	GameController controller;
	public void setGameController(GameController control)	{controller = control;	}

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if(controller.isGameOver()) return;
		
		int handPosition = message.get("position").asInt();	
		controller.cardSelected(handPosition);
		
	}

}
