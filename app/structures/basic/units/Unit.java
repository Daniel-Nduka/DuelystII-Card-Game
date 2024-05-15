package structures.basic.units;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameController;
import structures.GameEventController;
import structures.basic.ImageCorrection;
import structures.basic.Player;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.UnitAnimationSet;
import structures.basic.UnitAnimationType;
import structures.basic.GameEventListeners.NewTurnListener;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit implements NewTurnListener{

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	int id;
	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;

	
	@JsonIgnore
	protected Player owner;
	public void setOwner(Player player) {owner = player;}
	@JsonIgnore
	protected ActorRef out;	
	public Player getOwner() {return owner;}
	
	protected GameController controller;
	public void setController(GameController control) {this.controller = control;}
	public void setActorRef(ActorRef out) {this.out = out;}
		
	@JsonIgnore
	protected Boolean isDestroyed = false;
	public Boolean getIsDestroyed() {return isDestroyed;}
	private void setIsDestroyed(Boolean b) { isDestroyed = b;}
	
	protected Boolean triggerAbilities = true;
	public void setTriggerAbilities(Boolean b) {triggerAbilities = b;}
	public Boolean getTriggerAbilities() {return triggerAbilities;}

//PROVOKE CONDITIONS AND REFERENCES
	
	//used to check if unit is provoking when spawned in by player	
	protected Boolean provoked = false;

@JsonIgnore
protected List<Unit> provokingUnits;
@JsonIgnore
	public List<Unit> getProvokingUnits() {return provokingUnits;}
	public Boolean getIsProvoked() {return provoked;}
	
	public void unProvoke(ProvokingUnit unit) {
		provokingUnits.remove(unit);
		if(provokingUnits.isEmpty()) {
			provoked = false;
		}
	}
	public void provoke(Unit unit) {
		if(provokingUnits == null) {
			provokingUnits = new ArrayList<>();
		}
		provoked= true; 
		provokingUnits.add(unit);
		}
	
	public Boolean isProvokingUnit() {return false;}
	private Boolean notYetMoved = false;
	public Boolean notYetMoved() {return notYetMoved;}
	public void setNotYetMoved(Boolean b) {notYetMoved = b;}

//MOVEMENT & ATTACK CONDITIONS
	protected Boolean outOfMovement = true;
	public Boolean getOutOfMovement() {return outOfMovement;}
	public void setOutOfMovement(Boolean b) {outOfMovement = b;}
	
	protected Boolean outOfAttacks = true;
	public Boolean getOutOfAttacks() {return outOfAttacks;}
	public void setOutOfAttacks(Boolean b) {outOfAttacks = b;}	
	

//STUN/BEAMSHOCK CONDITIONS
	protected Boolean isStunned = false;
	public void stun() {
		isStunned= true; 
		turnsStunned = 1;
		}
	public Boolean getIsStunned() {return isStunned;}
	private int turnsStunned = 1;
	
//MOVEMENT & ATTACK VALUES
	protected int attack;
	public void setAttack(int value) {attack = value;}
	public int getAttack() {return attack;}
	
	protected int health;
	public void setHealth(int value) {health = value;}
	public int getHealth() {return health;}
	
	protected int maxHealth;
	public int getMaxHealth() {return maxHealth;}
	public void setMaxHealth(int h) {maxHealth = h;}
	
//changed to return true only for flying unit.
	private Boolean isFlying = false;
	public void setIsFlying(Boolean b) {isFlying = b;}
	public Boolean getIsFlying() {return isFlying;}
	
//changed to return true only for avatar units
	public Boolean getIsAvatar() {return false;}
	
//CURRENT AND PREVIOUS TILE LOCATIONS
	@JsonIgnore
	protected Tile currentTile;
	@JsonIgnore
	protected Tile previousTile; //only relevant to ai so units move direction can be checked
	@JsonIgnore
	public void setPreviousTile() {previousTile = currentTile;}
	@JsonIgnore
	public Tile getCurrentTile() {return currentTile;}
	
	//used to check if unit is actually on board before triggering abilities
	protected Boolean isSpawned = false;
	public void setIsSpawned(Boolean b) {isSpawned = b;}
	
	public Unit() {}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
	}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
		this.currentTile = currentTile;
	}
		
	
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}
	
	public void attack(Unit attackedUnit) {		
		//in case the ai is telling it to attack after it has been destroyed (crashing the game)
		if(isDestroyed) return;
		if(provoked && !provokingUnits.contains(attackedUnit)) {
			return;
		}
		playAnimation(UnitAnimationType.attack);
		attackedUnit.recieveAttack(this);
		setOutOfAttacks(true);		
		setOutOfMovement(true);
	}
	
	public void recieveAttack(Unit attackingUnit) {

		health -= attackingUnit.getAttack();
		playAnimation(UnitAnimationType.hit);
		if(health <= 0)
		{
			setHealth(0);
			BasicCommands.setUnitHealth(out, this, health);			
			destroy();

			
		}
		else {
			BasicCommands.setUnitHealth(out, this, health);			
			counterAttack(attackingUnit);				
		}		
	}
	
	public void recieveDamageNoCounter(int damage) {
		health -= damage;
		playAnimation(UnitAnimationType.hit);
		if(health <= 0)
		{
			setHealth(0);
			BasicCommands.setUnitHealth(out, this, health);		
			destroy();	
			
		}		
		else {
			BasicCommands.setUnitHealth(out, this, health);		

		}
	}
	
	public void counterAttack(Unit target) {
		if(attack <= 0) {
			return;
		}
		playAnimation(UnitAnimationType.attack);
		target.recieveCounterAttack(this);
	}

	public void recieveCounterAttack(Unit counteringUnit) {
		health -= counteringUnit.getAttack();
		playAnimation(UnitAnimationType.hit);
		if(this.getHealth() <= 0)
		{
			setHealth(0);
			BasicCommands.setUnitHealth(out, this, health);
			destroy();
		}
		else {
			BasicCommands.setUnitHealth(out, this, health);
		}
	}
	
	public void move(Tile tile) {
			
			//in case the ai is telling it to move after it has been destroyed (crashing the game)
			if(isDestroyed) return;
			int currentX = currentTile.getTilex();
			int currentY = currentTile.getTiley();
			
			//check if it is moving left
			if(previousTile == null) { // if it is a human unit moving
				currentX = currentTile.getTilex();
				currentY = currentTile.getTiley();
			}
			else {// if it is an ai unit moving (where current tile has been changed before it has been moved
				currentX = previousTile.getTilex();
				currentY = previousTile.getTiley();
			}
			if(tile.getTilex() < currentX) {
				//if so check if adjascent left tile is occupied - move by y if so
				if(controller.getBoard().validateTileForMove(currentX - 1, currentY)) {
					BasicCommands.moveUnitToTile(out, this, tile);
				}
				else {
					BasicCommands.moveUnitToTile(out, this, tile, true);
				}
			}
			//check if moving right
			else if(tile.getTilex() > currentX) {
				//if so check if adjascent right tile is occupied - move by y if so
				if(controller.getBoard().validateTileForMove(currentX + 1, currentY)) {
					BasicCommands.moveUnitToTile(out, this, tile);
				}
				else {
					BasicCommands.moveUnitToTile(out, this, tile, true);
				}
			}
			
		//else it is probably moving by y axis anyway
		else {
			BasicCommands.moveUnitToTile(out, this, tile);
		}
			setPositionByTile(tile);
			notYetMoved = false;
			
			//tell provking units to recalculate
			controller.getEventController().unitMovedEvent();			
			playAnimation(UnitAnimationType.move);
			setOutOfMovement(true);

	}
	
	public void moveAndAttack(Unit target, Tile position) {
		move(position);
		
		//waiting for unit stopped event is unreliable so just wait for a set 1 seconds instead
			try { 
				Thread.sleep(2000);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		
		attack(target);
	}
	
	/*ordinarily would make this abstract but we have direct Unit objects 
	 * Only overriden by opening Gambit units
	 * used to subscribe them to spawn events only after they have been spawned in
	 */
	public void startListenting(GameEventController events) {
		return;
	}
	
	public void destroy() {
		isDestroyed =true;
		
		playAnimation(UnitAnimationType.death);
		BasicCommands.deleteUnit(out, this);
		
		controller.getEventController().unsubscribeToNewTurn(this);
		if(triggerAbilities) {
		controller.getEventController().unitKilledEvent();
		}
		currentTile.removeUnit();
		owner.getPlayerUnitsOnBoard().remove(this);
	}

//helper method to play animation and wait for it to stop
protected void playAnimation(UnitAnimationType animation) {
	int miliseconds = BasicCommands.playUnitAnimation(out, this, animation);
	try {
		Thread.sleep(miliseconds);
	}
	catch (InterruptedException e){
		e.printStackTrace();
	}
}
	
	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
		if(currentTile != null)
		{
				currentTile.removeUnit();			
		}
		currentTile = tile;
		tile.setUnit(this);
	}


	//used by ai units when their move action is queued before it happens on screen
	//so ai can calculate taking into account changes

	/*recived by the GameController : resets movement and attack
	 * checks stun and provoke conditions
	 * */
	 
	public void newTurnResponse() {
		setOutOfMovement(false);
		setOutOfAttacks(false);
		
		if(isStunned) {
			if(turnsStunned == 1) {
				turnsStunned--;
			}
			else if(turnsStunned == 0) {
				turnsStunned = 1;
				isStunned = false;
			}
		}
	}
	
}
