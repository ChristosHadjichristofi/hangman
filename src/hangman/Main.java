package hangman;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        // TODO: Here the dictID should be inserted from user
        PlayGame playgame = new PlayGame("OL31390631M");

        // initialize the game
        playgame.initGame();

        // play the game until player reach max tries or finds the hidden word
        while (playgame.gameEnded == 0) {

            playgame.playerMove();
            boolean foundLetter = playgame.validatePlayerMove();
            System.out.println(playgame.roundInfo.candidateLettersProbs);
            playgame.updateState(foundLetter);
            playgame.checkGameEnded();
        }

        playgame.endGame();
    }

}
