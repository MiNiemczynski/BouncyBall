import java.awt.Graphics;

/**
 *
 * @author Valkyrien
 */
public class Tile extends Entity {
    private boolean checkpoint;
    
    public Tile(GamePanel gp) {
        super(gp);
        alive = true;
        this.checkpoint = false;
    }

    public Tile(GamePanel gp, int x, int y) {
        this(gp);
        posX = x;
        posY = y;
        width = 250;
        height = 50;
    }

    public Tile(GamePanel gp, int x, int y, int width, int height) {
        this(gp);
        posX = x;
        posY = y;
        this.width = width;
        this.height = height;
    }

    public Tile(GamePanel gp, int x, int y, int width) {
        this(gp, x, y, width, 50);
    }

    @Override
    public void draw(Graphics g) {
        g.fillRect(posX, posY - game.getCamera(), width, height);
    }

    public void checkIfActive() {
        if (posY - game.getCamera() > game.getHeight()-500) {
            alive = false;
        }
    }
}
