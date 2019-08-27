import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game {
    private GameState gs;
    private int StartPlayerCount;
    private int BotCount;
    private boolean removedDoubles = false;

    //Default Game constructor
    Game(){
        gs = new GameState();
        Scanner reader = new Scanner(System.in);

        //Sets the amount of clients meant to play the game
        while(true) {
            System.out.println("How many players do you wish to start the game with (2-5)?");
            StartPlayerCount = reader.nextInt();
            System.out.println();
            if (StartPlayerCount >= 2 && StartPlayerCount < 6)
                break;
        }

        //Sets the amount of BotClients meant to play the game
        while(true) {
            System.out.println("How many bots do you want (0-" +  StartPlayerCount + ")?");
            BotCount = reader.nextInt();
            System.out.println();
            if (BotCount >= 0 && BotCount <= StartPlayerCount)
                break;
        }
    }

    //Relinquishes control from the server and starts the game
    public void initialize(){
            //Starts the gameplay loop
            StartGame();

            List<Player> l = new ArrayList<>();
            int max = Collections.max(gs.getWinCount().values());     //Finds the maximum value in the map of players-to-scores

            //Prints the players with the highest points and adds them to a list of players
            for (Map.Entry<Integer, Integer> entry : gs.getWinCount().entrySet()) {
                if(max == entry.getValue()) {
                    l.add(gs.GetfromList(entry.getKey()-1));
                    System.out.println("Player " + entry.getKey() + " Has Highest!");
                }
            }

            //Adds each player's join time to a list
            List<LocalDateTime> ld = new ArrayList<>();
            for (Player p1 : l) {
                ld.add(p1.GetJoinTime());
            }

            //Outputs each player's join time to the server screen
            for (Player p1 : l) {
                p1.PrintJoinTime();
            }

            //Sorts the list of join times in ascending order and selects the latest time
            ld.sort(Collections.reverseOrder());
            LocalDateTime maxLDT = ld.get(0);

            //Prints the winner of the game (by join time) to the server screen
            int winner = 0;
            for (Player p1 : l) {
                if (maxLDT == p1.GetJoinTime()) {
                    System.out.println("Player " + p1.getMyPlayerId() + " Won!");
                    winner = p1.getMyPlayerId();
                }
            }

            //Prints the winning status of each client, to the client screens
            for (int i=0; i<gs.PlayerListSize(); i++){
                Player p = gs.GetfromList(i);
                p.hasWon(winner);
            }

            //Makes the user press ENTER before the game finally ends
            System.out.println("Press ENTER to Exit");
            Scanner sc = new Scanner(System.in);
            sc.hasNextLine();
            System.out.println("STOP ############################################################################");
    }

    //Starts the gameplay loop
    private void StartGame(){

        //Makes a random move for each player
        for(int i=0; i<StartPlayerCount; i++){
            Player p = gs.GetfromList(i);
            Move m;
            while(true) {
                m = p.makemove();
                boolean flag = gs.isMoveAllowed(m, p.getMyPlayerId());
                if (flag)
                    break;
            }
            Coordinates c = m.getFirstMove();
            gs.placeMove(c.getX(), c.getY(), p.getMyPlayerId());
        }

        PrintBoard();

        //Commences the gameplay loop whilst all the players aren't blocked
        while(CheckBlock()){
            //Loops each player in the game and makes the decision to make a move or skip a move
            for (int i=0; i<gs.PlayerListSize(); i++){
                Player p = gs.GetfromList(i);
                Move m;

                removeDoubles();    //Removes the Double card from all decks, once there is only one free move

                System.out.println("Its Player " + p.getMyPlayerId() + "'s Turn!");
                System.out.println("Waiting ...");

                //Prints the status of the game to the client
                for (int z=0; z<gs.PlayerListSize(); z++) {
                    Player p1 = gs.GetfromList(z);
                    p1.statusToClient(p.getMyPlayerId());
                }

                //Checks if the current player is blocked
                if (!p.isBlocked(gs)){
                    //Ends the game if there are no more free spaces
                    if(!freeSpaceCount())
                        break;

                    //Checks if the player has a Double card so it can be returned at the end of this move
                    boolean doubleExist = false;
                    if(p.getPlayerCards().contains(InfluenceCard.DOUBLE))
                        doubleExist = true;

                    //If the player has either Freedom or Replacement then allow them to use it instead of skipping their move
                    if (p.getPlayerCards().contains(InfluenceCard.FREEDOM)||p.getPlayerCards().contains(InfluenceCard.REPLACEMENT)){

                        System.out.println("Must Use REPLACEMENT or FREEDOM Card!");

                        if (p.getPlayerCards().contains(InfluenceCard.FREEDOM)&&p.getPlayerCards().contains(InfluenceCard.REPLACEMENT))
                            p.setCardRandom(new int[]{1,2});    //Only allows the Bot to use Freedom or Replacement
                        else if (p.getPlayerCards().contains(InfluenceCard.FREEDOM)&&!(p.getPlayerCards().contains(InfluenceCard.REPLACEMENT)))
                            p.setCardRandom(new int[]{1});      //Only allows the Bot to use Freedom
                        else if (p.getPlayerCards().contains(InfluenceCard.REPLACEMENT)&&!(p.getPlayerCards().contains(InfluenceCard.FREEDOM)))
                            p.setCardRandom(new int[]{2});    //Only allows the Bot to use Replacement

                        p.boardToClient(gs, p.getMyPlayerId());     //Prints the board to the client

                        m = p.makeMove(gs, 1);      //Allows the player to make a move (1 - Only allows player to choose Freedom or Replacement)
                        gs.placeMove(m, p.getMyPlayerId());

                        //Prints the board to all the clients
                        for (int z=0; z<gs.PlayerListSize(); z++) {
                            Player p1 = gs.GetfromList(z);
                            p1.boardToClient(gs, p.getMyPlayerId());
                        }

                        //Remvoes the card used from the current player's deck
                        p.getPlayerCards().remove(m.getCard());

                        //Returns the card random array back to its original values (excluding the value used, that is set to zero)
                        if (p.getPlayerCards().contains(InfluenceCard.FREEDOM)&&p.getPlayerCards().contains(InfluenceCard.REPLACEMENT)) {
                            if (m.getCard() == InfluenceCard.FREEDOM) {
                                p.setCardRandom(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                            } else if (m.getCard() == InfluenceCard.REPLACEMENT) {
                                p.setCardRandom(new int[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                            }
                        }else if (p.getPlayerCards().contains(InfluenceCard.FREEDOM)&&!(p.getPlayerCards().contains(InfluenceCard.REPLACEMENT))){
                            p.setCardRandom(new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
                        }else if (p.getPlayerCards().contains(InfluenceCard.REPLACEMENT)&&!(p.getPlayerCards().contains(InfluenceCard.FREEDOM))){
                            p.setCardRandom(new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
                        }

                        //If the double card was in the deck return it to the CardRandom array for the Bot
                        if(doubleExist)
                            p.changeCardRandom((p.getCardRandomSize()-1), 3);

                        PrintBoard();       //Prints the board to the server screen
                        continue;
                    }
                    else {
                        System.out.println("Player " + p.getMyPlayerId() + " Is Blocked!");
                        System.out.println("Skipping Move!");
                        p.notifyBlock(p.getMyPlayerId());
                        if (i == (gs.PlayerListSize() - 1))
                            break;
                        continue;
                    }
                }

                p.boardToClient(gs, p.getMyPlayerId());     //Prints the board to the client

                m = p.makeMove(gs, 2);      //Allows the player to make a move (2 - Allows player to choose any card from the deck)
                gs.placeMove(m, p.getMyPlayerId());     //Places the move the player made

                //Prints the booard to all the clients
                for (int z=0; z<gs.PlayerListSize(); z++) {
                    Player p1 = gs.GetfromList(z);
                    p1.boardToClient(gs, p.getMyPlayerId());
                }

                //Removes the card chosen, from the deck
                if (m.getCard() != InfluenceCard.NONE)
                    p.getPlayerCards().remove(m.getCard());

                PrintBoard();
            }
        }
    }

    //Retuns the StartPlayer Count
    public int getStartPlayerCount() {
        return StartPlayerCount;
    }

    //Returns the Bot count
    public int getBotCount() {
        return BotCount;
    }

    //Returns the current Game State
    public GameState getGs() {
        return gs;
    }

    //Returns true if there is one or more free spaces left in the game
    private boolean freeSpaceCount(){
        int spaceCount = 0;
        for (int[] rows : gs.getBoard()) {
            for (int columns : rows) {
                if (columns == 0)
                    spaceCount++;
            }
        }
        return spaceCount >= 1;
    }

    //Removes each player's double cards
    private void removeDoubles(){
        //Checks whether the doubles have been removed before
        if(!removedDoubles) {
            int count = 0;
            //Checks if there is only one free move left in the game
            for (int[] rows : gs.getBoard()) {
                for (int columns : rows) {
                    if (columns == 0)
                        count++;
                }
            }
            if (count < 2) {
                for (int z = 0; z < gs.PlayerListSize(); z++) {
                    Player p1 = gs.GetfromList(z);
                    p1.getPlayerCards().remove(InfluenceCard.DOUBLE);       //Remove the Double card from the players deck
                    p1.changeCardRandom((p1.getCardRandomSize()-1), 0);     //Removes the Double card option from the CardRandom Array
                    p1.notifyDoubleRemove();        //notifies the client that the double cards have been removed
                }
                System.out.println("Only One Move Left On The Board!");
                System.out.println("Removing Double Cards!");
                removedDoubles = true;
            }
        }
    }

    //Prints the board to the server output screen
    private void PrintBoard(){
        for (int[] rows: gs.getBoard()) {
            for (int columns : rows) {
                System.out.print(columns);
            }
            System.out.println();
        }
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println();
    }

    //Checks if the players are all blocked
    private boolean CheckBlock(){
        int blockedCount = 0;

        //Checks each player one by one whether they are blocked
        for (int z=0; z<gs.PlayerListSize(); z++) {
            Player p1 = gs.GetfromList(z);

            boolean checkBlock = p1.isBlocked(gs);
            //If the current player isn't blocked then break the loop iteration
            if (!checkBlock){
                blockedCount++;
            }
        }
        return blockedCount < getStartPlayerCount();    //If the amount of players blocked matches with the players in the game then return false
    }
}
