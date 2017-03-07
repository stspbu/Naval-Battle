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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static nbattle.GameLogic.*;

public class MainController {
    private static final int MAX_CELLS = 10;
    private static final String MAIN_URL = "http://f0123592.xsph.ru/backend/";
    public static ArrayList<Cell> fieldFriend = new ArrayList<>(), fieldEnemy = new ArrayList<>();
    public static boolean isRun = false;

    @FXML
    private Button mainStart, mainNet, btnQuit, btnMain,
            netCreate, netFind;

    @FXML
    public static GridPane gameGrid;

    private Main activity_main;

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
            gameGrid = (GridPane) ((Pane) root).lookup("#gameGrid");

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
            //get reference to the button's stage
            stage = (Stage) mainNet.getScene().getWindow();
            //load up OTHER FXML document
            root = FXMLLoader.load(getClass().getResource("network.fxml"));
        } else if(e.getSource() == netFind){
            stage = (Stage) netFind.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("find.fxml"));

            ScrollPane scroll = (ScrollPane) ((Pane) root).lookup("#tableScroll");
            scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            FlowPane table = new FlowPane();
            table.getStyleClass().add("t_container");

            // getting content
            String resultJson = JsonUtils.parseUrl(MAIN_URL + "list.php", "");
            JSONArray gamesArray = JsonUtils.parseListJson(resultJson);

            if (gamesArray != null) {
                for (int i = 0; i < gamesArray.size(); i++) {
                    JSONObject gamesData = (JSONObject) gamesArray.get(i);
                    addRowInto(table, "#" + gamesData.get("id"), gamesData.get("host").toString());

                    // print out ~ debug ~
                    // System.out.println("ID игры: " + gamesData.get("id"));
                    /// System.out.println("Host: " + gamesData.get("host"));
                }
            }

            for(int i = 0; i < 10; i++)

            scroll.setContent(table);
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

    public void setMainApp(Main main) {
        this.activity_main = main;
    }

    private void addRowInto(FlowPane table, String sid, String sgame){
        HBox row = new HBox();
        row.getStyleClass().add("t_row");

        Label id = new Label(sid), game = new Label(sgame);
        id.getStyleClass().add("t_id");
        game.getStyleClass().add("t_game");

        Button btn = new Button();
        btn.getStyleClass().add("t_btn");

        btn.setOnAction(event -> {
            // handle join click
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("handle join click");

            alert.showAndWait();
        });

        row.getChildren().addAll(id, game, btn);
        table.getChildren().add(row);
    }
}
