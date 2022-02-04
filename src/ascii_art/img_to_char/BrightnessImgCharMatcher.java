package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.HashMap;

public class BrightnessImgCharMatcher {

    private static final int PIXELS_NUM = 16;
    private final Image image;
    private final String fontName;
    private final HashMap<Image, Double> cache = new HashMap<>();
    private int numCharsInRow;


    /**
     * consructor
     * @param image the image to convert
     * @param fontName which determent the ascii font
     */
    public BrightnessImgCharMatcher(Image image, String fontName) {
        this.image = image;
        this.fontName = fontName;
        numCharsInRow = 0;
    }

    /**
     * creates an array of doubles which represents the given char array brightnesses
     * @param charSet array of chars
     * @return array of chars' brightnesses that calculated as number of white pixels/255
     */
    private double[] charsBrightnesses(Character[] charSet) {
        double[] brightnessArray = new double[charSet.length];
        if (charSet.length == 0) {
            return brightnessArray;
        }
        for (int i = 0; i < charSet.length; i++) {
            boolean[][] charMap = CharRenderer.getImg(charSet[i],PIXELS_NUM, fontName);
            for (boolean[] row: charMap) {
                for (boolean pixel :row) {
                    if (pixel) {
                        brightnessArray[i]++;
                    }
                }
            }
            brightnessArray[i] /= (PIXELS_NUM * PIXELS_NUM - 1);
        }
        linearStretch(brightnessArray);
        return brightnessArray;
    }

    /**
     * expand the range of brightnesses, so more chars could get used as image-ascii conversion
     * @param brightnessArray the array of brightnesses to stretch
     */
    private static void linearStretch(double[] brightnessArray) {
        double min = brightnessArray[0], max = brightnessArray[0];
        for (double brightnessVal: brightnessArray) {
            min = Math.min(brightnessVal, min);
            max = Math.max(brightnessVal, max);
        }

        for (int i = 0; i < brightnessArray.length; i++) {
            brightnessArray[i] = (brightnessArray[i] - min) / (max - min);
        }
    }

    /**
     * calculates the average pixel color value of a given image, so a suit ascii char could get picked for
     * it's conversion
     * @param image an image object to calculate it's average pixel value
     * @return double between 0 and 1, represents the average value
     */
    private static double averagePixelValue(Image image) {
        double brightnessesSum = 0, pixelsNum = 0f;
        for (Color pixel: image.pixels()) {
            brightnessesSum += pixel.getRed() * 0.2126 + pixel.getGreen() * 0.7152 + pixel.getBlue() * 0.0722;
            pixelsNum++;
        }
        return brightnessesSum / (pixelsNum * 255f);
    }

    /**
     * find the char which will fit the most to an image, the char to be picked hold the brightness value
     * which closest the most to the image average pixel value.
     * Finding it by linear search.
     * @param avgImagePixelVal the image average pixel value
     * @param charBrightnessValues the array of chars brightnesses
     * @param charSet the corresponded chars array
     * @return the best character
     */
    private static char findBestMatch(double avgImagePixelVal, double[] charBrightnessValues,
                                      Character[] charSet) {
        double bestMatchDist = Math.abs(avgImagePixelVal - charBrightnessValues[0]);
        char bestMatchChar = charSet[0];
        for (int i = 1; i < charBrightnessValues.length; i++) {
            if (Math.abs(avgImagePixelVal - charBrightnessValues[i]) < bestMatchDist) {
                bestMatchChar = charSet[i];
                bestMatchDist = Math.abs(avgImagePixelVal - charBrightnessValues[i]);
            }
        }
        return bestMatchChar;
    }

    /**
     * convert the colored image, to ascii represented image, doing it by splitting the original image to
     * sub-images, and calculates the most fit ascii char to substitute it
     * @param charBrightnessValues the linear stretched char brightnesses values array
     * @param numCharsInRow determent how much sub-images will split the original one
     * @param charSet the character set used for the new image coloring
     * @return a table of chars, used as the sub-images ascii replacements
     */
    private char[][] convertImageToAscii(double[] charBrightnessValues, int numCharsInRow,
                                         Character[] charSet) {
        int pixels = image.getWidth() / numCharsInRow;
        char[][] asciiArt = new char[image.getHeight()/pixels][image.getWidth()/pixels];
        if (charSet.length == 0) {
            return asciiArt;
        }

        int row = 0, col = 0;
        for (Image subImage : image.squareSubImagesOfSize(pixels)) {
            if (!cache.containsKey(subImage))
                cache.put(subImage, averagePixelValue(subImage));

            asciiArt[row][col] = findBestMatch(cache.get(subImage), charBrightnessValues, charSet);
            col++;
            if (col == image.getWidth()/pixels) {
                col = 0;
                row++;
            }
        }
        return asciiArt;
    }

    /**
     * calculate the ascii representation of the instance image
     * @param numCharsInRow determent the resolution of the image conversion
     * @param charSet that will color the image conversion
     * @return char table used as the ascii representation
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        if (numCharsInRow != this.numCharsInRow) {
            cache.clear();
            this.numCharsInRow = numCharsInRow;
        }
        double[] charBrightnessValuesAfterStretch = charsBrightnesses(charSet);
        return convertImageToAscii(charBrightnessValuesAfterStretch, numCharsInRow, charSet);
    }
}
