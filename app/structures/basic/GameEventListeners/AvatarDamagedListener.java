package structures.basic.GameEventListeners;

import structures.basic.units.Unit;

/*Triggered by avatar when they recieve and attack or other damage. 
 * Listened to by SilverGuard Knight only (Zeal ability). * 
 * */

public interface AvatarDamagedListener {
	
	public void AvatarDamagedResponse(Unit unit);

}
