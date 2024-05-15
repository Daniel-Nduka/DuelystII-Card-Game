package structures.basic.units;

import java.util.ArrayList;

import structures.GameEventController;
import structures.basic.GameEventListeners.OpeningGambit;

public class NightsorrowAssassin extends Unit implements OpeningGambit {
	
	 public void unitSpawnedResponse() {
	        // Check adjacent tiles for an enemy unit below max health and destroy one
	        ArrayList<Unit> adjacentUnits = controller.getBoard().getEnemiesInAttackRange(this);
	        for (Unit unit : adjacentUnits) {
	            if (unit != null &&  unit.getHealth() < unit.getMaxHealth() && !unit.getIsAvatar()) {
	                unit.destroy();
	                break; // only destroy one 
	            }
	        }
	 }

	public void destroy() {
		controller.getEventController().unsubscribeToUnitSpawned(this);
		super.destroy();
	}
	
	@Override
	public void startListenting(GameEventController events) {
		events.subscribeToUnitSpawned(this);
	}
}
