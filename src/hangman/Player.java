package hangman;

public class Player {
    public int points;
    public int tries;
    public int totalGuesses;
    public int correctGuesses;
    public double successRate;
    public Pair<Integer, Character> currentGuess;

    public Player() {
        this.totalGuesses = 0;
        this.correctGuesses = 0;
        this.successRate = 100.00;
        this.points = 0;
        this.tries = 6;
    }

    // mode 0 -> add points
    // mode 1 -> remove points
    public void updateScore(int mode, Double pointsWorth) {
        if (mode == 0) {
            if (pointsWorth >= 60.0) this.points += 5;
            else if (pointsWorth < 60 && pointsWorth >= 40) this.points += 10;
            else if (pointsWorth < 40 && pointsWorth >= 25) this.points += 15;
            else this.points += 25;
        }
        else {
            if (this.points - 15 >= 0) this.points -= 15;
        }
    }

    public void subFromTries() {
        this.tries--;
    }

    public void updateTotalGuesses() {
        this.totalGuesses++;
    }

    public void updateCorrectGuesses() {
        this.correctGuesses++;
    }

    public void updateSuccessRate() {
        if (this.correctGuesses == 0 && this.totalGuesses != 0)
            this.successRate = 0.0;
        else if (this.correctGuesses != 0 && this.totalGuesses != 0)
            this.successRate = ((double) this.correctGuesses) / ((double) this.totalGuesses) * 100;
    }
}
