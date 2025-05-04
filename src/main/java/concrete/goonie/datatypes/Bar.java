package concrete.goonie.datatypes;

public class Bar {

    int direction;
    int value;

    public Bar(int direction, int value) {
        this.direction = direction;
        this.value = value;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
