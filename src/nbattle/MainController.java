package nbattle;

import com.sun.istack.internal.Nullable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static nbattle.JsonUtils.createDuringConnecting;
import static nbattle.Main.*;
import static nbattle.GameLogic.*;


public class MainController {
    private static final int MAX_LENGTH = 32; // for online nickname

    public static final int DELAY = 350;
    private static final int MAX_CELLS = 10;
    public static final String APP_TITLE = "Naval Battle";
    public static final String MAIN_URL = "http://f0123592.xsph.ru/backend/";

    public static ArrayList<Cell> fieldFriend = new ArrayList<>(), fieldEnemy = new ArrayList<>();
    public static boolean isRun = false;
    public static Scene lastScene;

    @FXML
    private Button mainStart, mainNet, btnQuit, btnMain,
            netCreate, netFind;

    private static GridPane gameGrid;
    private static Label friendNick, enemyNick;

    @FXML
    private void buttonListener(ActionEvent e) throws IOException {
        if (e.getSource() == btnQuit) {
            //Stage stage = (Stage) btnQuit.getScene().getWindow();
            //stage.close();
            System.exit(0);
            // Platform.exit();
            return; // redundant ??
        }

        Stage stage;
        Parent root;

        if (e.getSource() == mainStart) {
            isOnline = false;
            //isHost = true;
            resetGlobals();

            stage = (Stage) lastScene.getWindow();
            root = FXMLLoader.load(getClass().getResource("game.fxml"));

            String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", ""};
            gameGrid = (GridPane) root.lookup("#gameGrid");

            // ~ looking up for labels ~
            friendNick = (Label) root.lookup("#friendNick");
            enemyNick = (Label) root.lookup("#enemyNick");

            // ~ change the text ~
            friendNick.setText("Player's fleet");
            enemyNick.setText("Turing machine");

            String btnText;

            for (int i = 0; i < MAX_CELLS * 2 + 2 + 1; i++) {
                gameGrid.addColumn(i);

                for (int j = 0; j < MAX_CELLS + 1; j++) {
                    btnText = "";
                    Cell btn = new Cell();

                    if (j == 0 && i > 0 && i != MAX_CELLS + 2 - 1 && i != MAX_CELLS * 2 + 2 + 1 - 1) {
                        btnText = alphabet[(i - 1) % 11];
                        btn.getStyleClass().add("g_info");
                    } else if (j > 0 && (i == 0 || i == MAX_CELLS * 2 + 2 + 1 - 1)) {
                        btnText = Integer.toString(j - 1);
                        btn.getStyleClass().add("g_info");
                    } else if (j == 0 && i == 0
                            || j == 0 && i == MAX_CELLS * 2 + 2 + 1 - 1
                            || i == MAX_CELLS + 2 - 1) {
                        btn.getStyleClass().add("g_separator");
                    } else {
                        btn.getStyleClass().add("g_ship");

                        if (i <= MAX_CELLS) {
                            btn.setDisable(true);

                            btn.mine = true;
                            btn.x = j - 1;
                            btn.y = i - 1;

                            fieldFriend.add(btn);
                        } else {
                            btn.mine = false;
                            btn.x = j - 1;
                            btn.y = i - 1 - 11;

                            fieldEnemy.add(btn);
                        }

                        btn.setOnAction(event -> {
                            Cell cthis = (Cell) event.getSource();
                            if (cthis.mine) {
                                return;
                            }

                            handleClick(cthis);
                        });
                    }

                    btn.setText(btnText);
                    gameGrid.add(btn, i, j);
                }
            }
            randomPlacer(true);
            randomPlacer(false);
            new GameLoop();
        } else if (e.getSource() == mainNet) {
            stage = (Stage) lastScene.getWindow();
            root = FXMLLoader.load(getClass().getResource("network.fxml"));

            TextField local_netNick = (TextField) root.lookup("#netNick");

            if(!sNetId.isEmpty()){
                // trying to disable the game
                String resultJson = JsonUtils.parseUrl(MAIN_URL + "inactive.php", "&id=" + sNetId);
                System.out.println(resultJson);

                // JsonUtils.parseNoParamJson(resultJson);
                sNetId = "";
            }

            if (!sNetNick.isEmpty()) {
                local_netNick.setText(sNetNick);
            }

            local_netNick.textProperty().addListener((observable, oldData, newData) -> {
                newData = newData.trim();
                if (newData.length() > MAX_LENGTH)
                    newData = newData.substring(0, MAX_LENGTH);

                sNetNick = newData;
                local_netNick.setText(newData);
            });
        } else if (e.getSource() == netCreate) {
            if (sNetNick.isEmpty()) {
                alertShow("Incorrect nickname!", "You should enter your nickname before creating a new game.", Alert.AlertType.ERROR);
                return;
            }

            String resultJson = JsonUtils.parseUrl(MAIN_URL + "create.php", "&host=" + sNetNick);
            sNetId = JsonUtils.parseCreateJson(resultJson);

            if (sNetId != null) {
                stage = (Stage) lastScene.getWindow();
                root = FXMLLoader.load(getClass().getResource("create.fxml"));

                    /* <--> id & snick are important <--> */
                Label info = (Label) (root.lookup("#infoFlow")).lookup("#netInfo");
                info.setText("Game id: #" + sNetId + ", name: " + sNetNick);

                //isHost = true;
                Waiting waiting = new Waiting();
            } else {
                alertShow("Oops! Something's wrong!", "Nickname's in use or connection problems occurred.", Alert.AlertType.ERROR);
                return;
            }
        } else if (e.getSource() == netFind) {
            if (sNetNick.isEmpty()) {
                alertShow("Incorrect nickname!", "You should enter your nickname before finding games.", Alert.AlertType.ERROR);
                return;
            }

            String resultJson = JsonUtils.parseUrl(MAIN_URL + "list.php", "");
            JSONArray gamesArray = JsonUtils.parseListJson(resultJson);

            if (gamesArray != null && gamesArray.size() > 0) {
                stage = (Stage) lastScene.getWindow();
                root = FXMLLoader.load(getClass().getResource("find.fxml"));

                ScrollPane scroll = (ScrollPane) root.lookup("#tableScroll");
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                FlowPane table = new FlowPane();
                table.getStyleClass().add("t_container");

                for (Object aGamesArray : gamesArray) {
                    JSONObject gamesData = (JSONObject) aGamesArray;
                    addRowInto(table, gamesData.get("id").toString(), gamesData.get("host").toString());
                }

                scroll.setContent(table);
            } else {
                alertShow("Oops! Something's wrong!", "There are no active games at the moment.", Alert.AlertType.WARNING);
                return;
            }
        } else if (e.getSource() == btnMain) {
            stage = (Stage) lastScene.getWindow();
            root = FXMLLoader.load(getClass().getResource("main.fxml"));

            isOnline = false;
            gameOver = true;

            if(!sNetId.isEmpty()){
                // trying to disable the game
                String resultJson = JsonUtils.parseUrl(MAIN_URL + "inactive.php", "&id=" + sNetId);
                System.out.println(resultJson);

                // JsonUtils.parseInactiveJson(resultJson);
                sNetId = "";
            }
        } else {
            return;
        }

        // Create a new scene with root and set the stage.
        lastScene = new Scene(root);
        stage.setScene(lastScene);
        stage.show();
    }

