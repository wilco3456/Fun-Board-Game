// This class represents coordinates on the board
public final class Coordinates {
    private final int x;
    private final int y;

    public Coordinates(int x, int y)
    {
        assert y >= 0 && y < GameState.ROWS;
        assert x >= 0 && x < GameState.COLUMNS;

        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
