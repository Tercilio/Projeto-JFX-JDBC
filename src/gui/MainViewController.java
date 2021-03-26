package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {

	//CRIANDO ITEMS DE CONTROLE
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		});
	}
	
	
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}
	
	
	@FXML
	public void onMenuItemAbooutAction() {
		loadView("/gui/About.fxml", x -> {});
	}
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		
		
	}
	
	//FUNÇÃO PARA ABRIR UMA OUTRA TELA
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {  //SYNCHRONIZED faz com que a aplicação não seja interrrompida durante o Multitread
		
		try {
			//CARREGANDO A VIEW
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			//MOSTRAR A VIEW DENTRO DA JANELA PRINCIPAL
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); //REFERENCIA PARA O SCROLLPANE
		
			//INCLUINDO OS FILHOS DA JANELA QUE ESTIVER ABERTA
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			//EXECUTA A FUNÇÃO QUE ESTA SENDO PASSADA COMO ARGUMENTO
			T controller = loader.getController();
			initializingAction.accept(controller);
			
		} catch (IOException e) {
			Alerts.showAlert("IOException", "Error Loading View", e.getMessage(), AlertType.ERROR);
		}
	}

}
