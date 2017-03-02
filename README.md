# GameTrackerDb assignment given on 2-27
The requirements for this assignment were:
Create the Connection and execute a query to create a games table that stores the game name and other attributes.
Write a static method insertGame and run it in the /create-game route. It should insert a new row with the user-supplied information.
Write a static method deleteGame and run it in the /delete-game route. It should remove the correct row using id.
Write a static method selectGames that returns an ArrayList<Game> containing all the games in the database.
Remove the global ArrayList<Game> and instead just call selectGames inside the "/" route.
Add a form to edit the game name and other attributes, and create an /edit-game route. Write a static method updateGame and use it in that route. Then redirect to "/".
