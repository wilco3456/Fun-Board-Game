import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
// Game server using sockets
        public static final int PORT = 8888;
        private static int loginCount = 0;

        @SuppressWarnings("resource")
        public static void main(String[] args) throws IOException {
            Game game = new Game();     //Initialize a new game object
            ServerSocket server = new ServerSocket(PORT);     //Initialize a new server object
            System.out.println("Started GameServer at port " + PORT);
            System.out.println("Waiting for players to connect...");

            //Waits for the player to specify the number of players
            while(game.getStartPlayerCount() < 1){}

            //Connects each user client to the game one by one
            for(int i = loginCount; i<(game.getStartPlayerCount()-game.getBotCount()); i++) {
                Socket socket = server.accept();     //Accepts a connection from the client
                System.out.println("Player connected to Putty.");

                loginCount = loginCount + 1;    //Increments the login count by one

                game.getGs().AddtoList(new UserClient(loginCount, socket));     //Adds a user client to the player list
                game.getGs().logPlayerJoin(loginCount);     //Logs the players joining time

                System.out.println("Login: Player " + loginCount);
            }

            //Connects each bot client to the game one by one
            for(int i = loginCount; i<game.getStartPlayerCount(); i++) {
                Socket socket = server.accept();
                System.out.println("Player connected to Putty.");

                loginCount = loginCount + 1;

                game.getGs().AddtoList(new BotClient(loginCount, socket));      //Adds a bot client to the player list
                game.getGs().logPlayerJoin(loginCount);

                System.out.println("Login: Player " + loginCount);
            }

            //Ensures all the players have joined
            while (game.getGs().PlayerListSize() < game.getStartPlayerCount()){
            }

            game.initialize();      //Hands over control to the game object
        }
}
