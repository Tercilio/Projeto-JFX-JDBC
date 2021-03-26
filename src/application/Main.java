package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Scene mainScene;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load(); // CARREGA A MAINVIEW
			
			//PARA DEIXAR O SCROLLPANE AJUSTADO AO TAMANHO DA JANELA
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			mainScene = new Scene(scrollPane); //CENA PRINCIPAL
			primaryStage.setScene(mainScene); 
			primaryStage.setTitle("Sample JavaFX application"); //TÍTULO DO PALCO
			primaryStage.show();  //MOSTRA O PALCO
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Scene getMainScene() {
		return mainScene;  //RETORNA O OBJETO MAINSCENE
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
