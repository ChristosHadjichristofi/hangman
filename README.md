# Hangman Game - Multimedia NTUA

The project was to implement a variant of the Hangman game. The goal was to write this project with Java, using the OOP principles.

## Details
* The user can create new Dictionaries by just giving as input a dictionary ID, which will be used to make an api call to ```https://openlibrary.org/works/{OPEN-LIBRARY-ID}.json```. Of course the dictionary ID is the OPEN-LIBRARY-ID. This dictionary ID has to be valid (the url must exist) or else the game won't continue. The response then is retrieved, and if the status of the request was ok (less than 299) it will be saved. After this response was saved, it will be processed. From the response we care about the value attribute. This value attribute is basically a description of a book and we only want to keep words with size greater than or equal to 6. All special chars and numbers are eliminated. Also the 20% of the words in the dictionary must have length >= 9, duplicates can not exist and the total dictionary length has to be >= 20. In any other case the dictionary is invalid.
* The user can load Dictionaries (dictionaries that were created).
* The hidden word will be chosen randomly from the active dictionary. The active dictionary is being set from the user (load).
* In every round the user has to choose a letter for a specific position that he didn't find. At each position the user can choose from a scrollable list that shows the probabilities that specific letter exists at that position, in every word in the active dictionary. Note that the active dictionary, at every round is being refreshed, so the probabilities and the choices at every position will change accordingly.
* In case the player finds the specific char at that position, then all the other words which do not have that specific character in that position will be eliminated from the active dictionary.
* In case the player does a wrong guess, all the words that have that specific guess (letter) at that position, will be eliminated from the active dictionary.
* In case of a correct guess the player gets points (based on how high was the probability of that letter in that position).
* In case of a wrong guess the player loses 15 points. The points can not go less than 0.
* The game ends when the player either finds the hidden word, or runs out of tries. The tries are represented with a classic hangman image.

## Required Jars
* [JSON Java](https://github.com/stleary/JSON-java)
* [Java FX 11](https://openjfx.io/openjfx-docs/)

## GUI
Images will be uploaded
