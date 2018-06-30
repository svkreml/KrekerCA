import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/HashGui.fxml"));
        primaryStage.setTitle("HashGuiController");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }


}
