package poker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HandCompare {
    // Constants for hand rankings
    private static final int HIGH_CARD = 0;
    private static final int PAIR = 1;
    private static final int TWO_PAIR = 2;
    private static final int THREE_OF_A_KIND = 3;
    private static final int STRAIGHT = 4;
    private static final int FLUSH = 5;
    private static final int FULL_HOUSE = 6;
    private static final int FOUR_OF_A_KIND = 7;
    private static final int STRAIGHT_FLUSH = 8;
    private static final int ROYAL_FLUSH = 9;

    public static Integer handcompare(Card[] hand1, Card[] hand2) {
        // Sort hands by value in descending order
        Arrays.sort(hand1, (a, b) -> b.value - a.value);
        Arrays.sort(hand2, (a, b) -> b.value - a.value);

        int rank1 = getHandRank(hand1);
        int rank2 = getHandRank(hand2);

        if (rank1 != rank2) {
            return rank1 > rank2 ? 1 : 2;
        }

        // If ranks are equal, compare the hands
        return compareHandsOfSameRank(hand1, hand2, rank1);
    }

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

    private static boolean isRoyalFlush(Card[] hand) {
        return isStraightFlush(hand) && hand[0].value == 14; // Ace high
    }

    private static boolean isStraightFlush(Card[] hand) {
        return isFlush(hand) && isStraight(hand);
    }

    private static boolean isFourOfAKind(Card[] hand) {
        Map<Integer, Integer> valueCount = getValueCount(hand);
        return valueCount.containsValue(4);
    }

    private static boolean isFullHouse(Card[] hand) {
        Map<Integer, Integer> valueCount = getValueCount(hand);
        return valueCount.containsValue(3) && valueCount.containsValue(2);
    }

    private static boolean isFlush(Card[] hand) {
        char suit = hand[0].suit;
        return Arrays.stream(hand).allMatch(card -> card.suit == suit);
    }

    private static boolean isStraight(Card[] hand) {        
        for (int i = 0; i < hand.length - 1; i++) {
            if (hand[i].value != hand[i + 1].value + 1) {
                return false;
            }
        }
        return true;
    }

    private static boolean isThreeOfAKind(Card[] hand) {
        Map<Integer, Integer> valueCount = getValueCount(hand);
        return valueCount.containsValue(3);
    }

    private static boolean isTwoPair(Card[] hand) {
        Map<Integer, Integer> valueCount = getValueCount(hand);
        return valueCount.values().stream().filter(count -> count == 2).count() == 2;
    }

    private static boolean isPair(Card[] hand) {
        Map<Integer, Integer> valueCount = getValueCount(hand);
        return valueCount.containsValue(2);
    }

    private static Map<Integer, Integer> getValueCount(Card[] hand) {
        Map<Integer, Integer> valueCount = new HashMap<>();
        for (Card card : hand) {
            valueCount.merge(card.value, 1, Integer::sum);
        }
        return valueCount;
    }

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

    private static Integer compareHighCards(Card[] hand1, Card[] hand2) {
        for (int i = 0; i < hand1.length; i++) {
            if (hand1[i].value != hand2[i].value) {
                return hand1[i].value > hand2[i].value ? 1 : 2;
            }
        }
        return 0; // Tie
    }

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

    private static Integer comparePair(Card[] hand1, Card[] hand2, Map<Integer, Integer> count1, Map<Integer, Integer> count2) {
        int pair1 = count1.entrySet().stream().filter(e -> e.getValue() == 2).findFirst().get().getKey();
        int pair2 = count2.entrySet().stream().filter(e -> e.getValue() == 2).findFirst().get().getKey();
        
        if (pair1 != pair2) {
            return pair1 > pair2 ? 1 : 2;
        }
        
        // Compare kickers
        return compareHighCards(hand1, hand2);
    }
}
