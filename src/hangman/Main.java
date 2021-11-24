package hangman;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class Main extends Application implements Initializable {

    PlayGame game;
    public String dictID;

    @FXML
    private AnchorPane gameInfoAnchor;
    @FXML
    private Pane loadCreatePane;
    @FXML
    private TextField inputDictTf;
    @FXML
    private TextField createDictTf;
    @FXML
    private Label dictLbl;
    @FXML
    private Label successRateLbl;
    @FXML
    private Label messagePosLbl;
    @FXML
    private Label messageLbl;
    @FXML
    private Label activeDictWordsLbl;
    @FXML
    private Label scoreLbl;
    @FXML
    private ImageView imgContainer;
    @FXML
    private GridPane wordContainer;
    @FXML
    private ScrollPane scrollableContainer;

    private void setActiveDictWordsLbl(int activeWords) { this.activeDictWordsLbl.setText("# of words in Dictionary: " + activeWords); }
    private void setScoreLbl(double score) { this.scoreLbl.setText("Score: " + String.format("%.2f", score)); }
    private void setSuccessRateLbl(double successRate) { this.successRateLbl.setText("Success Rate: " + String.format("%.2f", successRate)); }
    private void setImgContainer(String src) { this.imgContainer.setImage(new Image(new File(src).toURI().toString())); }
    private void setMessagePosLbl(String m) { this.messagePosLbl.setText(m); }
    private void setMessageLbl(String m) { this.messageLbl.setText(m); }
    private  void setDictLbl(String m) { this.dictLbl.setText(m); }

    public static class Letter extends TextField {
        public int pos;

        public Letter(String s, int pos) {
            super();
            this.pos = pos;
            setText(s);
            setEditable(false);
            this.setId("letter");
        }
    }

    public static class ProbabilityButton extends Button {
        public int pos;
        public Character letter;

        public ProbabilityButton(String s, int pos, Character letter) {
            super();
            this.pos = pos;
            this.letter = letter;
            setText(s);
            this.setId("probabilityButton");
        }
    }

    public void throwAlert(String exceptionID, String errMessage) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(exceptionID + "Error!");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(errMessage);
        alert.showAndWait();
        initialize();
    }

    public void startAction() throws IOException {
        if (dictID == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Start Error");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("To (re)start a game you need to load a dictionary first.");
            alert.showAndWait();
        }
        else initialize();
    }

    public void createAction() throws IOException {
        TextInputDialog lDialog = new TextInputDialog("Dictionary_ID");
        lDialog.setHeaderText("Enter your the Dictionary_ID you want to create:");
        lDialog.showAndWait();
        dictID = lDialog.getEditor().getText();
        createDictAction();
    }

    public void loadAction() throws IOException {
        TextInputDialog lDialog = new TextInputDialog("Dictionary_ID");
        lDialog.setHeaderText("Enter your the Dictionary_ID you want to load:");
        lDialog.showAndWait();
        dictID = lDialog.getEditor().getText();
        if (new File("medialab/hangman_" + dictID + ".txt").isFile()) {
            loadCreatePane.setStyle("visibility: false");
            gameInfoAnchor.setStyle("visibility: true");
            scrollableContainer.setStyle("visibility: true");
            initialize();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dictionary 404");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("This Dictionary does not exist.\n Try an other dictionary, or create a new one.");
            alert.showAndWait();
        }
    }

    public void exitAction() { Platform.exit(); }

    public void showSolutionAction() throws IOException {
        showHiddenWord();

        StringBuilder hidden = new StringBuilder(this.game.roundInfo.hiddenWord.size());
        for (Character c : this.game.roundInfo.hiddenWord) hidden.append(c);

        this.game.roundInfo.pastRounds.add(new Triplet<>(hidden.toString(), this.game.player.tries, "Computer"));
        this.game.roundInfo.updatePrevRoundsDetails();

        setMessagePosLbl("");
        setMessageLbl("You gave up. You lost!");
        scrollableContainer.setContent(new Pane());
    }

    public void showPastRoundsAction() throws IOException {
        StringBuilder pastRounds = new StringBuilder();

        ArrayList<Triplet<String, Integer, String>> rounds = this.game.roundInfo.getPrevRoundDetails();
        for (Triplet<String, Integer, String> entry : rounds) {
            pastRounds.append("Word: ")
                    .append(entry.getWord()).append(", Tries left: ")
                    .append(entry.getTries()).append(", Winner: ").append(entry.getWinner()).append("\n");
        }

        if (rounds.size() == 0) pastRounds.append("No past rounds available.");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dictionary Words Length Frequency");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(pastRounds.toString());
        alert.showAndWait();
    }

    public void dictDetailsAction() {
        StringBuilder dictDetails = new StringBuilder();

        for (Map.Entry<Integer, Integer> entry : this.game.roundInfo.freqLengthInfo.entrySet()) {
            dictDetails.append("Words with length equal to ")
                    .append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dictionary Words Length Frequency");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(dictDetails.toString());
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void loadDictAction() throws IOException {
        dictID = inputDictTf.getText();
        if (new File("medialab/hangman_" + dictID + ".txt").isFile()) {
            // proceed to the next scene
            loadCreatePane.setStyle("visibility: false");
            gameInfoAnchor.setStyle("visibility: true");
            scrollableContainer.setStyle("visibility: true");
            initialize();
        }
        else {
            inputDictTf.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dictionary 404");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("This Dictionary does not exist.\n Try an other dictionary, or create a new one.");
            alert.showAndWait();
        }
    }

    public void createDictAction() throws IOException {
        ConnectAPI connectAPI = new ConnectAPI(dictID);

        // Connect to the API and get the response
        connectAPI.connect();

        if (connectAPI.urlExists) {
            // Create the dictionary and store it to ConnectAPI object as an attribute
            connectAPI.createDict();

            // Check if the dictionary passes all tests before creating the txt
            try {
                connectAPI.checkDictValidity();
            } catch (InvalidRangeException e) {
                throwAlert("InvalidRangeException", e.getMessage());
            } catch (UnbalancedException e) {
                throwAlert("UnbalancedException", e.getMessage());
            } catch (UndersizeException e) {
                throwAlert("UndersizeException", e.getMessage());
            } catch (InvalidCountException e) {
                throwAlert("InvalidCountException", e.getMessage());
            } catch (IOException e) {
                throwAlert("IOException", e.getMessage());
            }
        }
        else {
            createDictTf.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("URL 404");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("This LibraryID does not exist.\nThe GET Request failed.\nTry an other LibraryID.");
            alert.showAndWait();
        }

    }

    public void initialize() throws IOException {
        setDictLbl("DictionaryID:" + dictID);
        setMessageLbl("");

        game = new PlayGame(dictID);

        // initialize the game
        game.initGame();

        // update word container interface
        updateWordContainer();
        // focus on the first letter
        setFocusOnLetter(0, false);
        // update the statistics
        updateStats();
        // update the image
        updateHangmanImage();
    }

    private void updateHangmanImage() {
        if (this.game.player.tries == 0) setImgContainer("assets/img/hangman6.png");
        else if (this.game.player.tries == 1) setImgContainer("assets/img/hangman5.png");
        else if (this.game.player.tries == 2) setImgContainer("assets/img/hangman4.png");
        else if (this.game.player.tries == 3) setImgContainer("assets/img/hangman3.png");
        else if (this.game.player.tries == 4) setImgContainer("assets/img/hangman2.png");
        else if (this.game.player.tries == 5) setImgContainer("assets/img/hangman1.png");
        else setImgContainer("assets/img/hangman0.png");
    }

    private void updateStats() {
        // active dictionary words
        setActiveDictWordsLbl(this.game.roundInfo.wordsInDict);
        // score
        setScoreLbl(this.game.player.points);
        // success rate
        setSuccessRateLbl(this.game.player.successRate);
    }

    private void updateWordContainer() {
        for (int i = 0; i < this.game.roundInfo.hiddenWord.size(); i++) {
            Letter letter = new Letter(this.game.roundInfo.playerGuess[i].toString(), i);
            letter.setOnMouseClicked(this::showLetterProbabilities);
            wordContainer.add(letter, i, 0);
        }
    }

    private void showHiddenWord() {
        for (int i = 0; i < this.game.roundInfo.hiddenWord.size(); i++) {
            Letter letter = new Letter(this.game.roundInfo.hiddenWord.get(i).toString(), i);
            wordContainer.add(letter, i, 0);
        }
    }

    private void showLetterProbabilities(MouseEvent e) {
        Letter letter = (Letter) e.getSource();
        setMessagePosLbl("Choosing Letter for position " + (letter.pos + 1) + ".");
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(8));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        Map<Character, Double> m = this.game.roundInfo.candidateLettersProbs.get(letter.pos);
        int i = 0;
        for (Character key : m.keySet()) {
            String buttonLabel = key + " : " + String.format("%.2f", m.get(key)) + "%";
            ProbabilityButton button = new ProbabilityButton(buttonLabel, letter.pos, key);
            button.setOnMouseClicked(this::inputSelected);
            gridPane.add(button, 0, i++);
        }

        scrollableContainer.setContent(gridPane);

    }

    private void inputSelected(MouseEvent e) {
        if (game.gameEnded == 0) {
            ProbabilityButton button = (ProbabilityButton) e.getSource();
            this.game.playerMove(button.pos, button.letter);

            boolean foundLetter = game.validatePlayerMove();
            game.updateState(foundLetter);

            updateWordContainer();
            setFocusOnLetter(button.pos, foundLetter);
            updateStats();
            updateHangmanImage();

            game.checkGameEnded();

            if (game.gameEnded != 0) {
                scrollableContainer.setContent(new Pane());
                try {
                    this.game.constructRoundDetails();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                if (this.game.gameEnded == 1) {
                    setMessageLbl("You couldn't find the hidden word. You lost!");
                    showHiddenWord();
                }
                else {
                    setMessageLbl("You found the hidden word. You won!");
                    setMessagePosLbl("");
                }
            }
        }
    }

    private void setFocusOnLetter(int position, boolean foundLetter) {
        if (foundLetter && (position + 1) <= game.roundInfo.hiddenWord.size()) {
            wordContainer.getChildren().get(position + 1).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null));
            wordContainer.getChildren().get(position + 1).requestFocus();
            if ((position + 1) == game.roundInfo.hiddenWord.size()) {
                setMessagePosLbl("");
            }
            else setMessagePosLbl("Choosing Letter for position " + (position + 2) + ".");

        }
        else {
            wordContainer.getChildren().get(position).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null));
            wordContainer.getChildren().get(position).requestFocus();
            setMessagePosLbl("Choosing Letter for position " + (position + 1) + ".");
        }
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try{
            URL url = new File("src/hangman/hangmanGui.fxml").toURI().toURL();
            root = FXMLLoader.load(url);
        } catch(Exception e) {
            e.printStackTrace();
        }
        assert root != null;
        Scene scene = new Scene(root,600,600);
        scene.getStylesheets().add((new File("assets/css/styles.css")).toURI().toString());
        primaryStage.setScene(scene);
        primaryStage.setTitle("MediaLab Hangman");
        primaryStage.setResizable(false);

        primaryStage.show();
    }

}