package model;

public class Player {

    private final String id;
    private final int points;
    private final int lives;
    private final String shipId;

    public Player(String id, int points, int lives, String shipId) {
        this.id = id;
        this.points = points;
        this.lives = lives;
        this.shipId = shipId;
    }

    public Player(String id, int lives, String shipId) {
        this(id, 0, lives, shipId);
    }

    public Player getNewPlayer(){
        return new Player(id, points, lives, shipId);
    }

    public String getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public int getLives() {
        return lives;
    }

    public String getShipId() {
        return shipId;
    }
}
