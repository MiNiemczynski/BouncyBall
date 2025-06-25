
import java.util.*;


/**
 *
 * @author Valkyrien
 */
public class MapBuilder extends Thread {
    private GamePanel game;
    private Random random = new Random();
    private int tileCount;
    private int tileNumber;
    
    public MapBuilder(GamePanel gp, int n) {
        this.game = gp;
        this.tileNumber = n;
        tileCount = 0;
    }
    
    public void generateTile() {
        int offsetY = 800 - (tileCount * 200 + random.nextInt(5) * 10);
        
        int offsetX = 300;
        int lastTilePosX = game.getTiles().get(tileCount).posX;
        
        if(tileCount > 0)
        do {
            offsetX = random.nextInt(6) * 100 + 50;
            if(tileCount % 2 == 0)
                offsetX += 80;
            else
                offsetX -= 80;
        } while(lastTilePosX <= offsetX + 100 && lastTilePosX >= offsetX - 100);
        tileCount++;
        game.getTiles().add(new Tile(game, offsetX, offsetY - game.getCamera()));
    }
    
    public void run() {
        int i = 0;
        while(i < tileNumber) {
            i++;
            generateTile();
        }
    }
}
