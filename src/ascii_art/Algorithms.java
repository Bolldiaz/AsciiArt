package ascii_art;

import java.util.HashSet;

/**
 * @author: Matan Dizitser
 */
public class Algorithms {
    private static final String[] morseCode = new String[]{".-","-...","-.-.","-..",".","..-.","--.","....",
                                                           "..",".---","-.-",".-..","--","-.","---",".--.",
                                                           "--.-",".-.","...","-","..-","...-",".--","-..-",
                                                           "-.--","--.."};
    public static final int LONGEST_MORSE_LENGTH = 4;
    public static final char CHAR_DIFF_FROM_CORRESPOND_IDX = 'a';

    /**
     * find the duplicate element in the array
     * @param numList an array of integers in range of 1-n, with length of n+1
     * @return the duplicate element
     */
    public static int findDuplicate(int[] numList) {
        int slowPointer = numList[0], fastPointer = numList[numList[0]];

        // trying to reach the start of the cycle
        while (fastPointer != slowPointer) {
            fastPointer = numList[numList[fastPointer]];
            slowPointer = numList[slowPointer];
        }

        // return the slow back to the start of the array, while fast is located in the start of the cycle
        slowPointer = 0;
        while (fastPointer != slowPointer) {
            fastPointer = numList[fastPointer];
            slowPointer = numList[slowPointer];
        }
        return fastPointer;
    }

    /**
     * decode a given list of ascii-words to a set of correspond morse-word
     * @param words list of english alphabet words
     * @return set of decoded morse words
     */
    public static int uniqueMorseRepresentations(String[] words){
        HashSet<String> uniqueRepresentation = new HashSet<>();
        for (String word: words) {                                              // word.length = Si
            word = word.toLowerCase();                                          // O(Si)
            // create the concatenation of the morse code
            StringBuilder coded = new StringBuilder(word.length()* LONGEST_MORSE_LENGTH);   // O(Si)
            for (int i = 0; i < word.length(); i++) {
                /* reducing the ascii value of 'a' from each char is the index of to correspond morse code */
                int morse_idx = word.charAt(i) - CHAR_DIFF_FROM_CORRESPOND_IDX;
                coded.append(morseCode[morse_idx]);                     // O(1)
            }
            uniqueRepresentation.add(coded.toString());                         // O(Si)
        }
        return uniqueRepresentation.size();
    }

}
