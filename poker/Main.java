package poker;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Integer[] results = processInputs();
        System.out.println("Player 1: " + results[0]);
        System.out.println("Player 2: " + results[1]);
    }

    private static Integer[] processInputs() {
        Scanner scanner = new Scanner(System.in);
        int wins1 = 0;
        int wins2 = 0;
        Integer[] results = new Integer[2];

        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()){
                    continue;
                } 

                String[] cards = line.split("\\s+");
                if (cards.length != 10) {
                    throw new IllegalArgumentException("Each line must contain exactly 10 cards");
                }

                // Convert string arrays to card arrays
                Card[] hand1 = new Card[5];
                Card[] hand2 = new Card[5];
                
                for (int i = 0; i < 5; i++) {
                    hand1[i] = new Card(cards[i]);
                    hand2[i] = new Card(cards[i + 5]);
                }

                int winner = HandCompare.handcompare(hand1, hand2);
                if (winner == 1) {
                    wins1++;
                } else {
                    wins2++;
                }
            }
        } finally {
            scanner.close();
        }

        results[0] = wins1;
        results[1] = wins2;
        return results;
    }   
}