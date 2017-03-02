/**
 * Created by jeffbrinkley on 2/22/17.
 */
public class Game {
    int id;
    String name;
    String genre;
    String platform;
    int releaseYear;

    public Game(int id, String name, String genre, String platform, int releaseYear) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.platform = platform;
        this.releaseYear = releaseYear;
    }

    @Override
    public String toString() {
        return "ID: "+ this.id + ", Name: " + this.name + ", Genre: " + this.genre + ", Platform: " + this.platform + ", Release Year: " + this.releaseYear + "\n";

    }

}
