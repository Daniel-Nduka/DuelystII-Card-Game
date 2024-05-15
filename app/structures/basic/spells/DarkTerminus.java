package structures.basic.spells;

import commands.BasicCommands;
import structures.GameController;
import structures.basic.GameBoard;
import structures.basic.Tile;
import structures.basic.units.Unit;

public class DarkTerminus extends Spell{
	
	public Boolean requiresTargets() {return true;}
	public Boolean excludesAvatar() {return true;}

	
	public DarkTerminus(GameController controller) {
		super(controller);
	}
	
	public Boolean performSpell(Tile tile) {
		
		if(!tile.getIsOccupied()) return false;
		Unit target = tile.getUnit();	
		
		//target of if is enemy and not an avatar
		if(target.getOwner() == controller.getHumanPlayer()|| target.getIsAvatar())
		{
			return false;			
		}
		else {
			target.destroy();
			controller.getUnitBuilder().spawnWraithling(tile);
			return true;
		}
	}
}
