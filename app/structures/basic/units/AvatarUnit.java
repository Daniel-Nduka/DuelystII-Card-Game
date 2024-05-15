package structures.basic.units;

import java.util.ArrayList;
import java.util.Random;

import commands.BasicCommands;
import structures.basic.UnitAnimationType;

public class AvatarUnit extends Unit {
	
	protected Boolean outOfMovement = false;
	protected Boolean outOfAttacks = false;
	
	protected int maxHealth = 20;
	public int getMaxHealth() {return maxHealth;}
	public void setMaxHealth(int h) {maxHealth = h;}
	public void setOutOfAttacks(Boolean b) {outOfAttacks = b;}
	public Boolean getOutOfAttacks() {return outOfAttacks;}
	public Boolean getOutOfMovement() {return outOfMovement;}
	public void setOutOfMovement(Boolean b) {outOfMovement = b;}
	
	
	public void setHealth(int health) {
		this.health = health;
		owner.changeHealth(health);
	}
	public Boolean getIsAvatar() {return true;}
	private Boolean artefact = false;
	private int artefactRobustness = 3;
	
	public void applyArtefact() {		
		artefact = true;
		artefactRobustness = 3;
		}
	
	public void checkArtefact() {
		if(artefact && artefactRobustness > 0) {
			artefactRobustness--;
			controller.getUnitBuilder().spawnWraithlingAroundUnit(this);
		}		
	}	
	
	public void recieveAttack(Unit attackingUnit) {
		super.recieveAttack(attackingUnit);
		owner.changeHealth(this.health);	
		controller.getEventController().avatarDamagedEvent(this);
	}

	public void recieveCounterAttack(Unit counteringUnit) {
		super.recieveCounterAttack(counteringUnit);
		owner.changeHealth(this.health);	
		controller.getEventController().avatarDamagedEvent(this);	
	}


	public void recieveDamageNoCounter(int damage) {
		health -= damage;
		playAnimation(UnitAnimationType.hit);
		owner.changeHealth(getHealth());
		if(health <= 0)
		{
			setHealth(0);
			destroy();			
		}
		else {
			BasicCommands.setUnitHealth(out, this, health);		
		}
		controller.getEventController().avatarDamagedEvent(this);
		
	}
	
	public void destroy() {
		isDestroyed =true;
		
		playAnimation(UnitAnimationType.death);
		BasicCommands.deleteUnit(out, this);
		controller.getEventController().unsubscribeToNewTurn(this);
		
		currentTile.removeUnit();
		owner.getPlayerUnitsOnBoard().remove(this);
		controller.gameOverEvent(this);
	}
	
	
	public void attack(Unit unit) {
		super.attack(unit);	
		checkArtefact();
	}
	
	public void counterAttack(Unit unit) {
		super.counterAttack(unit);
		checkArtefact();
	}
	

}
