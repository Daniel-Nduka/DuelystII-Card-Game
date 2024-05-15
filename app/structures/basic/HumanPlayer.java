package structures.basic;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameController;
import structures.basic.GameEventListeners.NewTurnListener;
import structures.basic.spells.Spell;
import structures.basic.units.AvatarUnit;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

/* Extension of Player class to store and initialse Player 1 data
 * and allow Player 1 specific Commands to be sent.
 * i.e. update mana, health, carddeck etc. */


public class HumanPlayer extends Player{
	
	
	public void setMana(int newMana) {
		super.setMana(newMana);
		BasicCommands.setPlayer1Mana(out, this);		
	}
	
	
	public HumanPlayer(GameController controller, ActorRef ref)
	{
		this.controller = controller;
		this.out = ref;
	}
	
	public void drawHandToScreen() {
		if(cardHand.size() == 0) return;
		
		for(Card card : cardHand) {
			BasicCommands.drawCard(out, card, cardHand.indexOf(card), 0);
		}
	}
	
	public void spawnUnit(Card card, Tile tile)
	{
		super.spawnUnit(card, tile);
		BasicCommands.setPlayer1Mana(out, this);
				
	}
	@Override
	public Boolean playSpell(Spell spell, Card spellCard, Tile targetTile) {
		super.playSpell(spell, spellCard, targetTile);
		BasicCommands.setPlayer1Mana(out, this);
		return true;
	}
	
	public void changeHealth(int newHealth) {
		
		health = newHealth;
		BasicCommands.setPlayer1Health(out, this);
	}
		
	public void initialise() {
		
		BasicCommands.setPlayer1Health(out, this);
		BasicCommands.setPlayer1Mana(out, this);
		
		//create avatar and load onto map
		AvatarUnit humanAvatar = (AvatarUnit)BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 43, AvatarUnit.class);
		initialiseAvatar(humanAvatar, 1 , 2);
		
		//fill card deck
		cardDeck = OrderedCardLoader.getPlayer1Cards(2);
		
		//fill card hand with first 3 tiles in deck
		drawStartingHand();
		drawHandToScreen();
	}
}
