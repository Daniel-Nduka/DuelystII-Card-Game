package structures.basic.units;

import commands.BasicCommands;
import structures.GameEventController;
import structures.basic.GameEventListeners.OpeningGambit;
import utils.StaticConfFiles;

public class SilverGuardSquire extends Unit implements OpeningGambit {
	
	@Override
	public void unitSpawnedResponse() {
//		Give any adjacent allied unit that is directly in-front (left)
//		or behind (right) the owning player’s avatar +1 attack and +1 health permanently
//		(this increases those creatures maximum health).
		if(!isSpawned) return;
		method();
	}
	@Override
	public void startListenting(GameEventController events) {
		events.subscribeToUnitSpawned(this);
	}
	

	public void method() {
		int currentX = owner.getAvatar().getCurrentTile().getTilex();
		int currentY = owner.getAvatar().getCurrentTile().getTiley();
		Unit left;
		Unit right;
	
		//CHECKS AND BUFFS UNITS TO LEFT OF Avatar
		if (controller.getBoard().checkTileForFriendly(currentX - 1, currentY, owner)) {
			left = controller.getBoard().getTileAtXY(currentX - 1, currentY).getUnit();
			
			left.setHealth(left.getHealth() + 1);
			left.setAttack(left.getAttack() + 1);
			
			controller.playAnimation(StaticConfFiles.f1_buff, controller.getBoard().getTileAtXY(currentX - 1, currentY));

			BasicCommands.setUnitHealth(out, left, left.getHealth());
			BasicCommands.setUnitAttack(out, left, left.getAttack());
		}
		//CHECKS AND BUFFS UNITS TO RIGHT OF Avatar

		if (controller.getBoard().checkTileForFriendly(currentX + 1, currentY, owner)) {
			right = controller.getBoard().getTileAtXY(currentX + 1, currentY).getUnit();
			
			right.setHealth(right.getHealth() + 1);
			right.setAttack(right.getAttack() + 1);
			
			controller.playAnimation(StaticConfFiles.f1_buff, controller.getBoard().getTileAtXY(currentX + 1, currentY));
			
			BasicCommands.setUnitHealth(out, right, right.getHealth());
			BasicCommands.setUnitAttack(out, right, right.getAttack());
		}

	}
	public void destroy() {
		controller.getEventController().unsubscribeToUnitSpawned(this);
		super.destroy();
	}
	
	

}