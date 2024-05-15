package structures.basic.GameEventListeners;

/*Used by every unit and player to perform certain actions (i.e. reset moves) relevant to new turn.
 * Called by GameController when the Ai passes control back to human player. * 
 * */
public interface NewTurnListener {
	
	public void newTurnResponse();


}
