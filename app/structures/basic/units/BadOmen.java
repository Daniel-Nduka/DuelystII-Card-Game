package structures.basic.units;

import commands.BasicCommands;
import structures.basic.GameEventListeners.Deathwatch;

public class BadOmen extends Unit implements Deathwatch {


	public void unitKilledResponse() {

		this.setAttack(attack + 1);
		
		BasicCommands.setUnitAttack(out, this, this.getAttack());
	}
	
	public void destroy() {
		controller.getEventController().unsubscribeToUnitKilled(this);
		super.destroy();
	}

}
