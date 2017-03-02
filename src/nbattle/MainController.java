package nbattle;

import com.sun.media.jfxmediaimpl.platform.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
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

            for(int i = 0; i < 10*2+2+1; i++){
                gameGrid.addColumn(i);

                for(int j = 0; j < 10+1; j++) {
                    btnText = "";
                    Button btn = new Button();

                    if(j == 0 && i > 0 && i != 12-1 && i != 10*2+2+1 - 1){
                        btnText = alphabet[(i-1)%11];
                        btn.getStyleClass().add("g_info");
                    }else if(j > 0 && (i == 0 || i == 10*2+2+1 - 1)){
                        btnText = Integer.toString(j-1);
                        btn.getStyleClass().add("g_info");
                    }else if(j == 0 && i == 0
                        || j == 0 && i == 10*2+2+1 - 1
                        || i == 12 - 1){
                        btn.getStyleClass().add("g_separator");
                    }else{
                        btn.getStyleClass().add("g_button");
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

    public void setMainApp(Main main){
        this.activity_main = main;
    }
}
