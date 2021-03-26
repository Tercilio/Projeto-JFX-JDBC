package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity; // OBJETO DEPARTMENT PARA POPULAR AS CAIXAS DE TEXTO DO FORMULÁRIO

	private DepartmentService service;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {

		if (entity == null) {
			throw new IllegalStateException("Entity Was Null");
		}

		if (service == null) {
			throw new IllegalStateException("Service Was Null");
		}

		try {
			// SALVAR O DEPARTAMENTO NO BANCO DE DADOS
			entity = getFormData(); // INSTANCIA O DEPARTAMENTO
			service.saveOrUpdate(entity);// SALVADO! INJETE-O NO DEPARTMENT LIST CONTROLLER PARA FUNCIONAR

			// Notificar a lista para ela ser atualizada automaticamente, logo após ser
			// criado um novo departamento
			notifyDataChangeListeners();

			// DEPOIS QUE APERTAR NO BOTAO SALVAR, TEM QUE FECHAR A JANELA AUTOMATICAMENTE:
			utils.currentStage(event).close();

		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error Saving Object", null, e.getMessage(), AlertType.ERROR);
		}

	}

	private void notifyDataChangeListeners() {

		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	private Department getFormData() {

		Department obj = new Department();

		ValidationException exception = new ValidationException("Validation Error");

		obj.setId(utils.tryParseToint(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		utils.currentStage(event).close(); // AÇÃO DE FECHAR A JANELA
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldDouble(txtId); // ACEITA SOMENTE NUMEROS INTEIROS
		Constraints.setTextFieldMaxLength(txtName, 30); // Aceita até 30 caracteres
	}

	public void updateFormData() {

		if (entity == null) {
			throw new IllegalStateException("Entity Was Null");
		}

		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}

	}

}
