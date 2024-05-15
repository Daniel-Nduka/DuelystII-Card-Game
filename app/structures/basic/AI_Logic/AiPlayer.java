package structures.basic.AI_Logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameController;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.GameEventListeners.NewTurnListener;
import structures.basic.spells.Spell;
import structures.basic.units.AvatarUnit;
import structures.basic.units.Unit;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;
import com.fasterxml.jackson.annotation.JsonIgnore;

/*Inherits the basic data of the Player class but contains all decision making logic for the ai.
 * 
 * */

public class AiPlayer extends Player{
	
	@JsonIgnore
	private Random rand;
	private ActionQueue queue;
	private int turnMana;
	
	public AiPlayer(GameController controller, ActorRef ref)	{
		this.controller = controller;
		this.out = ref;
		rand = new Random();
		queue = new ActionQueue();
	}
	@JsonIgnore
	public GameController getController() {return controller;}
	
	public void setMana(int newMana) {
		super.setMana(newMana);
		BasicCommands.setPlayer2Mana(out, this);		
	}
	public void changeHealth(int newHealth) {	
		health = newHealth;
		BasicCommands.setPlayer2Health(out, this);		
	}
	
	// this list stores enemies that will be killed during the ai turn 
	//so that they aren't targeted after killed 
	private List<Unit> enemiesKilled = new ArrayList<>();
		

/*Unit needs to be created before it is spawned so the ai can take it into consideration
 * as if it was on the board. It spawns from the unit and just uses the card to get its health/attack values.
 * */
	public void spawnUnit(Card unitCard, Unit unit, Tile tile)
	{
		if(unit.getTriggerAbilities()) {
		controller.getEventController().unitSpawnedEvent();
		}
		//subscribe it to unitSpawned events if relevant.
		unit.startListenting(controller.getEventController());
		
		unit.setIsSpawned(true);		
		int attack = unit.getAttack();
		int health = unit.getHealth();
		
		//units with provoke ability will recalculate their adjascent enemies
		controller.getEventController().unitMovedEvent();
		
		controller.playAnimation(StaticConfFiles.f1_summon, tile);
		
		BasicCommands.drawUnit(out,unit, tile);	
		//sleep just to allow browser to find unit on screen to update visual values
		try {Thread.sleep(300);}
		catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.setUnitAttack(out, unit, attack);
		BasicCommands.setUnitHealth(out, unit, health);
		
		cardsRemovedThisTurn.add(unitCard);
		
		setMana(mana- unitCard.getManacost());
		if(cardHand.contains(unitCard))
		{
			BasicCommands.deleteCard(out, cardHand.indexOf(unitCard));
		}		
	}
	
	//ENTRY POINT TO AI Turn
		public void initiateAiTurn() {
			turnMana = mana;
			enemiesKilled.clear();
			assessState();
			controller.listenForHeartbeat(true);		
		}
		
	/*Sequence of decision making methods focusing on playing cards first; then handling avatar, then handling units
	 * This will assess the gamestate and its conditions and when decided it will store its actions in a queue.	*/
		
		private void assessState() {		

			AssessAvailableCards();
			assessAvatarMovement();
			assessAvatarAttack();
			assessAllUnitsMoveAndAttack();
			//check  twice in case any moves left
			assessAllUnitsMoveAndAttack();
			
		}	

