package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class AboutWindow {
	
	private final static String ABOUT_TEXT = "Thank you!";
	final static int WINDOW_WIDTH = 350;
	final static int WINDOW_HEIGHT = 70;
	static boolean windowActive = false;
	
	public static void create(){
		if (!windowActive) {
			Stage aboutWindow = new Stage();
			Pane layout = new Pane();
			
			Label textLabel = new Label(ABOUT_TEXT);
			textLabel.setWrapText(true);
			textLabel.setMaxWidth(WINDOW_WIDTH);
			textLabel.setPadding(new Insets(10,10,10,10));
			textLabel.setTextAlignment(TextAlignment.CENTER);
			
			
			layout.getChildren().add(textLabel);
			Scene mainFrame = new Scene(layout,WINDOW_WIDTH,WINDOW_HEIGHT);
			
			aboutWindow.setTitle("About window");
			aboutWindow.setScene(mainFrame);
			aboutWindow.setResizable(false);
			aboutWindow.show();
			
			aboutWindow.setOnCloseRequest(e -> {
				windowActive = false;
			});
			
			windowActive = true;
		}else {
			System.out.println("The about window is already open ;/");
		}
	}

}
