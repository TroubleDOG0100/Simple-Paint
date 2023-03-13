package application;
	
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utility.LoadedPageResult;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private final static String APP_NAME = "Paint Simple";
	public static Controller mainControl;
	public static PaintController paintControl;
	public static CCreateController createControl;
	
	public static Stage mainStage;
	public static BorderPane mainPane;
	public static AnchorPane createPane;
	public static BorderPane paintPane;
	
	@Override
	public void start(Stage stage) {
		try {
			// Sets up mainStage
			mainStage = stage;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("core.fxml"));
			mainPane = (BorderPane) loader.load();
			mainStage.setTitle(APP_NAME);
			mainStage.setScene(new Scene(mainPane));
			mainStage.setMinWidth(500);
			mainStage.setMinHeight(300);
			
			mainControl = loader.getController();
			
			setupEvents();
			
			// Setup pages.
			LoadedPageResult result = loadPage("canvasCreate", false);
			createControl = (CCreateController) result.control;
			createPane = (AnchorPane) result.root;
			
			result = loadPage("paintPane", true);
			paintControl = (PaintController) result.control;
			paintPane = (BorderPane) result.root;
			
			
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
	
	
	public LoadedPageResult loadPage(String fxmlName, boolean openOnLoad) throws IOException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName + ".fxml"));
			Node node = loader.load();
			ControllerInterface controller = loader.getController();
			
			if ((controller != null) && openOnLoad) {
				controller.open();
			}
			
			return new LoadedPageResult(node, controller);
		}catch(IOException e){
			System.out.println("Could not load scene: " + fxmlName + " error: " + e.getMessage());
		}
		return null;
	}
	
	// Returns a bool indicating whether the window was closed or not. TODO: Have some structure that keeps history about canvas (i.e. collects changes)
	public static boolean closeWindow() {
		if (true) 
			if (displayConfirmAlert("You have unsaved changed!", "You have made some changes to the canvas that have not been saved. Do you wish you save them?"))
				mainStage.close();
		return false;
	}

	// Displays a confirmation alert box, yielding thread until a response has been received from user.
	public static boolean displayConfirmAlert(String title, String content) {
		Alert alertWin = new Alert(AlertType.CONFIRMATION);
		alertWin.setTitle(title);
		alertWin.setContentText(content);
		
		return alertWin.showAndWait().get() == ButtonType.OK;
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