	/*This is called by the GameController everytime it recieves the heartbeat event during the Ai turn
	 * It calls off the stored actions in its action queue as previously decided by assessState()*/
		public void performActions() {
			if(controller.getIsUnitMoving() || controller.getIfPlayerTurn()) {
				return;
			}
			if(!controller.isGameOver()) {
			
				if(queue.getHasItems()) {
					queue.callAction();			
				}
				else {
					controller.listenForHeartbeat(false);
					controller.endTurnEvent();
				}
			}	
			else {
				controller.listenForHeartbeat(false);
			}
		}
		
				
	//Checks all cards that are within its mana budget and calls decision methods for each.
	private void AssessAvailableCards() {	

		ArrayList<Card> usableSpells = new ArrayList<>();
		ArrayList<Card> usableUnits = new ArrayList<>();
		ArrayList<Tile> spawnableTiles = controller.getBoard().getAllUnitSpawnRadius(unitsOnBoard);
		
		for(Card c : cardHand) {				
			if(c.getManacost() <= turnMana ) {
				if(c.getIsCreature()) {
					usableUnits.add(c);				
				}
				else {
					usableSpells.add(c);				
				}
			}
		}
		//Play the cards in the following order
			assessUnitCardsHighestAttack(usableUnits,spawnableTiles);
			assessAvailableSpells(usableSpells);	
			assessUnitCardsLowestMana(usableUnits,spawnableTiles);	
	}
	
//loops through available unit cards and plays lowest mana cards first until out of mana.
private void assessUnitCardsLowestMana(ArrayList<Card> usableUnits, ArrayList<Tile> spawnableTiles ) {
	
			if(!usableUnits.isEmpty() && !spawnableTiles.isEmpty()) {
				Card c = getLowestManaCard(usableUnits);
				
				while(c.getManacost() <= turnMana && !usableUnits.isEmpty()) {
					Tile tile = getRandomObjectInRange(spawnableTiles);
					
					AiAction spawn = new AiSpawnUnit(c,tile,this,cardHand.indexOf(c));
					queue.add(spawn);
					
					turnMana -= c.getManacost();
					spawnableTiles.remove(tile);
					usableUnits.remove(c);
					
					if(!usableUnits.isEmpty()) {
					c = getLowestManaCard(usableUnits);
				}		
			}
	}
	}
	
//play single Unit card with the highest attack value
	private void assessUnitCardsHighestAttack(ArrayList<Card> usableUnits, ArrayList<Tile> spawnableTiles) {
		
			if(!usableUnits.isEmpty() && !spawnableTiles.isEmpty()) {
				Card c = getHighestAttackUnitCard(usableUnits);		
				
				if(c.getManacost() <= turnMana) {
					Tile tile = getRandomObjectInRange(spawnableTiles);
					AiAction spawn = new AiSpawnUnit(c,tile,this,cardHand.indexOf(c));
					
					queue.add(spawn);
					turnMana -= c.getManacost();
					
					spawnableTiles.remove(tile);
					usableUnits.remove(c);
				}
			}
	}

//looks at each available spell and plays it if there are appropriate targets
	private void assessAvailableSpells(List<Card> usableSpells) {
		
		if (!usableSpells.isEmpty()){	
			
			ArrayList<Unit> allEnemies = controller.getHumanPlayer().getPlayerUnitsOnBoard();
			Card c = usableSpells.get(0);	
			
			while(c.getManacost() <= turnMana && !usableSpells.isEmpty()  ) {
				
					Unit target = null;					
					Spell spell = controller.getSpellBuilder().createSpell(c);
					
					//if it causes damage effect the lowest health enemy with the highest attack
					if(spell.hurtsEnemy())
					{
						List<Unit> twoHealthUnits = new ArrayList<>();
						for(Unit u : allEnemies) {
							if(u.getHealth() <= 2) {
								twoHealthUnits.add(u);
							}
						}
						if(!twoHealthUnits.isEmpty()) {
							target = getHighestAttackUnit(twoHealthUnits);
							enemiesKilled.add(target);
						}
						else {
							target = getLowestHealthUnit(allEnemies);
						}
					}
					//if spell effects but not damages enemy (beamshock)
					else if(spell.effectsEnemy())
					{
						ArrayList<Unit> targets = new ArrayList<Unit>();

						targets = controller.getBoard().getEnemiesInAttackRange(avatar);
					//target enemy adjascent to friendly Avatar first
						if(!targets.isEmpty()) {
							target = getRandomObjectInRange(targets);	

						}
					//then target enemy besides any friendly Unit
						else {
							targets.addAll(controller.getBoard().getAllEnemiesInAttackRange(getPlayerUnitsOnBoard()));
							if(!targets.isEmpty()) {
								target = getRandomObjectInRange(targets);
							}
					//then target random enemy 
							else {
								target = getRandomObjectInRange(allEnemies);	
								}
						}						
					}
					//heal the avatar first then the lowest health friendly
					else if(spell.targetsFriendly() && !getDamagedUnits().isEmpty()) {
						if(avatar.getMaxHealth() - avatar.getHealth() > 2) {
							target = avatar;
						}
						else {
							target = getLowestHealthUnit(getDamagedUnits());					

						}						
					}					
					//use the card and target and store the play spell action in the queue
					if(target != null) {
						
						AiAction spellAction = new AiPlaySpell(c,spell,target.getCurrentTile(),this,cardHand.indexOf(c));
						queue.add(spellAction);
						turnMana  -= c.getManacost();						
						usableSpells.remove(c);
					}	
					else// the card is unplayable remove it from consideration
					{
						usableSpells.remove(0);
					}					
					if(!usableSpells.isEmpty()) {
						c = usableSpells.get(0);
					}
			}		
		}
	}
	

