import java.awt.Graphics;
import java.io.*;

/**
 *
 * @author Valkyrien
 */
public abstract class Entity implements Serializable {
    protected int posX;
    protected int posY;
    protected int width;
    protected int height;
    volatile boolean alive;
    
    protected GamePanel game;

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }
    
    public Entity(GamePanel gp) {
        this.game = gp;
    }

    public abstract void draw(Graphics g);
    
    public boolean touchesTop(Entity other) {
        return this.posX < other.posX + other.width &&
               this.posX + this.width > other.posX &&
               this.posY + height == other.posY;
    }
    public boolean touches(Entity other) {
        return this.posX == other.posX + other.width ||
               this.posX + this.width == other.posX ||
               this.posY == other.posY + other.height ||
               this.posY + this.height == other.posY;
    }
    public boolean colides(Entity other) {
        return this.posX < other.posX + other.width &&
               this.posX + this.width > other.posX &&
               this.posY < other.posY + other.height &&
               this.posY + this.height > other.posY;
    }
    
    public boolean colidesX(Entity other) {
        return this.posX < other.posX + other.width &&
               this.posX + this.width > other.posX &&
               this.posY < other.posY + other.height &&
               this.posY + this.height > other.posY;
    }
    public boolean colidesY(Character other) {
        return this.posX + this.width < other.posX + other.width &&
               this.posX + this.width > other.posX &&
               this.posY < other.posY + other.height &&
               this.posY + this.height > other.posY;
    }
}
