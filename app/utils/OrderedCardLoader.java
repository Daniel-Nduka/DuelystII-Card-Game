package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import structures.basic.Card;

/**
 * This is a utility class that provides methods for loading the decks for each
 * player, as the deck ordering is fixed. 
 * @author Richard
 *
 */
public class OrderedCardLoader {

	public static String cardsDIR = "conf/gameconfs/cards/";
	

	
	//because the 0 and 1 are for the avatars
	
	/**
	 * Returns all of the cards in the human player's deck in order
	 * @return
	 */
	public static List<Card> getPlayer1Cards(int copies) {
	
		List<Card> cardsInDeck = new ArrayList<>(20);
		
		int cardID = 1;
		for (int i =0; i<copies; i++) {
			String[] filenames = new File(cardsDIR).list();
			Arrays.sort(filenames);
			for (String filename : filenames) {
				if (filename.startsWith("1_")) {
					// this is a deck 1 card					
					cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, Card.class));
					cardID++;
				}
			}
		}			
		return cardsInDeck;
	}
	
	
	/**
	 * Returns all of the cards in the human player's deck in order
	 * @return
	 */
	public static List<Card> getPlayer2Cards(int copies) {
	
		List<Card> cardsInDeck = new ArrayList<>(20);
		
		int cardID = 21;
		for (int i =0; i<copies; i++) {
			String[] filenames = new File(cardsDIR).list();
			Arrays.sort(filenames);
			for (String filename : filenames) {
				
				if (filename.startsWith("2_")) {
					// this is a deck 2 card
					cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR+filename, cardID, Card.class));
					cardID++;
				
				}
			}
		}
		
		return cardsInDeck;
	}
	
}
