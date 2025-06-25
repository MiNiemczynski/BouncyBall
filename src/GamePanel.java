import java.awt.*;
import java.io.*;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public final class GamePanel extends JPanel implements Serializable, Runnable {
    private Player player;
    private ArrayList<Tile> tiles;

    boolean paused;
    private int camera;
    private Timer timer;
    private int start;
    private int stop;
    private long startTime;
    private static int ANIMATION_DURATION = 400;

    private Color sky;
    private Color ent;
    private int[] RGB;
    int currentColorValue;
    private boolean increasing;
    private boolean isRGB;

    public GamePanel() {
        super();
        setPreferredSize(new Dimension(800, 800));

        sky = new Color(255, 255, 255);
        ent = new Color(0, 0, 0);
        RGB = new int[]{61, 255, 61};
        increasing = true;
        currentColorValue = 0;
        
        setVisible(true);
    }
    
    public GamePanel(GamePanel old) {
        this();
        this.tiles = new ArrayList<>();
        for (Tile tile : old.getTiles()) {
            tiles.add(new Tile(this, tile.getPosX(), tile.getPosY()));
        }
        this.camera = old.getCamera();
        this.player = new Player(this, old.getPlayer().getPosX(), old.getPlayer().posY);

        paused = false;
        
        startAnimation();
    }

    public GamePanel(int tileNumber) {
        this();
        tiles = new ArrayList<>();

        camera = 0;
        player = new Player(this, 50, 700);

        tiles.add(new Tile(this, 0, 750, 800, 50));

        MapBuilder randomMap = new MapBuilder(this, tileNumber);
        randomMap.start();

        startAnimation();
    }

    public int getCamera() {
        return camera;
    }
    public Player getPlayer() {
        return player;
    }

    public void setCamera(int newCamera) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        start = camera;
        stop = newCamera - 700;
        startTime = System.currentTimeMillis();

        timer = new Timer(1000 / 60, e -> animateCamera());
        timer.start();
    }

    private void animateCamera() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        float progress = Math.min(1, (float) elapsedTime / ANIMATION_DURATION);
        float interpolatedProgress = (float) ((-Math.cos(progress * Math.PI) / 2) + 0.5);

        camera = (int) (start + interpolatedProgress * (stop - start));

        if (progress >= 1) {
            timer.stop();
        }

        this.repaint();
    }
    
    public void switchColorMode() {
        if(isRGB) {
            isRGB = false;
            sky = new Color(255, 255, 255);
            ent = new Color(0, 0, 0);
        } else {
            isRGB = true;
            ent = new Color(0, 0, 0);
        }
    }
    
    boolean getRGB() {
        return isRGB;
    }

    public void swapRGB() {
        Color tmp = sky;
        sky = ent;
        ent = tmp;
    }

    public void updateRGB() {
        if(RGB[0] == RGB[1] && currentColorValue == 0) {
            currentColorValue = 1;
            increasing = !increasing;
        } else if(RGB[1] == RGB[2] && currentColorValue == 1) {
            currentColorValue = 2;
            increasing = !increasing;
        } else if(RGB[2] == RGB[0] && currentColorValue == 2) {
            currentColorValue = 0;
            increasing = !increasing;
        }

        if (increasing) {
            RGB[currentColorValue]++;
            if(RGB[currentColorValue] == 255) {
                increasing = false;
                currentColorValue = (currentColorValue + 1) % 3;
            }
        } else {
            RGB[currentColorValue]--;
            if(RGB[currentColorValue] == 61) {
                increasing = true;
                currentColorValue = (currentColorValue + 1) % 3;
            }
        }
        sky = new Color(RGB[0], RGB[1], RGB[2]);
    }
    
    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void startAnimation() {
        Thread game = new Thread(this);
        game.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());

        if(isRGB)
            updateRGB();
        g.setColor(sky);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(ent);
        
        player.draw(g);
        for (Tile tile : tiles) {
            tile.draw(g);
        }
        g.setColor(new Color(50, 0, 50));
    }
    
    public void reset(int score) {
        JOptionPane.showMessageDialog(this, "You died with score: " + score);
        System.exit(0);
    }
    
    @Override
    public void run() {
        while (player.alive) {
            try {
                if(paused)
                    continue;
                sleep(14);
                player.grav();
                player.jump();
                if (player.movingLeft && player.movX > -8 && player.movX <= 0)
                    player.moveLeft();
                else if (player.movingRight && player.movX < 8 && player.movX >= 0)
                    player.moveRight();
                player.updatePosition();
                repaint();
            } catch (InterruptedException ex) {
                currentThread().interrupt();
                System.out.println("Dead");
            }
        }
    }
}
