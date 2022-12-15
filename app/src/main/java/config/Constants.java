package config;

import java.util.List;

import static config.Reader.getLines;

public class Constants {


    //GAME CONFIGURATION
    public static final List<String> LINES = getLines("app/src/main/java/config/GameConfiguration");
    public static final String DIRECTORY = "app/src/main/java/config/config";

    public static final double GAME_WIDTH = 1920;
    public static final double GAME_HEIGHT = 1080;


    //SHIP CONSTANTS

    public static final double SHIP_ROTATION_NORMAL = 20;

    public static final double SHIP_ROTATION_Y = 180;
    public static final double SHIP_HEIGHT = 40;
    public static final double SHIP_WIDTH = 40;
    public static final double SHIP_DIRECTION = 180;
    public static final double SHIP_MAX_BOOST = 1000;
    public static final double SHIP_ACCELERATION = 70;


    //BULLET CONSTANTS
    public static int BULLET_COUNT = 0;
    public static final int BULLET_DAMAGE = 25;
    public static final int LASER_DAMAGE = 50;
    public static final int ROCKET_DAMAGE = 75;
    public static final int SUPA_ROCKET_DAMAGE = 1000;


    //CUSTOM BULLET CONSTANTS
    public static final int CUSTOM_BULLET_DAMAGE = 10;

    //ASTEROID CONSTANTS
    public static int ASTEROID_COUNT = 0;

}
