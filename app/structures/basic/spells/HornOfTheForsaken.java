package structures.basic.spells;
import structures.GameController;
import structures.basic.Tile;
import structures.basic.units.AvatarUnit;
import structures.basic.units.Unit;
import utils.StaticConfFiles;

public class HornOfTheForsaken extends Spell{
	
	public Boolean targetsAvatar(){return true;}

	public HornOfTheForsaken(GameController control) {
		super(control);
	}
	
	public Boolean performSpell(Tile selectedTile) {	
		if(!selectedTile.getIsOccupied()) return false;
		Unit target = selectedTile.getUnit();
		if(target.getOwner() != controller.getHumanPlayer() ||
				!target.getIsAvatar()) {
			return false;
		}		
		else {
			AvatarUnit unit = (AvatarUnit)target;
			controller.playAnimation(StaticConfFiles.f1_buff, selectedTile);
			unit.applyArtefact();
			return true;
		}		
	}	

}
