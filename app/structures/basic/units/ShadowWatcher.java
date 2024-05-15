package structures.basic.units;

import commands.BasicCommands;
import structures.basic.UnitAnimationType;
import structures.basic.GameEventListeners.Deathwatch;

public class ShadowWatcher extends Unit implements Deathwatch{

	
	public void unitKilledResponse() {
        // Be triggered whenever a unit is killed
        this.attack += 1; 
        this.health += 1;                 
        BasicCommands.setUnitAttack(out, this, this.attack);
        BasicCommands.setUnitHealth(out, this, this.health);
    	
    }


	
	public void destroy() {
		controller.getEventController().unsubscribeToUnitKilled(this);
		super.destroy();
	}
	
}
