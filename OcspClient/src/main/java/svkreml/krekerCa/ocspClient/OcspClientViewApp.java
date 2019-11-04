package svkreml.krekerCa.ocspClient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import svkreml.krekerCa.core.BcInit;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

public class OcspClientViewApp extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BcInit.init();
        Parent root = FXMLLoader.load(
                Paths.get(
                        "OcspClient\\src\\main\\resources\\svkreml\\krekerCa\\ocspClient\\OcspClientView.fxml"
                ).toUri().toURL()
        );
        primaryStage.setTitle("OcspClientView.fxml");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }


}
