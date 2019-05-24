package pd.adt;

import java.io.Serializable;

import pd.cprime.Cstring;
import pd.cprime.Ctype;

public final class Tag implements Comparable<Tag>, Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DELIMETER = ':';

    /**
     * null is less
     */
    public static int compare(Tag one, Tag another) {
        if (one == another) {
            return 0;
        }
        if (one == null) {
            return -1;
        }
        if (another == null) {
            return 1;
        }
        int k = Cstring.compare(one.value, another.value);
        if (k != 0) {
            return k;
        }
        return Cstring.compare(one.cti, another.cti);
    }

    public static Tag fromString(String s) {
        return fromString(s, DELIMETER);
    }

    public static Tag fromString(String s, int delimeter) {
        assert s != null;
        assert Ctype.isVisible(delimeter) || Ctype.isWhitespace(delimeter);
        int i = s.indexOf(delimeter);
        if (i == -1) {
            return new Tag(s, null);
        } else {
            return new Tag(s.substring(i + 1), s.substring(0, i));
        }
    }

    public final String value;

    /**
     * cti: the concept and/or scope of the tag
     */
    public final String cti;

    public Tag(String value) {
        this(value, null);
    }

    public Tag(String value, String cti) {
        assert value != null && !value.isEmpty();
        if (cti != null && cti.isEmpty()) {
            cti = null;
        }
        this.value = value;
        this.cti = cti;
    }

    @Override
    public int compareTo(Tag o) {
        return Tag.compare(this, o);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && this.getClass() == obj.getClass()) {
            return this.compareTo((Tag) obj) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Cstring.hashCode(this.value, this.cti);
    }

    @Override
    public String toString() {
        return toString(DELIMETER);
    }

    public String toString(int delimeter) {
        StringBuilder sb = new StringBuilder();
        if (cti != null) {
            sb.append(cti).appendCodePoint(delimeter);
        }
        sb.append(value);
        return sb.toString();
    }
}
