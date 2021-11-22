package hangman;

/**
 * triplet class in order to create objects that hold 3 values.
 * this Class was used to hold the pastRounds info when read from the txt file
 * before appending the latest round on top
 * @param <T1> any variable type
 * @param <T2> any variable type
 * @param <T3> any variable type
 */
public class Triplet<T1, T2, T3> {

    private final T1 word;
    private final T2 tries;
    private final T3 winner;

    public Triplet(T1 word, T2 tries, T3 winner) {
        this.word = word;
        this.tries = tries;
        this.winner = winner;
    }

    public T1 getWord() { return word; }
    public T2 getTries() { return tries; }
    public T3 getWinner() { return winner; }
}