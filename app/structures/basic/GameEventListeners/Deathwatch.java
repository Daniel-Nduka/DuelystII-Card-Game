package structures.basic.GameEventListeners;

//used by units with the 'deathwatch' ability who will perform their ability when a unit 
//on the board is killed. Depending on which player the killed unit belonged to.

public interface Deathwatch {
	
	public void unitKilledResponse();
	public Boolean getIsDestroyed();

}
