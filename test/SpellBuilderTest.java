import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorRef;
import structures.GameController;
import structures.basic.BigCard;

import structures.basic.Card;
import structures.basic.spells.*;
import structures.basic.MiniCard;


public class SpellBuilderTest {
	static SpellBuilder spellBuilder;
	
	
    GameController controller;
    ActorRef out;
  

    @Before
    public void setUp() {
        // Initialize unitBuilder with the required parameters
    	controller = new GameController(out);
      
        spellBuilder = new SpellBuilder(controller);
        
    }  

   
    @Test
    public void testCreateSpell() {
        testSpellCreation(new Card(5, "Wraithling Swarm", 3, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/wraithling_swarm.json"), WraithlingSwarm.class);
        testSpellCreation(new Card(2, "Horn Of The Forsaken", 2, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/horn_of_the_forsaken.json"), HornOfTheForsaken.class);
        testSpellCreation(new Card(8, "Dark Terminus", 4, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/dark_terminus.json"), DarkTerminus.class);
        testSpellCreation(new Card(25, "Beam Shock", 5, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/beamshock.json"), BeamShock.class);
        testSpellCreation(new Card(39, "Sundrop Elixir", 1, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/sundrop_elixir.json"), SundropElixir.class);
        testSpellCreation(new Card(30, "True Strike", 3, new MiniCard(), new BigCard(), true, "conf/gameconfs/units/truestrike.json"), TrueStrike.class);
        
    }
    
    private <T extends Spell> void testSpellCreation(Card spellCard, Class<T> expectedSpellType) {
        SpellBuilder spellBuilder = new SpellBuilder(controller);
        Spell createdSpell = spellBuilder.createSpell(spellCard);

        assertNotNull(createdSpell);
        assertTrue(expectedSpellType.isInstance(createdSpell));
    }

}
