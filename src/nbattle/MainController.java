package nbattle;

import com.sun.istack.internal.Nullable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static nbattle.Main.*;
import static nbattle.GameLogic.*;

public class MainController {
    private static final int MAX_LENGTH = 32; // for online nickname

    private static final int MAX_CELLS = 10;
    public static final String APP_TITLE = "Naval Battle";
    private static final String MAIN_URL = "http://f0123592.xsph.ru/backend/";

    public static ArrayList<Cell> fieldFriend = new ArrayList<>(), fieldEnemy = new ArrayList<>();
    public static boolean isRun = false;

    @FXML
    private Button mainStart, mainNet, btnQuit, btnMain,
            netCreate, netFind;

    @FXML
    private TextField netNick;

    @FXML
    public static GridPane gameGrid;

    @FXML
    private void buttonListener(ActionEvent e) throws IOException {
        if (e.getSource() == btnQuit) {
            //Stage stage = (Stage) btnQuit.getScene().getWindow();
            //stage.close();
            System.exit(0);
            return; // redundant ??
        }

        Stage stage;
        Parent root;

        if (e.getSource() == mainStart) {
            fieldFriend.clear();
            fieldEnemy.clear();
            step = true;
            gameOver = false;
            countDeathFriend = 0;
            countDeathEnemy = 0;

            stage = (Stage) mainStart.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("game.fxml"));

            String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", ""};
            gameGrid = (GridPane) root.lookup("#gameGrid");

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
            GameLoop gameLoop = new GameLoop();
        } else if (e.getSource() == mainNet) {
            stage = (Stage) mainNet.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("network.fxml"));

            TextField local_netNick = (TextField) root.lookup("#netNick");

            if(!sNetNick.isEmpty()){
                local_netNick.setText(sNetNick);
            }

            local_netNick.textProperty().addListener((observable, oldData, newData) -> {
                newData = newData.trim();
                if(newData.length() > MAX_LENGTH)
                    newData = newData.substring(0, MAX_LENGTH);

                sNetNick = newData;
                local_netNick.setText(newData);
            });
        } else if(e.getSource() == netCreate){
            if(sNetNick.isEmpty()){
                alertShow("Incorrect nickname!", "You should enter your nickname before creating a new game.", Alert.AlertType.ERROR);
                return;
            }else{
                String resultJson = JsonUtils.parseUrl(MAIN_URL + "create.php", "&host=" + sNetNick);
                sNetId = JsonUtils.parseCreateJson(resultJson);

                if(sNetId != null){
                    stage = (Stage) netFind.getScene().getWindow();
                    root = FXMLLoader.load(getClass().getResource("create.fxml"));

                    /* <--> id & snick are important <--> */
                    Label info = (Label) (root.lookup("#infoFlow")).lookup("#netInfo");
                    info.setText("Game id: #" + sNetId + ", name: " + sNetNick);

                    /* Connection logic begins ... */
                    /* Connection logic begins ... */
                    /* Connection logic begins ... */
                    /* Connection logic begins ... */
                }else{
                    alertShow("Oops! Something's wrong!", "Nickname's in use or connection problems occurred.", Alert.AlertType.ERROR);
                    return;
                }
            }
        } else if(e.getSource() == netFind){
            String resultJson = JsonUtils.parseUrl(MAIN_URL + "list.php", "");
            JSONArray gamesArray = JsonUtils.parseListJson(resultJson);

            if (gamesArray != null && gamesArray.size() > 0) {
                stage = (Stage) netFind.getScene().getWindow();
                root = FXMLLoader.load(getClass().getResource("find.fxml"));

                ScrollPane scroll = (ScrollPane) root.lookup("#tableScroll");
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                FlowPane table = new FlowPane();
                table.getStyleClass().add("t_container");

                for (int i = 0; i < gamesArray.size(); i++) {
                    JSONObject gamesData = (JSONObject) gamesArray.get(i);
                    addRowInto(table, gamesData.get("id").toString(), gamesData.get("host").toString());
                }

                scroll.setContent(table);

                /* Connection logic begins ... */
                /* Connection logic begins ... */
                /* Connection logic begins ... */
                /* Connection logic begins ... */
            }else{
                alertShow("Oops! Something's wrong!", "There are no active games at the moment.", Alert.AlertType.WARNING);
                return;
            }
        }
        else if (e.getSource() == btnMain) {
            stage = (Stage) btnMain.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("main.fxml"));
        } else {
            return;
        }

        // Create a new scene with root and set the stage.
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleClick(Cell cell) {
        System.out.println("Clicked: " + cell.x + ":" + cell.y);
        if (step && !gameOver) {
            checkField(cell.x, cell.y, step, fieldEnemy);
            if (isWin(fieldEnemy))
                for (Cell cellX : fieldEnemy) {
                    cellX.getStyleClass().add("cell-damaged");
                }
        }
    }

    @Nullable
    public static Cell getCell(ArrayList<Cell> field, int x, int y) {
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

    private void addRowInto(FlowPane table, String sid, String sgame){
        HBox row = new HBox();
        row.getStyleClass().add("t_row");

        Label id = new Label("#" + sid), game = new Label(sgame);
        id.getStyleClass().add("t_id");
        game.getStyleClass().add("t_game");

        TableCell btn = new TableCell(sid, sgame);
        btn.getStyleClass().add("t_btn");

        btn.setOnAction(event -> {
            TableCell tc = (TableCell) event.getSource();
            alertShow("Join dialog here!", "You tried to join to #" + tc.id + ", created by: " + tc.game, Alert.AlertType.INFORMATION);
        });

        row.getChildren().addAll(id, game, btn);
        table.getChildren().add(row);
    }

    private void alertShow(String header, String content, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(APP_TITLE);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