	private void assessAllUnitsMoveAndAttack() {
		//assess all the units that are able to move this turn
		
		for(Unit u : unitsOnBoard) {
			if(!u.getIsStunned() && !u.getOutOfAttacks()) {
				
				//first attack available targets - if no targets then move if available
				if( !assessSingleUnitAttack(u) ) {	
					
					if(!u.getIsProvoked() && !u.getOutOfMovement()) {
						assessSingleUnitMovement(u);
					}
				}
			}
		}		
	}
	
//used with AiPlaySpell Class if its original target has since been destroyed	
	public void reassessSpells() {
		
		List<Card> usableSpells = new ArrayList<>();		
		for(Card c : cardHand) {				
			if(c.getManacost() <= turnMana ) {
				if(!c.getIsCreature()) {
					usableSpells.add(c);				
				}			
			}
		}		
		assessAvailableSpells(usableSpells);		
	}
	
//Seperate method for Avatar to assess its attack - to act in self preservation
//only targets enemies that wont counter attack
	
	private Boolean assessAvatarAttack() {
		if(avatar.getOutOfAttacks()) return false;
		
	List<Unit> adjascentEnemies = controller.getBoard().getEnemiesInAttackRange(avatar);
	List<Unit> weakUnits = new ArrayList<>();
	
	for(Unit enemy : adjascentEnemies) {
		//checks if either they can be killed or if they cannot attack
		if(enemy.getHealth() <= avatar.getAttack() || enemy.getAttack() == 0) {
			weakUnits.add(enemy);
		}
	}
	if(!weakUnits.isEmpty()) {
		//if one of those units is a provoking unit only attack them
		if(avatar.getIsProvoked()) {			
			Unit target = getLowestHealthUnit(avatar.getProvokingUnits());
			
			if(weakUnits.contains(target)){				
				AiAction attack = new AiAttackUnit(avatar,target);
				queue.add(attack);
				
				if(target.getHealth() <= avatar.getAttack()) {
					
					//remove them from later decision making consideration if they will be killed
					enemiesKilled.add(target);
					controller.getHumanPlayer().getPlayerUnitsOnBoard().remove(target);
				}	
				return true;					
			}
			else {
				return false;
			}
		}
		else {
			//else just target the highest attack unit
			Unit target = getHighestAttackUnit(weakUnits);
			if(target.getHealth() <= avatar.getAttack()) {
				enemiesKilled.add(target);
				controller.getHumanPlayer().getPlayerUnitsOnBoard().remove(target);
			}			
			AiAction attack = new AiAttackUnit(avatar, target);
			queue.add(attack);
			return true;			
		}		
	}
	else {
		return false;
	}
	}



	private Boolean assessSingleUnitAttack(Unit unit) {
		//don't attack if it cannot damage the enemy
		if(unit.getAttack() == 0) return false;	
		
		//use seperate attack assessment method if avatar
		if(unit == avatar) return false;
		
		List<Unit> attackRange = controller.getBoard().getEnemiesInAttackRange(unit);	
		
		//extra check to make sure that it only attacks provoking units if they are in range
		List<Unit> provokers = new ArrayList<>();
		for(Unit u : attackRange) {
			if(u.isProvokingUnit()) {
				provokers.add(u);
			}
		}
		if(!provokers.isEmpty()) {
			attackRange = provokers;
		}
		
		//remove units that will have been killed already by other units
		for(Unit u : enemiesKilled) {
			attackRange.remove(u);
		}	
		
		if(attackRange.isEmpty()) {	return false;}	
		
			Unit target;
			//if unit is provoked- get lowest health provoker - attack them - else return
			if(unit.getIsProvoked()) {
				Unit provoker = getLowestHealthUnit(unit.getProvokingUnits());
				
				if(attackRange.contains(provoker)){
					
					AiAction attack = new AiAttackUnit(unit, provoker);
					queue.add(attack);
					
					if(provoker.getHealth() <= unit.getAttack()) {
						enemiesKilled.add(provoker);
						controller.getHumanPlayer().getPlayerUnitsOnBoard().remove(provoker);
					}
					return true;					
				}
				else {
					return false;
				}
			}
			List<Unit> targets = new ArrayList<>();
			//if one of the units can be killed by the attack target them - choose one with highest attack value
				for(Unit u : attackRange) {
					if(u.getHealth() <= unit.getAttack()) {
						targets.add(u);
					}
				}
				if(!targets.isEmpty()) {
					target = getHighestAttackUnit(targets);
					enemiesKilled.add(target);
					controller.getHumanPlayer().getPlayerUnitsOnBoard().remove(target);
					AiAction attack = new AiAttackUnit(unit, target);
					queue.add(attack);
					return true;
				}
				
			//if one of the units is the opponent avatar - target them
				for(Unit u : attackRange) {
						if(u.getIsAvatar()) {
							AiAction attack = new AiAttackUnit(unit, u);
							queue.add(attack);
							return true;
						}						
					}
			//else choose lowest attack enemy - only attack if it wont kill the unit in counter attack
				target = getLowestAttackUnit(attackRange);
				if(target.getAttack() < unit.getHealth()){
					AiAction attack = new AiAttackUnit(unit, target);
					queue.add(attack);
					return true;		
				}
				else {
					return false;
				}
					
			}	
	
	
	
