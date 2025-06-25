import java.io.Serializable;

/**
 *
 * @author Valkyrien
 */
public class Message implements Serializable {
    public CommandEnum command;
    public String nickname;
    public GamePanel game;

    public Message(CommandEnum command, String nickname) {
        this.command = command;
        this.nickname = nickname;
    }

    public Message(CommandEnum command, String nickname, GamePanel game) {
        this.command = command;
        this.nickname = nickname;
        this.game = game;
    }
}
