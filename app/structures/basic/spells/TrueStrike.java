package structures.basic.spells;

import structures.GameController;
import structures.basic.Tile;
import structures.basic.units.Unit;
import utils.StaticConfFiles;

public class TrueStrike extends Spell{
	
	
	public Boolean hurtsEnemy() {return true;}
	public Boolean targetsFriendly() { return false;}
	public Boolean requiresTargets() {return true;}
	public TrueStrike(GameController control) {
		super(control);
	}
	
	@Override
	public Boolean performSpell(Tile selectedTile) {
		if(!selectedTile.getIsOccupied()) return false;
	
		Unit target = selectedTile.getUnit();
		
		//if the unit belongs to the ai player return false
		if(target.getOwner() == controller.getAiPlayer() || target.getIsDestroyed()) {
			return false;					
		}
		//else reduce the health
		else {
			controller.playAnimation(StaticConfFiles.f1_inmolation, selectedTile);
			target.recieveDamageNoCounter(2);;
			return true;
		}
		
				
	}
}
