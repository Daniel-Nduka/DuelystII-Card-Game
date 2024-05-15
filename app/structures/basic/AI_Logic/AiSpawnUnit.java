package structures.basic.AI_Logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.units.Unit;


public class AiSpawnUnit extends AiAction{
	
	private Unit unit;
	private Card card;
	private Tile tile;
	private int cardPosition;
	private AiPlayer ai;

	public AiSpawnUnit(Card unitCard, Tile tile, AiPlayer player, int position) {
		
		this.unit = player.getController().getUnitBuilder().createUnit(unitCard);
		this.tile = tile;		
		this.ai = player;
		this.card = unitCard;
		this.cardPosition = position;
		this.unit.setOwner(player);
		ai.getPlayerUnitsOnBoard().add(unit);
		unit.setPositionByTile(tile);		
		
	}
		
		public  void action() {
			ActorRef out = ai.getActorRef();
			
			BasicCommands.drawCard(out, card ,cardPosition, 1);
			
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e){
				BasicCommands.addPlayer1Notification(ai.getActorRef(), "huh!", 2);
			}	
			finally {
				BasicCommands.drawCard(out, card ,cardPosition, 0);
				ai.spawnUnit(card, unit, tile);			
			}			
		}
		


}
