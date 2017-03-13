package nbattle;

import javafx.application.Platform;
import java.awt.*;

import static nbattle.GameLogic.checkField;
import static nbattle.GameLogic.step;
import static nbattle.Main.*;
import static nbattle.MainController.*;


public class Processing implements Runnable {
    Processing() {
        super();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        String resultJson = JsonUtils.parseUrl(MAIN_URL + "mover.php", "&id=" + sNetId);
        step = JsonUtils.parseMoverJson(resultJson) == isHost;
        Point point;
        Point last = new Point(-1, -1);
        while (isOnline) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String resultCoordJson = JsonUtils.parseUrl(MAIN_URL + "coord.php", "&id=" + sNetId);
            point = JsonUtils.parseCoordJson(resultCoordJson);
            if (point != last && !step && point != null) {
                last = point;
                final Point p = point;
                Platform.runLater(() -> {
                    checkField(p.x, p.y, step, fieldFriend);
                });
            }
        }
    }
}