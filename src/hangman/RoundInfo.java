package hangman;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * RoundInfo class
 * hiddenWord - The word that was hidden by the game logic
 * playerGuess - A Character Array that keeps the guesses of the player
 * activeDictID - The ID of the active dictionary
 * wordsInDict - counter of the words in the dictionary
 * freqLengthInfo - Map that keeps how many words of length X appear in the dictionary
 * candidateLettersFreqs - The letters frequency at every position (depends on the activeDictionary - renewed at every guess)
 * candidateLettersProbs - The letters probabilities at every position (depends on the activeDictionary - renewed at every guess)
 * pastRounds - Used to keep the past five rounds.
 */
public class RoundInfo {
    public ArrayList<Character> hiddenWord;
    public Character[] playerGuess;
    public String activeDictID;
    public Set<String> activeDict;
    public int wordsInDict;
    public Map<Integer, Integer> freqLengthInfo;
    public ArrayList<Map<Character, Integer>> candidateLettersFreqs;
    public ArrayList<Map<Character, Double>> candidateLettersProbs;
    ArrayList<Triplet<String, Integer, String>> pastRounds;

    /**
     * Constructor for RoundInfo
     * @param _activeDict The activeDictionary ID
     */
    public RoundInfo(String _activeDict) {
        this.activeDictID = _activeDict;
        this.activeDict = new HashSet<>();
        this.hiddenWord = new ArrayList<>();
        this.playerGuess = null;
        this.freqLengthInfo = new HashMap<>();
        this.candidateLettersFreqs = new ArrayList<>();
        this.candidateLettersProbs = new ArrayList<>();
        this.pastRounds = new ArrayList<>();
    }

    /**
     * Method that chooses a random word from the dictionary
     */
    public void chooseRandWord() {
        // convert HashSet to an array
        String[] dictArr = this.activeDict.toArray(new String[this.activeDict.size()]);

        // generate a random number
        Random rand = new Random();

        // this will generate a random number between 0 and activeDict.size - 1
        int randNum = rand.nextInt(this.activeDict.size());

        // get the element at random number index
        String randomWord = dictArr[randNum];

        // initialize playerGuess array
        this.playerGuess = new Character[randomWord.length()];
        Arrays.fill(playerGuess, '?');

        // store the random word into the hiddenWord arrayList
        for (int i = 0; i < randomWord.length(); i++) {
            hiddenWord.add(randomWord.charAt(i));
        }
    }

    /**
     * Method that updates the Active Dictionary
     * this method is called only the first time when the hiddenWord is chosen
     * must remove all words that have size != hiddenWord.size()
     */
    public void updateActiveDict() {
        this.activeDict.removeIf(word -> word.length() != this.hiddenWord.size());
        this.wordsInDict = this.activeDict.size();
    }

    /**
     * @param mode This method can be called with 2 different modes
     *             0 -> remove all words that do not contain char c at position pos
     *             1 -> remove all words that contain char c at position pos
     * @param c Which char to remove
     * @param pos At which position this character exists
     */
    public void updateActiveDict(int mode, Character c, Integer pos) {
        if (mode == 0) this.activeDict.removeIf(word -> word.charAt(pos) != c);
        else this.activeDict.removeIf(word -> word.charAt(pos) == c);

        // update wordsInDict
        this.wordsInDict = this.activeDict.size();
    }

    /**
     * createCandidateLettersFreqs method
     * Every time this method is called it (re)creates the candidateLettersFreqs
     * The candidateLettersFreqs ArrayList must be cleaned every time before adding the new data inside
     */
    public void createCandidateLettersFreqs() {
        this.candidateLettersFreqs.clear();
        // append to an array list X HashMaps, where X is the length of the size of the hidden word
        for (int i = 0; i < this.hiddenWord.size(); i++) {
            this.candidateLettersFreqs.add(new HashMap<>());
        }

        // for every word in the activeDictionary
        for (String word : this.activeDict) {
            // iterate through the word and at this.candidateLetters.get(i) (access map)
            // update the value of map at word.charAt(i)
            for (int i = 0; i < word.length(); i++) {
                this.candidateLettersFreqs.get(i).put(word.charAt(i), this.candidateLettersFreqs.get(i).getOrDefault(word.charAt(i),0) + 1);
            }
        }
    }

    /**
     * createCandidateLettersProbs method
     * Every time this method is called it (re)creates the candidateLettersProbs
     * The candidateLettersProbs ArrayList must be cleaned every time before adding the new data inside
     */
    public void createCandidateLettersProbs() {
        this.candidateLettersProbs.clear();
        // loop for all positions of the hiddenWord - because the ArrayList must consist of
        // hiddenWord.size() positions - each position has the probabilities that each letter might show up
        // in that particular position in the active dictionary
        for (int i = 0; i < this.hiddenWord.size(); i++) {
            HashMap<Character, Double> hm = new LinkedHashMap<>();
            HashMap<Character, Double> m = new HashMap<>();

            for (Character letter : this.candidateLettersFreqs.get(i).keySet()) {
                Double v = ((double) candidateLettersFreqs.get(i).get(letter)) / ((double) this.wordsInDict) * 100;
                m.put(letter, v);
            }

            m.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEachOrdered(x -> hm.put(x.getKey(), x.getValue()));

            this.candidateLettersProbs.add(hm);
        }
    }

    /**
     * createFreqLength method
     * Fills the Map that keeps how many words of length X appear in the dictionary
     */
    public void createFreqLengthInfo() {
        for (String word : this.activeDict) {
            this.freqLengthInfo.put(word.length(), freqLengthInfo.getOrDefault(word.length(),0) + 1);
        }
    }

    /**
     * @return ArrayList of Triplets with the past five Rounds (The Triplet contains Word, Tries, Winner)
     * @throws IOException This occurs when something goes wrong with opening the file to read the past 5 rounds
     */
    public ArrayList<Triplet<String, Integer, String>> getPrevRoundDetails() throws IOException {
        ArrayList<Triplet<String, Integer, String>> rounds = new ArrayList<>();
        String fileName = "medialab/rounds/pastRounds.txt";
        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            int i = 0;
            // get the first 5 lines (if they exist)
            while (((line = bufferedReader.readLine()) != null) && i < 5) {
                String[] splitLine = line.split("-");
                rounds.add(new Triplet<>(splitLine[0], Integer.parseInt(splitLine[1]), splitLine[2]));
                i++;
            }
            return rounds;
        }
    }

    /**
     * updatePrevRoundsDetails method
     * updates the rounds/pastRounds.txt file with the 5 past rounds
     * @throws IOException This occurs when something goes wrong with opening the file to write the past 5 rounds
     */
    public void updatePrevRoundsDetails() throws IOException {
        String fileName = "medialab/rounds/pastRounds.txt";
        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            int i = 0;
            // get the first 4 lines (if they exist)
            while (((line = bufferedReader.readLine()) != null) && i < 4) {
                String[] splitLine = line.split("-");
                this.pastRounds.add(new Triplet<>(splitLine[0], Integer.parseInt(splitLine[1]), splitLine[2]));
                i++;
            }
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            // write all words in the txt file
            int i = 0;
            for (Triplet<String, Integer, String> t : this.pastRounds) {
                if (++i == this.pastRounds.size()) writer.write(t.getWord() + "-" + t.getTries() + "-" + t.getWinner());
                else writer.write(t.getWord() + "-" + t.getTries() + "-" + t.getWinner() + "\n");
            }
        }
    }

}
