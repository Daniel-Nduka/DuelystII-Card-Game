package structures.basic.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameController;
import structures.basic.Card;

/*Used to return the correct spell class given the card. Uses the id of the card
 * to match the correct spell. 
 * */

public class SpellBuilder {
	
	GameController controller;
	
	public SpellBuilder(GameController control) {
		controller = control;
	}
	
	public Spell createSpell(Card selectedCard) {
		
		
		Spell spell = null;
		
		switch(selectedCard.getId()){
			
		case 2:
		case 12:
			spell = new HornOfTheForsaken(controller);				
			break;
		case 5:
		case 15:
			spell = new WraithlingSwarm(controller);

			break;
		case 8:
		case 18:
			spell = new DarkTerminus(controller);
			break;
		case 25:
		case 35:
			spell = new BeamShock(controller);
			break;
		case 39:
		case 29:
			spell = new SundropElixir(controller);
			break;
		case 30:
		case 40:
			spell = new TrueStrike(controller);
			break;
			
		default:
			ActorRef out = controller.getActorRef();
			BasicCommands.addPlayer1Notification(out, "could't load spell", 2);
			
		}		
		return spell;
	}

}
