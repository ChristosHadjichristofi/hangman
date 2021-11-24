package hangman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * PlayGame Class
 * roundInfo - roundInfo object
 * player - player object
 * gameEnded - 3 Modes
 *             0 -> game not ended
 *             1 -> player reached the max tries
 *             2 -> player found the word
 */
public class PlayGame {
    public RoundInfo roundInfo;
    public Player player;
    public int gameEnded;

    /**
     * PlayGame constructor
     * Initializes the attributes of the object
     * @param dictID The dictionary ID
     */
    public PlayGame(String dictID) {
        this.roundInfo = new RoundInfo(dictID);
        this.player = new Player();
        this.gameEnded = 0;
    }

    /**
     * InitGame method
     * Initializes the game (wordsInDict, activeDict, createFreqLengthInfo,
     *                       updateActiveDict, createCandidateLettersFreqs, createCandidateLettersProbs)
     * @throws IOException Occurs when trying to load the dictionary but something goes wrong when opening the file
     */
    public void initGame() throws IOException {
        // load the Scenario based on dictID
        Set<String> dictionary = loadDictionary();
        // add how many words exist in the current dictionary
        this.roundInfo.wordsInDict = dictionary.size();
        // add the current dictionary in roundInfo object
        this.roundInfo.activeDict = dictionary;
        // keep frequencyLengthInfo to show to user in details
        this.roundInfo.createFreqLengthInfo();
        // chose random word to be the hidden
        this.roundInfo.chooseRandWord();
        // update the activeDict to contain only words with the same length as the selected hidden word
        this.roundInfo.updateActiveDict();
        // construct for the first time the candidateLettersFreqs (ArrayList of Map<Character, Integer>)
        this.roundInfo.createCandidateLettersFreqs();
        // construct for the first time the candidateLetterProbs (Probabilities - sorted)
        this.roundInfo.createCandidateLettersProbs();
    }

    /**
     * @return Returns a set with the contents of the selected DictionaryID
     * @throws IOException Error occurs when trying to load the dictionary from the txt file
     */
    private Set<String> loadDictionary() throws IOException {
        String fileName = "medialab/hangman_" + this.roundInfo.activeDictID + ".txt";
        String line;
        Set<String> s = new HashSet<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            while ((line = bufferedReader.readLine()) != null) s.add(line);
        }
        return s;
    }

    /**
     * playerMove method that completes the move of the player
     * @param position Which position he chose to enter the letter
     * @param letter Which letter he chose to enter
     */
    public void playerMove(int position, Character letter) {
        this.player.currentGuess = new Pair<>(position, letter);
        // update total guesses
        this.player.updateTotalGuesses();
    }

    /** validatePlayerMove method
     *  validating the move of the player (if the player found a letter or not)
     * @return True if he found a letter, False if not
     */
    public boolean validatePlayerMove() {
        Integer position = this.player.currentGuess.first();
        Character letter = this.player.currentGuess.second();

        return letter == this.roundInfo.hiddenWord.get(position);
    }

    /**
     * updateState method
     * updates all variables that the game is depending on (points, tries, correctGuesses, playerGuess, updateActiveDict)
     * @param foundLetter If the player found a letter or not
     */
    public void updateState(boolean foundLetter) {
        Integer pos = this.player.currentGuess.first();
        Character letter = this.player.currentGuess.second();
        if (foundLetter) {
            // update correct guesses counter
            this.player.updateCorrectGuesses();
            // update playerGuess
            this.roundInfo.playerGuess[pos] = letter;
            // update score based on letter chosen and position
            Double pointsWorth = this.roundInfo.candidateLettersProbs.get(pos).get(letter);
            this.player.updateScore(0, pointsWorth);
            // update activeDict
            this.roundInfo.updateActiveDict(0, letter, pos);
        }
        else {
            // subtract from tries
            this.player.subFromTries();
            // update score (-15)
            this.player.updateScore(1, 0.0);
            // update activeDict
            this.roundInfo.updateActiveDict(1, letter, pos);
        }

        // recreate createCandidateLettersFreqs, createCandidateLettersProbs
        this.roundInfo.createCandidateLettersFreqs();
        this.roundInfo.createCandidateLettersProbs();
        // update successRate
        this.player.updateSuccessRate();
    }

    /**
     * checkGameEnded method
     * If player reached max tries or if player found the hidden word
     */
    public void checkGameEnded() {
        if (this.player.tries == 0) this.gameEnded = 1;
        else if (!Arrays.toString(this.roundInfo.playerGuess).contains("?")) this.gameEnded = 2;
    }

    /**
     * constructRoundDetails method
     * update the prevRounds (append the latest and remove the oldest)
     * @throws IOException Error occurs when trying to open the pastRounds.txt and write the previous rounds
     */
    public void constructRoundDetails() throws IOException {
        // recreate the hiddenWord as a string (now is an ArrayList<Character>)
        StringBuilder hidden = new StringBuilder(this.roundInfo.hiddenWord.size());
        for (Character c : this.roundInfo.hiddenWord) hidden.append(c);

        // player reach max tries
        if (this.gameEnded == 1) this.roundInfo.pastRounds.add(new Triplet<>(hidden.toString(), this.player.tries, "Computer"));
        // player found the word
        else this.roundInfo.pastRounds.add(new Triplet<>(hidden.toString(), this.player.tries, "Player"));

        this.roundInfo.updatePrevRoundsDetails();
    }
}
