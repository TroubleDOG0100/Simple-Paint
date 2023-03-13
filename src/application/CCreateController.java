package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class CCreateController implements ControllerInterface {

	@FXML AnchorPane mainScene;
	@FXML Button createCanvasBtn;
	@FXML TextField cWidthField;
	@FXML TextField cHeightField;
	
	@Override
	public void open() {
		Main.mainPane.setCenter(mainScene);
		mainScene.setVisible(true);
	}

	@Override
	public void close() {
		mainScene.setVisible(false);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	@FXML public void createCanvas(ActionEvent e) {
		if (validateFields()) {
			// TODO: Move to the canvas.
		}else {
			Alert nAlert = new Alert(AlertType.ERROR);
			nAlert.setTitle("Fields are incorrent!");
			nAlert.setContentText("The following errors must be solved!");
			nAlert.show();
		}
	}
	
	// Returns true if the current values inside the text fields are valid.
	private boolean validateFields() {
		return false;
	}
	
}
