package hangman;

/**
 * Player Class
 * points - Keeps the points of the player
 * tries - Keeps the tries of the player (starting from 6)
 * totalGuesses - counter that keeps the total guesses of the player
 * correctGuesses - counter that keeps the correct guesses of the player
 * successRate - correctGuesses / totalGuesses
 * currentGuess - Pair that keeps the current guess of a player (Letter and Position)
 */
public class Player {
    public int points;
    public int tries;
    public int totalGuesses;
    public int correctGuesses;
    public double successRate;
    public Pair<Integer, Character> currentGuess;

    /**
     * Player constructor
     * Initializes the attributes of the object
     */
    public Player() {
        this.totalGuesses = 0;
        this.correctGuesses = 0;
        this.successRate = 0.00;
        this.points = 0;
        this.tries = 6;
    }

    /**
     * @param mode This method can be called with 2 different modes
     *             mode 0 -> Add Points
     *             mode 1 -> Remove Points
     * @param pointsWorth Points the guess worth
     * When the guess is correct add points, when guess is wrong subtract 15 (points can not be less than 0)
     */
    public void updateScore(int mode, Double pointsWorth) {
        if (mode == 0) {
            if (pointsWorth >= 60.0) this.points += 5;
            else if (pointsWorth < 60 && pointsWorth >= 40) this.points += 10;
            else if (pointsWorth < 40 && pointsWorth >= 25) this.points += 15;
            else this.points += 25;
        }
        else {
            if (this.points - 15 >= 0) this.points -= 15;
            else this.points = 0;
        }
    }

    /**
     * subFromTries method
     * decreasing tries counter
     */
    public void subFromTries() {
        this.tries--;
    }

    /**
     * updateTotalGuesses method
     * increases total guesses
     */
    public void updateTotalGuesses() {
        this.totalGuesses++;
    }

    /**
     * updateCorrectGuesses method
     * increases correct guesses
     */
    public void updateCorrectGuesses() {
        this.correctGuesses++;
    }

    /**
     * updateSuccessRate method
     * update the success rate of the player
     */
    public void updateSuccessRate() {
        if (this.correctGuesses == 0 && this.totalGuesses != 0)
            this.successRate = 0.0;
        else if (this.correctGuesses != 0 && this.totalGuesses != 0)
            this.successRate = ((double) this.correctGuesses) / ((double) this.totalGuesses) * 100;
    }
}
