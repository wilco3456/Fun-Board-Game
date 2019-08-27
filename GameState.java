import java.util.*;

// This class (not yet fully implemented) will give access to the current state of the game.
public final class GameState {
    public static final int ROWS = 6;
    public static final int COLUMNS = 10;
    private static int[][] BOARD;
    private static List<Player> PlayerList;

    //Default contructor for the GameState class
    public GameState(){
        BOARD = new int[6][10];
        PlayerList = new ArrayList<>();
    }

    //Adds player to the list of players
    public void AddtoList(Player p){
        PlayerList.add(p);
    }

    //Gets player from the list of players
    public Player GetfromList(int i){
        return PlayerList.get(i);
    }

    //Returns player list size
    public int PlayerListSize(){
        return PlayerList.size();
    }

    //Searches for the player's id and logs their join time
    public void logPlayerJoin(int player){
        for (Player players : PlayerList)
            if (players.getMyPlayerId() == player)
                players.logJoinTime();
    }

    //Places a move using X & Y Coordinates
    public void placeMove(int x, int y, int playerID){
        BOARD[x][y] = playerID;
    }

    //Places a move using Move object
    public boolean placeMove(Move m, int playerID){
        if(m.getCard() == InfluenceCard.DOUBLE){        //Checks if the Influence Card's value is DOUBLE
            placeMove(m.getFirstMove().getX(), m.getFirstMove().getY(), playerID);      //Places the player's first move on the board

            //Check if the second move is valid
            Boolean flag;
            flag = isMoveAllowed(new Move(InfluenceCard.NONE, new Coordinates(m.getSecondMove().getX(), m.getSecondMove().getY()), new Coordinates(0,0)), playerID);
            if (flag) {
                placeMove(m.getSecondMove().getX(), m.getSecondMove().getY(), playerID);     //Places the player's second move on the board
                return true;
            }
            else{
                System.out.println("Second Move Invalid!");
                placeMove(m.getFirstMove().getX(), m.getFirstMove().getY(), 0); //Revert changes & remove the first move made
                return false;
            }
        }
        else {
            BOARD[m.getFirstMove().getX()][m.getFirstMove().getY()] = playerID;     //Places the player's first move on the board
            return true;
        }
    }

    //Returns the total player count
    public Map<Integer, Integer> getWinCount() {
        Map<Integer, Integer> map = new HashMap<>();
        for(int[] rows : BOARD) {
            for (int columns : rows){
                int count = map.getOrDefault(columns, 0);       //If not initialized set value as zero
                map.put(columns, count + 1);        //Adds one to the player's score count
            }
        }
        return map;
    }

    // Returns a rectangular matrix of board cells, with six rows and ten columns.
    // Zeros indicate empty cells.
    // Non-zero values indicate stones of the corresponding player.  E.g., 3 means a stone of the third player.
    public int[][] getBoard() {
        return BOARD;
    }

