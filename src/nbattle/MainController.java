package nbattle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class MainController {
    private static final int MAX_CEILS = 10;
    private static ArrayList<Cell> fieldFriend = new ArrayList<>(), fieldEnemy = new ArrayList<>();

    @FXML
    private Button mainStart, mainNet, btnQuit, btnMain;

    private Main activity_main;

    @FXML
    private void buttonListener(ActionEvent e) throws IOException {
        if(e.getSource() == btnQuit) {
            System.exit(0);
            return; // redundant ??
        }

        Stage stage;
        Parent root;

        if(e.getSource()== mainStart){
            stage = (Stage) mainStart.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("game.fxml"));

            String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", ""};
            GridPane gameGrid = (GridPane) ((Pane) root).lookup("#gameGrid");

            String btnText;

            for(int i = 0; i < MAX_CEILS*2+2+1; i++){
                gameGrid.addColumn(i);

                for(int j = 0; j < MAX_CEILS+1; j++) {
                    btnText = "";
                    Cell btn = new Cell();

                    if(j == 0 && i > 0 && i != MAX_CEILS+2 - 1 && i != MAX_CEILS*2+2+1 - 1){
                        btnText = alphabet[(i-1) % 11];
                        btn.getStyleClass().add("g_info");
                    }else if(j > 0 && (i == 0 || i == MAX_CEILS*2+2+1 - 1)){
                        btnText = Integer.toString(j-1);
                        btn.getStyleClass().add("g_info");
                    }else if(j == 0 && i == 0
                        || j == 0 && i == MAX_CEILS*2+2+1 - 1
                        || i == MAX_CEILS + 2 - 1){
                        btn.getStyleClass().add("g_separator");
                    }else{
                        btn.getStyleClass().add("g_ship");

                        if(i <= MAX_CEILS){
                            btn.setDisable(true);

                            btn.mine = true;
                            btn.x = j-1;
                            btn.y = i-1;

                            fieldFriend.add(btn);
                        }else{
                            btn.mine = false;
                            btn.x = j-1;
                            btn.y = i-1 - 11;

                            fieldEnemy.add(btn);
                        }

                        btn.setOnAction(event -> {
                            Cell cthis = (Cell)event.getSource();
                            if(cthis.mine){
                                return;
                            }

                            handleClick(cthis);
                        });
                    }

                    btn.setText(btnText);
                    gameGrid.add(btn, i, j);
                }
            }
        }
        else if(e.getSource()== mainNet){
            //get reference to the button's stage
            stage = (Stage) mainNet.getScene().getWindow();
            //load up OTHER FXML document
            root = FXMLLoader.load(getClass().getResource("network.fxml"));
        }else if(e.getSource()== btnMain) {
            stage = (Stage) btnMain.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("main.fxml"));
        }else{
            return;
        }

        //create a new scene with root and set the stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleClick(Cell cell){
        System.out.println("Clicked: " + cell.x + ":" + cell.y);
    }

    public void setMainApp(Main main){
        this.activity_main = main;
    }
}
