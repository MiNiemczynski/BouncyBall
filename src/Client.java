import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 *
 * @author Valkyrien
 */
public final class Client extends JFrame implements KeyListener, WindowListener {
    private GamePanel game;
    String nickname;
    private final int HEIGHT = 800;
    private final int WIDTH = 800;

    private Socket socket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    public Client() {
        super("Bouncy Ball");
        setVisible(true);
        
        try {
            socket = new Socket("localhost", 2024);
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());
            System.out.println("> Client is running");
        } catch (IOException ex) {
            System.out.println("Error during creating client!");
        }
        init();
    }

    public void init() {
        setSize(800, 600);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setBounds(0, 0, WIDTH, HEIGHT);
        addKeyListener(this);

        boolean login = JOptionPane.showConfirmDialog(this, "Would you like to sign in?\nEnter your nickname to be able to save your game!", "Login", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        if(login)
            nickname = JOptionPane.showInputDialog(this, "Enter your nickname:");
        if (!(nickname == null || nickname.isBlank())) {
            System.out.println("Logging in");
            Message loginRequest = new Message(CommandEnum.LOGIN, nickname);
            try {
                objectOut.writeObject(loginRequest);
                objectOut.flush();
            } catch (IOException ex) {
                System.out.println("Error during logging in! <Client>: " + ex);
            }
            loadGame();
        } else {
            System.out.println("Playing as a guest");
            game = new GamePanel(1000);
        }

        this.addWindowListener(this);
        
        add(game);
        pack();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_M:
                game.switchColorMode();
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                game.getPlayer().movingLeft = true;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                game.getPlayer().movingRight = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                game.getPlayer().movingLeft = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                game.getPlayer().movingRight = false;
                break;
        }
    }

    public void saveGame() {
        try {
            objectOut.writeObject(new Message(CommandEnum.SAVE, nickname, game)); // save game request
            objectOut.flush();
            
            JOptionPane.showMessageDialog(this, "Game has been saved successfully!");
        } catch (IOException ex) {
            System.out.println("Error during game saving! <Client>: " + ex);
        }
    }

public void loadGame() {
    try {
        objectOut.writeObject(new Message(CommandEnum.LOAD, nickname)); // load game request
        objectOut.flush();

        GamePanel gp = (GamePanel) objectIn.readObject();
        if (gp != null) {
            boolean loadSave = JOptionPane.showConfirmDialog(this, "Save file detected, do you want to load it?", "Save file detected", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            game = loadSave ? new GamePanel(gp) : new GamePanel(1000);
        } else {
            System.out.println("No save to load! - creating default game");
            game = new GamePanel(1000);
        }
    } catch (Exception ex) {
        System.out.println("Error during game loading! <Client>: " + ex);
        nickname = null;
        game = new GamePanel(1000);
    }
}


    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        game.paused = true;
        if(nickname == null)
            System.exit(0);
        int operation = JOptionPane.showConfirmDialog(this, "You are about to exit game, do you want to save progress?", "Exiting game", JOptionPane.YES_NO_CANCEL_OPTION);
        switch (operation) {
            case JOptionPane.YES_OPTION:
                if(!game.getPlayer().alive) {
                    JOptionPane.showMessageDialog(this, "You are dead! Game won't be saved");
                } else {
                    saveGame();
                }
            case JOptionPane.NO_OPTION:
            {    
                try {
                    Message quit = new Message(CommandEnum.QUIT, nickname);
                    objectOut.writeObject(quit);
                    objectOut.flush();

                    objectIn.close();
                    objectOut.close();
                    socket.close();
                } catch (IOException ex) {
                    System.out.println("Closing error: " + ex);
                }
                System.exit(0);
                break;
            }
            case JOptionPane.CANCEL_OPTION:
                game.paused = false;
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    
    
    public static void main(String args[]) throws Exception {
        new Client();
    }
}