    private static void handleClick(Cell cell) {
        System.out.println("Clicked: " + cell.x + ":" + cell.y);
        if (step && !gameOver) {
            checkField(cell.x, cell.y, step, fieldEnemy);

            if (isOnline) {
                JsonUtils.parseUrl(MAIN_URL + "update.php", "&id=" + sNetId +
                        "&coord=" + cell.x + "," + cell.y + "&who=" + (isHost ? "1" : "0"));
                // true of false: JsonUtils.parseListJson(resultJson);
            }

            if (isWin(fieldEnemy))
                for (Cell cellX : fieldEnemy) {
                    cellX.getStyleClass().add("cell-damaged");
                }
        }
    }

    @Nullable
    public static Cell getCell(ArrayList<Cell> field, int x, int y) {
        /* sometimes really produce null pointer exception */

        for (Cell cell : field)
            if (cell.x == x && cell.y == y)
                return cell;

        return null;
    }

    @Nullable
    public static ArrayList<Cell> getCellArray(ArrayList<Cell> field, int x, int y) {
        ArrayList<Cell> cells = new ArrayList<>();
        Cell cellN = getCell(field, x, y);
        for (Cell cell : field)
            if (cell.id == cellN.id)
                cells.add(cell);
        return cells;
    }

    private void addRowInto(FlowPane table, String sid, String sgame) {
        HBox row = new HBox();
        row.getStyleClass().add("t_row");

        Label id = new Label("#" + sid), game = new Label(sgame);
        id.getStyleClass().add("t_id");
        game.getStyleClass().add("t_game");

        TableCell btn = new TableCell(sid, sgame);
        btn.getStyleClass().add("t_btn");

        btn.setOnAction(event -> {
            TableCell tc = (TableCell) event.getSource();
            sNetId = tc.id;
            sNetEnemy = tc.game;

            if (sNetNick.equals(sNetEnemy)) {
                alertShow("Incorrect nickname", "Your nickname is already in-use!", Alert.AlertType.WARNING);
                return;
            }/*else if(!JsonUtils.parseNoParamJson(JsonUtils.parseUrl(MAIN_URL + "isactive.php", "&id=" + sNetId))){
                alertShow("Invalid game id", "It seems that this game is already started!", Alert.AlertType.WARNING);
                return;
            }*/

            try {
                createStage();
                isHost = false;
                isOnline = true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            randomPlacer(true);
            randomPlacer(false);

            String resultJson = JsonUtils.parseUrl(MAIN_URL + "connect.php", "&id=" +
                    sNetId + "&map1=" + createDuringConnecting(coordinatesEnemy) + "&map2=" +
                    createDuringConnecting(coordinatesFriend) + "&player=" + sNetNick);

            // here was a bug with 1 == false, don't know wtf java's doing, the same nicks were not the crash reason(
            step = JsonUtils.parseMoverJson(resultJson) == isHost;

            changeStepDesign();
            new Processing();
        });

        row.getChildren().addAll(id, game, btn);
        table.getChildren().add(row);
    }

    public static void alertShow(String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(APP_TITLE);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public static void createStage() throws IOException {
        resetGlobals();

        Stage stage = (Stage) lastScene.getWindow();
        Parent root = FXMLLoader.load(controller.getClass().getResource("game.fxml"));

        String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", ""};
        gameGrid = (GridPane) root.lookup("#gameGrid");

        // ~ looking up for labels ~
        friendNick = (Label) root.lookup("#friendNick");
        enemyNick = (Label) root.lookup("#enemyNick");

        friendNick.setText(sNetNick);
        enemyNick.setText(sNetEnemy);

        String btnText;

        for (int i = 0; i < MAX_CELLS * 2 + 2 + 1; i++) {
            gameGrid.addColumn(i);

            for (int j = 0; j < MAX_CELLS + 1; j++) {
                btnText = "";
                Cell btn = new Cell();

                if (j == 0 && i > 0 && i != MAX_CELLS + 2 - 1 && i != MAX_CELLS * 2 + 2 + 1 - 1) {
                    btnText = alphabet[(i - 1) % 11];
                    btn.getStyleClass().add("g_info");
                } else if (j > 0 && (i == 0 || i == MAX_CELLS * 2 + 2 + 1 - 1)) {
                    btnText = Integer.toString(j - 1);
                    btn.getStyleClass().add("g_info");
                } else if (j == 0 && i == 0
                        || j == 0 && i == MAX_CELLS * 2 + 2 + 1 - 1
                        || i == MAX_CELLS + 2 - 1) {
                    btn.getStyleClass().add("g_separator");
                } else {
                    btn.getStyleClass().add("g_ship");

                    if (i <= MAX_CELLS) {
                        btn.setDisable(true);

                        btn.mine = true;
                        btn.x = j - 1;
                        btn.y = i - 1;

                        fieldFriend.add(btn);
                    } else {
                        btn.mine = false;
                        btn.x = j - 1;
                        btn.y = i - 1 - 11;

                        fieldEnemy.add(btn);
                    }

                    btn.setOnAction(event -> {
                        Cell cthis = (Cell) event.getSource();
                        if (cthis.mine) {
                            return;
                        }

                        handleClick(cthis);
                    });
                }

                btn.setText(btnText);
                gameGrid.add(btn, i, j);
            }
        }

        // Create a new scene with root and set the stage.
        lastScene = new Scene(root);
        stage.setScene(lastScene);
        stage.show();
    }

    private static void resetGlobals() {
        fieldFriend.clear();
        fieldEnemy.clear();
        step = false;
        gameOver = false;
        countDeathFriend = 0;
        countDeathEnemy = 0;
    }
}
