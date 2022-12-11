import config.GameConfiguration;
import config.GameState;
import config.ReaderSaverManager;
import factory.EntityFactory;
import javafx.scene.input.KeyCode;
import model.Asteroid;
import model.Entity;
import model.Player;
import model.Starship;
import model.enums.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private GameState gameState;
    private boolean isPaused;
    private final GameConfiguration gameConfiguration;
    private final List<String> eliminated;
    private final Map<String, Integer> playerPoints;
    private boolean finished;

    public Game() {
        this.gameConfiguration = new GameConfiguration();
        this.eliminated = new ArrayList<>();
        this.finished = false;
        this.playerPoints = new HashMap<>();
    }
    public void addEliminated(String id){
        eliminated.add(id);
    }

    public void start(boolean initializeFromSavedState){
        if (initializeFromSavedState) loadSavedGame();
        else loadNewGame();
        this.isPaused = false;
        loadPlayerPoints();
    }

    private void loadPlayerPoints() {
        for (Player player : getPlayers()){
            playerPoints.put(player.getId(), 0);
        }
    }

    private void loadSavedGame(){
        this.gameState = ReaderSaverManager.getSavedGameState();
    }
    private void loadNewGame(){
        List<Player> players = EntityFactory.generatePlayer(gameConfiguration);
        this.gameState = new GameState(EntityFactory.generate(players.size(), players, gameConfiguration), players);
    }
    public void sumPoints(String playerId, int points){
        int pastPoints = playerPoints.get(playerId);
        playerPoints.put(playerId, pastPoints+points);
    }
    public Map<String, KeyCode> getKeyBoardConfig(){
        return gameConfiguration.getKeyboardConfiguration();
    }
    public void shoot(String shipId){
        List<Entity> newEntities = new ArrayList<>();
        Starship bulletsShip = null;
        for (Entity obj : getEntities()){
            if (obj.getId().equals(shipId)){
                bulletsShip = (Starship) obj;
                if (bulletsShip.canShoot()) {
                    newEntities.add(bulletsShip.shootsBullet());
                }
                else {
                    newEntities.add(bulletsShip.getNewGameObject());
                }
            }
            else {
                newEntities.add(obj.getNewGameObject());
            }
        }
        if (bulletsShip != null && bulletsShip.canShoot()){
            newEntities.add(EntityFactory.generateBullet(bulletsShip));
        }
        refreshGameState(newEntities, getNewPlayers());
    }
    public void moveShip(String shipId, boolean up){
        List<Entity> newEntities = new ArrayList<>();
        for (Entity obj : getEntities()){
            if (obj.getId().equals(shipId) && !isPaused){
                Starship ship = (Starship) obj;
                newEntities.add(ship.move(up));
            }
            else {
                newEntities.add(obj.getNewGameObject());
            }
        }
        refreshGameState(newEntities, getNewPlayers());
    }
    public List<Player> getNewPlayers(){
        List<Player> players = new ArrayList<>();
        for (Player player : getPlayers()){
            players.add(player.getNewPlayer());
        }
        return players;
    }
    public List<Entity> getNewEntities(){
        List<Entity> entities= new ArrayList<>();
        for (Entity Entity : getEntities()){
            entities.add(Entity.getNewGameObject());
        }
        return entities;
    }
    public void refreshGameState(List<Entity> entities, List<Player> players){
        this.gameState = new GameState(entities, players);
    }
    public void rotateShip(String shipId, double rotation){
        List<Entity> newEntities = new ArrayList<>();
        for (Entity obj : getEntities()){
            if (obj.getId().equals(shipId) && !isPaused){
                Starship ship = (Starship) obj;
                newEntities.add(ship.rotate(rotation));

            }
            else {
                newEntities.add(obj.getNewGameObject());
            }
        }
        refreshGameState(newEntities, getNewPlayers());
    }
    public void handleCollision(String id1, String id2){
        Entity first = null;
        Entity second = null;
        for (Entity entity : getEntities()){
            if (entity.getId().equals(id1)) first = entity;
            if (entity.getId().equals(id2)) second = entity;
        }
        if (first != null && second != null){
            this.gameState = CollisionHandler.handleCollision(first, second, gameState, this);
        }
        else {
            refreshGameState(getNewEntities(),getNewPlayers());
        }
    }

    public void update(){
        if (!isPaused && gameState != null){
            boolean hasShip = false;
            List<Entity> newEntities = new ArrayList<>();
            boolean entered = false;
            for (Entity entity : getEntities()){
                if (entity.getType() == EntityType.STARSHIP) hasShip = true;
                if (entity.getType() != EntityType.STARSHIP && !entered){
                    manageMeteorSpawn(newEntities);
                    entered = true;
                }
                Entity newEntity = entity.update();
                if (newEntity != null) newEntities.add(newEntity);
                else{
                    addEliminated(entity.getId());
                }
            }
            if (!hasShip) finishGame();
            if (getEntities().size() == getPlayers().size()){
                manageMeteorSpawn(newEntities);
            }
            this.gameState = new GameState(newEntities, getNewPlayers());
        }
    }

    public List<String> getEliminated(){
        return eliminated;
    }

    private void manageMeteorSpawn(List<Entity> newEntities) {
        List<Asteroid> meteors = new ArrayList<>();
        for (Entity entity : getEntities()){
            if (entity.getType() == EntityType.ASTEROID){
                meteors.add((Asteroid) entity);
            }
        }
        EntityFactory.manageAsteroidGeneration(meteors, newEntities);
    }

    public boolean hasFinished(){
        return finished;
    }

    public void finishGame(){
        this.finished = true;
    }

    public void printLeaderBoard(){
        if (getPlayers() != null){
            System.out.println("LEADERBOARD");
            playerPoints.forEach((key, value) -> System.out.println(key + " = " + value + " points"));
        }
    }

    public List<Entity> getEntities() {
        return gameState ==null ? null : gameState.getEntities();
    }

    public List<Player> getPlayers() {
        return gameState ==null ? null : gameState.getPlayers();
    }

    public void pauseOrResumeGame(){
        this.isPaused = !isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void saveGame(){
        ReaderSaverManager.saveGameState(gameState);
    }
}