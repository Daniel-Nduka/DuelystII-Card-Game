import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorRef;
import structures.GameController;
import structures.UnitBuilder;
import structures.basic.*;



;


public class UnitBuilderTest {

	static UnitBuilder unitBuilder;
    GameController controller;
    ActorRef out;

    @Before
    public void setUp() {
        // Initialize unitBuilder with the required parameters
    	controller = new GameController(out);
        unitBuilder = new UnitBuilder(controller, out);
        
    }  

    @Test
    public void testCreateWraithling() {
        Unit wraithling = unitBuilder.createWraithling();

        assertNotNull(wraithling);
        assertTrue(wraithling instanceof Unit);

    }
    
    
    @Test  
    public void testCreateUnit() {  
    	//test cases for Human Player units
        testUnitCreation(new Card(1, "BadOmen", 2, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/bad_omen.json"), BadOmen.class);
        testUnitCreation(new Card(3, "GloomChaser", 2, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/gloom_chaser.json"), GloomChaser.class);
        testUnitCreation(new Card(4, "ShadowWatcher", 3, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/shadow_watcher.json"), ShadowWatcher.class);
        testUnitCreation(new Card(6, "NightSorrowAssassin", 4, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/nightsorrow_assassin.json"), NightsorrowAssassin.class);
        testUnitCreation(new Card(7, "RockPulveriser", 5, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/rock_pulveriser.json"), RockPulveriser.class);
        testUnitCreation(new Card(9, "BloodMoonPriestess", 6, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/bloodmoon_priestess.json"), BloodPriestess.class);
        testUnitCreation(new Card(10, "ShadowDancer", 7, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/shadowdancer.json"), ShadowDancer.class);

        // test cases for AI player units
        testUnitCreation(new Card(21, "SkyrockGolem", 8, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/skyrock_golem.json"), Unit.class);
        testUnitCreation(new Card(22, "SwampEntangler", 9, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/swamp_entangler.json"), SwampEntangler.class);
        testUnitCreation(new Card(23, "SilverguardKnight", 10, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/silverguard_knight.json"), SilverguardKnight.class);
        testUnitCreation(new Card(24, "SaberspineTiger", 11, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/saberspine_tiger.json"), SaberspineTiger.class);
        testUnitCreation(new Card(26, "YoungFlamewing", 12, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/young_flamewing.json"), YoungFlamewing.class);
        testUnitCreation(new Card(27, "SilverguardSquire", 13, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/silverguard_squire.json"), SilverGuardSquire.class);
        testUnitCreation(new Card(28, "IroncliffGuardian", 14, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/ironcliff_guardian.json"), IroncliffGuardian.class);

    }

    //A helper method to create the unit
    private <T extends Unit> void testUnitCreation(Card unitCard, Class<T> expectedUnitType) {
    	unitBuilder = new UnitBuilder(controller, out);
        Unit createdUnit = unitBuilder.createUnit(unitCard);

        assertNotNull(createdUnit);
        assertTrue(expectedUnitType.isInstance(createdUnit));
    }

}
