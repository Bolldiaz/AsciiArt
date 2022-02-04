package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Shell {
    private static final String ADD_ALL_COMMAND = "add all";
    private static final String ADD_SPACE_COMMAND = "add space";
    private static final String ADD_SINGLE_CHAR_PATTERN = "^add (?<char>[ -~])$";
    private static final String ADD_RANGE_OF_CHARS_PATTERN = "^add (?<first>[ -~])-(?<second>[ -~])$";

    private static final String REMOVE_ALL_COMMAND = "remove all";
    private static final String REMOVE_SPACE_COMMAND = "remove space";
    private static final String REMOVE_SINGLE_CHAR_PATTERN = "^remove (?<char>[ -~])$";
    private static final String REMOVE_RANGE_OF_CHARS_PATTERN = "^remove (?<first>[ -~])-(?<second>[ -~])$";

    private static final char FIRST_ASCII_CHAR = 32;
    private static final char LAST_ASCII_CHAR = 126;
    private static final String INITIAL_CHARS_RANGE = "add 0-9";

    private static final String EXIT_COMMAND = "exit";
    private static final String PRINT_CHARS_COMMAND = "chars";
    private static final String CONSOLE_COMMAND = "console";
    private static final String RENDER_COMMAND = "render";
    public static final String INPUT_CURSOR = ">>> ";

    private static final int RES_FACTOR = 2;
    private static final String RES_UP_COMMAND = "res up";
    private static final String RES_DOWN_COMMAND = "res down";

    private static final String FONT_NAME = "Courier New";
    private static final String OUTPUT_FILENAME = "out.html";

    private static final String USAGE_MESSAGE = "USAGE: chars/[add/remove (all/space/<char>/<char>-<char>)]";
    private static final String WIDTH_SET_SUCCEEDED_MES = "Width set to %d%n";

    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int MIN_PIXELS_PER_CHAR = 2;

    private Set<Character> charSet = new HashSet<>();
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private BrightnessImgCharMatcher charMatcher;
    private AsciiOutput output;
    private boolean addOrRemoveOrResolution;
    private char[][] charTable;

    /**
     * constructor
     * @param img an image object, designated to be converted to ascii representation
     */
    public Shell(Image img) {
        addChars(INITIAL_CHARS_RANGE);
        charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        output = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
    }

    /**
     * print the current set of chars used for image conversion
     */
    private void showChars() {
        charSet.stream().sorted().forEach(c-> System.out.print(c + " "));
        System.out.println();
//        System.out.println(charSet.toString());
    }

    /**
     * add a range of chars to the CharSet
     * @param lowerBound the first char to add
     * @param upperBound the last char to add
     */
    private void addRangeOfChars(char lowerBound, char upperBound) {
        for (char c = lowerBound; c <= upperBound; c++) {
            charSet.add(c);
        }
    }

    /**
     * add some chars to the CharSet if the user have asked it, could be a single one or a range of chars.
     * @param cmd the user command
     * @return true if some chars added to the CharSet successfully, , o.w false
     */
    private boolean addChars(String cmd) {
        // add all chars case:
        if (cmd.equals(ADD_ALL_COMMAND)) {
            addRangeOfChars(FIRST_ASCII_CHAR, LAST_ASCII_CHAR);
            return true;
        }

        // add the space char case:
        if (cmd.equals(ADD_SPACE_COMMAND)) {
            charSet.add(' ');
            return true;
        }

        // add any other char case:
        Pattern pattern = Pattern.compile(ADD_SINGLE_CHAR_PATTERN);
        Matcher matcher = pattern.matcher(cmd);
        boolean matchFound = matcher.find();
        if (matchFound) {
            charSet.add(matcher.group("char").charAt(0));
            return true;
        }

        // add range of chars case:
        pattern = Pattern.compile(ADD_RANGE_OF_CHARS_PATTERN);
        matcher = pattern.matcher(cmd);
        matchFound = matcher.find();
        if (matchFound){
            char firstBound = matcher.group("first").charAt(0);
            char secondBound = matcher.group("second").charAt(0);
            addRangeOfChars((char) Math.min(firstBound, secondBound),
                    (char) Math.max(firstBound, secondBound));
            return true;
        }
        return false;
    }

    /**
     * remove a range of chars from the CharSet
     * @param lowerBound the first char to add
     * @param upperBound the last char to add
     */
    private void removeRangeOfChars(char lowerBound, char upperBound) {
        for (char c = lowerBound; c <= upperBound; c++) {
            charSet.remove(c);
        }
    }

    /**
     * remove some chars of the CharSet if the user have asked it, could be a single one or a range of chars.
     * @param cmd the user command
     * @return true if some chars removed from the CharSet successfully, o.w false
     */
    private boolean removeChars(String cmd) {
        // remove all chars case:
        if (cmd.equals(REMOVE_ALL_COMMAND)) {
            charSet.clear();
            return true;
        }

        // remove the space char case:
        if (cmd.equals(REMOVE_SPACE_COMMAND)) {
            charSet.remove(' ');
            return true;
        }

        // remove any other char case:
        Pattern pattern = Pattern.compile(REMOVE_SINGLE_CHAR_PATTERN);
        Matcher matcher = pattern.matcher(cmd);
        boolean matchFound = matcher.find();
        if (matchFound) {
            charSet.remove(matcher.group("char").charAt(0));
            return true;
        }

        // remove range of chars case:
        pattern = Pattern.compile(REMOVE_RANGE_OF_CHARS_PATTERN);
        matcher = pattern.matcher(cmd);
        matchFound = matcher.find();
        if (matchFound){
            char firstBound = matcher.group("first").charAt(0);
            char secondBound = matcher.group("second").charAt(0);
            removeRangeOfChars((char) Math.min(firstBound, secondBound),
                    (char) Math.max(firstBound, secondBound));
            return true;
        }
        return false;
    }

    /**
     * change the resolution of the image, if the user have asked to up/down the resolution
     * @param cmd the user command
     * @return true if the resolution had changed successfully, o.w false
     */
    private boolean resolutionChange(String cmd) {
        // rise up the resolution
        if (cmd.equals(RES_UP_COMMAND)) {
            if (charsInRow * RES_FACTOR > maxCharsInRow) {
                System.out.printf(WIDTH_SET_SUCCEEDED_MES, maxCharsInRow);
            } else {
                charsInRow *= RES_FACTOR;
                System.out.printf(WIDTH_SET_SUCCEEDED_MES, charsInRow);
            }
            return true;
        }

        // low down the resolution
        if (cmd.equals(RES_DOWN_COMMAND)) {
            if (charsInRow / RES_FACTOR < minCharsInRow) {
                System.out.printf(WIDTH_SET_SUCCEEDED_MES, minCharsInRow);
            } else {
                charsInRow /= RES_FACTOR;
                System.out.printf(WIDTH_SET_SUCCEEDED_MES, charsInRow);
            }
            return true;
        }

        // none of them was prompt
        return false;
    }

    /**
     * render the ascii representation of the converted image, by using output object
     * the output object could be a console or html output type.
     */
    private void render() {
        if (addOrRemoveOrResolution || charTable == null) {
            charTable = charMatcher.chooseChars(charsInRow, charSet.toArray(new Character[0]));
        }
        output.output(charTable);
    }

    /**
     * change the output object to a console type
     */
    private void console() {
        output = new ConsoleAsciiOutput();
    }

    /**
     * the conversion process, which add/remove chars to the CharSet, change the conversion resolution,
     * render the output by the current output type, all of this actions will take place according the user
     * prompt, until they will ask to exit the process.
     */
    public void run() {
        System.out.print(INPUT_CURSOR);
        Scanner scanner = new Scanner(System.in);
        String cmd = scanner.nextLine();
        while (!cmd.equals(EXIT_COMMAND)) {

            if (cmd.equals(PRINT_CHARS_COMMAND)) {          // printing the current char-set
                showChars();
            } else if (cmd.equals(CONSOLE_COMMAND)) {       // change output to console
                console();
            } else if (cmd.equals(RENDER_COMMAND)) {        // render the output
                render();
            } else if (addChars(cmd)) {                     // add chars to the charSet
                addOrRemoveOrResolution = true;
            } else if (removeChars(cmd)) {                  // remove char from the charSet
                addOrRemoveOrResolution = true;
            } else if (resolutionChange(cmd)) {
                addOrRemoveOrResolution = true;
            } else if (!cmd.equals("")) {
                System.out.println(USAGE_MESSAGE);
            }

            System.out.print(INPUT_CURSOR);
            cmd = scanner.nextLine();
            addOrRemoveOrResolution = false;
        }
    }

}