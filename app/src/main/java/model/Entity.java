package model;

import config.Reader;
import model.enums.EntityShapeType;
import model.enums.EntityType;

import static config.Constants.GAME_HEIGHT;


public abstract class Entity extends Reader {

    private final String id;
    private final EntityType type;
    private final EntityShapeType shapeType;
    private final double xPosition;
    private final double yPosition;
    private final double rotation;
    private final double direction;
    private final double height;
    private final double width;

    public Entity(String id, EntityType type, EntityShapeType shapeType, double xPosition, double yPosition,
                  double rotation, double direction, double height, double width) {
        this.id = id;
        this.type = type;
        this.shapeType = shapeType;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.rotation = rotation;
        this.direction = direction;
        this.height = height;
        this.width = width;
    }




    public abstract Entity update();
    public abstract Entity getNewGameObject();

    public boolean isInsideLimit(){
        return xPosition > 0 && xPosition < GAME_HEIGHT && yPosition > 0 && yPosition < 800;
    }
    public boolean isInsideLimit(double xPosition, double yPosition){
        return xPosition > 0 && xPosition < 725 && yPosition > 0 && yPosition < 700;
    }


    public String getId(){
        return id;
    }

    public EntityType getType() {
        return type;
    }

    public EntityShapeType getShapeType() {
        return shapeType;
    }

    public double getxPosition() {
        return xPosition;
    }

    public double getyPosition() {
        return yPosition;
    }

    public double getRotation() {
        return rotation;
    }

    public double getDirection() {
        return direction;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }
}
