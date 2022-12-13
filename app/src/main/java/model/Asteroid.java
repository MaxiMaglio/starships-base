package model;

import model.enums.EntityShapeType;
import model.enums.EntityType;


public class Asteroid extends Entity {

    private final boolean clockwise;
    private final int initialHealthBar;
    private final int currentHealthBar;

    public Asteroid(String id, double positionX, double positionY, double rotation, double height, double width, double direction, boolean clockwise,
                    int initialHealthBar, int currentHealthBar) {
        super(id, EntityType.ASTEROID, EntityShapeType.ELLIPTICAL, positionX, positionY, rotation,  direction, height, width);
        this.clockwise = clockwise;
        this.initialHealthBar = initialHealthBar;
        this.currentHealthBar = currentHealthBar;
    }

    @Override
    public Entity update() {
        if (runOutOfLimit(3,3)){
            return null;
        }
        return move();
    }

    @Override
    public Entity getNewGameObject() {
        return new Asteroid(getId(), getxPosition(), getyPosition(),getRotation(),getHeight(),getWidth(),getDirection(),clockwise,initialHealthBar,currentHealthBar);
    }

    private Asteroid move(){
        double newX =  getxPosition() + 0.7 * Math.sin(Math.PI * 2 * getDirection() / 360);
        double newY =  getyPosition() + 0.7 * Math.cos(Math.PI * 2 * getDirection() / 360);
        double newRotation;
        if (clockwise) {
            newRotation = getRotation() + 2;
        } else {
            newRotation = getRotation() - 2;
        }
        return new Asteroid(getId(), newX, newY,newRotation,getHeight(),getWidth(),getDirection(),clockwise,initialHealthBar,currentHealthBar);
    }

    private boolean runOutOfLimit(double shiftx, double shifty) {
        return !isInsideLimit(getxPosition()+shiftx, getyPosition()+shifty);
    }
    public boolean isClockwise() {
        return clockwise;
    }


    public int getCurrentHealthBar() {
        return currentHealthBar;
    }
    public int getInitialHealthBar(){
        return initialHealthBar;
    }
    public int getPoints(){
        return initialHealthBar;
    }
}