    //Used to avoid out of bounds run-time error
    public boolean boundDodger(int x, int y, int player){
        if (x == 0){        //Checks if X is at its minimum boundary
            if (y == 0)        //Checks if Y is at its minimum boundary
                return (BOARD[x+1][y] == player || BOARD[x][y+1] == player || BOARD[x+1][y+1] == player);
            else if (y == 9)        //Checks if Y is at its maximum boundary
                return (BOARD[x][y-1] == player || BOARD[x+1][y] == player || BOARD[x+1][y-1] == player);
            else
                return (BOARD[x][y-1] == player || BOARD[x+1][y] == player || BOARD[x][y+1] == player || BOARD[x+1][y+1] == player || BOARD[x+1][y-1] == player);
        }
        else if (x == 5){        //Checks if X is at its maximum boundary
            if (y == 0)        //Checks if Y is at its minimum boundary
                return (BOARD[x-1][y] == player || BOARD[x][y+1] == player || BOARD[x-1][y+1] == player);
            else if (y == 9)        //Checks if Y is at its maximum boundary
                return (BOARD[x-1][y] == player || BOARD[x][y-1] == player || BOARD[x-1][y-1] == player);
            else
                return (BOARD[x-1][y] == player || BOARD[x][y-1] == player || BOARD[x-1][y-1] == player ||  BOARD[x][y+1] == player || BOARD[x-1][y+1] == player);
        }
        else{
            if (y == 0)        //Checks if Y is at its minimum boundary
                return (BOARD[x-1][y] == player || BOARD[x+1][y] == player || BOARD[x][y+1] == player || BOARD[x+1][y+1] == player || BOARD[x-1][y+1] == player);
            else if (y == 9)        //Checks if Y is at its maximum boundary
                return (BOARD[x-1][y] == player || BOARD[x][y-1] == player || BOARD[x-1][y-1] == player || BOARD[x+1][y] == player || BOARD[x+1][y-1] == player);
            else
                return (BOARD[x-1][y] == player || BOARD[x][y-1] == player || BOARD[x-1][y-1] == player || BOARD[x+1][y] == player || BOARD[x][y+1] == player ||
                        BOARD[x+1][y+1] == player || BOARD[x-1][y+1] == player || BOARD[x+1][y-1] == player);
        }
    }

    // Checks if the specified move is allowed for the given player.
    public boolean isMoveAllowed(Move move, int player) {
        InfluenceCard card =  move.getCard();
        Coordinates firstMove = move.getFirstMove();

        //If no Influence card was used, check if the spot is empty & if there are any adjacent plays
        if (card == InfluenceCard.NONE){
            if (BOARD[firstMove.getX()][firstMove.getY()] == 0){
                if (boundDodger(firstMove.getX(), firstMove.getY(), player)) {      //Checks if there are any adjacent plays, around current coordinates
                    return true;
                }

                else {
                    System.out.println("No Adjacent Plays Available on Move!");
                    return false;
                }
            }
            else {
                System.out.println("Spot Not Empty!");
                return false;
            }
        }

        //If the DOUBLE Influence card was used, check if the spot is empty & if there are any adjacent plays
        if (card == InfluenceCard.DOUBLE){
            Coordinates secondMove = move.getSecondMove();
            boolean flag;

            //Checks if the second & first move are equal
            if (secondMove.getY() == firstMove.getY() && secondMove.getX() == firstMove.getX()) {
                System.out.println("Equal First & Second Moves!");
                return false;
            }

            if (BOARD[firstMove.getX()][firstMove.getY()] == 0){
                if (boundDodger(firstMove.getX(), firstMove.getY(), player)) {

                    placeMove(move.getFirstMove().getX(), move.getFirstMove().getY(), player);  //Places a temporary move on the board
                    if (BOARD[secondMove.getX()][secondMove.getY()] == 0) {
                        if (boundDodger(secondMove.getX(), secondMove.getY(), player)) {
                            placeMove(move.getFirstMove().getX(), move.getFirstMove().getY(), 0); //Remove the temporary move made
                            return true;
                        } else {
                            placeMove(move.getFirstMove().getX(), move.getFirstMove().getY(), 0); //Remove the temporary move made
                            System.out.println("No Adjacent Plays Available on Second Move!");
                            return false;
                        }
                    }
                }
                else {
                    System.out.println("No Adjacent Plays Available on First Move!");
                    return false;
                }
            }
            else {
                System.out.println("First Spot Not Empty!");
                return false;
            }
        }

        //If the REPLACEMENT Influence card was used, checks if there are any adjacent plays
        if (card == InfluenceCard.REPLACEMENT){
            if (boundDodger(firstMove.getX(), firstMove.getY(), player))
                return true;

            else {
                System.out.println("No Adjacent Plays Available on Move!");
                return false;
            }
        }

        //If the FREEDOM Influence card was used, checks if the spot is empty
        if (card == InfluenceCard.FREEDOM){
            if (BOARD[firstMove.getX()][firstMove.getY()] == 0)
                return true;
            else {
                System.out.println("Spot Not Empty!");
                return false;
            }
        }

        return false;
    }
}