	private void assessSingleUnitMovement(Unit unit){
		//skip if the unit is the player's avatar - to be worked out seperately
		if(unit.getIsAvatar()) return;
		
		List<Tile> range = controller.getBoard().getMovementRange(unit);
		if(range.isEmpty())return;
		
		//if there is an enemy in its movement range - move to it - choose by avatar first then randomly		
		List<Tile> besidesEnemy = new ArrayList<>();		
		for(Tile tile : range) {
			List<Unit> enemies = new ArrayList<>(controller.getBoard().getEnemiesInTileRange(tile, this));
			if(!enemies.isEmpty()) {
				if(enemies.contains(controller.getHumanPlayer().getAvatar()))
						{	
					AiAction move = new AiMoveUnit(tile, unit, controller);
					queue.add(move);
					return;
				}
				else{
					besidesEnemy.add(tile);					
				}				
			}			
			}
			if(!besidesEnemy.isEmpty()) {
				AiAction move = new AiMoveUnit(getRandomObjectInRange(besidesEnemy), unit, controller);
				queue.add(move);
				return;
		}	
		
	//if no enemies in range	
	//get the farthest most left tile and move there
		int x = unit.getCurrentTile().getTilex();
		
		//RECYCLING EMPTY LIST// NOW REPRESENTS ALL TILES TO THE LEFT OF SELECTED UNIT
		besidesEnemy.clear(); //just reuse this list since it wont be used again (rather than create new one).	
		List<Tile> leftTiles = besidesEnemy;
		for(Tile t : range) {
			if(t.getTilex() < unit.getCurrentTile().getTilex()) {
				// if the x -2 is available move there
				if(t.getTilex() == x - 2) {
					AiAction move = new AiMoveUnit (t, unit, controller);
					queue.add(move);
					return;					
				}
				//else add tile to list to pick from at random
				leftTiles.add(t);
			}			
		}	
		if(!leftTiles.isEmpty()) {
		//chose from left tiles at random and move unit to it
		AiAction move = new AiMoveUnit (getRandomObjectInRange(leftTiles), unit, controller);
		queue.add(move);
		return;
		}
		//will not move if only options are in y axis and not near an enemy 
}

	
	/*To ensure its self preservation the avatar will decide its movement seperately.
	 * Moves towards left of board if it is not near an enemy
	 * if near an enemy it will move away if its attack is greater than 1
	 * */
private void assessAvatarMovement() {
	if(avatar.getIsProvoked() || avatar.getOutOfMovement()) return;
	
	List<Unit> enemiesNear = controller.getBoard().getEnemiesInAttackRange(avatar);
	List<Tile> movementRange = controller.getBoard().getMovementRange(avatar);
	if(movementRange.isEmpty()) {
		return;
	}
	
	//If there are no enemies adjascent
	if(enemiesNear.isEmpty()) {
		
		//if it is on the right side of the map : move left
		if(avatar.getCurrentTile().getTilex() > 4) {
			List<Tile> leftTiles = new ArrayList<>();
			for(Tile t : movementRange) {
				if(t.getTilex() < avatar.getCurrentTile().getTilex()) {
					
					// if the x -2 is available move there
					if(t.getTilex() == avatar.getCurrentTile().getTilex() - 2) {
						AiAction move = new AiMoveUnit (t, avatar, controller);
						queue.add(move);
						return;					
					}
					//else add tile to list to pick from at random
					leftTiles.add(t);
				}			
			}	
			if(!leftTiles.isEmpty()) {
			//chose from left tiles at random and move unit to it
			AiAction move = new AiMoveUnit (getRandomObjectInRange(leftTiles), avatar, controller);
			queue.add(move);
			return;
			}			
		}
		else {
		// if it is in the centre or left side of the map and not near an enemy just stay still
			return;
		}
	}
	//if there are enemies adjascent
	else {
		//if the only nearby enemies are <= 1 attack don't bother moving
		if(getHighestAttackUnit(enemiesNear).getAttack() <= 1) {
			return;
		}
		else {
			Tile position = getFarthestTileFromEnemey(enemiesNear, avatar);
			AiAction move = new AiMoveUnit (position, avatar, controller);
			queue.add(move);
			return;	
		}
	}	
}	
	
	
//HELPER METHODS

