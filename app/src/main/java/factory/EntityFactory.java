package factory;

import config.GameConfiguration;
import model.*;
import model.enums.BulletType;
import model.enums.Color;
import model.enums.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static config.Constants.*;

public class EntityFactory {

    static Random random = new Random();
    public static List<Entity> generate(String bulletTypeAsString, int amountOfShips, List<Player> players, GameConfiguration gameConfiguration){
        List<Entity> objects = new ArrayList<>();
        generateStarships(bulletTypeAsString, objects, amountOfShips, players, gameConfiguration);
        return objects;
    }

    private static void generateStarships(String bulletTypeAsString, List<Entity> objects, int amountOfShips, List<Player> players, GameConfiguration gameConfiguration){
        Map<String, Color> shipColors = gameConfiguration.getShipColors();
        Map<String, BulletType> bulletTypes = gameConfiguration.getBulletTypes();
        for (int i = 1; i < amountOfShips+1; i++) {
            String id = "starship-" + i;
            double xPosition =((GAME_WIDTH / amountOfShips) * i) /2;
            Player player = players.get(i-1);
            Starship starship = new Starship(id, xPosition, 300, SHIP_ROTATION_Y, SHIP_HEIGHT, SHIP_WIDTH, player.getId(), shipColors.get("color-" + i), System.currentTimeMillis(), SHIP_DIRECTION, 0, bulletTypes.get(bulletTypeAsString));
            objects.add(starship);
        }
    }

    public static Bullet generateBullet(Starship ship){
        String id = "bullet-" + ++ BULLET_COUNT;
        Random random = new Random();
        double n = 2 + (5 - 2) * random.nextDouble();
        return switch (ship.getBulletType()) {
            case BULLET -> new Bullet(id, ship.getxPosition()+16,ship.getyPosition(), ship.getRotation(), n*5, n*2, ship.getDirection(), ship.getColor(), ship.getId(), BULLET_DAMAGE, ship.getBulletType());
            case LASER -> new Bullet(id, ship.getxPosition()+16,ship.getyPosition(), ship.getRotation(), n*7, n*7, ship.getDirection(), ship.getColor(),ship.getId(), LASER_DAMAGE, ship.getBulletType());
            case ROCKET -> new Bullet(id, ship.getxPosition()+16,ship.getyPosition(), ship.getRotation()-20, n*12, n*12, ship.getDirection(), ship.getColor(), ship.getId(), ROCKET_DAMAGE, ship.getBulletType());
            case SUPA_ROCKET -> new Bullet(id, ship.getxPosition()+16,ship.getyPosition(), ship.getRotation()-30, n*20, n*20, ship.getDirection(), ship.getColor(), ship.getId(), SUPA_ROCKET_DAMAGE, ship.getBulletType());
            case CUSTOM -> new Bullet(id, ship.getxPosition()+16,ship.getyPosition(), ship.getRotation(), 5, 5, ship.getDirection(), ship.getColor(), ship.getId(), CUSTOM_BULLET_DAMAGE, ship.getBulletType());
        };
    }


    public static void manageAsteroidGeneration(List<Asteroid> asteroids, List<Entity> gameObjects){
        //adds asteroids if there are less than 5 on screen
        if (asteroids.size() < 5){
            addNewMeteor(gameObjects);
        }
    }
    private static void addNewMeteor(List<Entity> gameObjects) {
        List<Starship> ships = getShipsList(gameObjects);
        double x;
        double y;
        Starship targetedShip = getRandomShip(ships);
        if (targetedShip == null) return;
        int side = random.nextInt(4);
        double n = 1 + (GAME_WIDTH- 1) * random.nextDouble();
        switch (side) {
            case 0 -> {
                //meteor comes from top of screen
                x = n;
                y = 0;
            }
            case 1 -> {
                //meteor comes from left of screen
                x = 0;
                y = n;
            }
            case 2 -> {
                //meteor comes from bottom of screen
                x = n;
                y = GAME_HEIGHT;
            }
            default -> {
                //meteor comes from right of screen
                x = GAME_HEIGHT;
                y = n;
            }
        }
        double direction = getDirection(x,y, targetedShip);
        String id = "meteor-" + ++ ASTEROID_COUNT;
        double height = 50 + (150 - 50) * random.nextDouble();
        double width = 50 + (150 - 50) * random.nextDouble();
        int healthBar = calculateHealthBar(width,height);
        gameObjects.add(new Asteroid(id, x, y, 180, height,width,direction, random.nextBoolean(), healthBar,healthBar));
    }
    private static Starship getRandomShip(List<Starship> ships) {
        return ships.isEmpty() ? null : ships.get(random.nextInt(ships.size()));
    }

    private static double getDirection(double x, double y, Starship target) {
        return Math.toDegrees(Math.atan2(target.getxPosition() - x, target.getyPosition() - y)) + 1 + (20 - 1) * random.nextDouble();
    }

    private static int calculateHealthBar(double width, double height){
        //average is 100 hp
        return (int) ((width * height)/100);
    }

    private static List<Starship> getShipsList(List<Entity> gameObjects){
        List<Starship> ships = new ArrayList<>();
        for (Entity gameObject : gameObjects){
            if (gameObject.getType().equals(EntityType.STARSHIP)) ships.add((Starship) gameObject);
        }
        return ships;
    }
}
