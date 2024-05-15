package structures.basic.spells;

import structures.GameController;
import structures.basic.Tile;
import structures.basic.units.Unit;
import utils.StaticConfFiles;

public class BeamShock extends Spell{
	
	public Boolean requiresTargets() {return true;}
	
	public Boolean effectsEnemy() {return true;}
	
	public BeamShock(GameController control) {
		super(control);
	}
	
	public Boolean performSpell(Tile selectedTile) {
		
		if(!selectedTile.getIsOccupied()) return false;
		Unit target = selectedTile.getUnit();
		
		controller.playAnimation(StaticConfFiles.f1_inmolation, selectedTile);
		target.stun();
		return true;	
		}
}
