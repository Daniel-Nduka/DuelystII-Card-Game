package structures.basic.AI_Logic;

/*Wraps primary functions (move, spawn, attack) into objects to allow them to be stored and queued.
 * Allows ai decisions and actions to appear sequential in real time
 * */

public abstract class AiAction {
	
	public abstract void action();

}
