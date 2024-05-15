package structures.basic.units;
import commands.BasicCommands;
import structures.basic.Tile;
import structures.basic.GameEventListeners.AvatarDamagedListener;

public class SilverguardKnight extends ProvokingUnit implements AvatarDamagedListener{
	
	public void AvatarDamagedResponse(Unit unit) {
		if(!isSpawned) return;
		if(controller.checkIfAiUnit(unit)) {
			this.setAttack(this.attack += 2);
			BasicCommands.setUnitAttack(out, this, attack);
		}		
	}		

}
