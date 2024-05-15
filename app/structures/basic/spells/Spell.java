package structures.basic.spells;

import structures.GameController;
import structures.basic.Tile;

public abstract class Spell{
		
//For checking which tiles the spell requires to be highlighted
	public Boolean requiresSpawnRadius() {return false;}
	public Boolean requiresTargets() {return false;}
	public Boolean targetsAvatar() {return false;}
	public Boolean excludesAvatar() {return false;}
	
//used exclusively for wraithling spawn to continue effects after first played
	public int getRepetitionsLeft() {return 0;}
	protected Boolean active = false;
	public void setActive(Boolean b) {active = b;}
	public Boolean getActive() {return active;}
	
	
	protected GameController controller;
	
//used by the ai to check how the spell is used	
	public Boolean hurtsEnemy() {return false;}
	public Boolean targetsFriendly() { return false;}
	public Boolean effectsEnemy() {return false;}
	
	
/*Returns True/False depending on whether the spell conditions were met		
 * if true it plays the card (reducing mana etc) if false it skips and the card is still playable.
 * Called by Human/Ai players and by GameController for wraithling spawn.
 */
	public abstract Boolean performSpell(Tile tile);
	
	public Spell(GameController controller) {
		this.controller = controller;
	}
		
}
