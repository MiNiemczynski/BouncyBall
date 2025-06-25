import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    public static final int SERVER_PORT = 2024;
    ServerSocket s;

    /* Constructor creates socket to clients connecting and acceptance */
    Server() {
        try {
            s = new ServerSocket(SERVER_PORT);
            System.out.println("> Server is running");
        } catch (IOException e) {
            System.out.println("> Socket creating failed");
            System.exit(1);
        }
    }

    void work() throws Exception {
        new ClientService().loadFromFile();
        while (true) {
            Socket socket = s.accept();
            ClientService cs = new ClientService(socket);
            cs.start();
        }
    }

    public class ClientData {
        private String nick;
        private GamePanel game;

        private boolean active;
        private boolean saved;

        ClientData(String nick) {
            this.nick = nick;
            this.saved = false;
        }
        ClientData(ClientData old) {
            this.nick = old.getNick();
            this.game = old.getGame();
            this.saved = true;
        }

        public void setSave(GamePanel game) {
            this.game = game;
            this.saved = true;
        }
        
        public void switchActive() {
            this.active = !active;
        }

        public String getNick() {
            return nick;
        }

        public GamePanel getGame() {
            return game;
        }

        public boolean isSaved() {
            return saved;
        }
        public boolean isActive() {
            return active;
        }

    }

    public class ClientService extends Thread {
        private String nickname;
        private Socket socket;
        static ArrayList<ClientData> clients = new ArrayList();
        private ObjectOutputStream objectOut;
        private ObjectInputStream objectIn;

        ClientService() {

        }

        ClientService(Socket s) {
            this.socket = s;
        }

        @Override
        public void run() {
            try {
                objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectIn = new ObjectInputStream(socket.getInputStream());

                Message loginRequest = (Message) objectIn.readObject();
                this.nickname = loginRequest.nickname;
                System.out.println("> " + nickname + " logs in");

                ClientData client = null;
                for (ClientData c : clients) {
                    if (c.getNick().equals(nickname)) {
                        if(c.isActive()) {
                            System.out.println(" >> Client is occupied - disconnected");
                            throw new IOException();
                        }
                        client = new ClientData(c);
                        clients.add(client);
                        clients.remove(c);
                        System.out.println(" >> We have seen him before..");
                        break;
                    }
                }

                if (client == null) {
                    client = new ClientData(nickname);
                    clients.add(client);
                }
                
                client.switchActive();
                while (true) {
                    Message request = (Message) objectIn.readObject();
                    if (request == null) continue;

                    switch (request.command) {
                        case SAVE:
                            saveGame(client, request.game);
                            break;
                        case LOAD:
                            loadGame(client);
                            break;
                        case QUIT:
                            System.out.println("> " + nickname + " quitted game");
                            client.switchActive();
                            return;
                    }
                }
            } catch (Exception e) {
                System.out.println("> " + nickname + " disconnected");
                try {
                    objectIn.close();
                    objectOut.close();
                    socket.close();
                } catch (IOException ex) {
                    System.out.println("Closing error: " + ex);
                }
            }
        }

        public void saveGame(ClientData client, GamePanel g) {
            client.setSave(g);
            saveToFile(g, client.getNick());
        }

        public void saveToFile(GamePanel game, String nick) {
            File f = new File("saves");
            if (!f.exists()) {
                f.mkdir();
            }
            String file = f + "/" + nick + ".dat";
            try {
                ObjectOutputStream objectOutFile = new ObjectOutputStream(new FileOutputStream(file));
                objectOutFile.writeObject(game);
            } catch (IOException ex) {
                System.out.println("Error during saving game to file! <Server>: " + ex);
            }
        }

        public void loadGame(ClientData client) {
            try {
                objectOut.writeObject(client.getGame());
                objectOut.flush();
            } catch (IOException ex) {
                System.out.println("Error during game loading! <Server>: " + ex);
            }
        }

        public void loadFromFile() {
            File folder = new File("saves");
            File[] saves = folder.listFiles();

            for (File user : saves) {
                System.out.println("Reading from " + user + ".dat");
                String nick = user.getName().replace(".dat", "");
                try {
                    ObjectInputStream objectInFile = new ObjectInputStream(new FileInputStream(user));
                    GamePanel g = (GamePanel) objectInFile.readObject();
                    ClientData loaded = new ClientData(nick);
                    loaded.setSave(g);
                    clients.add(loaded);
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println("Error during loading games from file! <Server>: " + ex);
                }
            }
        }
    }

    public static void main(String args[]) throws Exception {
        Server server = new Server();
        server.work();

        server.s.close();
    }
}
