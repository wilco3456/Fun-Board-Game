import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class UserClient extends Player
{
    public Scanner in;
    public PrintWriter out;
    public Socket socket;

    //Default constructor for the UserClient class
    public UserClient(int id, Socket socket){
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

        out.println("Welcome Player " + getMyPlayerId() + "!");

        logJoinTime();      //Logs the player's join time
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

    @Override
    //Sets the CardRandom array
    public void setCardRandom(int[] arr) {

    }

    @Override
    //Changes the CardRandom array (by index)
    public void changeCardRandom(int index, int value) {

    }

    @Override
    //Returns the size of the CardRandom array
    public int getCardRandomSize() {
        return 0;
    }

    @Override
    //Allows the client to select their move
    public Move makeMove(GameState game, int i) {
        InfluenceCard card = InfluenceCard.NONE;
        String decision;

        if(i == 1){
            out.println("Player " + this.getMyPlayerId() + ":");
            out.println("You must Use Either REPLACEMENT or FREEDOM!");

            while (true) {
                //Make user choose whether they wish to use the replacement card or the freedom card
                out.println("Which Influence Card do you wish to use (REPLACEMENT (R)/FREEDOM (F))?");

                //Print available Influence Cards in the deck
                out.print("Available Deck:  ");
                for (InfluenceCard cards : PlayerCards) {
                    if (cards == InfluenceCard.FREEDOM || cards == InfluenceCard.REPLACEMENT)
                        out.print(cards + ",  ");
                }
                out.print("DONE ||  ");
                out.println();

                //Check if the selected card is in the deck and save the user's Influence Card selection
                decision = in.nextLine().trim();
                if (PlayerCards.size() != 0) {
                    if (decision.equals("REPLACEMENT") || decision.equals("R")) {
                        card = InfluenceCard.REPLACEMENT;
                        if (getPlayerCards().contains(card))
                            break;
                    } else if (decision.equals("FREEDOM") || decision.equals("F")) {
                        card = InfluenceCard.FREEDOM;
                        if (getPlayerCards().contains(card))
                            break;
                    }
                }
            }
        }

        else if(i == 2) {
            //Check if the user wishes to place an Influence Card
            out.println("Player " + this.getMyPlayerId() + ":");
            out.println("Do you wish to place an Influence Card (Y/N)?");

            while (true) {
                //Check if the user wishes to place an Influence Card
                //out.println("Player " + this.getMyPlayerId() + ":");
                //out.println("Do you wish to place an Influence Card (Y/N)?");

                decision = in.nextLine().trim();    //Reads the user's input

                if (decision.equals("Y")) {
                    while (true) {
                        out.println("Which Influence Card do you wish to use (DOUBLE (D)/REPLACEMENT (R)/FREEDOM (F)/CANCEL (C))?");

                        //Print available Influence Cards in the deck
                        out.print("Available Deck:  ");
                        for (InfluenceCard cards : PlayerCards)
                            out.print(cards + ",  ");
                        out.print("CANCEL ||  ");
                        out.println();

                        //Check if the selected card is in the deck and save the user's Influence Card selection
                        decision = in.nextLine().trim();
                        if (PlayerCards.size() != 0) {
                            if (decision.equals("DOUBLE") || decision.equals("D")) {
                                card = InfluenceCard.DOUBLE;
                                if (getPlayerCards().contains(card))
                                    break;
                            } else if (decision.equals("REPLACEMENT") || decision.equals("R")) {
                                card = InfluenceCard.REPLACEMENT;
                                if (getPlayerCards().contains(card))
                                    break;
                            } else if (decision.equals("FREEDOM") || decision.equals("F")) {
                                card = InfluenceCard.FREEDOM;
                                if (getPlayerCards().contains(card))
                                    break;
                            } else if (decision.equals("CANCEL") || decision.equals("C")) {
                                card = InfluenceCard.NONE;
                                break;
                        }
                        } else if (decision.equals("CANCEL") || decision.equals("C")) {
                            card = InfluenceCard.NONE;
                            break;
                        } else
                            out.println("Your Influence Cards are finished, Select CANCEL (C)!");
                    }
                    break;
                }
                else if (decision.equals("N")) {
                    card = InfluenceCard.NONE;
                    break;
                }
            }
        }
        Coordinates coordinates1;
        Coordinates coordinates2;
        int decisionX;
        int decisionY;

        //Store the user's X & Y values, for their first move
        out.println("Place your first move:");
        while (true) {
            out.print("Place X (0-5): ");
            out.println();
            decisionX = in.nextInt();
            if (decisionX >= 0 && decisionX < 6)
                break;
        }
        while (true) {
            out.print("Place Y (0-9): ");
            out.println();
            decisionY = in.nextInt();
            if (decisionY >= 0 && decisionY < 10)
                break;
        }

        coordinates1 = new Coordinates(decisionX, decisionY);

        //Check if the user, chose to make a double move
        if (card == InfluenceCard.DOUBLE) {
            //Store the user's X & Y values, for their second move
            out.println("Place your second move:");
            while (true) {
                out.print("Place X (0-5): ");
                out.println();
                decisionX = in.nextInt();
                if (decisionX >= 0 && decisionX < 6)
                    break;
            }
            while (true) {
                out.print("Place Y (0-9): ");
                out.println();
                decisionY = in.nextInt();
                if (decisionY >= 0 && decisionY < 10)
                    break;
            }

            coordinates2 = new Coordinates(decisionX, decisionY);
        } else
            coordinates2 = new Coordinates(0, 0);   //If the DOUBLE card wasn't selected, then initialise the second X & Y values as 0

        //Checks if the move is allowed, if not use recursion to try another move
        Boolean flag = game.isMoveAllowed(new Move(card, coordinates1, coordinates2), this.getMyPlayerId());
        if (!flag) {
            out.println("Invalid Move, try again!");
            out.println("---------------------------------------------------------------------------------");
            out.println();
            if(i==1)
                return makeMove(game, 1);
            else if(i==2)
                return makeMove(game, 2);
        }

        return new Move(card, coordinates1, coordinates2);
    }
}
