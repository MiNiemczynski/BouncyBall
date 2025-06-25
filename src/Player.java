import java.awt.*;

/**
 *
 * @author Valkyrien
 */
public class Player extends Character{
    public Player(GamePanel gp) {
        super(gp);
        setRight();
        movingRight = false;
        movingLeft = false;
    }

    public Player(GamePanel gp, int x, int y) {
        this(gp);
        posX = x;
        posY = y;
    }

    @Override
    public void moveRight() {
        movX += 1;
        isRight = true;
    }

    @Override
    public void moveLeft() {
        movX += -1;
        isRight = false;
    }

    public void jump() {
        if (checkOnSurface()) {
            movY = -15;
            if(!game.getRGB())
                game.swapRGB();
        }
    }

    @Override
    public void draw(Graphics g) {
        int w = 50;
        int h = 50;
        this.width = w;
        this.height = h;
        g.fillOval(posX, posY - game.getCamera(), w, h);
    }

    @Override
    public void deathCheck() {
        if(posY - game.getCamera() > game.getHeight() + 10) {
            alive = false;
            int score = (int)(game.getCamera() * -0.5)/10;
            game.reset(score);
            System.out.println("Game over!");
            System.out.println("Your score is: " + score);
        }
    }
}
