package chocolatte;

import java.lang.Enum;

public enum MuseumTypes {
    WALL(0), EMPTY(1), OUEST(2), EST(3), NORD(4), SUD(5);

    private final int value;

    private MuseumTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
