package structures.basic.AI_Logic;

import structures.basic.units.Unit;

public class AiAttackUnit extends AiAction{

	private Unit attacker;
	private Unit target;
	
	public AiAttackUnit(Unit attacker, Unit target) {
		this.attacker = attacker;
		this.target = target;
		attacker.setOutOfAttacks(true);
		attacker.setOutOfMovement(true);
	}
	
	public void action() {
		attacker.attack(target);		
	}
	
}
