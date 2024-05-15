package structures.basic.units;

public class SaberspineTiger extends Unit{
	
	protected Boolean outOfMovement = false;
	protected Boolean outOfAttacks = false;
	public void setOutOfAttacks(Boolean b) {outOfAttacks = b;}
	public Boolean getOutOfAttacks() {return outOfAttacks;}
	public Boolean getOutOfMovement() {return outOfMovement;}
	public void setOutOfMovement(Boolean b) {outOfMovement = b;}
}
