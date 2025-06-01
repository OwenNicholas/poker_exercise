package poker;

public class Card {
    public int value;
    public char suit;

    // Assigns each card suit and value for ease

    public Card(String code) {
        this.suit = code.charAt(1);
        this.value = mapCharToValue(code.charAt(0));
    }

    // Assigns a value to the char

    private int mapCharToValue(char c) {
        switch (c) {
            case 'T': return 10;
            case 'J': return 11;
            case 'Q': return 12;
            case 'K': return 13;
            case 'A': return 14;
            default:  return Character.getNumericValue(c); // '2'–'9'
        }
    }
} 