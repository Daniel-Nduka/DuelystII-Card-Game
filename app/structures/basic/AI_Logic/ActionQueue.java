package structures.basic.AI_Logic;

import java.util.ArrayList;

import structures.GameController;

/*Wrapped ArrayList that allows actions to be queued up and called off every heartbeat event.
 * 
 * */

public class ActionQueue {
	
	private ArrayList<AiAction> actions;
	private Boolean hasItems = false;
	public Boolean getHasItems() {return hasItems;}
	
	public void add(AiAction action)
	{
		actions.add(action);
		hasItems = true;
	}
	
	public void callAction() {
		if(actions.isEmpty())
		{
			hasItems = false;
			return;
		}
		actions.get(0).action();
		actions.remove(0);
	}
	
	public ActionQueue() {
		actions = new ArrayList<AiAction>();
	}

}
