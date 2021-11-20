package hangman;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {


    public static String connect(String dictID) {
        HttpURLConnection connection = null;
        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();

        try {
            URL url = new URL("https://openlibrary.org/works/" + dictID + ".json");
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            assert connection != null;
            connection.disconnect();
        }
        return responseContent.toString();
    }

    public static void createDict(String s, String dictID)
    throws InvalidCountException, UndersizeException, InvalidRangeException, UnbalancedException, IOException {
        // create the dictionary (implemented as a set - no duplicates)
        Set<String> dict = new HashSet<>();

        // Break the string to an array of strings to iterate through (split on space)
        String[] words = s.split(" ");

        // append to dictionary only words with size >= 6
        for (String w : words) {
            if (w.length() >= 6) {
                dict.add(w.toUpperCase());
            }
        }

        // check for invalidRangeException even though this dictionary will only contain words
        // with size >= 6, so basically this exception will never be used
        int invalidRangeCounter = 0;
        // check for invalidCountException even though this dictionary will only contain each word
        // only one time (because it is a set)
        Map<String, Integer> wordsFreq = new HashMap<>();
        // counter to keep how many words have length >= 9. Needs to be used for UnbalancedException
        int unbalancedExceptionCounter = 0;
        for (String word : dict) {
            wordsFreq.put(word,  wordsFreq.getOrDefault(word, 0) + 1);
            if (word.length() < 6) invalidRangeCounter++;
            if (word.length() >= 9) unbalancedExceptionCounter++;
        }

        for (Map.Entry<String, Integer> e : wordsFreq.entrySet()) {
            if (e.getValue() > 1) {
                throw new InvalidCountException("This dictionary contains word " + e.getKey() + " " + e.getValue() + " times!");
            }
        }

        // check UndersizeException
        if (dict.size() < 20) {
            throw new UndersizeException("This dictionary has only " + dict.size() + " words. It must include 20 words at least!");
        }

        if (invalidRangeCounter > 0) {
            throw new InvalidRangeException("This dictionary has " + invalidRangeCounter + " word(s) with less than 6 letters!");
        }

        // calculate the 20% of the words that are contained in the dictionary
        int dict20Percent = dict.size() * 20 / 100;
        // check UnbalancedException
        if (unbalancedExceptionCounter < dict20Percent) {
            throw new UnbalancedException("This dictionary contains only " + unbalancedExceptionCounter
                    + " words with 9 or more letters. Should include at least " + dict20Percent + "words!");
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("medialab/" + "hangman_" + dictID + ".txt"), StandardCharsets.UTF_8))) {
            // write all words in the txt file
            int i = 0;
            for (String word : dict) {
                if (++i == dict.size()) writer.write(word);
                else writer.write(word + "\n");
            }
        }
        catch (IOException e) {
            throw(e);
        }
    }

    public static void main(String[] args)
    throws InvalidRangeException, UnbalancedException, UndersizeException, InvalidCountException, IOException {
        // The response of the connection
        // TODO: Make user add the ID
        String responseContent = connect("OL31390631M");

        // Convert response to json object
        JSONObject json = new JSONObject(responseContent);

        // get the attribute that has the words
        String valueAttr = json.getJSONObject("description").get("value").toString();

        valueAttr = valueAttr.replaceAll("[^a-zA-Z0-9 ]", "");

        // create the dictionary and get all exceptions
        try {
            createDict(valueAttr, "OL31390631M");
        } catch (InvalidRangeException | UnbalancedException | UndersizeException | InvalidCountException | IOException e) {
            throw(e);
        }

    }

}
