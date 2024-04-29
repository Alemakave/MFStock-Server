package ru.alemakave.slib.utils;

import ru.alemakave.slib.utils.function.Function;

public class ArrayUtils {
    public static String toString(Object[] array, Function function) {
        if (array == null) {
            return "null";
        } else {
            int iMax = array.length - 1;
            if (iMax == -1) {
                return "[]";
            } else {
                StringBuilder b = new StringBuilder();
                b.append('[');
                int i = 0;

                while(true) {
                    b.append(function.apply(array[i]).toString());
                    if (i == iMax) {
                        return b.append(']').toString();
                    }

                    b.append(", ");
                    ++i;
                }
            }
        }
    }
}
