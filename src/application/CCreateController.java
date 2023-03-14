package application;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utility.Validator;
import utility.Vector2D;

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
		Validator valid = validateFields();
		if (valid.success) {
			// Creates user-defined canvas.
			Main.paintControl.setCanvasWidthAndHeight(Integer.parseInt(valid.getField("width")), Integer.parseInt(valid.getField("height")));
			this.close();
			Main.paintControl.open();
		}else {
			Main.displayErrorAlert("Invalid input data", "The following errors must be solved:" + "\n" + valid.message);
		}
	}
	
	// Returns true if the current values inside the text fields are valid.
	private Validator validateFields() {
		Validator valid = new Validator();
		
		valid.addField("width", cWidthField.getText());
		valid.addField("height", cHeightField.getText());
		
		try {
			int width = Integer.parseInt(valid.getField("width"));
			int height = Integer.parseInt(valid.getField("height"));
			
			if (width <= 0) {
				valid.appendError("Width of canvas must be greater than 0.");
			}
			
			if (height <= 0) {
				valid.appendError("Height of canvas must be greater than 0.");
			}
			
			Vector2D maxDim = Main.paintControl.getMaxCanvasDimensions();
			
			// TODO: Verify that the height/width doesn't exceed available screen space.
			if (width > maxDim.x) {
				valid.appendError("Width cannot exceed the screen width: " + (int) maxDim.x);
			}
			
			if (height > maxDim.y) {
				valid.appendError("Width cannot exceed the screen width: " + (int) maxDim.y);
			}
			
			Main.mainStage.getHeight();
			
		}catch (NumberFormatException e) {
			valid.appendError("Width and height must be filled and must be an integer.");
		}
		
		return valid;
	}
	
}
