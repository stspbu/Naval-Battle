package nbattle;

import java.awt.*;
import java.util.*;

import static nbattle.MainController.getCell;
import static nbattle.GameLogic.isDead;

public class Bot {
    private int x = 0, y = 0, firstX = 0, firstY = 0, lastX = 0, lastY = 0;
    private Point point = new Point(0, 0);
    private boolean isTargeted = false, isHitted = false, up = true, right = true, left = true, down = true;
    private Random random = new Random();

    // Метод получения рандомной точки для атаки, исключает повторения точек.
    public Point getCoord(ArrayList<Cell> field) {
        int dx = random.nextInt(10);
        int dy = random.nextInt(10);
        isTargeted = false;
        isHitted = false;
        while ((getCell(field, dx, dy).type == 2) || (getCell(field, dx, dy).type == 3) || (getCell(field, dx, dy).type == 4)) {
            dx = random.nextInt(10);
            dy = random.nextInt(10);
        }

        if (getCell(field, dx, dy).type == 1) {
            x = dx;
            y = dy;
            firstX = dx;
            firstY = dy;
            lastX = dx;
            lastY = dy;
            isHitted = true;
            checkShipStatus(dx, dy, field);
            up = true;
            left = true;
            right = true;
            down = true;
        }
        point.x = dx;
        point.y = dy;
        return point;
    }

    // Метод для определения, нужно ли добивать подбитый корабль.
    public boolean getIsTargeted() {
        return isTargeted;
    }

    // Метод проверки состояния корабля (на плаву/затонул).
    private void checkShipStatus(int dx, int dy, ArrayList<Cell> field) {
        Cell cell = getCell(field, dx, dy);
        if (cell != null) {
            isTargeted = !isDead(field, cell.id);
        }
    }

    // Метод для добивания подбитых корабей.
    public Point nextDamage(ArrayList<Cell> field) {
        int dy1 = y - 1, dy2 = y + 1;       // Изменения координат по Y.
        int dx1 = x - 1, dx2 = x + 1;       // Изменения координат по X.
        int index = random.nextInt(4) + 1;

        boolean flag = true;
        checkShipStatus(firstX, firstY, field);     // Проверяем, не потонул ли уже корабль, если да, то
        if (!isTargeted) {      // вызываем обычный метод.
            return getCoord(field);
        }

        // Если нет, то вычисляем следующие возможные координаты для атаки.
        if (!isHitted) {
            if (!up && !left && !right && !down) {
                up = true;
                left = true;
                right = true;
                down = true;
                point.x = firstX;
                point.y = firstY;
                x = firstX;
                y = firstY;
                lastX = firstX;
                lastY = firstY;
                dy1 = y - 1;
                dy2 = y + 1;
                dx1 = x - 1;
                dx2 = x + 1;
            } else {
                point.x = lastX;
                point.y = lastY;
                x = lastX;
                y = lastY;
                dy1 = y - 1;
                dy2 = y + 1;
                dx1 = x - 1;
                dx2 = x + 1;
            }
            isHitted = true;
        }

        while (flag) {
            if (!up && !left && !right && !down) {
                up = true;
                left = true;
                right = true;
                down = true;
                point.x = firstX;
                point.y = firstY;
                x = firstX;
                y = firstY;
                lastX = firstX;
                lastY = firstY;
                dy1 = y - 1;
                dy2 = y + 1;
                dx1 = x - 1;
                dx2 = x + 1;
            }
            switch (index) {
                case 1:
                    if (dy1 >= 0 && up) {
                        point.x = x;
                        point.y = dy1;
                        if (getCell(field, x, dy1).type != 1) { //getCell(field, x, dy2).type
                            up = false;
                            isHitted = false;
                        } else {
                            up = true;
                            left = true;
                            right = true;
                            down = false;
                            isHitted = true;
                            lastX = x;
                            lastY = dy1;
                        }
                        flag = false;
                    } else {
                        up = false;
                    }
                    break;
                case 2:
                    if (dx1 >= 0 && left) {
                        point.x = dx1;
                        point.y = y;
                        if (getCell(field, dx1, y).type != 1) {
                            left = false;
                            isHitted = false;
                        } else {
                            up = true;
                            left = true;
                            right = false;
                            down = true;
                            isHitted = true;
                            lastX = dx1;
                            lastY = y;
                        }
                        flag = false;
                    } else {
                        left = false;
                    }
                    break;
                case 3:
                    if (dy2 <= 9 && down) {
                        point.x = x;
                        point.y = dy2;
                        if (getCell(field, x, dy2).type != 1) {
                            down = false;
                            isHitted = false;
                        } else {
                            up = false;
                            left = true;
                            right = true;
                            down = true;
                            isHitted = true;
                            lastX = x;
                            lastY = dy2;
                        }
                        flag = false;
                    } else {
                        down = false;
                    }
                    break;
                case 4:
                    if (dx2 <= 9 && right) {
                        point.x = dx2;
                        point.y = y;
                        if (getCell(field, dx2, y).type != 1) {
                            right = false;
                            isHitted = false;
                        } else {
                            up = true;
                            left = false;
                            right = true;
                            down = true;
                            isHitted = true;
                            lastX = dx2;
                            lastY = y;
                        }
                        flag = false;
                    } else {
                        right = false;
                    }
                    break;
            }
            index++;
            if (index > 4)
                index = 1;
            if (!isHitted) {
                flag = true;
                continue;
            }
        }

        x = point.x;
        y = point.y;
        return point;
    }
}
