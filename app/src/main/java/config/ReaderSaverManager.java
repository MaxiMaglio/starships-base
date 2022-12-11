package config;

import model.*;
import model.enums.BulletType;
import model.enums.Color;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static config.Constants.DIRECTORY;

public class ReaderSaverManager extends Reader{

    public static void saveGameState(GameState gameState){
        writeGameStateInFile(gameState.getEntities(), gameState.getPlayers());
    }

    private static void writeGameStateInFile(List<Entity> gameObjects, List<Player> players){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(DIRECTORY))){
            writeGameObjects(gameObjects, writer);
            writePlayers(players, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeGameObjects(List<Entity> entities, BufferedWriter writer) throws IOException {
        for (Entity entity : entities){
            switch (entity.getType()){
                case ASTEROID -> writer.write(getStringToWriteAsteroid((Asteroid) entity) + "\n");
                case STARSHIP -> writer.write(getStringToWriteStarship((Starship) entity) + "\n");
                case BULLET -> writer.write(getStringToWriteBullet((Bullet) entity) + "\n");
            }
        }
        writer.write("%\n"); //separates gameObjects from players
    }


    private static void writePlayers(List<Player> players, BufferedWriter writer) throws IOException {
        for (Player player : players){
            String toWrite = getStringToWritePlayer(player);
            writer.write(toWrite + "\n");
        }
    }

    private static String getStringToWritePlayer(Player player) {
        return "id:" + player.getId() + ";points:" + player.getPoints() + ";lives:" + player.getLives() + ";shipId:" + player.getShipId();
    }

    private static String getStringToWriteStarship(Starship starship) {
        return "id:" + starship.getId() + ";type:" + starship.getType().toString() + ";xPosition:" +
                starship.getxPosition() + ";yPosition:" + starship.getyPosition() + ";rotation:" +
                starship.getRotation() + ";direction:" + starship.getDirection() + ";height:" + starship.getHeight() +
                ";width:" + starship.getWidth() + ";shape:" + starship.getShapeType() + ";color:" +
                starship.getColor().toString() + ";lastBulletShot:" +  starship.getLastBulletShot() + ";playerId:" +
                starship.getPlayerId() + ";boost:" + starship.getBoost() + ";bulletType:" + starship.getBulletType();
    }

    private static String getStringToWriteBullet(Bullet bullet) {
        return "id:" + bullet.getId() + ";type:" + bullet.getType().toString() + ";xPosition:" +
                bullet.getxPosition() + ";yPosition:" + bullet.getyPosition() + ";rotation:" +
                bullet.getRotation() + ";direction:" + bullet.getDirection() + ";height:" + bullet.getHeight() +
                ";width:" + bullet.getWidth() + ";shape:" + bullet.getShapeType();
    }

    private static String getStringToWriteAsteroid(Asteroid asteroid) {
        return "id:" + asteroid.getId() + ";type:" + asteroid.getType().toString() + ";xPosition:" +
                asteroid.getxPosition() + ";yPosition:" + asteroid.getyPosition() + ";rotation:" +
                asteroid.getRotation() + ";direction:" + asteroid.getDirection() + ";height:" + asteroid.getHeight() +
                ";width:" + asteroid.getWidth() + ";shape:" + asteroid.getShapeType() + ";clockwise:" +
                asteroid.isClockwise() + ";initialHealthBar:" + asteroid.getInitialHealthBar() + ";currentHealthBar:" +
                asteroid.getCurrentHealthBar();
    }

    public static GameState getSavedGameState() {
        List<String> configLines = getLines(DIRECTORY);
        List<String> o = null;
        List<String> p = null;
        for (int i = 0; i < configLines.size(); i++) {
            String line = configLines.get(i);
            if (line.equals("%")){
                o = configLines.subList(0, i);
                p = configLines.subList(i+1, configLines.size());
                break;
            }
        }
        List<Entity> gameObjects = getSavedGameObjects(o);
        List<Player> players = getSavedPlayers(p);
        return new GameState(gameObjects, players);
    }

    private static List<Player> getSavedPlayers(List<String> lines) {
        List<Player> players = new ArrayList<>();
        for (String line : lines){
            String[] parts = line.split(";");
            String id = (String) transform(parts[0]);
            int points = (int) transform(parts[1]);
            int lives = (int) transform(parts[2]);
            String shipId = (String) transform(parts[3]);
            Player player = new Player(id, points, lives, shipId);
            players.add(player);
        }
        return players;
    }

    private static Object transform(String line){
        String[] str = line.split(":");
        String variable = str[0];
        String value = str[1];
        return switch (variable){
            case "id", "playerId", "type", "color", "shape", "shipId", "bulletType" -> value;
            case "points", "lives", "initialHealthBar", "currentHealthBar", "damage" -> Integer.parseInt(value);
            case "clockwise" -> Boolean.parseBoolean(value);
            case "xPosition", "yPosition", "rotation", "direction", "height", "width", "boost" -> Double.parseDouble(value);
            case "lastBulletShot" -> Long.parseLong(value);
            default -> "";
        };
    }

    private static List<Entity> getSavedGameObjects(List<String> lines) {
        List<Entity> gameObjects = new ArrayList<>();
        for (String line : lines){
            String[] parts = line.split(";");
            String id = (String) transform(parts[0]);
            String type = (String) transform(parts[1]);
            double xPosition = (double) transform(parts[2]);
            double yPosition = (double) transform(parts[3]);
            double rotation = (double) transform(parts[4]);
            double direction = (double) transform(parts[5]);
            double height = (double) transform(parts[6]);
            double width = (double) transform(parts[7]);
            String shape = (String) transform(parts[8]);
            String color = (String) transform(parts[9]);
            gameObjects.add(createGameObject(parts, id, type, xPosition, yPosition, rotation, direction, height, width, color));
        }
        return gameObjects;
    }

    private static Entity createGameObject(String[] parts, String id, String type, double xPosition, double yPosition, double rotation, double direction, double height, double width, String color){
        switch (type){
            case "STARSHIP" -> {
                long lastBulletShot = (long) transform(parts[10]);
                String playerId = (String) transform(parts[11]);
                double boost = (double) transform(parts[12]);
                String bulletType = (String) transform(parts[13]);
                return new Starship(id, xPosition, yPosition, rotation, height, width, playerId, getColor(color), lastBulletShot, direction, boost, getBulletType(bulletType));
            }
            case "ASTEROID" -> {
                boolean clockwise = (boolean) transform(parts[10]);
                int initialHealthBar = (int) transform(parts[11]);
                int currentHealthBar = (int) transform(parts[12]);
                return new Asteroid(id, xPosition, yPosition, rotation, height, width, direction, clockwise, initialHealthBar,currentHealthBar);
            }
            case "BULLET" -> {
                String shipId = (String) transform(parts[10]);
                int damage = (int) transform(parts[11]);
                String bulletType = (String) transform(parts[12]);
                return new Bullet(id, xPosition, yPosition, rotation, height, width, direction,getColor(color),shipId, damage, getBulletType(bulletType));
            }
        }
        return null;
    }
    private static Color getColor(String s) {
        return switch (s) {
            case "GREEN" -> Color.GREEN;
            case "BLUE" -> Color.BLUE;
            default -> Color.RED;
        };
    }
    private static BulletType getBulletType(String s) {
        return switch (s) {
            case "LASER" -> BulletType.LASER;
            case "ROCKET" -> BulletType.ROCKET;
            case "SUPA_ROCKET" -> BulletType.SUPA_ROCKET;
            case "CUSTOM" -> BulletType.CUSTOM;
            default -> BulletType.BULLET;
        };
    }
}
