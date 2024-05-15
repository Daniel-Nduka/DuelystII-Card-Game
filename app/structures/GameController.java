package structures;
import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.GameBoard;
import structures.basic.HumanPlayer;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.AI_Logic.AiPlayer;
import structures.basic.spells.Spell;
import structures.basic.spells.SpellBuilder;
import structures.basic.units.Unit;
import utils.BasicObjectBuilders;


public class GameController {	

private	GameBoard board;
public GameBoard getBoard() {return board;}
private	ActorRef out;
public  ActorRef getActorRef() {return out;}

//UNIT & SPELL BUILDERS
private UnitBuilder unitBuilder;
public UnitBuilder getUnitBuilder() {return unitBuilder;}
private SpellBuilder spellBuilder;
public SpellBuilder getSpellBuilder() {return spellBuilder;}

//Player and Avatar References
private HumanPlayer humanPlayer;
public Player getHumanPlayer() {return humanPlayer;}
public Player getAiPlayer() {return aiPlayer;}
private AiPlayer aiPlayer;

public Boolean checkIfAiUnit(Unit unit) {
	return aiPlayer.getPlayerUnitsOnBoard().contains(unit);
}
public Boolean checkIfHumanUnit(Unit unit) {
	return humanPlayer.getPlayerUnitsOnBoard().contains(unit);
}

//tracks which player is currently active
private Boolean humanPlayerTurn = true;
public Boolean getIfPlayerTurn() {return humanPlayerTurn;}
private Boolean aiPlayerTurn = false;
private int numberOfTurns = 1;
private Boolean newTurn = false;

//variables storing the objects clicked on the board
private Unit selectedPlayerUnit;
private Tile selectedTile;
private Card selectedCard;
private Spell selectedSpell;

//variables to control the condition of clicked events
private Boolean tileSelected = false;
private Boolean humanPlayerUnitSelected = false;
private Boolean unitCardSelected = false;
private Boolean spellCardSelected = false;
private int cardPosition;

//Wait for units' move animations
private Boolean unitStillMoving = false;
public  void setUnitMoving(Boolean b) {unitStillMoving = b;}
public  Boolean getIsUnitMoving() {return unitStillMoving;}

//Lists of tiles to check if clicked tile is valid move/spawn radius
public ArrayList<Tile> validSpawnRadius;
public ArrayList<Tile> validMoveRadius;

//EventSubscribers
private GameEventController eventController;
public GameEventController getEventController() {return eventController;}

//checking for GameOver
private Boolean gameOver = false;
public Boolean isGameOver() {return gameOver;}


//Communicates heartbeat ticks with ai player during ai turn
private Boolean listentingToHeartbeat = false;
public void listenForHeartbeat(Boolean b) {
	listentingToHeartbeat = b;		
}


public GameController(ActorRef ref){
	out = ref;
	eventController = new GameEventController();
}

// CLICKED EVENT HANDLING //
//Called by the relevant  event processors

public void unitSelected(Unit unit){
	if(unitStillMoving || ! humanPlayerTurn) return;
	
	//if a player unit was not clicked on previously they are likely to be moving the unit or attacking (if they clicked a human unit)
	unhighlightCard();
	
	//if it is their own unit
	if(unit.getOwner() == humanPlayer && !spellCardSelected) 
	{	
		if(unit.getIsStunned()) { 
			BasicCommands.addPlayer1Notification(out, "stunned", 2);
			return;
		}
		//if it is provoked make sure it is only attacking a provoker
		else if(unit.getIsProvoked() && !unit.getOutOfAttacks()) {
			board.refreshAllMoveTiles();
			board.highlightEnemies(unit.getProvokingUnits());
			humanPlayerUnitSelected = true;
			selectedPlayerUnit = unit;	
			
		}
		//If it can move and attack
		else if(!unit.getOutOfMovement()&& !unit.getOutOfAttacks()&& !unit.getIsProvoked())			
		{
			board.highlightMovement(unit, humanPlayer);
			humanPlayerUnitSelected = true;
			selectedPlayerUnit = unit;
		}
		//if it is only attacking
		else if(!unit.getOutOfAttacks()) {	
			
				board.highlightAttack(unit);
				humanPlayerUnitSelected = true;
				selectedPlayerUnit = unit;		}
		
		else {
			board.refreshAllTiles();
		}
	}
	
	/*if a unit was selected in the previous click then they are likely to be attacking 
	 * if the next unit clicked belongs to the AI
	 */
	else if(humanPlayerUnitSelected && unit.getOwner()== aiPlayer)
	{
		if(!selectedPlayerUnit.getOutOfAttacks() && !selectedPlayerUnit.getIsStunned() ) {
		// attack enemy unit with selected unit 
		if(board.checkIfenemyInRadius(selectedPlayerUnit, unit))// if it is directly adjascent
		{
			if(!selectedPlayerUnit.getIsProvoked() || selectedPlayerUnit.getProvokingUnits().contains(unit)) {
			selectedPlayerUnit.attack(unit);	
			}
		}
		//if it is in movement range : to move and attack
		else if (!selectedPlayerUnit.getIsProvoked()){ 
			List<Unit> enemies= board.getOneUnitsEnemiesInMovementRange(selectedPlayerUnit, humanPlayer);
			if(enemies.contains(unit)) {
				Tile position = board.getNearestTileToEnemy(selectedPlayerUnit, unit);
				board.refreshAllTiles();

				selectedPlayerUnit.moveAndAttack(unit,position);			}

			}
		}
		board.refreshAllTiles();
	}
	//if a spell card was previously selected they are targeting a unit - apply that spell to the unit
	else if(spellCardSelected)
	{
		if(selectedSpell == null) return;
		if(selectedCard.getManacost() <= humanPlayer.getMana()) {
			humanPlayer.playSpell(selectedSpell,selectedCard, unit.getCurrentTile());			
		}		
		else {
			BasicCommands.addPlayer1Notification(out,"Not Enough Mana!" , 2);
		}
		board.refreshAllTiles();
	}
	
	tileSelected = false;
	unitCardSelected = false;
	spellCardSelected = false;
	
	//cancels wraithling spawn if there are still uses left
	if(selectedSpell != null && selectedSpell.getActive()) {
		selectedSpell.setActive(false);
	}
}


public void tileSelected(Tile tile)
{
	if(unitStillMoving || ! humanPlayerTurn) return;	
	unhighlightCard();

	/*if a unit card was previously selected then they are likely to spawn a unit on the selected tile 
	 * if that tile is within its spawn radius
	 */
	 if(unitCardSelected)
	{
		if(validSpawnRadius.contains(tile)) 
		{
			if(humanPlayer.getMana() >= selectedCard.getManacost()) {
				humanPlayer.spawnUnit(selectedCard, tile);
			}
			else {
				BasicCommands.addPlayer1Notification(out,"Not Enough Mana!" , 2);
			}
		}
		
		board.refreshAllTiles();
	}
	/*if a unit is currently selected & if the tile is in the valid movement radius of the selected unit
	they are likely to be moving it to the new tile
	*/
	else if(humanPlayerUnitSelected && board.getMovementRange(selectedPlayerUnit).contains(tile))
	{
		if(!selectedPlayerUnit.getOutOfMovement() && !selectedPlayerUnit.getIsStunned() && !selectedPlayerUnit.getIsProvoked())
		{
			selectedPlayerUnit.move(tile);
			board.refreshAllTiles();
		}
	}
	 //only relevant to wraithling swarm
	else if(spellCardSelected)
	{
		if(selectedSpell == null) return;
		if(selectedCard.getManacost() <= humanPlayer.getMana()) {
			
			//play the spell on the tile and save the fact that the spell is still active
			humanPlayer.playSpell(selectedSpell,selectedCard,tile);
			selectedTile = tile;
			
			tileSelected = true;
			humanPlayerUnitSelected = false;
			unitCardSelected = false;
			spellCardSelected = false;

			return;			
		}	
		else {
			BasicCommands.addPlayer1Notification(out,"Not Enough Mana!" , 2);
			board.refreshAllTiles();
		}
	}
	//if a spell is still active then perform it until condition is met (i.e. wraithling spawn)
	else if(selectedSpell != null) {
		if(selectedSpell.getRepetitionsLeft()> 0) {
			selectedSpell.performSpell(tile);
			
			if(selectedSpell.getRepetitionsLeft() < 1) {
				board.refreshAllTiles();
			}
			return;
		}		
	}
	//if there is no unit/ card currently selected then there is nothing to do
	//but keep the selected spell active
	if(!humanPlayerUnitSelected && !unitCardSelected && !spellCardSelected)	{
		
		return;
	}
	else if(selectedSpell != null && selectedSpell.getActive()) {
		selectedSpell.setActive(false);
	}
	
	selectedTile = tile;
	tileSelected = true;
	humanPlayerUnitSelected = false;
	unitCardSelected = false;
	spellCardSelected = false;

}

public void cardSelected(int handPosition){
	
	if(unitStillMoving || ! humanPlayerTurn) return;
	if(handPosition >= humanPlayer.getSizeOfHand()) return;
	
	
	//unhighlight the previously highlighted card if it is not first card clicked in game
	if(selectedCard != null && !newTurn) {
		unhighlightCard();
	}
	newTurn = false;
	
	
	selectedCard = humanPlayer.getCardFromHandPosition(handPosition);
	highlightCard(selectedCard, handPosition);
	
	//if the selected card is a unit
	if(selectedCard.getIsCreature())
	{
		if(selectedCard.getManacost() <= humanPlayer.getMana()) {
		//sends all the player's units to the gameboard to find their valid spawn radius
		unitCardSelected = true;
		spellCardSelected = false;
		
		validSpawnRadius = board.getAllUnitSpawnRadius(humanPlayer.getPlayerUnitsOnBoard());
		
		board.unhighlightAllEnemies();
		board.highlightTiles(validSpawnRadius);
		}
		else {
		//not enough mana so don't show spawn radius
			unitCardSelected = true;
			spellCardSelected = false;
			board.refreshAllTiles();
		}
		
	}
	// if it is a spell
	else if (!selectedCard.getIsCreature())
		{				
		spellCardSelected = true;
		unitCardSelected = false;
		tileSelected = false;
		humanPlayerUnitSelected = false;
		
		selectedSpell = spellBuilder.createSpell(selectedCard);
		
		//Highlight the tile relevant to the spell's effects: enemies, avatar or spawnable tiles
		if(selectedSpell.requiresSpawnRadius()) {
			board.highlightTiles(
			board.getAllUnitSpawnRadius(humanPlayer.getPlayerUnitsOnBoard())
			);			
		}
		else if(selectedSpell.requiresTargets()) {
			
			if(selectedSpell.excludesAvatar()) { //used for excluding the ai avatar from Dark Terminus targets
				ArrayList<Unit> aiUnits =  new ArrayList<>();
				aiUnits.addAll(aiPlayer.getPlayerUnitsOnBoard());
				aiUnits.remove(aiPlayer.getAvatar());
				board.highlightEnemies(aiUnits);			
			}
			else { 
				//highlight all enemies (not applicable to any actual current spell)
				board.highlightEnemies(aiPlayer.getPlayerUnitsOnBoard());
			}
		}
		else if(selectedSpell.targetsAvatar()) {
			//i.e. if Horn of Forsaken highlight human avatar tile white
			board.highlightSingleTile(humanPlayer.getAvatar().getCurrentTile());
		}
		return;
	}		
	tileSelected = false;
	humanPlayerUnitSelected = false;
	
	//the active spell (wraithling spawn) was not used so delete the card and set it inactive
	//this part is skipped when first selecting the spell
	if(selectedSpell != null && selectedSpell.getActive()) {
		selectedSpell.setActive(false);
	}
}

//END TURN EVENTS//

/* Called by the human player when they click end turn
 * prevents clicking on tiles/cards/units etc.
 * switches to AI turn response
 */
public void endTurnClickedEvent(){
	if(unitStillMoving) return;
	if(aiPlayerTurn)return;
	unhighlightCard();
	board.refreshAllTiles();

	humanPlayerTurn = false;
	humanPlayer.deleteCards();
	BasicCommands.addPlayer1Notification(out, "Player 2 Turn", 2);
	aiPlayer.playerTurnResponse();
	aiPlayer.initiateAiTurn();
	
	aiPlayerTurn = true;
	humanPlayerTurn = false;
}

/*Called by the AI player to switch back to the humanplayer's turn
represents a new round of turns i.e. increasing mana; drawing new cards; 
 reset unit movement and attack conditions.
*/
public void endTurnEvent() {
	aiPlayerTurn = false;
	humanPlayerTurn = true;	
	newTurn = true;	
	
	aiPlayer.deleteCards();
	BasicCommands.addPlayer1Notification(out, "Player 1 Turn", 2);
	humanPlayer.drawCardToHand();
	aiPlayer.drawCardToHand();	
	
	humanPlayer.setMana(numberOfTurns + 1);
	aiPlayer.setMana(numberOfTurns + 1);
	
	eventController.newTurnEvent();
	humanPlayer.playerTurnResponse();
	numberOfTurns++;
}	

//this is called by the ai/human avatar unit when its health reaches 0
public void gameOverEvent(Unit avatar) {
	gameOver = true;
	eventController.unsubscribeAll();
	if(humanPlayerTurn) {
		humanPlayer.deleteCards();
	}
	else {
		aiPlayer.deleteCards();
	}
	if(avatar == humanPlayer.getAvatar()) {
		BasicCommands.addPlayer1Notification(out, "Player 2 Wins!", 10);
	}
	else {
		BasicCommands.addPlayer1Notification(out, "Player 1 Wins!", 10);
	}
}

//Used by Ai Player to call off actions on its action queue with every heartbeat from the server
public void heartBeatEvent() {
	if(listentingToHeartbeat) {
		aiPlayer.performActions();
	}
}

//HELPER METHODS

// method to pause for an effect animation to finish
public void playAnimation(String effectFile, Tile tile) {
	EffectAnimation effect = BasicObjectBuilders.loadEffect(effectFile);
	int miliseconds = BasicCommands.playEffectAnimation(out, effect, tile);
	try {
		Thread.sleep(miliseconds);
		
	}
	catch (InterruptedException e){
		e.printStackTrace();
	}
}

//Changes small cards to and from 'selected' state on screen
public void highlightCard(Card card, int position) {
	cardPosition = position;
	BasicCommands.drawCard(out, card, position, 1);
}
public void unhighlightCard() {
	if(unitCardSelected || spellCardSelected) {
		BasicCommands.drawCard(out, selectedCard, cardPosition, 0);
	}
	
}

public void initialise() {

	board = new GameBoard(out, this);
	board.initialise();	
	unitBuilder = new UnitBuilder(this,out);
	spellBuilder = new SpellBuilder(this);
	
	
	humanPlayer = new HumanPlayer(this, out);
	aiPlayer = new AiPlayer(this, out);
	humanPlayer.initialise();
	aiPlayer.initialise();	

}


}
