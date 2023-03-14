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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private final static String APP_NAME = "Paint Simple";
	public final static String PATH_TO_APPLICATION_STYLESHEET = "applicationStyle.css";
	
	private final static int MAX_SAVE_ATTEMPTS = 3;
	
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
			LoadedPageResult result = loadPage("canvasCreate", true);
			createControl = (CCreateController) result.control;
			createPane = (AnchorPane) result.root;
			
			result = loadPage("paintPane", false);
			paintControl = (PaintController) result.control;
			paintPane = (BorderPane) result.root;
			
			
			mainStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
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
			node.setVisible(false);
			ControllerInterface controller = loader.getController();
			
			if ((controller != null) && openOnLoad) {
				controller.open();
			}
			
			return new LoadedPageResult(node, controller);
		}catch(IOException e){
			System.out.println("Could not load scene: " + fxmlName + " error: " + e.getMessage());
			// No point in continuing :(
			System.exit(50);
		}
		return null;
	}
	
	// Returns a bool indicating whether the window was closed or not. TODO: Have some structure that keeps history about canvas (i.e. collects changes)
	public static boolean closeWindow() {
		if (!paintControl.hasChanged || displayConfirmAlert("You have unsaved changed!", "You have made some changes to the canvas that have not been saved. Are you sure you want to close the window without saving?"))
			mainStage.close();
		return false;
	}

	// Displays a confirmation alert box, yielding thread until a response has been received from user.
	public static boolean displayConfirmAlert(String title, String content) {
		Alert alertWin = new Alert(AlertType.CONFIRMATION);
		alertWin.getDialogPane().getStylesheets().add(PATH_TO_APPLICATION_STYLESHEET);
		alertWin.setTitle(title);
		alertWin.setContentText(content);
		Button okBtn = (Button) alertWin.getDialogPane().lookupButton(ButtonType.OK);
		
		okBtn.setText("Yes");
		okBtn.getStyleClass().add("destructive-action-btn");
		
		Button cancelBtn = (Button) alertWin.getDialogPane().lookupButton(ButtonType.CANCEL);
		cancelBtn.getStyleClass().add("cancel-btn");
		
		return alertWin.showAndWait().get() == ButtonType.OK;
	}
	
	public static void displayErrorAlert(String title, String content) {
		Alert alertWin = new Alert(AlertType.ERROR);
		alertWin.setTitle(title);
		alertWin.setContentText(content);
		
		alertWin.show();
	}

	// Attempts to write to file the given image, returns a bool indicating whether file saved sucessfully.
	public static boolean saveImageToFile(File file, String imageFormat, WritableImage image) {
		boolean success = false;
		int attemptNo = 0;
		while (!success && attemptNo < MAX_SAVE_ATTEMPTS) {
			try {
				 if (ImageIO.write(SwingFXUtils.fromFXImage(image,null), imageFormat, file))
					 success = true;
			} catch (IOException e) {
				System.out.println("Saving failed: " + e);
			}
			attemptNo++;
		}
		
		if (!success)
			displayErrorAlert("Could not save file!", "Image could not be saved!");
		
		return success;
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
