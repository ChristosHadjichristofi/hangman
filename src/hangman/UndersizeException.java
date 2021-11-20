package hangman;

public class UndersizeException extends Exception {
    public UndersizeException(String e) {
        super(e);
    }
}
