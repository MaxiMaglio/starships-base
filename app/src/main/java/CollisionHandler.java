import config.GameState;
import model.*;
import model.enums.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollisionHandler {

    public static GameState handleCollision(Entity first, Entity second, GameState gameState, Game game){
        List<Entity> gameObjects = gameState.getEntities();
        List<Player> players = gameState.getPlayers();
        GameState newGameState = null;
        if ((first.getType() == EntityType.BULLET && second.getType() == EntityType.STARSHIP) || (second.getType() == EntityType.BULLET && first.getType() == EntityType.STARSHIP)){
            //bullet and ship collision
            newGameState = manageBulletShipCollision(first,second,gameObjects, players, game);
        }
        else if ((first.getType() == EntityType.BULLET && second.getType() == EntityType.ASTEROID) || (second.getType() == EntityType.BULLET && first.getType() == EntityType.ASTEROID)){
            //bullet and asteroid collision
            newGameState = manageBulletAsteroidCollision(first, second, players, gameObjects, game);
        }
        else if ((first.getType() == EntityType.STARSHIP && second.getType() == EntityType.ASTEROID) || (second.getType() == EntityType.STARSHIP && first.getType() == EntityType.ASTEROID)){
            //ship and asteroid collision
            newGameState = manageShipAsteroidCollision(first, second, players, gameObjects, game);
        }
        if (newGameState == null) return new GameState(game.getNewEntities(), game.getNewPlayers());
        return newGameState;
    }

    private static GameState manageBulletShipCollision(Entity first, Entity second,List<Entity> gameObjects, List<Player> players, Game game){
        Bullet bullet;
        Starship ship;
        if (first.getType() == EntityType.BULLET){
            bullet = (Bullet) first;
            ship = (Starship) second;
        }
        else{
            bullet = (Bullet) second;
            ship = (Starship) first;
        }
        //Check if the bullet belongs to the ship
        if (Objects.equals(ship.getId(), bullet.getShipId())) return null;

        List<Entity> newGameObjects = new ArrayList<>();
        List<Player> newPlayers = new ArrayList<>();
        Player shipPlayer = getPlayer(ship.getPlayerId(), players);
        Player newPlayer = null;

        for (Entity gameObject : gameObjects){
            if (bullet.getId().equals(gameObject.getId())){
                game.addEliminated(gameObject.getId());
            }
            else if (gameObject.getId().equals(ship.getId())) {
                newPlayer = new Player(shipPlayer.getId(),shipPlayer.getPoints(), shipPlayer.getLives()-1, shipPlayer.getShipId());
                if (newPlayer.getLives() > 0){
                    //reset position
                    newGameObjects.add(new Starship(ship.getId(),300,300,180, ship.getHeight(),ship.getWidth(),ship.getPlayerId(),ship.getColor(),ship.getLastBulletShot(),180,0,
                            ship.getBulletType()));
                }
                else{
                    game.addEliminated(ship.getId());
                }
            }
            else{
                newGameObjects.add(gameObject.getNewGameObject());
            }
        }
        for (Player player : players){
            if (player.getId().equals(shipPlayer.getId())){
                if (newPlayer == null) continue;
                newPlayers.add(newPlayer);
            }
            else {
                newPlayers.add(player.getNewPlayer());
            }
        }
        if (newPlayers.isEmpty()) game.finishGame();
        return new GameState(newGameObjects, newPlayers);
    }

    private static Player getPlayer(String playerId, List<Player> players){
        for (Player player : players){
            if (playerId.equals(player.getId())){
                return player;
            }
        }
        return null;
    }

    private static GameState manageBulletAsteroidCollision(Entity first, Entity second, List<Player> players, List<Entity> gameObjects, Game game){
        Bullet bullet;
        Asteroid asteroid;
        if (first.getType() == EntityType.BULLET){
            bullet = (Bullet) first;
            asteroid = (Asteroid) second;
        }
        else{
            bullet = (Bullet) second;
            asteroid = (Asteroid) first;
        }
        List<Entity> newGameObjects = new ArrayList<>();
        List<Player> newPlayers = new ArrayList<>();
        Player shipPlayer = getPlayer(bullet.getShipId(), players, gameObjects);
        Player newPlayer = null;
        
        for (Entity gameObject : gameObjects){
            if (bullet.getId().equals(gameObject.getId())){
                game.addEliminated(gameObject.getId());
            }
            else if (gameObject.getId().equals(asteroid.getId())) {
                //Update starship's lives
                Asteroid newAsteroid = new Asteroid(asteroid.getId(), asteroid.getxPosition(), asteroid.getyPosition(), asteroid.getRotation(), asteroid.getHeight(),
                        asteroid.getWidth(),asteroid.getDirection(), asteroid.isClockwise(), asteroid.getInitialHealthBar(), (int) (asteroid.getCurrentHealthBar()-bullet.getDamage()));
                if (newAsteroid.getCurrentHealthBar() <= 0){
                    //Update player's points, having killed an asteroid
                    newPlayer = new Player(shipPlayer.getId(),shipPlayer.getPoints() + newAsteroid.getPoints(), shipPlayer.getLives(), shipPlayer.getShipId());
                    game.sumPoints(newPlayer.getId(), newAsteroid.getPoints());
                    game.addEliminated(gameObject.getId());
                }
                else{
                    newGameObjects.add(newAsteroid);
                    newPlayer = new Player(shipPlayer.getId(),shipPlayer.getPoints(), shipPlayer.getLives(), shipPlayer.getShipId());
                }
            }
            else{
                newGameObjects.add(gameObject.getNewGameObject());
            }
        }
        for (Player player : players){
            if (player.getId().equals(shipPlayer.getId())){
                if (newPlayer == null) continue;
                newPlayers.add(newPlayer);
            }
            else {
                newPlayers.add(player.getNewPlayer());
            }
        }
        return new GameState(newGameObjects, newPlayers);
    }

    private static GameState manageShipAsteroidCollision(Entity first, Entity second, List<Player> players, List<Entity> gameObjects, Game game){
        Starship ship;
        Asteroid meteor;
        if (first.getType() == EntityType.STARSHIP){
            ship = (Starship) first;
            meteor = (Asteroid) second;
        }
        else{
            ship = (Starship) second;
            meteor = (Asteroid) first;
        }
        List<Entity> newGameObjects = new ArrayList<>();
        List<Player> newPlayers = new ArrayList<>();
        Player shipPlayer = getPlayer(ship.getPlayerId(), players);
        Player newPlayer = null;
        for (Entity gameObject : gameObjects){
            if (ship.getId().equals(gameObject.getId())){
                newPlayer = new Player(shipPlayer.getId(),shipPlayer.getPoints(), shipPlayer.getLives() - 1, shipPlayer.getShipId());
                if (newPlayer.getLives() > 0){
                    newGameObjects.add(new Starship(ship.getId(),300,300,180, ship.getHeight(),ship.getWidth(),ship.getPlayerId(),ship.getColor(),
                            ship.getLastBulletShot(),180,0, ship.getBulletType()));
                }
                else {
                    game.addEliminated(gameObject.getId());
                    newPlayer = null;
                }
            }
            else if (gameObject.getId().equals(meteor.getId())) {
                game.addEliminated(gameObject.getId());
            }
            else{
                newGameObjects.add(gameObject.getNewGameObject());
            }
        }
        for (Player player : players){
            if (player.getId().equals(shipPlayer.getId())){
                if (newPlayer == null) continue;
                newPlayers.add(newPlayer);
            }
            else {
                newPlayers.add(player.getNewPlayer());
            }
        }
        if (newPlayers.isEmpty()) game.finishGame();
        return new GameState(newGameObjects, newPlayers);
    }



    private static Player getPlayer(String shipId, List<Player> players, List<Entity> gameObjects){
        String playerId = "";
        for (Entity gameObject : gameObjects){
            if (gameObject.getType() == EntityType.STARSHIP && gameObject.getId().equals(shipId)) {
                Starship ship = (Starship) gameObject;
                playerId = ship.getPlayerId();
            }
        }
        return getPlayer(playerId, players);
    }
}
