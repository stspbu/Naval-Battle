package nbattle;

import java.util.*;

import static nbattle.MainController.*;


public class GameLogic {
    private static int idFriend = 0, idEnemy = 0;
    public static boolean step = true, gameOver = false;
    public static int countDeathFriend = 0, countDeathEnemy = 0;
    public static int[][][] coordinatesEnemy = new int[10][3][1];
    public static int[][][] coordinatesFriend = new int[10][3][1];

    // Постановка корабля на поле.
    private static void setShip(int shipSize, boolean horizontal, int xPos, int yPos, boolean isLeftSide) {
        if (isLeftSide) {
            coordinatesFriend[idFriend][0][0] = xPos;
            coordinatesFriend[idFriend][1][0] = yPos;
            coordinatesFriend[idFriend][2][0] = (horizontal) ? 1 : 0;
            idFriend++;
            if (idFriend > 9)
                idFriend = 0;
        } else {
            coordinatesEnemy[idEnemy][0][0] = xPos;
            coordinatesEnemy[idEnemy][1][0] = yPos;
            coordinatesEnemy[idEnemy][2][0] = (horizontal) ? 1 : 0;
            idEnemy++;
            if (idEnemy > 9)
                idEnemy = 0;
        }

        if (horizontal) {
            for (int i = 0; i < shipSize; i++) {
                if (isLeftSide) {
                    setCellShip(fieldFriend, xPos, yPos + i, i, shipSize, idFriend, horizontal);
                } else {
                    setCellShip(fieldEnemy, xPos, yPos + i, i, shipSize, idEnemy, horizontal);
                }
            }
        } else {
            for (int i = 0; i < shipSize; i++) {
                if (isLeftSide) {
                    setCellShip(fieldFriend, xPos + i, yPos, i, shipSize, idFriend, horizontal);
                } else {
                    setCellShip(fieldEnemy, xPos + i, yPos, i, shipSize, idEnemy, horizontal);
                }
            }
        }
    }

    // Постановка блока корабля на поле.
    private static void setCellShip(ArrayList<Cell> field, int xPos, int yPos, int i, int shipSize, int idShip, boolean horizontal) {
        getCell(field, xPos, yPos).type = 1;
        getCell(field, xPos, yPos).id = idShip;
        if (field.equals(fieldFriend)) {
            //getCell(field, xPos, yPos).getStyleClass().add("cell-ship");
            if (shipSize == 1) {
                getCell(field, xPos, yPos).bodyType = Cell.BodyType.Body;
                if (horizontal)
                    getCell(field, xPos, yPos).getStyleClass().add("cell-body-horizontal");
                else
                    getCell(field, xPos, yPos).getStyleClass().add("cell-body-vertical");
            } else {
                if (i == 0) {
                    getCell(field, xPos, yPos).bodyType = Cell.BodyType.Head;
                    if (horizontal)
                        getCell(field, xPos, yPos).getStyleClass().add("cell-head-horizontal");
                    else
                        getCell(field, xPos, yPos).getStyleClass().add("cell-head-vertical");
                } else if (i == shipSize - 1) {
                    getCell(field, xPos, yPos).bodyType = Cell.BodyType.BackSide;
                    if (horizontal)
                        getCell(field, xPos, yPos).getStyleClass().add("cell-backside-horizontal");
                    else
                        getCell(field, xPos, yPos).getStyleClass().add("cell-backside-vertical");
                } else {
                    getCell(field, xPos, yPos).bodyType = Cell.BodyType.Body;
                    if (horizontal)
                        getCell(field, xPos, yPos).getStyleClass().add("cell-body-horizontal");
                    else
                        getCell(field, xPos, yPos).getStyleClass().add("cell-body-vertical");
                }
            }
        }
    }

    // Проверка на корректность растановки кораблей.
    private static boolean isCorrect(int sizeShip, boolean vertical, int xPos, int yPos, boolean isLeft) {
        if (vertical) {
            for (int i = yPos - 1; i < yPos + sizeShip + 1; i++) {
                for (int j = xPos - 1; j <= xPos + 1; j++) {
                    if ((i >= -1 && i < 11 && j >= -1 && j < 11)) {
                        if ((i >= 0 && i < 10 && j >= 0 && j < 10 && !((isLeft && getCell(fieldFriend, j, i).type == 0) || (!isLeft && getCell(fieldEnemy, j, i).type == 0))))
                            return false;
                    } else
                        return false;
                }
            }
            return true;
        } else {
            for (int i = xPos - 1; i < xPos + sizeShip + 1; i++) {
                for (int j = yPos - 1; j <= yPos + 1; j++) {
                    if ((i >= -1 && i < 11 && j >= -1 && j < 11)) {
                        if ((i >= 0 && i < 10 && j >= 0 && j < 10 && !((isLeft && getCell(fieldFriend, i, j).type == 0) || (!isLeft && getCell(fieldEnemy, i, j).type == 0))))
                            return false;
                    } else
                        return false;
                }
            }
            return true;
        }
    }

