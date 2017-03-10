package nbattle;

import javafx.scene.control.Button;


public class TableCell extends Button {
    public String id, game;

    TableCell(String id, String game) {
        this.id = id;
        this.game = game;
    }
}
