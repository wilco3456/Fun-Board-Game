// This class represents a single move of a player.
public final class Move
{
    private final InfluenceCard card;
    private final Coordinates firstMove;
    private final Coordinates secondMove;

    public Move(InfluenceCard card, Coordinates firstMove, Coordinates secondMove) {
        assert firstMove != null;
        assert card == InfluenceCard.DOUBLE || secondMove == null;

        this.card = card;
        this.firstMove = firstMove;
        this.secondMove = secondMove;
    }

    public Coordinates getFirstMove() { return firstMove; }
    public InfluenceCard getCard() { return card; }
    public Coordinates getSecondMove() { return secondMove; }
}
