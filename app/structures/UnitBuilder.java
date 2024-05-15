package structures;

import java.util.ArrayList;
import java.util.Random;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.units.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class UnitBuilder {	


GameController controller;
ActorRef out;
private int wraithlingID= 45;
	
public UnitBuilder(GameController control, ActorRef ref) {
	this.controller = control;
	this.out = ref;
}
/*
Used to create the correct Unit based on the given card's ID
Subscribes it to the relevant eventlisteners depending on unit abilities
*/
	
public Unit createUnit(Card unitCard)
	{
		if (!unitCard.getIsCreature()) return null;
		
		Unit unit = null;
		int cardID = unitCard.getId();
		String unitConfig = unitCard.getUnitConfig();
		
		//Get the health and attack values from the card's big card
		int unitHealth = unitCard.getBigCard().getHealth();
		int unitAttack = unitCard.getBigCard().getAttack();	
		
		switch(cardID) {
		
//Human Player Units		
		case 1: //Bad Omen : Deathwatch
		case 11:
			BadOmen bad = (BadOmen)BasicObjectBuilders.loadUnit(unitConfig, cardID, BadOmen.class);
			unit = bad;
			controller.getEventController().subscribeToUnitKilled(bad);
			break;
		
		case 3: //Gloom Chaser : Opening Gambit
		case 13:
			GloomChaser gloom = (GloomChaser)BasicObjectBuilders.loadUnit(unitConfig,cardID,GloomChaser.class);
			unit = gloom;
			break;
			
		case 4: //Shadow Watcher : Deathwatch
		case 14:
			ShadowWatcher shadowWatcher = (ShadowWatcher)BasicObjectBuilders.loadUnit(unitConfig,cardID,ShadowWatcher.class);
			unit = shadowWatcher;
			controller.getEventController().subscribeToUnitKilled(shadowWatcher);
			break;
			
		case 6: //NightSorrow Assassin : Opening Gambit
		case 16:
			NightsorrowAssassin nightsorrow = (NightsorrowAssassin)BasicObjectBuilders.loadUnit(unitConfig,cardID,NightsorrowAssassin.class);
			unit = nightsorrow;
			break;
			
		case 7: //Rock Pulveriser : provoke
		case 17:
			ProvokingUnit rock = (ProvokingUnit)BasicObjectBuilders.loadUnit(unitConfig,cardID,ProvokingUnit.class);
			unit = rock;
			controller.getEventController().subscribeToUnitMoved(rock);
			break;
		case 9: //BloodMoon Priestess : Deathwatch
		case 19:
			BloodPriestess bloodMoon = (BloodPriestess)BasicObjectBuilders.loadUnit(unitConfig,cardID,BloodPriestess.class);
			unit = bloodMoon;
			controller.getEventController().subscribeToUnitKilled(bloodMoon);
			break;
			
		case 10: //Shadowdancer : Deathwatch
		case 20:
			ShadowDancer shadow = (ShadowDancer)BasicObjectBuilders.loadUnit(unitConfig,cardID,ShadowDancer.class);
			unit = shadow;
			controller.getEventController().subscribeToUnitKilled(shadow);
			break;
			
//Ai Player Units
		case 21: //Skyrock Golem : No ability
		case 31:
			Unit skyRock = BasicObjectBuilders.loadUnit(unitConfig,cardID,Unit.class);
			unit = skyRock;
			break;
			
		case 22: //Swamp Entangler : Provoke
		case 32:
			ProvokingUnit swamp = (ProvokingUnit)BasicObjectBuilders.loadUnit(unitConfig,cardID,ProvokingUnit.class);
			controller.getEventController().subscribeToUnitMoved(swamp);
			unit = swamp;
			break;
			
		case 23: //SilverGuard Knight : provoke : Zeal
		case 33:
			SilverguardKnight guard = (SilverguardKnight)BasicObjectBuilders.loadUnit(unitConfig,cardID,SilverguardKnight.class);
			controller.getEventController().subscribeToAvatarDamaged(guard);
			controller.getEventController().subscribeToUnitMoved(guard);
			unit = guard;			
			break;
		
		case 24: //Saberspine Tiger : Rush
		case 34:
			SaberspineTiger saberSpine = (SaberspineTiger)BasicObjectBuilders.loadUnit(unitConfig,cardID,SaberspineTiger.class);
			unit = saberSpine;
			break;
 			
		case 26: // Young Flamewing : Flying
		case 36:
			unit = BasicObjectBuilders.loadUnit(unitConfig,cardID,Unit.class);
			unit.setIsFlying(true);			
			break;
			
		case 27: //Silverguard Squire : Opening Gambit
		case 37:
			SilverGuardSquire squire = (SilverGuardSquire)BasicObjectBuilders.loadUnit(unitConfig,cardID,SilverGuardSquire.class);
			unit = squire;
			break;
					
		case 28: //Ironcliff Guardian : Provoke
		case 38:
			ProvokingUnit ironCliff = (ProvokingUnit)BasicObjectBuilders.loadUnit(unitConfig,cardID,ProvokingUnit.class);			
			controller.getEventController().subscribeToUnitMoved(ironCliff);
			unit = ironCliff;
			break;
	}		
		
		unit.setHealth(unitHealth);
		unit.setMaxHealth(unitHealth);
		unit.setAttack(unitAttack);
		unit.setActorRef(controller.getActorRef());
		unit.setController(controller);
		controller.getEventController().subscribeToNewTurn(unit);
		
		return unit;
	}

public Unit createWraithling() {
	String config = StaticConfFiles.wraithling;
	Unit wraithling = BasicObjectBuilders.loadUnit(config,wraithlingID++,Unit.class);
	
	wraithling.setAttack(1);
	wraithling.setHealth(1);
	if (controller != null){
		wraithling.setActorRef(controller.getActorRef());
	}
	wraithling.setActorRef(controller.getActorRef());
	wraithling.setController(controller);
	wraithling.setTriggerAbilities(false);
	
	controller.getEventController().subscribeToNewTurn(wraithling);
	return wraithling;
}

public void spawnWraithling(Tile tile) {
	Unit wraithling = createWraithling();
	
	wraithling.setPositionByTile(tile);	
	controller.getHumanPlayer().getPlayerUnitsOnBoard().add(wraithling);
	wraithling.setOwner(controller.getHumanPlayer());	
	controller.getEventController().unitMovedEvent();
	
	controller.playAnimation(StaticConfFiles.f1_summon, tile);
	BasicCommands.drawUnit(out, wraithling, tile);		
	
	try {Thread.sleep(300);}
	catch (InterruptedException e) {e.printStackTrace();}
	
	BasicCommands.setUnitAttack(out, wraithling, wraithling.getAttack());
	BasicCommands.setUnitHealth(out, wraithling, wraithling.getHealth());		
}

public void spawnWraithlingAroundUnit(Unit unit){
	ArrayList<Tile> emptyTiles = controller.getBoard().getUnitSpawnRadius(unit);
	
	if(emptyTiles.isEmpty()) return;
	
	Random rand = new Random();
	int index = rand.nextInt(emptyTiles.size());
	
	Tile randomTile = emptyTiles.get(index);
	spawnWraithling(randomTile);	
}

}
