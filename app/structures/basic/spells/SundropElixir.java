package structures.basic.spells;

import commands.BasicCommands;
import structures.GameController;
import structures.basic.Tile;
import structures.basic.units.Unit;
import utils.StaticConfFiles;
import akka.actor.ActorRef;


public class SundropElixir extends Spell{
	
	@Override
	public Boolean hurtsEnemy() {return false;}
	@Override
	public Boolean targetsFriendly() { return true;}


	public SundropElixir(GameController control) {
		super(control);
	}
	@Override	
	public Boolean performSpell(Tile selectedTile) {
		
			if(!selectedTile.getIsOccupied()) return false;
			
			Unit target = selectedTile.getUnit();
			
			//if the unit is not the aiPlayer (the player using the card or its target health is already = the max health return false
			if(target.getOwner() != controller.getAiPlayer() || target.getHealth() >= target.getMaxHealth()) {
				return false;
			}
			else {
				//else add 4 to the health
				int newHealth = target.getHealth() + 4;
				
				//If the health becomes more than the maxHealht, set the target unit to maxhealth
				if (newHealth > target.getMaxHealth()) {
				
					target.setHealth(target.getMaxHealth());
				}
				else {
					target.setHealth(newHealth);
				}
				controller.playAnimation(StaticConfFiles.f1_buff, selectedTile);
				ActorRef out = controller.getActorRef();
				BasicCommands.setUnitHealth(out, target, target.getHealth());
				return true;
			}
		}
}
