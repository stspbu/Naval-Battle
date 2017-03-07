package nbattle;

import javafx.scene.control.Button;

/**
 * Created by w7uu on 08.03.2017.
 */
public class TableCell extends Button {
    public String id, game;

    TableCell(String id, String game){
        this.id = id;
        this.game = game;
    }
}
