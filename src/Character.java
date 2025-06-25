import java.awt.*;

/**
 *
 * @author Valkyrien
 */
public abstract class Character extends Entity {
    boolean movingRight, movingLeft;

    protected int movX;
    protected int movY;

    protected boolean isRight;

    int countForGravity = 0;
    int countForSlowingDown = 0;
    int countForSpeedingUp = 0;
    boolean speedingUp = false;

    public Character(GamePanel gp) {
        super(gp);
        alive = true;
    }

    public void setRight() {
        isRight = true;
    }

    public void setLeft() {
        isRight = false;
    }

    public abstract void moveRight();

    public abstract void moveLeft();

    @Override
    public abstract void draw(Graphics g);

    public void bounceX() {
        movX *= -0.5;
        if (movX > 0) {
            movX += 3;
        } else if (movX < 0) {
            movX += -3;
        }
        if (movY < 0 && movY > -15) {
            movY *= 1.5;
        }
        movingRight = false;
        movingLeft = false;
        if (posX < 0) {
            posX = 1;
        } else if (posX + width > game.getWidth()) {
            posX = game.getWidth() - 1 - width;
        }
    }

    public void bounceY() {
        if (checkNearSurface() && (movY > -2 && movY < 2)) {
            movY = 0;
        }

        if (posY < 0) {
            posY = 1;
        } else if (posY + height > game.getHeight()) {
            posY = game.getHeight() - 1 - height;
        }
    }

    public boolean checkNearSurface() {
        return posY + height >= game.getHeight() - 20;
    }

    public void updateCamera(Entity e) {
        if (e.posY - 600 < game.getCamera()) {
            game.setCamera(e.posY);
        }
    }

    public boolean checkOnSurface() {
        for (int i = 0; i < game.getTiles().size(); i++) {
            for (int j = movY; j > 0; j--) {
                if (this.touchesTop(game.getTiles().get(i))) {
                    if (this instanceof Player) {
                        updateCamera(game.getTiles().get(i));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkTouches() {
        for (int i = 0; i < game.getTiles().size(); i++) {
            for (int j = movY; j > 0; j--) {
                if (this.touches(game.getTiles().get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void movingBySteps() {
        // checking X
        if (movX > 0) { // check if player is going right
            for (int i = movX; i > 0; i--) { // calculating how much pixels they need to go right
                if ((posX + width) + 1 < game.getWidth()) {
                    posX += 1; // moving right by one pixel if player has space for it
                } else {
                    bounceX(); // bouncing if pixel is touching a wall
                    break;
                }
            }
        } else if (movX < 0) { // check if player is going left
            for (int i = movX; i < 0; i++) { // calculating how much pixels they need to go left
                if (posX - 1 > 0) {
                    posX -= 1; // moving left by one pixel if player has space for it
                } else {
                    bounceX(); // bouncing if pixel is touching a wall
                    break;
                }
            }
        }

        // checking Y
        if (movY > 0) {
            for (int i = movY; i > 0; i--) {
                if (!checkOnSurface()) {
                    posY += 1;
                } else {
                    break;
                }
            }
        } else if (movY < 0) {
            for (int i = movY; i < 0; i++) {
                posY -= 1;
            }
        }
    }

    public void slowDown() {
        if (!(movingRight && movingLeft && speedingUp)) {
            countForSlowingDown++;
            if (countForSlowingDown == 3) {
                movX -= 0.2 * movX;
                countForSlowingDown = 0;
            }
        }
    }

    public void speedUp() {
        if (movingRight || movingLeft) {
            countForSpeedingUp++;
            if (countForSpeedingUp == 5) {
                if (movX < 8 && movX > -8) {
                    movX *= 1.2;
                }
                countForSpeedingUp = 0;
            }
        }
    }

    public void grav() {
        if (!checkOnSurface()) {
            countForGravity++;
            if (countForGravity == 2) {
                movY += 1;
                countForGravity = 0;
            }
        }
    }

    public abstract void deathCheck();

    public void updatePosition() {
        movingBySteps();
        slowDown();
        speedUp();
        deathCheck();
    }
}
