# Knots-and-Crosses

Apk: https://appdistribution.firebase.dev/i/0fc32da124be905d

This is a client for playing Knots-and-Crosses on this https://generic-game-service.herokuapp.com/ gameserver. It's using async programming in form of callbacks and coroutines when doing network requests to the game server to stop the application from freezing. I have used two activities for this game. The first activity is the MainActivity and it is the menu screen where one can join or create a new game. The second activity is GameActvity. It's here the game is played.

The MainActivity has nothing fancy - only a 'Create game' button and a 'Join button'. If a user presses either 'Create game' or 'Join game' a dialog-popup will appear and ask for either a playerId or a gameId and a playerId respectivly. This is used to create or join the game. Let's look at what happens if someone creates a new game.

![MainActivity](https://user-images.githubusercontent.com/77720910/116812893-77783780-ab51-11eb-83f5-efe454a70206.jpg)

# Creating a new game
After the user have input the playerId, the application will start the GameActivity with an intent and set a bunch of variables in the GameManager to prepare for a new game. This is done to make sure that it is possible to start a new game again later without unexpected behavior. You will now be met with a bunch of buttons that are disabled for the moment. If you look to the bottom right corner you can see that the application is waiting for the opponent. It will make a poll request every 5 seconds to see if someone has joined the game. When Someone has joined the game the application will change 'Waiting for opponent' to the playerId string that has joined. When an opponent has joined you can either wait for the opponent to start or start yourself. The application will give you the cross if you are the first one to make a move, or it will give you the circle if you are the second one to make a move.

![GameInProgress](https://user-images.githubusercontent.com/77720910/116818735-2e35e100-ab6d-11eb-9363-ae6e35932a3c.jpg)


When you have made a move, all the buttons will be disabled until the application polls a new state for the game - meaining that the other player have made a move. You can also see who has the turn by looking at the color of the names. A green color means that it's that players turn. If one of the players have won a dialog will pop up to tell you so. A dialog will also tell you if it's a draw.

# Joining a game
Very similiar to the create game, but here you must also provide a gameId (gotten from the other person you want to play). Again you can choose to go first or wait.

# Cheat mode
Sometimes you just want to win over your friends no matter what. If you really want to, you have the option. By clicking the Cheat mode button you will get one extra mark to place down. This should give you a pretty hefty advantage. Will you crash the other's game? Probably. will they notice you cheated? Perhaps. Will it be fun to try? Absolutely!

If you regret your decision you might still be able to revert it! If you press the standard mode button before you have placed your second mark, it will revert your first mark and you'll be able to place down only one mark again.. Or switch to cheat mode again to finish the job. It's all up to you.


