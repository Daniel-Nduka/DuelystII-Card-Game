package structures.basic.spells;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameController;
import structures.basic.Tile;

public class WraithlingSwarm extends Spell{
	
	private int wraithlingsLeft = 3;
	
	protected Boolean active = true;	
	public void setActive(Boolean b) {active = b;}
	public Boolean getActive() {return active;}
	
	private Boolean started = false;
	public int getRepetitionsLeft() {return wraithlingsLeft;}
	public Boolean requiresSpawnRadius() {return true;}
	private ArrayList<Tile> tiles;
	
	public WraithlingSwarm(GameController control) {
		super(control);
	}

	@Override
	public Boolean performSpell(Tile tile) {
		if(!active) return false;
		ActorRef out = controller.getActorRef();
		
		//Get valid spawn radius the first time it is played
		if(!started) {
			tiles = controller.getBoard().getAllUnitSpawnRadius(controller.getHumanPlayer().getPlayerUnitsOnBoard());
			started = true;
		}
		//if it is not a valid tile to spawn on return: the card can be played again
		if(!tiles.contains(tile)) return false;
		
		controller.getUnitBuilder().spawnWraithling(tile);
		wraithlingsLeft--;
		if(wraithlingsLeft == 1) {
			BasicCommands.addPlayer1Notification(out, wraithlingsLeft + " use left", 1);
		}
		else {
			BasicCommands.addPlayer1Notification(out, wraithlingsLeft + " uses left", 1);
		}
		
		tiles.remove(tile);
		return true;
	}
}