    // После вызова этой функции текущие данные будут потерены.
    public static void randomPlacer(boolean isLeft) {
        Random rnd = new Random();
        if (isLeft) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++)
                    getCell(fieldFriend, i, j).type = 0;
            }
            for (int i = 4; i >= 1; i--) {
                for (int j = 1; j <= 5 - i; j++) {
                    int x, y;
                    if (rnd.nextBoolean()) {
                        do {
                            y = Math.abs(rnd.nextInt() % (10 - i));
                            x = Math.abs(rnd.nextInt() % 10);
                        } while (!isCorrect(i, true, y, x, true));
                        setShip(i, true, y, x, true);
                    } else {
                        do {
                            x = Math.abs(rnd.nextInt() % (10 - i));
                            y = Math.abs(rnd.nextInt() % 10);
                        } while (!isCorrect(i, false, y, x, true));
                        setShip(i, false, y, x, true);
                    }
                }
            }
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++)
                    getCell(fieldEnemy, i, j).type = 0;
            }
            for (int i = 4; i >= 1; i--) {
                for (int j = 1; j <= 5 - i; j++) {
                    int x, y;
                    if (rnd.nextBoolean()) {
                        do {
                            y = Math.abs(rnd.nextInt() % (10 - i));
                            x = Math.abs(rnd.nextInt() % 10);
                        } while (!isCorrect(i, true, y, x, false));
                        setShip(i, true, y, x, false);
                    } else {
                        do {
                            x = Math.abs(rnd.nextInt() % (10 - i));
                            y = Math.abs(rnd.nextInt() % 10);
                        } while (!isCorrect(i, false, y, x, false));
                        setShip(i, false, y, x, false);
                    }
                }
            }
        }
    }

    // Метод определения состояния корабля (на плаву/затонул).
    public static boolean isDead(ArrayList<Cell> field, int idCell) {
        for (Cell cell : field)
            if (cell.id == idCell && cell.type == 1)
                return false;
        return true;
    }

    // Метод обработки выстрелов по игровым полям.
    public static void checkField(int dx, int dy, boolean nextStep, ArrayList<Cell> field) { // Проверка на попадание по кораблю.
        int countDeath = 0, countDeathNew = 0;
        Cell cellN = getCell(field, dx, dy);
        if (isDead(field, getCell(field, dx, dy).id))
            countDeath++;
        if ((getCell(field, dx, dy).type != 1) && (getCell(field, dx, dy).type != 2) && (getCell(field, dx, dy).type != 3) && (getCell(field, dx, dy).type != 4)) {
            step = !nextStep;

            if (!step) {
                for (Cell cell : fieldEnemy) {
                    cell.getStyleClass().add("no-clickable");
                }
            } else {
                for (Cell cell : fieldEnemy) {
                    cell.getStyleClass().remove("no-clickable");
                }
            }
        }

        if (getCell(field, dx, dy).type == 0) {
            getCell(field, dx, dy).type = 3;
            getCell(field, dx, dy).getStyleClass().add("cell-empty");
        }
        if (getCell(field, dx, dy).type == 1) {
            getCell(field, dx, dy).type = 2;
            getCell(field, dx, dy).getStyleClass().add("cell-damaged");
            if (isDead(field, getCell(field, dx, dy).id))
                countDeathNew++;
            if (countDeathNew > countDeath) {
                if (nextStep)
                    countDeathEnemy++;
                else
                    countDeathFriend++;
                for (Cell cell : field) {
                    if (cell.id == cellN.id) {
                        getCell(field, cell.x, cell.y).type = 4;
                        getCell(field, cell.x, cell.y).getStyleClass().add("cell-killed");
                        int dy1 = cell.y - 1, dy2 = cell.y + 1;
                        int dx1 = cell.x - 1, dx2 = cell.x + 1;
                        if (dy1 >= 0)
                            if ((getCell(field, cell.x, dy1).type != 1) && (getCell(field, cell.x, dy1).type != 2) && (getCell(field, cell.x, dy1).type != 4)) {
                                getCell(field, cell.x, dy1).type = 3;
                                getCell(field, cell.x, dy1).getStyleClass().add("cell-empty");
                            }
                        if (dx1 >= 0)
                            if ((getCell(field, dx1, cell.y).type != 1) && (getCell(field, dx1, cell.y).type != 2) && (getCell(field, dx1, cell.y).type != 4)) {
                                getCell(field, dx1, cell.y).type = 3;
                                getCell(field, dx1, cell.y).getStyleClass().add("cell-empty");
                            }
                        if ((dy1 >= 0) && (dx1 >= 0))
                            if ((getCell(field, dx1, dy1).type != 1) && (getCell(field, dx1, dy1).type != 2) && (getCell(field, dx1, dy1).type != 4)) {
                                getCell(field, dx1, dy1).type = 3;
                                getCell(field, dx1, dy1).getStyleClass().add("cell-empty");
                            }
                        if (dy2 <= 9)
                            if ((getCell(field, cell.x, dy2).type != 1) && (getCell(field, cell.x, dy2).type != 2) && (getCell(field, cell.x, dy2).type != 4)) {
                                getCell(field, cell.x, dy2).type = 3;
                                getCell(field, cell.x, dy2).getStyleClass().add("cell-empty");
                            }
                        if (dx2 <= 9)
                            if ((getCell(field, dx2, cell.y).type != 1) && (getCell(field, dx2, cell.y).type != 2) && (getCell(field, dx2, cell.y).type != 4)) {
                                getCell(field, dx2, cell.y).type = 3;
                                getCell(field, dx2, cell.y).getStyleClass().add("cell-empty");
                            }
                        if ((dy2 <= 9) && (dx2 <= 9))
                            if ((getCell(field, dx2, dy2).type != 1) && (getCell(field, dx2, dy2).type != 2) && (getCell(field, dx2, dy2).type != 4)) {
                                getCell(field, dx2, dy2).type = 3;
                                getCell(field, dx2, dy2).getStyleClass().add("cell-empty");
                            }
                        if ((dy1 >= 0) && (dx2 <= 9))
                            if ((getCell(field, dx2, dy1).type != 1) && (getCell(field, dx2, dy1).type != 2) && (getCell(field, dx2, dy1).type != 4)) {
                                getCell(field, dx2, dy1).type = 3;
                                getCell(field, dx2, dy1).getStyleClass().add("cell-empty");
                            }
                        if ((dy2 <= 9) && (dx1 >= 0))
                            if ((getCell(field, dx1, dy2).type != 1) && (getCell(field, dx1, dy2).type != 2) && (getCell(field, dx1, dy2).type != 4)) {
                                getCell(field, dx1, dy2).type = 3;
                                getCell(field, dx1, dy2).getStyleClass().add("cell-empty");
                            }
                    }
                }
                if (countDeathEnemy == 10 || countDeathFriend == 10)
                    gameOver = true;
            } else {
                int dy1 = dy - 1, dy2 = dy + 1;
                int dx1 = dx - 1, dx2 = dx + 1;
                if ((dy1 >= 0) && (dx1 >= 0))
                    if ((getCell(field, dx1, dy1).type != 1) && (getCell(field, dx1, dy1).type != 2) && (getCell(field, dx1, dy1).type != 4)) {
                        getCell(field, dx1, dy1).type = 3;
                        getCell(field, dx1, dy1).getStyleClass().add("cell-empty");
                    }
                if ((dy2 <= 9) && (dx2 <= 9))
                    if ((getCell(field, dx2, dy2).type != 1) && (getCell(field, dx2, dy2).type != 2) && (getCell(field, dx2, dy2).type != 4)) {
                        getCell(field, dx2, dy2).type = 3;
                        getCell(field, dx2, dy2).getStyleClass().add("cell-empty");
                    }
                if ((dy1 >= 0) && (dx2 <= 9))
                    if ((getCell(field, dx2, dy1).type != 1) && (getCell(field, dx2, dy1).type != 2) && (getCell(field, dx2, dy1).type != 4)) {
                        getCell(field, dx2, dy1).type = 3;
                        getCell(field, dx2, dy1).getStyleClass().add("cell-empty");
                    }
                if ((dy2 <= 9) && (dx1 >= 0))
                    if ((getCell(field, dx1, dy2).type != 1) && (getCell(field, dx1, dy2).type != 2) && (getCell(field, dx1, dy2).type != 4)) {
                        getCell(field, dx1, dy2).type = 3;
                        getCell(field, dx1, dy2).getStyleClass().add("cell-empty");
                    }
            }
        }
    }

    public static boolean isWin(ArrayList<Cell> field) {
        for (Cell cell : field)
            if (cell.type == 1)
                return false;
        return true;
    }
}