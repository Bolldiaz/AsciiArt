# AsciiArt

-----------------------------
-  Implementation details
-----------------------------

BrightnessImgMatcher:

    In order to find the best char replacement for each sub-image, I used findBestMatch static method, which
    run on the un-ordered chars brightness value array, and looked for the closest value to the image average
    pixel value, by using Math.abs.
    Notice I calculated the average value by converting each of the image pixels value to grey by the given
    formula, and then divide their sum by the image pixels number.

    Also to avoid redundant average value calculation for each of the subImages, I used a HashMap collection,
    so that only for each *unique* sub-image the calculation took place and saved as the value of the
    subImage as key in the HashMap.

Shell:

    In this class, the only collection was used is HashSet as the CharSet.
    I chose use this collection to enable find/add/remove of chars in efficient time of O(1), another reason
    was the necessity of one instance of each char in the set which supported by an HashSet.

-----------------------------
-    Answers to questions
-----------------------------

1) findDuplicate:
    I used the approach of floyd-cycle solution, that has learned in Discrete-Mathematics course.
    By using "fast" and "slow" pointers which jump between the values of the array the duplicated value can be
    recognized.

    run-time:
    the first meeting of slow and fast pointers won't take more then O(n), because it bounded by the time slow
    pointer will reach the cycle in the list which is the the list's length - n.
    From the same reason, the second meeting would also be bounded to O(n).

2) uniqueMorseRepresentations:
    I chose the HashSet collection to contain all the unique morse-words decoded from the given ascii-words array.

    run-time:
    Each word w(i) of length s(i) in the array was decoded to morse, and added to the unique HashSet by doing:
    1) convert w(i) to lower-case                                           |    O(s(i))
    2) create StringBuilder to contain the decode morse-word                |    O(s(i)*4) = O(s(i))
    3) convert each char of w(i) to correspond representation in morse-code |    O(1*s(i)) = O(s(i)) (1@)
    4) append the correspond morse-code to the StringBuilder                |    O(1) (2@)
    5) add StringBuilder to the HashSet                                     |    O(1)

    (1@) converting each char is O(1) because I used a constant morse-codes array so that:
    arr[i] = morse(char(a+i)) which equivalent to arr[c-a] = morse(c) where c is a char in w(i).
    (2@) code length is bound by 4 so appending it to the builder will take O(1) because no realloc will
    take place (StringBuilder capacity was defined to 4*s(i) which bound the sum of the word decoding).
