package structures;

import java.util.ArrayList;
import java.util.List;

import structures.basic.GameEventListeners.AvatarDamagedListener;
import structures.basic.GameEventListeners.Deathwatch;
import structures.basic.GameEventListeners.NewTurnListener;
import structures.basic.GameEventListeners.OpeningGambit;
import structures.basic.units.ProvokingUnit;
import structures.basic.units.Unit;

public class GameEventController {
	
	private List<NewTurnListener> newTurnListeners;
	private List<Deathwatch> unitKilledListeners;
	private List<OpeningGambit> unitSpawnedListeners;
	private List<AvatarDamagedListener> avatarDamagedListeners;
	private List<ProvokingUnit> unitMoveListeners;
	
	//to handle edge cases where the unit is destroyed and unsubscribes during its own reponse
	private List<Deathwatch> destroyedUnitKilledListeners;
	private List<OpeningGambit> destroyedUnitSpawnedListeners;
	
	public GameEventController() {
		newTurnListeners = new ArrayList<>();
		unitKilledListeners = new ArrayList<>();
		unitSpawnedListeners =new ArrayList<>();
		avatarDamagedListeners = new ArrayList<>();
		unitMoveListeners = new ArrayList<>();
		
		destroyedUnitKilledListeners = new ArrayList<>();
		destroyedUnitSpawnedListeners = new ArrayList<>();
	}
	
	
	//EVENT SUBSCRIPTION / UNSUBSCRIPTION //

	//Units/objects should send themselves as parameters 
	//Units must unsubscribe if they are destroyed
	public void subscribeToNewTurn(NewTurnListener subscriber){
		newTurnListeners.add(subscriber);
	}
	public void unsubscribeToNewTurn(NewTurnListener subscriber){
		newTurnListeners.remove(subscriber);
	}
	public void subscribeToUnitKilled(Deathwatch subscriber) {
		unitKilledListeners.add(subscriber);
	}

	public void unsubscribeToUnitKilled(Deathwatch subscriber) {
		destroyedUnitKilledListeners.add(subscriber);
	}
	public void subscribeToUnitSpawned(OpeningGambit subscriber) {
		unitSpawnedListeners.add(subscriber);
	}
	public void subscribeToUnitMoved(ProvokingUnit subscriber) {
		unitMoveListeners.add(subscriber);
	}
	public void unsubscribeToUnitMoved(ProvokingUnit subscriber) {
		unitMoveListeners.remove(subscriber);
	}	

	//Opening Gambit units must be removed into seperate lists;
	//in case the nightsorrow assasin destroys them during the spawn event
	public void unsubscribeToUnitSpawned(OpeningGambit subscriber) {
		destroyedUnitSpawnedListeners.add(subscriber);
	}
	public void subscribeToAvatarDamaged(AvatarDamagedListener subscriber) {
		avatarDamagedListeners.add(subscriber);
	}
	public void unsubscribeToAvatarDamaged(AvatarDamagedListener subscriber) {
		avatarDamagedListeners.remove(subscriber);
	}
	
	//to help prevent events looping after end turn
 public void unsubscribeAll() {
	 
	 	newTurnListeners.clear();
		unitKilledListeners.clear();
		avatarDamagedListeners.clear();
		unitMoveListeners.clear();
		destroyedUnitSpawnedListeners.clear();	
 }
	
	
//EVENTS
 
 //DEATHWATCH
 //called by units in their destroy method
	public void unitKilledEvent() {
		for(Deathwatch subscriber : unitKilledListeners) {
			if(destroyedUnitKilledListeners.contains(subscriber)) {
				continue;
			}
			else {
				subscriber.unitKilledResponse();
			}
		}
		for(Deathwatch subscriber : destroyedUnitKilledListeners) {
			unitKilledListeners.remove(subscriber);
		}
	}

//OPENING GAMBIT
	
	//called by players when they spawn a unit
	//makes sure listeners aren't removed if they are destroyed during the event responses
	public void unitSpawnedEvent() {
		for(OpeningGambit subscriber : unitSpawnedListeners)	{
				if(destroyedUnitSpawnedListeners.contains(subscriber)) {
					continue;		
				}
				else {
					subscriber.unitSpawnedResponse();
				}
		}
		for(OpeningGambit sub : destroyedUnitSpawnedListeners) {
			unitSpawnedListeners.remove(sub);
		}
	}

/*Called by the Avatars when they recieved damaged
 * takes in the avatar damaged which the listeners will use to check which player it belongs to
 *  
 * */
	public void avatarDamagedEvent(Unit avatar) {
		for(AvatarDamagedListener subscriber : avatarDamagedListeners)	{
			subscriber.AvatarDamagedResponse(avatar);
		}
	}
//Used by provoke units to recalculate nearby enemies
	public void unitMovedEvent() {
		for(ProvokingUnit subscriber : unitMoveListeners)	{
			subscriber.provokeAbility();
		}
	}	

	public void newTurnEvent() {
		for(NewTurnListener listener : newTurnListeners) {
			listener.newTurnResponse();
		}
	}
	

}
