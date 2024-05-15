package structures.basic.units;
import commands.BasicCommands;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import structures.GameEventController;
import structures.UnitBuilder;
import structures.basic.Tile;
import structures.basic.GameEventListeners.OpeningGambit;
public class GloomChaser extends Unit implements OpeningGambit{
	

	
	public void unitSpawnedResponse() {
	
		
		//if the tile is left of current tile and its validated for a move, spawn a wraithling there
		if (controller.getBoard().validateTileForMove(currentTile.getTilex() -1 , currentTile.getTiley())) {
			Tile wraithlingTile = controller.getBoard().getTileAtXY(currentTile.getTilex() -1, currentTile.getTiley());
			controller.getUnitBuilder().spawnWraithling(wraithlingTile);
		}     
	}
	
	public void destroy() {
		controller.getEventController().unsubscribeToUnitSpawned(this);
		super.destroy();
	}
	
	@Override
	public void startListenting(GameEventController events) {
		events.subscribeToUnitSpawned(this);
	}
	
}
