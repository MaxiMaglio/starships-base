package config;

import javafx.scene.input.KeyCode;
import model.enums.BulletType;
import model.enums.Color;

import java.util.HashMap;
import java.util.Map;

import static config.Constants.LINES;

public class GameConfiguration extends Reader {

    private final int players_quantity;
    private final Map<String, KeyCode> keyboardConfiguration;
    private final int amountOfLives;

    private final Map<String, Color> shipColors;
    private final Map<String, BulletType> bulletTypes;

    public GameConfiguration() {
        Map<String, String> map = getMap(LINES);
        this.players_quantity = Integer.parseInt(map.get("players_quantity"));
        this.amountOfLives = Integer.parseInt(map.get("amountOfLives"));
        this.keyboardConfiguration = getKeyBoardMap(map.get("keyBoardSettings"));
        this.shipColors = getShipColorMap(map.get("starships"));
        this.bulletTypes = getBulletTypesMap(map.get("bulletTypes"));
    }

    public int getPlayers_quantity() {
        return players_quantity;
    }

    public int getAmountOfLives() {
        return amountOfLives;
    }
    private Map<String, Color> getShipColorMap(String ships) {
        Map<String, Color> map = new HashMap<>();
        String[] split = ships.split(";");
        for (String s : split) {
            String[] innerSplit = s.split("=");
            map.put(innerSplit[0], getColor(innerSplit[1]));
        }
        return map;
    }
    private Map<String, BulletType> getBulletTypesMap(String bulletTypes) {
        Map<String, BulletType> map = new HashMap<>();
        String[] split = bulletTypes.split(";");
        for (String s : split) {
            String[] innerSplit = s.split("=");
            map.put(innerSplit[0], getBulletType(innerSplit[1]));
        }
        return map;
    }
    private Map<String, KeyCode> getKeyBoardMap(String keyBoardSettings) {
        Map<String, KeyCode> map = new HashMap<>();
        String[] split = keyBoardSettings.split(";");
        for (String s : split) {
            String[] innerSplit = s.split("=");
            map.put(innerSplit[0], getKeyCode(innerSplit[1]));
        }
        return map;
    }

    private KeyCode getKeyCode(String str){
        return KeyCode.getKeyCode(str);
    }

    public Map<String, KeyCode> getKeyboardConfiguration() {
        return keyboardConfiguration;
    }

    private BulletType getBulletType(String s) {
        return switch (s) {
            case "LASER" -> BulletType.LASER;
            case "ROCKET" -> BulletType.ROCKET;
            case "SUPA_ROCKET" -> BulletType.SUPA_ROCKET;
            case "CUSTOM" -> BulletType.CUSTOM;
            default -> BulletType.BULLET;
        };
    }

    private Color getColor(String s) {
        return switch (s) {
            case "GREEN" -> Color.GREEN;
            case "BLUE" -> Color.BLUE;
            default -> Color.RED;
        };
    }
    public Map<String, Color> getShipColors() {
        return shipColors;
    }

    public Map<String, BulletType> getBulletTypes() {
        return bulletTypes;
    }


}
