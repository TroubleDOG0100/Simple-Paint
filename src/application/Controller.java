package application;

import java.net.URL;
import java.util.ResourceBundle;


import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Controller implements Initializable {

	@FXML VBox sidePanel;
	@FXML BorderPane scene;
	@FXML MenuBar menuBar;
	@FXML MenuItem closeButton;
	@FXML MenuItem saveButton;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		scene.getStylesheets().add(Main.PATH_TO_APPLICATION_STYLESHEET);
	}
	
	@FXML public void onOpenClicked(ActionEvent event){
		Main.openFile();
	}
	
	@FXML public void onSaveClicked(ActionEvent event) {
		Main.paintControl.onSave();
	}
	
	@FXML public void onAboutPressed(ActionEvent event) {
		AboutWindow.create();
	}
	
}
