package welfare.system.util;

import java.util.List;

public class ArrayUtil {
    public static<T> String arrayToString(T[] array) {
        StringBuilder arrayString = new StringBuilder();
        arrayString.append("(");
        int length = array.length;
        for (int i = 0; i < length-1; i++) {
            arrayString.append(array[i]).append(",");
        }
        arrayString.append(array[length-1]).append(")");
        return String.valueOf(arrayString);
    }

    public static<T> String listToString(List<T> list) {
        StringBuilder listString = new StringBuilder();
        listString.append("(");
        int length = list.size();
        for (int i = 0; i < length-1; i++) {
            listString.append(list.get(i)).append(",");
        }
        listString.append(list.get(length-1)).append(")");
        return String.valueOf(listString);
    }
}
