package nbattle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static nbattle.MainController.isRun;

public class Main extends Application {
    /* NETWORK PARAMS */
    public static String sNetId = "", sNetNick = "";

    @Override
    public void start(Stage primaryStage) throws Exception {
        isRun = true;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main.fxml"));

        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle(MainController.APP_TITLE);
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> isRun = false);

        // Даём контроллеру доступ к главному приложению.
        MainController controller = loader.getController();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
