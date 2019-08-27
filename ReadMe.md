# Simple Fun Board Game (Server-Client Communication)

## Explanation
The task is to implement a client-server Java application for a multi-player board game.  Each player will be represented as a client program, and the server is a program coordinating the game.  The game is as follows.

* The game is played by two to five players.  Each player is assigned a 'colour' (can be a shape or letter or anything else that would allow identification of the player; in the examples below, coloured letters 'R', 'G' and 'B' will be used).  We will say that players place stones on the board, like in the Go game.
* The board has 6 rows and 10 columns.  Initially, the server places one stone of each 'colour' on the board, randomly picking the locations.
* The players make their 'moves' in turn.  The order of the players is the same as the order in which the clients connect to the game server.  In the demos below, we will assume that the first player to connect was 'R', followed by 'G', followed by 'B'.
* In each move, a player places one stone of their 'colour' on the board.  They can place a stone only on an empty cell, and only on a cell adjacent to one of their stones.  Below is an example of the first few steps of the game.
* Each player is given three influence cards at the beginning of the game.  A player can use any of their influence cards when making a move.  Once a card is used, it is discarded (i.e. each influence card can be used at most once by each player).  An influence card makes an exception from the previous rule:
  * Double-move card: allows the player to place two stones in one move.  (In terms of the adjacency rule, this move is interpreted as two consecutive moves.)
  * Replacement card: allows the player to place a stone even if the cell is not empty.  The adjacency rule still applies.
  * Freedom card: allows the player to place a stone on any empty cell, even if it is not adjacent to their existing stones.
* A player is called 'blocked' if they cannot make a move, even if using one of the influence cards available to them.  If a player is blocked, they skip their turn.  In this example, the 'G' player is blocked (assuming they don't have either replacement or freedom influence cards):
* The game ends when all the players are blocked.
* The winner of the game is the player with the highest number of stones on the board.  If there are several such players, the one of them who joined the game last, wins.  The demo below shows the last few moves of the game.  At the end, the scores are as follows: 'R': 21, 'G': 18, 'B': 21.  Of the two players with the highest scores, the winner is 'B' because they joined the game later than 'R'.

## Platform Recommendation:
* This program has been run on Windows 10.0.15063 build 15063 and Mac OS X, developed using Java version 12.0.2, Intellij version 2018.3.5
Other systems have not been tested, and it is advised to have caution with untested OS.

## Note:
* This work was compiled in Intellij. To compile it for use on the same service, copy and paste all java files into an Intelij src folder, then add to the Java Library all files except Board_Game.jar, that is the executable file.

## To Start:
* Open a command line window and navigate to the folder holding the program's *.jar* file.
* Then type: java -jar Board_Game.jar, which should run the program.

## How to use the Program:



## Example Gameplay
<pre>
C\...\RPN_Evaluator_jar>java -jar Board_Game.jar


</pre>
