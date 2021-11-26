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
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * setActiveDictWordsLbl method that sets the content of the label
     * @param activeWords The active words that will appear in the ui
     */
    private void setActiveDictWordsLbl(int activeWords) { this.activeDictWordsLbl.setText("# of words in Dictionary: " + activeWords); }

    /**
     * setScoreLbl method that sets the content of the label
     * @param score The score that will appear in the ui
     */
    private void setScoreLbl(double score) { this.scoreLbl.setText("Score: " + String.format("%.2f", score)); }

    /**
     * setSuccessRateLbl method that sets the content of the label
     * @param successRate The success rate that will appear in the ui
     */
    private void setSuccessRateLbl(double successRate) { this.successRateLbl.setText("Success Rate: " + String.format("%.2f", successRate)); }

    /**
     * setSuccessRateLbl method that sets the content of the label
     * @param src Which image will be loaded
     */
    private void setImgContainer(String src) { this.imgContainer.setImage(new Image(new File(src).toURI().toString())); }

    /**
     * setMessagePosLbl method that sets the content of the label
     * @param m The message that will appear
     */
    private void setMessagePosLbl(String m) { this.messagePosLbl.setText(m); }

    /**
     * setMessageLbl method that sets the content of the label
     * @param m The message that will appear
     */
    private void setMessageLbl(String m) { this.messageLbl.setText(m); }

    /**
     * setDictLbl method that sets the content of the label
     * @param m The message that will appear
     */
    private  void setDictLbl(String m) { this.dictLbl.setText(m); }


    /**
     * Letter Class
     * Extends TextField
     * pos Holds the position of the letter
     */
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

    /**
     * ProbabilityButton Class
     * Extends Button
     * pos Holds the position of the Letter that this probability button belongs
     * letter Holds the letter that this probability button belongs
     */
    public static class ProbabilityButton extends Button {
        public int pos;
        public Character letter;

        /**
         * ProbabilityButton constructor
         * @param s The string that will be set as text
         * @param pos The position of the letter that this probability button belongs
         * @param letter The letter that this probability button belongs
         */
        public ProbabilityButton(String s, int pos, Character letter) {
            super();
            this.pos = pos;
            this.letter = letter;
            setText(s);
            this.setId("probabilityButton");
        }
    }
    /**
     * method to throw alert when error occurs
     * @param exceptionID id of exception
     * @param errMessage message to be thrown
     * @throws IOException Exception occurs when trying to open the txt that has the dictionary inside
     */
    public void throwAlert(String exceptionID, String errMessage) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(exceptionID + "Error!");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(errMessage);
        alert.showAndWait();
        initialize();
    }

    /**
     * method to (re)start game
     * @throws IOException Exception occurs when trying to open the txt that has the dictionary inside
     */
    public void startAction() throws IOException {
        if (dictID == null || !(new File("medialab/hangman_" + dictID + ".txt").isFile())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Start Error");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("To (re)start a game you need to load a dictionary first.");
            alert.showAndWait();
        }
        else initialize();
    }

    /**
     * method to create a new txt with a new dictionary retrieved from the GET Request
     * @throws IOException Exception occurs for a number of reasons (because creating a dictionary means validation)
     */
    public void createAction() throws IOException {
        TextInputDialog lDialog = new TextInputDialog("Dictionary_ID");
        lDialog.setHeaderText("Enter your the Dictionary_ID you want to create:");
        lDialog.showAndWait();
        dictID = lDialog.getEditor().getText();

        createDictAux();
    }

    /**
     * method that loads the specified txt (by giving the dictionary_id) as the current dictionary
     * @throws IOException Exception occurs when trying to open the txt that has the dictionary inside
     */
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
            alert.setContentText("This Dictionary does not exist.\nTry an other dictionary, or create a new one.");
            alert.showAndWait();
        }
    }

    /**
     * method to exit the app
     */
    public void exitAction() { Platform.exit(); }

    /**
     * method to show the solution and update all elements accordingly
     * @throws IOException Exception occurs when trying to open the pastRounds.txt to update it
     */
    public void showSolutionAction() throws IOException {
        // hidden word is not chosen yet (game not even started - the object is not created)
        if (game == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Solution Error");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("Game hasn't even started!\nTry not to give up so fast.");
            alert.showAndWait();
        }
        else if (game.gameEnded == 0) {
            showHiddenWord();

            StringBuilder hidden = new StringBuilder(this.game.roundInfo.hiddenWord.size());
            for (Character c : this.game.roundInfo.hiddenWord) hidden.append(c);

            this.game.roundInfo.pastRounds.add(new Triplet<>(hidden.toString(), this.game.player.tries, "Computer"));
            this.game.roundInfo.updatePrevRoundsDetails();

            setMessagePosLbl("");
            setMessageLbl("You gave up. You lost!");
            scrollableContainer.setContent(new Pane());
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Solution Error");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("You already found the word.\nRestart the game to play.");
            alert.showAndWait();
        }
    }

    public void showDictTxtNamesAction() {
        File f = new File("medialab");
        ArrayList<String> filesAvailable = new ArrayList<>();

        FilenameFilter textFilter = (dir, name) -> name.toLowerCase().endsWith(".txt");

        File[] files = f.listFiles(textFilter);
        assert files != null;
        for (File file : files) {
            if (!file.isDirectory()) filesAvailable.add(file.getName().split("_")[1].split("\\.")[0]);
        }

        if (filesAvailable.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No TextFiles found");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("No available txt files\nFirst load something.");
            alert.showAndWait();
        }
        else {
            StringBuilder m = new StringBuilder();
            int i = 1;
            for (String s : filesAvailable) m.append(i++).append(".").append(" ").append(s).append("\n");

            TextArea textArea = new TextArea(m.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            GridPane gridPane = new GridPane();
            gridPane.setMaxWidth(Double.MAX_VALUE);
            gridPane.add(textArea, 0, 0);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("TextFiles Available");
            alert.getDialogPane().setContent(gridPane);
            alert.showAndWait();
        }
    }

    /**
     * method that shows the past rounds
     * @throws IOException Exception occurs when trying to open the pastRounds.txt to update it
     */
    public void showPastRoundsAction() throws IOException {
        if (game == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Past Rounds Error");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("To see the past rounds you first need to load a dictionary.\nStart the game to see" +
                    " the information you want!");
            alert.showAndWait();
        }
        else {
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
    }

    /**
     * method that shows the dictionary details
     */
    public void dictDetailsAction() {
        if (game == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dictionary Details Error");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("Game has not even started.\n" +
                    "No dictionary is loaded.\n" +
                    "First load a dictionary to see this info");
            alert.showAndWait();
        }
        else {
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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    /**
     * method which is triggered when clicking the Load button
     * @throws IOException Exception occurs when trying to open the txt that has the dictionary inside
     */
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
            alert.setContentText("This Dictionary does not exist.\nTry an other dictionary, or create a new one.");
            alert.showAndWait();
        }
    }

    /**
     * method which is triggered when clicking the create button
     * tries to create a new txt file with the words retrieved from the GET Request
     * @throws IOException Exception occurs when trying to open the txt that has the dictionary inside
     */
    public void createDictAction() throws IOException {
        dictID = createDictTf.getText();
        createDictAux();
    }

    /**
     * Auxiliary method for creation of a txt dictionary (called by createDictAction, createAction)
     * @throws IOException Exception occurs when trying to open the txt that has the dictionary inside
     */
    private void createDictAux() throws IOException {
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

            createDictTf.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.setContentText("Dictionary created successfully.\nYou can load the dictionary immediately.");
            alert.showAndWait();
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

    /**
     * initialize method that takes care of the initialization of every component of the interface and the objects
     * that take care of the game logic
     * @throws IOException Exception occurs when trying to load the data of the txt that has the dictionary inside
     */
    public void initialize() throws IOException {
        setDictLbl("DictionaryID: " + dictID);
        setMessageLbl("");

        game = new PlayGame(dictID);

        // initialize the game
        game.initGame();
        System.out.println(game.roundInfo.hiddenWord.toString());
        // update word container interface
        updateWordContainer();
        // focus on the first letter
        setFocusOnLetter(0, false);
        // update the statistics
        updateStats();
        // update the image
        updateHangmanImage();
    }

    /**
     * method to update the images of the hangman
     */
    private void updateHangmanImage() {
        if (this.game.player.tries == 0) setImgContainer("assets/img/hangman6.png");
        else if (this.game.player.tries == 1) setImgContainer("assets/img/hangman5.png");
        else if (this.game.player.tries == 2) setImgContainer("assets/img/hangman4.png");
        else if (this.game.player.tries == 3) setImgContainer("assets/img/hangman3.png");
        else if (this.game.player.tries == 4) setImgContainer("assets/img/hangman2.png");
        else if (this.game.player.tries == 5) setImgContainer("assets/img/hangman1.png");
        else setImgContainer("assets/img/hangman0.png");
    }

    /**
     * method to update the stats of the game (wordsInDict, points, successRate)
     */
    private void updateStats() {
        // active dictionary words
        setActiveDictWordsLbl(this.game.roundInfo.wordsInDict);
        // score
        setScoreLbl(this.game.player.points);
        // success rate
        setSuccessRateLbl(this.game.player.successRate);
    }

    /**
     * method that updates the container with the word inside
     */
    private void updateWordContainer() {
        wordContainer.getChildren().clear();
        for (int i = 0; i < this.game.roundInfo.hiddenWord.size(); i++) {
            Letter letter = new Letter(this.game.roundInfo.playerGuess[i].toString(), i);
            letter.setOnMouseClicked(this::showLetterProbabilities);
            wordContainer.add(letter, i, 0);
        }
    }

    /**
     * method that shows the hidden word in the word container
     */
    private void showHiddenWord() {
        for (int i = 0; i < this.game.roundInfo.hiddenWord.size(); i++) {
            Letter letter = new Letter(this.game.roundInfo.hiddenWord.get(i).toString(), i);
            wordContainer.add(letter, i, 0);
        }
    }

    /**
     * Event handler that is triggered when clicking a letter in the word container
     * @param e The event object
     */
    private void showLetterProbabilities(MouseEvent e) {
        if (game.gameEnded == 0) {
            Letter letter = (Letter) e.getSource();
            setMessagePosLbl("Choosing Letter for position " + (letter.pos + 1) + ".");
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(8));
            gridPane.setHgap(5);
            gridPane.setVgap(5);

            // creating all the probability buttons of the clicked letter
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
    }

    /**
     * Event handler that is triggered when clicking a probability button
     * This click is basically how the user interacts with the game
     * @param e The event object
     */
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

    /**
     * method that focuses on a specific letter (makes a click on a specific letter so as
     * the probability buttons refresh)
     * @param position The position of the letter
     * @param foundLetter True if the player found a letter on the specific position, false if not
     */
    private void setFocusOnLetter(int position, boolean foundLetter) {
        if (foundLetter) {
            if (position < game.roundInfo.hiddenWord.size() - 1) {
                if (game.roundInfo.playerGuess[position + 1] != '?') {
                    setFocusOnLetter(position + 1, true);
                }
                else {
                    wordContainer.getChildren().get(position + 1).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null));
                    wordContainer.getChildren().get(position + 1).requestFocus();
                    setMessagePosLbl("Choosing Letter for position " + (position + 2) + ".");
                }
            }
            else if (position == game.roundInfo.hiddenWord.size() - 1) {
                if (!Arrays.toString(game.roundInfo.playerGuess).contains("?")) return;
                if (game.roundInfo.playerGuess[0] == '?') setFocusOnLetter(0, false);
                if (game.roundInfo.playerGuess[0] != '?') setFocusOnLetter(0, true);
            }
        }
        else {
            wordContainer.getChildren().get(position).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null));
            wordContainer.getChildren().get(position).requestFocus();
            setMessagePosLbl("Choosing Letter for position " + (position + 1) + ".");
        }
    }

    public static void main(String[] args) { launch(args); }

    /**
     * @param primaryStage primaryStage
     */
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