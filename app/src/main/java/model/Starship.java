package model;

import model.enums.BulletType;
import model.enums.Color;
import model.enums.EntityShapeType;
import model.enums.EntityType;


public class Starship extends Entity {

    private final String playerId;
    private final BulletType bulletType;
    private final double boost;
    private final long lastBulletShot;
    private final Color color;

    public Starship(String id,  double positionX, double positionY, double rotation, double height, double width, String playerId, Color color, long lastBulletShot, double direction, double boost, BulletType bulletType) {
        super(id, EntityType.STARSHIP, EntityShapeType.TRIANGULAR, positionX, positionY, rotation, direction, height, width);
        this.color = color;
        this.playerId = playerId;
        this.lastBulletShot = lastBulletShot;
        this.boost = boost;
        this.bulletType = bulletType;
    }

    @Override
    public Entity update() {
        if (boost > 0){
            double newX =  getxPosition() -  3.5 * Math.sin(Math.PI * 2 * getDirection() / 360);
            double newY =  getyPosition() +  3.5 * Math.cos(Math.PI * 2 * getDirection() / 360);
            if (!isInsideLimit(newX, newY)){
                return new Starship(getId(), getxPosition(), getyPosition(), getRotation(), getHeight(),getWidth(),playerId, getColor(), lastBulletShot, getDirection(), 0, bulletType);
            }
            else{
                return new Starship(getId(), newX, newY, getRotation(), getHeight(),getWidth(),playerId,getColor(),lastBulletShot, getDirection(), boost - 5, bulletType);
            }
        }
        return (Starship) getNewGameObject();
    }

    public Starship move(boolean up){
        if (up) {
            return addBoost();
        }
        else return slowDown();
    }

    public Starship addBoost(){
        if (boost < 1000){
            //boost keeps getting stronger
            return new Starship(getId(), getxPosition(), getyPosition(), getRotation(), getHeight(),getWidth(),getPlayerId(),getColor(),getLastBulletShot(), getDirection(),
                    boost + 70, bulletType);
        }
        return (Starship) getNewGameObject();
    }

    public Starship slowDown(){
        if (boost > 0){
            //boost keeps getting weaker
            return new Starship(getId(), getxPosition(), getyPosition(), getRotation(), getHeight(),getWidth(),getPlayerId(),getColor(),getLastBulletShot(), getDirection(),
                    boost - 170, bulletType);
        }
        return (Starship) getNewGameObject();
    }

    public Starship rotate(double rotation){
        return new Starship(getId(), getxPosition(), getyPosition(), getRotation() + rotation, getHeight(), getWidth(), playerId, getColor(),lastBulletShot, getDirection() + rotation,
                boost, bulletType);
    }

    @Override
    public Entity getNewGameObject() {
        return new Starship(getId(), getxPosition(), getyPosition(), getRotation(), getHeight(),getWidth(),playerId,getColor(),lastBulletShot, getDirection(), boost, bulletType);
    }

    public Color getColor() {
        return color;
    }
    public BulletType getBulletType() {
        return bulletType;
    }

    public long getLastBulletShot() {
        return lastBulletShot;
    }

    public String getPlayerId() {
        return playerId;
    }

    public double getBoost() {
        return boost;
    }

    //There is a cooldown between shots
    public boolean canShoot(){
        return System.currentTimeMillis() - lastBulletShot > 500;
    }
    public Starship shootsBullet(){
        return new Starship(getId(), getxPosition(), getyPosition(), getRotation(), getHeight(),getWidth(),playerId,getColor(),System.currentTimeMillis(), getDirection(),
                boost, bulletType);
    }
}
