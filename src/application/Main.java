package application;
	
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private final static String APP_NAME = "Paint Simple";
	public static Controller mainControl;
	public static PaintController paintControl;
	public static Stage mainStage;
	public static BorderPane mainPane;
	
	@Override
	public void start(Stage stage) {
		try {
			// Sets up mainStage
			mainStage = stage;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("core.fxml"));
			mainPane = (BorderPane) loader.load();
			mainStage.setTitle(APP_NAME);
			mainStage.setScene(new Scene(mainPane));
			mainStage.setMinWidth(600);
			mainStage.setMinHeight(400);
			
			mainControl = loader.<Controller>getController();
			
			setupEvents();
			
			//Sets up the paint scene
			FXMLLoader loaderPaint = new FXMLLoader(getClass().getResource("paintPane.fxml"));
			loaderPaint.load();
			paintControl = loaderPaint.getController();
			paintControl.open();
			
			mainStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setupEvents() {
		// Add event handler for closing window.
		mainControl.closeButton.setOnAction((e) -> closeWindow());
		
		mainStage.setOnCloseRequest((e) -> {
			e.consume(); // Do not let the stage to be closed.
			closeWindow();});
	}
	
	// Returns a bool indicating whether the window was closed or not. TODO: Have some structure that keeps history about canvas (i.e. collects changes)
	public boolean closeWindow() {
		if (true) 
			if (displayAlert())
				mainStage.close();
		return false;
	}

	public boolean displayAlert() {
		Alert alertWin = new Alert(AlertType.CONFIRMATION);
		alertWin.setTitle("You have unsaved changed!");
		alertWin.setContentText("You have made some changes to the canvas that have not been saved. Do you wish you save them?");
		
		if (alertWin.showAndWait().get() == ButtonType.OK) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static void saveImageToFile(File file, String imageFormat, WritableImage image) {
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image,null), imageFormat, file);
		} catch (IOException e) {
			System.out.println("Saving failed: " + e);
		}
	}

	public static void openFile() {
		FileChooser fileChooser = new FileChooser();
		
		//add extension filers, currently only you can open a png image.
		FileChooser.ExtensionFilter pngExtension = new FileChooser.ExtensionFilter("PNG Files (*.png)","*.png");
		fileChooser.getExtensionFilters().add(pngExtension);
		
		//Open up the file directory for the user to select the file he wants to open
		File file = fileChooser.showOpenDialog(Main.mainStage);
		
		if (file != null) {
			// Put the file on canvas
			paintControl.putOnCanvasImage(new Image(file.toURI().toString()));
		}
	}
}
