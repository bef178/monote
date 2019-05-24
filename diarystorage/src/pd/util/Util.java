package pd.util;

import java.util.Collection;

public class Util {

    public static String toString(Collection<? extends Object> objects, int delimeter) {
        StringBuilder sb = new StringBuilder();
        for (Object object : objects) {
            sb.append(object.toString()).appendCodePoint(delimeter);
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
