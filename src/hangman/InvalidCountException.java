package hangman;

/**
 * This Exception occurs when a word exists twice (or more than 2) in the dictionary
 */
public class InvalidCountException extends Exception {
    public InvalidCountException(String e) {
        super(e);
    }
}
