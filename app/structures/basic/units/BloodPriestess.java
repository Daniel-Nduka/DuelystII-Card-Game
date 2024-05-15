package structures.basic.units;
import java.util.ArrayList;

import commands.BasicCommands;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import structures.UnitBuilder;
import structures.basic.GameEventListeners.Deathwatch; 

public class BloodPriestess extends Unit implements Deathwatch{
	
	
	@Override
	public void unitKilledResponse() {
		controller.getUnitBuilder().spawnWraithlingAroundUnit(this);
	
	}

	public void destroy() {
		controller.getEventController().unsubscribeToUnitKilled(this);
		super.destroy();
	}	
	
}