	private Tile getFarthestTileFromEnemey(List<Unit> enemies, Unit friendly) {	
		
		//get range of nearby enemies and range of friendly
		List<Tile> rangeOfEnemies = controller.getBoard().getAllUnitsMovementRange(enemies);	
		List<Tile> friendlyRange = controller.getBoard().getMovementRange(friendly);
		
		//remove all tiles that overlap
		friendlyRange.removeAll(rangeOfEnemies);
		Tile newPosition;
		
			//if all tiles overlapped get the lowest attack enemy and their nearest tile and move to it
		if(friendlyRange.isEmpty()) {
			Unit lowestAttack = getLowestAttackUnit(enemies);
			newPosition = controller.getBoard().getNearestTileToEnemy(avatar, lowestAttack);
			return newPosition;
		}
		else {
			if(friendlyRange.size() == 1) {
				return friendlyRange.get(0);
			}
			else {
				//get the tile that is farthest from the friendly's current position
				int xF = friendly.getCurrentTile().getTilex();
				int yF = friendly.getCurrentTile().getTiley();
				int xD;
				int yD;
				int difference = 0;
				Tile farthest = friendlyRange.get(0);
				
				for(Tile tile : friendlyRange) {
					xD = Math.abs(xF - tile.getTilex());
					yD = Math.abs(yF - tile.getTiley());	
					if(xD + yD > difference) {
						difference = xD + yD;
						farthest = tile;
					}
				}
				return farthest;
			}			
		}		
	}
	
	private <F> F getRandomObjectInRange(List<F> objects) {
		int index = rand.nextInt(objects.size());
		return objects.get(index);
	}
	
	private Unit getLowestHealthUnit(List<Unit> list) {
		Unit lowest = list.get(0);
		for(Unit u : list) {
			if(u.getHealth() < lowest.getHealth() && !enemiesKilled.contains(u)) {
				lowest = u;
			}				
		}
		return lowest;		
	}
	private Card getLowestManaCard(ArrayList<Card> cards) {
		Card lowest = cards.get(0);
		for(Card c : cards) {
			if(c.getManacost() < lowest.getManacost()) {
				lowest = c;
			}
		}
		return lowest;
	}
	private Card getHighestAttackUnitCard(List<Card> cards) {
		Card highest = cards.get(0);
		for(Card c : cards) {
			if(c.getBigCard().getAttack() > highest.getBigCard().getAttack()) {
				highest = c;
			}
		}
		return highest;
	}
	private Unit getHighestAttackUnit(List<Unit> units) {
		Unit highest = units.get(0);
		for(Unit u : units) {
			if(u.getAttack() > highest.getAttack() && !enemiesKilled.contains(u)) {
				highest = u;
			}
		}
		return highest;
	}
	private Unit getLowestAttackUnit(List<Unit> units) {
		Unit lowest = units.get(0);
		for(Unit u : units) {
			if(u.getAttack() < lowest.getAttack() && !enemiesKilled.contains(u)) {
				lowest = u;
			}
		}
		return lowest;
	}
	
	private ArrayList<Unit> getDamagedUnits() {
		ArrayList<Unit> damaged = new ArrayList<>();
		for(Unit u : getPlayerUnitsOnBoard()) {
			if(u.getMaxHealth() > u.getHealth()) {
				damaged.add(u);
			}
		}
		return damaged;
	}
	


	
	
	public void initialise() {
		
		BasicCommands.setPlayer2Health(out, this);
		BasicCommands.setPlayer2Mana(out, this);	
		
//create avatar and load onto map
		AvatarUnit aiAvatar = (AvatarUnit)BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 42, AvatarUnit.class);
		initialiseAvatar(aiAvatar, 7,2);
		
		cardDeck = OrderedCardLoader.getPlayer2Cards(2);	
		drawStartingHand();	

	}	
}


