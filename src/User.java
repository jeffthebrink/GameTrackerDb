import java.util.ArrayList;

/**
 * Created by jeffbrinkley on 2/22/17.
 */
public class User {
    String name;
    ArrayList<Game> games = new ArrayList<>();

    public User(String name) {
        this.name = name;
        games = new ArrayList<>();
    }


}
