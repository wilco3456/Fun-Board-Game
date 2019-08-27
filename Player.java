import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

abstract public class Player implements PlayerLogic {
    private Integer ID;
    protected Set<InfluenceCard> PlayerCards = new HashSet<>(Arrays.asList(InfluenceCard.DOUBLE,InfluenceCard.REPLACEMENT,InfluenceCard.FREEDOM));
    private LocalDateTime JoinTime;

    //Player class constructor
    public Player(int id){
        this.ID = id;
    }

    public void logJoinTime(){
        //Stores the time the player joins the game
        LocalDateTime now = LocalDateTime.now();
        this.JoinTime = now;
    }

    public LocalDateTime GetJoinTime() {
        //Formats the time string and prints it to the screen
        return this.JoinTime;   //Retuns the Player's Join Time
    }

    public void PrintJoinTime() {
        //Formats the time string and prints it to the screen
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");
        System.out.println("Player " + getMyPlayerId() + " Joined at: " + dtf.format(JoinTime));
    }

    public Set<InfluenceCard> getPlayerCards() {
        return PlayerCards;     //Retuns the Player's Influence Card deck
    }

    @Override
    public int getMyPlayerId(){
        return this.ID;     //Retuns the Player's ID
    }

    @Override
    //Allows the client to select their move
    abstract public Move makeMove(GameState game, int i);

    //Prints the board to the client
    abstract public void boardToClient(GameState game, int player);

    //Prints the current game status to the client
    abstract public void statusToClient(int player);

    //Notifies the client if they're blocked
    abstract public void notifyBlock(int player);

    //Notifies the client when its time to remove the double cards
    abstract public void notifyDoubleRemove();

    //Sets the CardRandom array
    abstract public void setCardRandom(int[] arr);

    //Changes the CardRandom array (by index)
    abstract public void changeCardRandom(int index, int value);

    //Returns the size of the CardRandom array
    abstract public int getCardRandomSize();

    //Notifies a user if they have won or lost
    abstract public void hasWon(int player);

    //Randomly assigns the player their first move of the game
    public Move makemove(){
        int X,Y;
        Random rand = new Random();
        X = rand.nextInt(6);     //X bound = 5 (from 0)
        Y = rand.nextInt(10);    //Y bound = 9 (from 0)

        System.out.println("Player: " + this.ID);
        System.out.println("X: " + X);
        System.out.println("Y: " + Y);
        System.out.println();

        return new Move(InfluenceCard.FREEDOM, new Coordinates(X, Y), new Coordinates(0, 0));
    }

    //Makes a list of the coordinates, of this player's moves
    private List<List<Integer>> storePossibleMoves(GameState game){
        int[][] b = game.getBoard();
        List l = new ArrayList<>();
        for (int x=0; x<6; x++) {
            for (int y=0; y<10; y++) {
                //If the current position is occupied by this player, then add the coordinates to the list
                if(b[x][y] == this.getMyPlayerId()){
                    List tList = new ArrayList<>();
                    tList.add(x);
                    tList.add(y);
                    l.add(tList);
                }
            }
        }

        return l;
    }

    //Checks if the player is blocked and has to skip their move
    public boolean isBlocked(GameState game){
        List<List<Integer>> l = this.storePossibleMoves(game);      //stores the possible moves which can be made
        boolean notBlocked = false;

        for (int i = 0; i < l.size(); i++) {
            List<Integer> l1 = l.get(i);
            int x = l1.get(0);
            int y = l1.get(1);

            notBlocked = game.boundDodger(x,y,0);       //Checks if the current position is blocked

            //If the current move isn't blocked, break the loop iteration
            if (notBlocked)
                break;
        }
        return notBlocked;
    }
}
