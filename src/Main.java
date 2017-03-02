import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    public static void insertGames(Connection conn, String game_name, String game_genre, String game_platform, int release_year) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO games VALUES (NULL, ?, ?, ?, ? );");
        stmt.setString(1, game_name);
        stmt.setString(2, game_genre);
        stmt.setString(3, game_platform);
        stmt.setInt(4, release_year);
        stmt.execute();
    }

    public static void deleteGame(Connection conn, int id) throws SQLException {
        PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM games WHERE ID  = ?");
        stmt1.setInt(1, id);
        stmt1.execute();
    }

    public static ArrayList<Game> selectGames(Connection conn) throws SQLException {
        ArrayList<Game> gameArrayList = new ArrayList<>();
        PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM games");
        ResultSet results = stmt2.executeQuery();
        while (results.next()) {
            int id = results.getInt("ID");
            String game_name = results.getString("game_name");
            String game_genre = results.getString("game_genre");
            String game_platform = results.getString("game_platform");
            int release_year = results.getInt("release_year");
            gameArrayList.add(new Game(id, game_name, game_genre, game_platform, release_year));
        }

        System.out.println("Current game list:");
        for (Game game : gameArrayList) {
            System.out.print(game.toString()); //todo now display this array list onto the html page with mustache template
        }
        return gameArrayList;

    }

    public static void updateGame(
            Connection conn, int id, String new_game_name, String new_game_genre, String new_game_platform, int new_release_year) throws SQLException {
        PreparedStatement stmt3 = conn.prepareStatement(
                "UPDATE games SET game_name = ?, game_genre = ?, game_platform = ?, release_year= ? WHERE ID = ?"
        );
        stmt3.setString(1, new_game_name);
        stmt3.setString(2, new_game_genre);
        stmt3.setString(3, new_game_platform);
        stmt3.setInt(4, new_release_year);
        stmt3.setInt(5, id);
        stmt3.execute(); // todo left off here
    }


    public static void main(String[] args) throws SQLException {
        System.out.println("Starting GameTrackerDb...");

        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        Statement stmt = conn.createStatement();
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS games (id IDENTITY,  game_name VARCHAR, game_genre VARCHAR, game_platform VARCHAR, release_year INT )");

        Spark.init();

        Spark.get("/", (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);

                    HashMap m = new HashMap();
                    if (user == null) {
                        return new ModelAndView(m, "login.html");
                    } else {
                        user.games = selectGames(conn);
                        m.put("name", name);
                        m.put("games", user.games);
                        return new ModelAndView(m, "home.html");
                    }
                },
                new MustacheTemplateEngine()
        );

        Spark.post("/create-game", (request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            User user = users.get(name);
            if (user == null) {
                throw new Exception("User is not logged in");
            }
            String game_name = request.queryParams("gameName");
            String game_genre = request.queryParams("gameGenre");
            String game_platform = request.queryParams("gamePlatform");
            int release_year = Integer.parseInt(request.queryParams("gameYear"));
            insertGames(conn, game_name, game_genre, game_platform, release_year);
            response.redirect("/");
            return "";
        });

        Spark.post("/editGame", (request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            User user = users.get(name);
            if (user == null) {
                throw new Exception("User is not logged in");
            }
            int editGameNumber = Integer.parseInt(request.queryParams("editGameNumber"));
            String game_name = request.queryParams("editGameName");
            String game_genre = request.queryParams("editGameGenre");
            String game_platform = request.queryParams("editGamePlatform");
            int release_year = Integer.parseInt(request.queryParams("editGameYear"));
            updateGame(conn, editGameNumber, game_name, game_genre, game_platform, release_year);
            response.redirect("/");
            return "";
        });

        Spark.post("/deleteGame", (request, response) -> {
            Session session = request.session();
            String name = session.attribute("userName");
            User user = users.get(name);
            if (user == null) {
                throw new Exception("User is not logged in");
            }
            if (!request.queryParams("deleteGameNumber").isEmpty())
            {
                int id = Integer.parseInt(request.queryParams("deleteGameNumber"));
                deleteGame(conn, id);
            }
            response.redirect("/");
            return "";
        });

        Spark.post("/logout", (request, response) -> {
            Session session = request.session();
            session.invalidate();
            response.redirect("/");
            return "";
        });

        Spark.post("/login", (request, response) -> {
            String name = request.queryParams("loginName");
            if (!name.isEmpty()) {
                users.putIfAbsent(name, new User(name)); // simple way to register new user
            }
            Session session = request.session();
            session.attribute("userName", name);    // track user in session cookie
            response.redirect("/");
            return "";

        });
    }
}
