package structures.basic.units;

import commands.BasicCommands;
import structures.basic.GameEventListeners.Deathwatch;

public class ShadowDancer extends Unit implements Deathwatch {


	public void destroy() {
		controller.getEventController().unsubscribeToUnitKilled(this);
		super.destroy();
	}

	public void unitKilledResponse() {
//		Abilities: Deathwatch (whenever a unit, friendly or enemy dies, trigger the following 
//				effect): Deal 1 damage to the enemy avatar and heal yourself for 1.

		//if the avatar itself is killed - it will trigger this method and kill the avatar again - triggering the method
		//so just check for gameover to prevent a loop
		if(controller.isGameOver()) return;
		
		//increase friendly avatar's health ; will update player value at the same time
		Unit avatar = owner.getAvatar();
		if(avatar.getHealth() < avatar.getMaxHealth() ) {
			avatar.setHealth(avatar.getHealth() + 1);
			BasicCommands.setUnitHealth(out, avatar, avatar.getHealth());
		}	

		Unit aiAvatar = controller.getAiPlayer().getAvatar();
		aiAvatar.recieveDamageNoCounter(1);

	}

}

