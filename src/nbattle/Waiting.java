package nbattle;


import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;

import static nbattle.JsonUtils.parseMatrices;
import static nbattle.Main.sNetId;
import static nbattle.MainController.MAIN_URL;
import static nbattle.MainController.createStage;

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
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        parseMatrices(resultJsonWait, 1);
    }
}
