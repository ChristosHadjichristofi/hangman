package hangman;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

public class PlayGame {
    public ConnectAPI connectAPI;
    public RoundInfo roundInfo;
    public Player player;
    // gameEnded = 0 -> game not ended
    // gameEnded = 1 -> player reached the max tries
    // gameEnded = 2 -> player found the word
    public int gameEnded;

    public PlayGame(String dictID) {
        this.connectAPI = new ConnectAPI(dictID);
        this.roundInfo = new RoundInfo(dictID);
        this.player = new Player();
        this.gameEnded = 0;
    }

    public void initGame() {
        // Connect to the API and get the response
        this.connectAPI.connect();

        // Create the dictionary and store it to ConnectAPI object as an attribute
        Set<String> dictionary = this.connectAPI.createDict();

        // Check if the dictionary passes all tests before creating the txt
        try {
            this.connectAPI.checkDictValidity();
        } catch (InvalidRangeException | UnbalancedException | UndersizeException | InvalidCountException | IOException e) {
            System.out.println(e.getMessage());
        }

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

    public void playerMove() {
        Scanner sc = new Scanner(System.in);
        // User Input TODO: Player will give input through the ui, will need check if its alpha
        Integer position = sc.nextInt();
        Character letter = sc.next().charAt(0);
        // save User input as currentGuess
        this.player.currentGuess = new Pair<>(position, letter);
        // update total guesses
        this.player.updateTotalGuesses();
    }

    public boolean validatePlayerMove() {
        Integer position = this.player.currentGuess.first();
        Character letter = this.player.currentGuess.second();

        // if the letter the player chose at position is equal to the hiddenWord at position
        // he found the letter
        if (letter == this.roundInfo.hiddenWord.get(position)) return true;
        // he did not find the letter at position
        else return false;
    }

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

    public void checkGameEnded() {
        if (this.player.tries == 0) this.gameEnded = 1;
        else if (!Arrays.toString(this.roundInfo.playerGuess).contains("?")) this.gameEnded = 2;
    }

    public void endGame() throws IOException {
        // recreate the hiddenWord as a string (now is an ArrayList<Character>)
        StringBuilder hidden = new StringBuilder(this.roundInfo.hiddenWord.size());
        for (Character c : this.roundInfo.hiddenWord) hidden.append(c);

        // player reach max tries
        if (this.gameEnded == 1) {
            this.roundInfo.pastRounds.add(new Triplet<>(hidden.toString(), this.player.tries, "Computer"));
            // show hidden word and announce score
        }
        // player found the word
        else {
            this.roundInfo.pastRounds.add(new Triplet<>(hidden.toString(), this.player.tries, "Player"));
            // announce score
        }
        this.roundInfo.updatePrevRoundsDetails();
    }
}
