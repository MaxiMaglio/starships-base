package model;

import model.enums.BulletType;
import model.enums.Color;
import model.enums.EntityShapeType;
import model.enums.EntityType;

public class Bullet extends Entity  {


    private final String shipId;
    private final double damage;
    private final BulletType bulletType;
    private final Color color;

    public Bullet(String id, double positionX, double positionY, double rotation, double height, double width, double direction, Color color, String shipId, double damage, BulletType bulletType) {
        super(id, EntityType.BULLET, EntityShapeType.RECTANGULAR, positionX, positionY, rotation, direction, height, width);
        this.color = color;
        this.shipId = shipId;
        this.damage = damage;
        this.bulletType = bulletType;
    }

    public Color getColor() {
        return color;
    }

    public String getShipId(){
        return shipId;
    }

    public double getDamage() {
        return damage;
    }

    public BulletType getBulletType() {
        return bulletType;
    }

    @Override
    public Entity update() {
        if (runOutOfLimit()){
            return null;
        }
        return move();
    }

    private Bullet move(){
        double newX = getxPosition() - 4 * Math.sin(Math.PI * 2 * getDirection() / 360);
        double newY = getyPosition() + 4 * Math.cos(Math.PI * 2 * getDirection() / 360);
        return new Bullet(getId(), newX,newY,getRotation(),getHeight(),getWidth(),getDirection(), getColor(),shipId, damage, bulletType);
    }

    
    private boolean runOutOfLimit() {
        return !isInsideLimit();
    }

    @Override
    public Entity getNewGameObject() {
        return null;
    }
}
