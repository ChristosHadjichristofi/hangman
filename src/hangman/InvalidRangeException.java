package hangman;

/**
 * This Exception occurs when a dictionary contains less than 20 words
 */
public class InvalidRangeException extends Exception {
    public InvalidRangeException(String e) {
        super(e);
    }
}