package enums;

public enum MovieEnum {
    TITLE(0),
    GENRE(1);
    final int column;

    MovieEnum(int column) {
        this.column = column;
    }

    public int getValue() {
        return this.column;
    }
}



