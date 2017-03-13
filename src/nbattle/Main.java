package nbattle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static nbattle.MainController.isRun;
import static nbattle.MainController.lastScene;


public class Main extends Application {
    /* NETWORK PARAMS */
    public static boolean isHost, isOnline;
    public static String sNetId = "", sNetNick = "", sNetEnemy = "";
    public static MainController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        isRun = true;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main.fxml"));

        lastScene = new Scene(loader.load());
        primaryStage.setTitle(MainController.APP_TITLE);
        primaryStage.setScene(lastScene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> isRun = false);

        // Даём контроллеру доступ к главному приложению.
        controller = loader.getController();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
