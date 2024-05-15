package structures.basic;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameController;
import structures.basic.GameEventListeners.NewTurnListener;
import structures.basic.spells.Spell;
import structures.basic.units.AvatarUnit;
import structures.basic.units.Unit;
import utils.StaticConfFiles;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */

public abstract class Player{

	protected int health;
	protected int mana;
	public int getHealth() {
		return health;
	}
	public int getMana() {
		return mana;
	}

	protected ArrayList<Unit> unitsOnBoard;
	protected GameController controller;
	
	@JsonIgnore
	protected ActorRef out;
	@JsonIgnore
	public ActorRef getActorRef() {return out;}
	
	protected Unit avatar;
	public Unit getAvatar() {return avatar;}
	
	protected List<Card> cardDeck;
	protected List<Card> cardHand;
	protected List<Card> cardsRemovedThisTurn;
	public int getSizeOfHand() {return cardHand.size();}
	
	public Player() {
		super();
		this.health = 20;
		this.mana = 1;
		cardHand = new ArrayList<>();
		unitsOnBoard = new ArrayList<>();
		cardsRemovedThisTurn = new ArrayList<>();
	}
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
		cardHand = new ArrayList<Card>();
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void setMana(int mana) {
		if(mana > 9) this.mana = 9;
		else {
			this.mana = mana;
		}
	}
	public ArrayList<Unit> getPlayerUnitsOnBoard(){
		return unitsOnBoard;
	}
	
	
	public void spawnUnit(Card unitCard, Tile tile)
	{		
		Unit unit = controller.getUnitBuilder().createUnit(unitCard);	
		unit.setPositionByTile(tile);

		
		if(unit.getTriggerAbilities()) {
			controller.getEventController().unitSpawnedEvent();	
		}
		//opening gambit units need to start listening to unit spawned event only after they have been spawned
		unit.startListenting(controller.getEventController());
		
		unit.setIsSpawned(true);
		unit.setOwner(this);
		unitsOnBoard.add(unit);		
		
		//Allows provoke units to recalculate and provoke when new unit spawned
		controller.getEventController().unitMovedEvent();
		
		int attack = unit.getAttack();
		int health = unit.getHealth();
		
		controller.playAnimation(StaticConfFiles.f1_summon, tile);
		
		BasicCommands.drawUnit(out,unit, tile);	
		try {Thread.sleep(300);}
		catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, unit, attack);
		BasicCommands.setUnitHealth(out, unit, health);
		
		cardsRemovedThisTurn.add(unitCard);
		
		setMana(mana- unitCard.manacost);
		if(cardHand.contains(unitCard))
		{
			BasicCommands.deleteCard(out, cardHand.indexOf(unitCard));
		}
	}
	
	public Boolean playSpell(Spell spell, Card spellCard, Tile targetTile) {
		
		//only reduces mana and deletes card if spell conditions were met
		if(spell.performSpell(targetTile)) {
			cardsRemovedThisTurn.add(spellCard);
			setMana(mana - spellCard.getManacost());
			BasicCommands.deleteCard(out, cardHand.indexOf(spellCard));	
			return true;
			}
			else {
				return false;
			}			
	}
	
	
	
	protected void drawStartingHand()
	{
		for(int i =0; i <3; i++)
		{
		drawCardToHand();
		}		
	}
	
	public void drawCardToHand() {
		if(cardHand.size()>= 6 || cardDeck.isEmpty()) return;
		
		//uses index of 0 each time because the remove call will shift the next card to the top
		cardHand.add(cardDeck.get(0));
		cardDeck.remove(0);		
	};
	
	protected void clearUsedCards() {
		for(Card card : cardsRemovedThisTurn) {
			cardHand.remove(card);
		}
	}
	
	protected void initialiseAvatar(AvatarUnit avatar, int tileX, int tileY) {
		avatar.setOwner(this);
		avatar.setController(controller);
		controller.getEventController().subscribeToNewTurn(avatar);
		
		avatar.setActorRef(out);
		Tile startingTile = controller.getBoard().getTileAtXY(tileX,tileY);
		avatar.setPositionByTile(startingTile);
		
		avatar.setHealth(20);
		avatar.setAttack(1);
		avatar.setMaxHealth(20);
		
		BasicCommands.drawUnit(out, avatar, startingTile );

		try {Thread.sleep(300);}
		catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.setUnitHealth(out, avatar, avatar.getHealth());
		BasicCommands.setUnitAttack(out, avatar, avatar.getAttack());

		unitsOnBoard.add(avatar);
		this.avatar = avatar;
	}
	
	
	
	
	public abstract void changeHealth(int newHealth);
	
	public Card getCardFromHandPosition(int position) {		
		return cardHand.get(position);
	}
	
	public void drawHandToScreen() {
		if(cardHand.size() == 0) return;
		
		for(Card card : cardHand) {
			BasicCommands.drawCard(out, card, cardHand.indexOf(card), 0);
		}
	}
	
	public void deleteCards() {
		for(Card card : cardHand) {
			BasicCommands.deleteCard(out, cardHand.indexOf(card));
		}
	}
	
	public void playerTurnResponse() {
		clearUsedCards();
		drawHandToScreen();
	}

	

	
}
