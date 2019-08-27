// This interface represents a player's logic.
// There will be two implementations of this interface: a UI (letting the user play the game) and a bot.
public interface PlayerLogic
{
    // Returns a number between 1 and 5, corresponding to the index of the player.
    // The indices are assigned by the server in the same order in which clients connected to the server.
    int getMyPlayerId();

    // Returns the move that the player makes.
    // In a bot implementation, this method will make a decision on the next move.
    // In a UI implementation, this method will let the user make a move.
    Move makeMove(GameState game, int i);
}
