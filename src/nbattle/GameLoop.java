package nbattle;

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
                System.out.println("Error 01: " + e);
            }

            // Инструкции ходов для бота.
            if (!step && !gameOver) {
                if (bot.getIsTargeted()) {
                    point = bot.nextDamage(fieldFriend);
                    checkField(point.x, point.y, step, fieldFriend);
                } else {
                    point = bot.getCoord(fieldFriend);
                    checkField(point.x, point.y, step, fieldFriend);
                }
            }
        }
    }
}
