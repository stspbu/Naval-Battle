package nbattle;

import javafx.application.Platform;

import java.awt.*;

import static nbattle.MainController.*;
import static nbattle.GameLogic.*;


public class GameLoop implements Runnable {
    private Bot bot = new Bot();

    GameLoop() {
        super();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        // Главный цикл игры.
        Point point;
        while (!gameOver && isRun) {
            try {
                Thread.sleep(300);       // Задерживает поток. Необходимо для того, чтобы было наглядней видно, куда кто стреляет.
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Инструкции ходов для бота.
            if (!step && !gameOver && isRun) {
                if (bot.getIsTargeted()) {
                    point = bot.nextDamage(fieldFriend);
                }else {
                    point = bot.getCoord(fieldFriend);
                }

                // scene is modified only from original thread
                // runLater should be where css.add styles are

                Point finalPoint = point;
                Platform.runLater(() -> {
                    checkField(finalPoint.x, finalPoint.y, step, fieldFriend);

                    if (isWin(fieldFriend))
                        for (Cell cell : fieldFriend) {
                            cell.getStyleClass().add("cell-damaged");
                        }
                });

            }
        }
    }
}
