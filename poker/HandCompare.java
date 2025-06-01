package poker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HandCompare {
    // Constants representing different poker hand rankings, from lowest (0) to highest (9)
    private static final int HIGH_CARD = 0;      // No special combination
    private static final int PAIR = 1;           // Two cards of same value
    private static final int TWO_PAIR = 2;       // Two different pairs
    private static final int THREE_OF_A_KIND = 3;// Three cards of same value
    private static final int STRAIGHT = 4;       // Five consecutive values
    private static final int FLUSH = 5;          // Five cards of same suit
    private static final int FULL_HOUSE = 6;     // Three of a kind plus a pair
    private static final int FOUR_OF_A_KIND = 7; // Four cards of same value
    private static final int STRAIGHT_FLUSH = 8; // Straight and flush
    private static final int ROYAL_FLUSH = 9;    // Ace-high straight flush

    /**
     * Compares two poker hands and returns the winner
     * param hand1 First player's hand
     * param hand2 Second player's hand
     * return 1 if hand1 wins, 2 if hand2 wins, 0 if tie
    */

    public static Integer handcompare(Card[] hand1, Card[] hand2) {
        // Sort hands by value in descending order for easier comparison
        Arrays.sort(hand1, (a, b) -> b.value - a.value);
        Arrays.sort(hand2, (a, b) -> b.value - a.value);

        // Get the rank of each hand (e.g., pair, flush, etc.)
        int rank1 = getHandRank(hand1);
        int rank2 = getHandRank(hand2);

        // If hands have different ranks, higher rank wins
        if (rank1 != rank2) {
            return rank1 > rank2 ? 1 : 2;
        }

        // If hands have same rank, compare them based on their specific type
        return compareHandsOfSameRank(hand1, hand2, rank1);
    }

    /**
     * Determines the rank of a poker hand
     * Checks from highest rank (royal flush) to lowest (high card)
     */
    private static int getHandRank(Card[] hand) {
        if (isRoyalFlush(hand)) return ROYAL_FLUSH;
        if (isStraightFlush(hand)) return STRAIGHT_FLUSH;
        if (isFourOfAKind(hand)) return FOUR_OF_A_KIND;
        if (isFullHouse(hand)) return FULL_HOUSE;
        if (isFlush(hand)) return FLUSH;
        if (isStraight(hand)) return STRAIGHT;
        if (isThreeOfAKind(hand)) return THREE_OF_A_KIND;
        if (isTwoPair(hand)) return TWO_PAIR;
        if (isPair(hand)) return PAIR;
        return HIGH_CARD;
    }

    /**
     * Checks if hand is a royal flush (Ace-high straight flush)
     */
    private static boolean isRoyalFlush(Card[] hand) {
        return isStraightFlush(hand) && hand[0].value == 14; // Ace high
    }

    /**
     * Checks if hand is a straight flush (straight and flush)
     */
    private static boolean isStraightFlush(Card[] hand) {
        return isFlush(hand) && isStraight(hand);
    }

    /**
     * Checks if hand has four cards of the same value
     */
    private static boolean isFourOfAKind(Card[] hand) {
        Map<Integer, Integer> valueCount = getValueCount(hand);
        return valueCount.containsValue(4);
    }

    /**
     * Checks if hand has three of a kind and a pair
     */
    private static boolean isFullHouse(Card[] hand) {
        Map<Integer, Integer> valueCount = getValueCount(hand);
        return valueCount.containsValue(3) && valueCount.containsValue(2);
    }

    /**
     * Checks if all cards are of the same suit
     */
    private static boolean isFlush(Card[] hand) {
        char suit = hand[0].suit;
        return Arrays.stream(hand).allMatch(card -> card.suit == suit);
    }

    /**
     * Checks if cards form a straight (five consecutive values)
     */
    private static boolean isStraight(Card[] hand) {
        
        // Check if each card is one less than the previous
        for (int i = 0; i < hand.length - 1; i++) {
            if (hand[i].value != hand[i + 1].value + 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if hand has three cards of the same value
     */
    private static boolean isThreeOfAKind(Card[] hand) {
        Map<Integer, Integer> valueCount = getValueCount(hand);
        return valueCount.containsValue(3);
    }

    /**
     * Checks if hand has two different pairs
     */
    private static boolean isTwoPair(Card[] hand) {
        Map<Integer, Integer> valueCount = getValueCount(hand);
        return valueCount.values().stream().filter(count -> count == 2).count() == 2;
    }

    /**
     * Checks if hand has one pair
     */
    private static boolean isPair(Card[] hand) {
        Map<Integer, Integer> valueCount = getValueCount(hand);
        return valueCount.containsValue(2);
    }

    /**
     * Creates a map counting how many times each card value appears in the hand
     */
    private static Map<Integer, Integer> getValueCount(Card[] hand) {
        Map<Integer, Integer> valueCount = new HashMap<>();
        for (Card card : hand) {
            valueCount.merge(card.value, 1, Integer::sum);
        }
        return valueCount;
    }

    /**
     * Compares two hands that have the same rank
     * Uses different comparison methods based on the hand type
     */
    private static Integer compareHandsOfSameRank(Card[] hand1, Card[] hand2, int rank) {
        Map<Integer, Integer> valueCount1 = getValueCount(hand1);
        Map<Integer, Integer> valueCount2 = getValueCount(hand2);

        switch (rank) {
            case ROYAL_FLUSH:
            case STRAIGHT_FLUSH:
            case STRAIGHT:
            case FLUSH:
            case HIGH_CARD:
                return compareHighCards(hand1, hand2);
            
            case FOUR_OF_A_KIND:
                return compareFourOfAKind(hand1, hand2, valueCount1, valueCount2);
            
            case FULL_HOUSE:
                return compareFullHouse(hand1, hand2, valueCount1, valueCount2);
            
            case THREE_OF_A_KIND:
                return compareThreeOfAKind(hand1, hand2, valueCount1, valueCount2);
            
            case TWO_PAIR:
                return compareTwoPair(hand1, hand2, valueCount1, valueCount2);
            
            case PAIR:
                return comparePair(hand1, hand2, valueCount1, valueCount2);
            
            default:
                return 0;
        }
    }

    /**
     * Compares hands by highest card, then second highest, etc.
     * Used for high card, straight, flush, and straight flush comparisons
     */
    private static Integer compareHighCards(Card[] hand1, Card[] hand2) {
        for (int i = 0; i < hand1.length; i++) {
            if (hand1[i].value != hand2[i].value) {
                return hand1[i].value > hand2[i].value ? 1 : 2;
            }
        }
        return 0; // Tie
    }

    /**
     * Compares four of a kind hands
     * First compares the four of a kind value, then the kicker
     */
    private static Integer compareFourOfAKind(Card[] hand1, Card[] hand2, 
                                           Map<Integer, Integer> count1, Map<Integer, Integer> count2) {
        int four1 = count1.entrySet().stream().filter(e -> e.getValue() == 4).findFirst().get().getKey();
        int four2 = count2.entrySet().stream().filter(e -> e.getValue() == 4).findFirst().get().getKey();
        
        if (four1 != four2) {
            return four1 > four2 ? 1 : 2;
        }
        
        // Compare kickers
        int kicker1 = count1.entrySet().stream().filter(e -> e.getValue() == 1).findFirst().get().getKey();
        int kicker2 = count2.entrySet().stream().filter(e -> e.getValue() == 1).findFirst().get().getKey();
        
        return kicker1 > kicker2 ? 1 : 2;
    }

    /**
     * Compares full house hands
     * First compares the three of a kind value, then the pair value
     */
    private static Integer compareFullHouse(Card[] hand1, Card[] hand2,
                                         Map<Integer, Integer> count1, Map<Integer, Integer> count2) {
        int three1 = count1.entrySet().stream().filter(e -> e.getValue() == 3).findFirst().get().getKey();
        int three2 = count2.entrySet().stream().filter(e -> e.getValue() == 3).findFirst().get().getKey();
        
        if (three1 != three2) {
            return three1 > three2 ? 1 : 2;
        }
        
        int pair1 = count1.entrySet().stream().filter(e -> e.getValue() == 2).findFirst().get().getKey();
        int pair2 = count2.entrySet().stream().filter(e -> e.getValue() == 2).findFirst().get().getKey();
        
        return pair1 > pair2 ? 1 : 2;
    }

    /**
     * Compares three of a kind hands
     * First compares the three of a kind value, then the kickers
     */
    private static Integer compareThreeOfAKind(Card[] hand1, Card[] hand2,
                                            Map<Integer, Integer> count1, Map<Integer, Integer> count2) {
        int three1 = count1.entrySet().stream().filter(e -> e.getValue() == 3).findFirst().get().getKey();
        int three2 = count2.entrySet().stream().filter(e -> e.getValue() == 3).findFirst().get().getKey();
        
        if (three1 != three2) {
            return three1 > three2 ? 1 : 2;
        }
        
        // Compare kickers
        return compareHighCards(hand1, hand2);
    }

    /**
     * Compares two pair hands
     * First compares higher pair, then lower pair, then kicker
     */
    private static Integer compareTwoPair(Card[] hand1, Card[] hand2,
                                       Map<Integer, Integer> count1, Map<Integer, Integer> count2) {
        int[] pairs1 = count1.entrySet().stream()
            .filter(e -> e.getValue() == 2)
            .mapToInt(Map.Entry::getKey)
            .sorted()
            .toArray();
        int[] pairs2 = count2.entrySet().stream()
            .filter(e -> e.getValue() == 2)
            .mapToInt(Map.Entry::getKey)
            .sorted()
            .toArray();
        
        // Compare higher pairs
        if (pairs1[1] != pairs2[1]) {
            return pairs1[1] > pairs2[1] ? 1 : 2;
        }
        
        // Compare lower pairs
        if (pairs1[0] != pairs2[0]) {
            return pairs1[0] > pairs2[0] ? 1 : 2;
        }
        
        // Compare kickers
        int kicker1 = count1.entrySet().stream().filter(e -> e.getValue() == 1).findFirst().get().getKey();
        int kicker2 = count2.entrySet().stream().filter(e -> e.getValue() == 1).findFirst().get().getKey();
        
        return kicker1 > kicker2 ? 1 : 2;
    }

    /**
     * Compares pair hands
     * First compares pair value, then kickers
     */
    private static Integer comparePair(Card[] hand1, Card[] hand2,
                                    Map<Integer, Integer> count1, Map<Integer, Integer> count2) {
        int pair1 = count1.entrySet().stream().filter(e -> e.getValue() == 2).findFirst().get().getKey();
        int pair2 = count2.entrySet().stream().filter(e -> e.getValue() == 2).findFirst().get().getKey();
        
        if (pair1 != pair2) {
            return pair1 > pair2 ? 1 : 2;
        }
        
        // Compare kickers
        return compareHighCards(hand1, hand2);
    }
}
