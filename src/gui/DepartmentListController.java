package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service;

	// REFERENCIAS PARA OS COMPONENTES DA TELA DEPARTMENTLIST
	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;

	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Department> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		// ACESSANDO STAGE
		Stage parentStage = utils.currentStage(event);
		Department obj = new Department(); // PARA INICIAR O FORMULÁRIO VAZIO, PARA CADASTRAR UM NOVO DEPARTAMENTO
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	// MÉTODO SET PARA INJETAR DEPENDÊNCIA EM OUTRO LUGAR, DE DEPARTMENT SERVICE
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		InitializeNodes();
	}

	private void InitializeNodes() {

		// COMANDO PARA INICIAR O COMPORTAMENTO DAS COLUNAS DAS TABELAS
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// PARA A TABELA ACOMPANHAR A LARGURA E ALTURA DA JANELA
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); // Acompanha a altura da janela
	}

	// ACESSAR OS SERVIÇOS CARREGAR OS DEPARTAMENTOS E COLOCAR NA OBSLIST
	public void updateTableView() {

		if (service == null) {
			throw new IllegalStateException("Service was Null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list); // Carregando a lista dentro o ObservableList

		tableViewDepartment.setItems(obsList);
		initEditButtons(); // ACRESCENTA UM NOVO BOTAO "EDITAR" EM CADA LINHA DA TABELA
		initRemoveButtons();
	
	}

	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		// INSTANCIANDO JANELA DE DIALOGO
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController(); // PEGANDO A REFERENCIA PARA O CONTROLADOR DO
																			// FORMULÁRIO
			controller.setDepartment(obj); // INJETANDO O DEPARTAMENTO NESSE CONTROLADOR
			controller.setDepartmentService(new DepartmentService()); // Injeção de Dependencia
			controller.subscribeDataChangeListener(this); // INSCREVER PARA OUVIR O OBJETO DO DATACHANGE
			controller.updateFormData(); // CARREGA OS DADOS NO FORMULÁRIO

			// INSTANCIANDO UM NOVO PALCO, QUE VAI APARECER EM CIMA DE OUTRO
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department Data"); // TÍTULO DA JANELA
			dialogStage.setScene(new Scene(pane)); // CENA DO STAGE
			dialogStage.setResizable(false); // JANELA NÃO PODERÁ SER REDIMENSIONADA (False)
			dialogStage.initOwner(parentStage); // QUEM É O PAI DA JANELA
			dialogStage.initModality(Modality.WINDOW_MODAL); // JANELA VAI SER MODAL, TRAVADA ATÉ VC FECHAR
			dialogStage.showAndWait();
		} catch (IOException e) {
			
			e.printStackTrace();
			Alerts.showAlert("IOException", "Error Loading View", e.getMessage(), AlertType.ERROR);

		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Department obj) {
	
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure  to delete?");
		
		if(result.get() == ButtonType.OK) {
			

			if (service == null) {
				throw new IllegalStateException("Service was Null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}catch(DbIntegrityException e){
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
