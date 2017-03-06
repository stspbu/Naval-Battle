package nbattle;

import javafx.scene.control.Button;

public class Cell extends Button {
    enum BodyType {
        Body, Head, BackSide;
    }

    BodyType bodyType;
    int x, y;
    int id;
    int type;
    boolean mine;
}

