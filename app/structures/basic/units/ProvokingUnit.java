package structures.basic.units;

import java.util.ArrayList;
import java.util.List;

import structures.basic.Tile;
/*
 * This covers all units with the provoking ability. They listen to a UnitMoved event
 * which is triggered after a unit is moved or spawned. Works by adding itself to the enemy unit's 
 * list of provoking units. If list is not empty the unit is provoked and can only target units 
 * that are in the list.
 */

public class ProvokingUnit extends Unit{
	
	private List<Unit> provokedUnits;
	
	@Override
	public Boolean isProvokingUnit() {return true;}

	@Override
	public void destroy() {	
		controller.getEventController().unsubscribeToUnitMoved(this);
		if(provokedUnits != null) {
			for(Unit u : provokedUnits) {
				u.unProvoke(this);
			}
		}
		super.destroy();
		
	}
	
	public void provokeAbility() {
		
	
		if(provokedUnits == null) {
			provokedUnits = new ArrayList<>();
		}
		//clears all units to account for units no longer in provoking range		
		for(Unit u : provokedUnits) {			
			u.unProvoke(this);
		}
		provokedUnits.clear();
		
		//gets all units in range and adds itself to their list of provoking units	

		ArrayList<Unit> enemies = controller.getBoard().getEnemiesInAttackRange(this);
		for(Unit unit : enemies) {
			if(!unit.notYetMoved()) {
			unit.provoke(this);
			provokedUnits.add(unit);
			}
		}
		}

}
