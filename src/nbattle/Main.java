package nbattle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main.fxml"));

        primaryStage.setTitle("Naval Battle");
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();

        // Даём контроллеру доступ к главному приложению.
        MainController controller = loader.getController();
        controller.setMainApp(this);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
