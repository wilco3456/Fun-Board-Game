import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BotClient extends Player {
    //Default constructor for the UserClient class
    private int[] cardRandom = {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,3};
    public Scanner in;
    public PrintWriter out;
    public Socket socket;

    //Default constructor for the BotClient class
    BotClient(int id, Socket socket){
        super(id);

        this.socket = socket;

        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.println("Press ENTER To Join The Game!");       //Prints output to the client using the PrintWriter object
        in.nextLine().trim();       //Waits for the user to press ENTER

        out.println("BOT INITIALIZED!");
        out.println("Welcome Player " + getMyPlayerId() + "!");

        logJoinTime();
    }

    @Override
    //Sets the CardRandom array
    public void setCardRandom(int[] arr){
        cardRandom = arr;
    }

    @Override
    //Changes the CardRandom array (by index)
    public void changeCardRandom(int index, int value){
        cardRandom[index] = value;
    }

    @Override
    //Returns the size of the CardRandom array
    public int getCardRandomSize() {
        return cardRandom.length;
    }

    @Override
    //Notifies a user if they have won or lost
    public void hasWon(int player) {
        if (player == getMyPlayerId()) {
            out.println("#############################################################################");
            out.println("YOU'VE WON THE GAME!");
        }
        else {
            out.println("#############################################################################");
            out.println("YOU'VE LOST!");
        }
    }

    //Makes a list of the coordinates, which have values of zero (are empty)
    private List<List<Integer>> collectPossibleMoves(GameState game){
        int[][] b = game.getBoard();
        List l = new ArrayList<>();
        for (int x=0; x<6; x++) {
            for (int y=0; y<10; y++) {
                //If the current position is empty, then add the coordinates to the list
                if(b[x][y] == 0){
                    List tList = new ArrayList<>();
                    tList.add(x);
                    tList.add(y);
                    l.add(tList);
                }
            }
        }

        return l;
    }

    //Makes a list of the coordinates, which have values of zero (are empty)
    private List<List<Integer>> collectPossibleReplacementMoves(GameState game){
        int[][] b = game.getBoard();
        List l = new ArrayList<>();
        for (int x=0; x<6; x++) {
            for (int y=0; y<10; y++) {
                //If the current position is occupied by another player, then add the coordinates to the list
                if(b[x][y] != this.getMyPlayerId() && b[x][y] != 0){
                    List tList = new ArrayList<>();
                    tList.add(x);
                    tList.add(y);
                    l.add(tList);
                }
            }
        }

        return l;
    }

    @Override
    //Allows the client to select their move
    public Move makeMove(GameState game, int k) {
        List<Integer> l1 = new ArrayList<>();
        List<Integer> ll1 = new ArrayList<>();
        Random r = new Random();
        InfluenceCard card = InfluenceCard.NONE;

        //Selects a random number from the array CardRandom
        int randomIndex = r.nextInt(cardRandom.length);
        int selectedCard = cardRandom[randomIndex];
        if (selectedCard == 0)
            card = InfluenceCard.NONE;
        else if (selectedCard == 1)
            card = InfluenceCard.FREEDOM;
        else if (selectedCard == 2)
            card = InfluenceCard.REPLACEMENT;
        else if (selectedCard == 3)
            card = InfluenceCard.DOUBLE;

        //Sets the selected value's index to zero
        cardRandom[randomIndex] = 0;

        //Executes if the freedom card was selected
        if (card == InfluenceCard.FREEDOM){

            List<List<Integer>> l = this.collectPossibleMoves(game);      //stores the possible moves which can be made
            Collections.shuffle(l);     //Shuffles the possible moves

            //selects the first possible move
            l1 = l.get(0);
        }
        //Executes if the replacement card was selected
        else if (card == InfluenceCard.REPLACEMENT){

            List<List<Integer>> lr = this.collectPossibleReplacementMoves(game);      //stores the possible replacement moves which can be made
            Collections.shuffle(lr);     //Shuffles the possible replacement moves

            for (List<Integer> aLr : lr) {
                l1 = aLr;
                int x = l1.get(0);
                int y = l1.get(1);

                //Checks if there is an adjacent player with the same player id
                if (game.boundDodger(x, y, getMyPlayerId()))
                    break;
            }
        }

        else if (card == InfluenceCard.NONE) {

            List<List<Integer>> l = this.collectPossibleMoves(game);
            Collections.shuffle(l);

            //select first move
            for (List<Integer> aL : l) {
                l1 = aL;
                int x = l1.get(0);
                int y = l1.get(1);

                if (game.boundDodger(x, y, getMyPlayerId()))
                    break;
            }
        }

        else {

            List<List<Integer>> l = this.collectPossibleMoves(game);
            Collections.shuffle(l);

            for (List<Integer> aL : l) {
                l1 = aL;
                int x = l1.get(0);
                int y = l1.get(1);

                if (game.boundDodger(x, y, getMyPlayerId()))
                    break;
            }
            game.placeMove(l1.get(0), l1.get(1), getMyPlayerId());  //Places a temporary move on the board

            List<List<Integer>> l2 = this.collectPossibleMoves(game); //Collects new possible moves from the board
            for (List<Integer> aL2 : l2) {
                ll1 = aL2;
                int x = ll1.get(0);
                int y = ll1.get(1);

                if (game.boundDodger(x, y, getMyPlayerId()))
                    break;
            }
            game.placeMove(l1.get(0), l1.get(1), 0);  //Removes temporary move from the board
        }

        out.println("Player: " + this.getMyPlayerId());
        if(!l1.isEmpty()) {
            //Print available Influence Cards in the deck
            out.print("Available Deck:  ");
            for (InfluenceCard cards : PlayerCards)
                out.print(cards + ",  ");
            out.print("DONE ||  ");
            out.println();

            //Prints the details of the move made by the bot
            out.println("Influence Card: " + card);
            out.println("X: " + l1.get(0));
            out.println("Y: " + l1.get(1));

            //Prints the details of the second coordinates, if the card was a double
            if (card == InfluenceCard.DOUBLE){
                out.println();
                out.println("2X: " + ll1.get(0));
                out.println("2Y: " + ll1.get(1));
            }
        }

        try {
            TimeUnit.SECONDS.sleep(1);      //Makes sure the Bot's move takes a second to carry out
        }catch(Exception e){}

        if (card == InfluenceCard.DOUBLE)
            return( new Move( card, new Coordinates(l1.get(0), l1.get(1)), new Coordinates(ll1.get(0), ll1.get(1)) ) );

        return( new Move( card, new Coordinates(l1.get(0), l1.get(1)), new Coordinates(0,0) ) );
    }

    @Override
    //Prints the board to the client
    public void boardToClient(GameState game, int player){
        if (player == getMyPlayerId()){
            for (int[] rows: game.getBoard()) {
                for (int columns : rows) {
                    out.print(columns);
                }
                out.println();
            }
            out.println();
        }else{
            for (int[] rows: game.getBoard()) {
                for (int columns : rows) {
                    out.print(columns);
                }
                out.println();
            }
            out.println("-----------------------------------------------------------------------------");
            out.println();
        }
    }

    @Override
    //Prints the current game status to the client
    public void statusToClient(int player){
        if (player == getMyPlayerId()){
            out.println("Its Your Turn!");
            out.println();
        }else {
            out.println("Its Player " + player + "'s Turn!");
            out.println("Waiting ...");
            out.println();
        }
    }

    @Override
    //Notifies the client if they're blocked
    public void notifyBlock(int player){
        if (player == getMyPlayerId()){
            out.println("Your Blocked!");
            out.println("Skipping Move ...");
            out.println("-----------------------------------------------------------------------------");
            out.println();
        }else {
            out.println("Player " + player + " Is Blocked");
            out.println("Skipping Move ...");
            out.println("-----------------------------------------------------------------------------");
            out.println();
        }
    }

    @Override
    //Notifies the client when its time to remove the double cards
    public void notifyDoubleRemove(){
        out.println("Only One Move Left On The Board!");
        out.println("Removing Double Cards!");
    }
}
