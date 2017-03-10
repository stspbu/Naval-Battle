package nbattle;


import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;

import static nbattle.JsonUtils.parseMatrices;
import static nbattle.Main.sNetId;
import static nbattle.MainController.MAIN_URL;
import static nbattle.MainController.createStage;
import static nbattle.GameLogic.*;

public class Waiting implements Runnable {
    private final int DELAY = 750;
    public boolean isConnected = false;

    Waiting() {
        super();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (!isConnected) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String resultJsonWait = JsonUtils.parseUrl(MAIN_URL + "status.php", "&id=" + sNetId);
            ArrayList<String> list = JsonUtils.parseStatusJson(resultJsonWait);
            System.out.println("Current List = " + list);
            if (list != null) {
                connection(list, resultJsonWait);
                isConnected = true;
            }
        }
    }

    public void connection(ArrayList<String> list, String resultJsonWait) {
        Platform.runLater(() -> {
            try {
                createStage();

                coordinatesFriend = parseMatrices(resultJsonWait, 1);
                coordinatesEnemy = parseMatrices(resultJsonWait, 2);

                int k = 4;
                for (int i = 0; i < 10; i++) {
                    if (i == 1)
                        k--;
                    else if (i == 3)
                        k--;
                    else if (i == 6)
                        k--;
                    setShipNet(k, coordinatesFriend[i][2][0] == 1 ? true : false, coordinatesFriend[i][0][0], coordinatesFriend[i][1][0], true);
                    setShipNet(k, coordinatesEnemy[i][2][0] == 1 ? true : false, coordinatesEnemy[i][0][0], coordinatesEnemy[i][1][0], false);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
