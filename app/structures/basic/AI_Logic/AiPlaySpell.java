package structures.basic.AI_Logic;

import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.spells.Spell;
import structures.basic.units.Unit;
import akka.actor.ActorRef;


public class AiPlaySpell extends AiAction {
	Card card;
	Spell spell;
	Tile target;
	AiPlayer ai;
	int cardPosition;
	
	public AiPlaySpell(Card card, Spell spell, Tile target, AiPlayer ai, int position) {
		
		this.card = card;
		this.spell = spell;
		this.target = target;
		this.ai = ai;
		this.cardPosition = position;
	}
	public void action() {
	//Gives visual que as to which card is being played
		BasicCommands.drawCard(ai.getActorRef(), card, cardPosition, 1);		
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
		
			BasicCommands.drawCard(ai.getActorRef(), card, cardPosition, 0);
			
			//if the conditions have changed and the spell couldn't be played. Try targeting again.
			if(!ai.playSpell(spell, card, target)) {
				ai.reassessSpells();
			}
		
	}

}
