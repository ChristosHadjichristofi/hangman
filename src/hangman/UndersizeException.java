package hangman;

/**
 * This Exception occurs when the dictionary has 1 or more words with length less than 6
 */
public class UndersizeException extends Exception {
    public UndersizeException(String e) {
        super(e);
    }
}
