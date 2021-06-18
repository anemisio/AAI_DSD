package chat.main.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainUser extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("/chat/telas/Login.fxml"));
			Scene scene = new Scene(root, 480, 257);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setMaxHeight(257);
			primaryStage.setMaxWidth(480);

			primaryStage.setScene(scene);

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
