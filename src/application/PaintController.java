package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import utility.CursorBounds;

public class PaintController implements Initializable {
	
	private final static String[] PAINT_TOOLS = {"Paint","Erase","Text"};
	
	private final static String INITIAL_DIR_PATH = "C:\\\\Users\\\\User\\\\Documents\\\\Testing";
	private final static double  DEFAULT_BRUSH_SIZE = 10.0;
	private Color paintColor = new Color((25/255), (25/255), (25/255), 1);
	private String currentTool;
	private String selectedText = "Text";
	private double textSize = 15;
	private double brushSize;
	
	private CursorBounds cursorBounds;
	
	@FXML private BorderPane mainScene;
	@FXML private VBox toolsPanel;
	@FXML private HBox toolPropertyPanel;
	@FXML private Canvas canvas;
	@FXML private Pane canvas_anchor;
	
	private GraphicsContext canvasGraphicContext;
	
	
	public void open() {
		Main.mainPane.setCenter(mainScene);
		createPropertyPanel(PAINT_TOOLS[0]);
		mainScene.setVisible(true);
		System.out.println(mainScene.isVisible());
	}
	
	public void close() {
		mainScene.setVisible(false);
	}

	public void onSave() {
		if (mainScene.isVisible()) {
			try {
				FileChooser fileChooser = new FileChooser();
				
				// Create extensionfilter to only
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)","*.png");
				fileChooser.getExtensionFilters().add(extFilter);
				
				File initialDirectory = new File(INITIAL_DIR_PATH);
				
				// File will always return an abstract pathname, so it will be never null, but you can check if you can write in it to see if it exists and is accesable.
				if (!initialDirectory.canWrite()) {
					initialDirectory = new File(System.getProperty("user.home"));
				}
				
				fileChooser.setInitialDirectory(initialDirectory);
				
				//Open up the file directory for the user to save the file.
				File file = fileChooser.showSaveDialog(Main.mainStage);
				
				// After a location has been chosen the code below will run, and the file is going to be the chosen by user, if he cancels it, it will be null
				if (file != null) {
					Main.saveImageToFile(file,"png",canvas.snapshot(null,null));
				}} catch(Exception e) {
					System.out.println("Saving failed :(: " + e);
			}
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Paint intialized");
		
		// get graphics context in which we will draw
		canvasGraphicContext = canvas.getGraphicsContext2D();
		
		cursorBounds = new CursorBounds(canvas_anchor);
		
		// Set the brushSize (also changes the size of the cursor bounds)
		setBrushSize(DEFAULT_BRUSH_SIZE);
		
		// Create all painting tools.
		for (String toolName : PAINT_TOOLS) {
			createPaintTool(toolName);
		}
		
		setCurrentTool(PAINT_TOOLS[0]);
		
		// Setup canvas event listeners/handlers
		canvas.setOnMouseDragged(e -> {
			double x = e.getX() - brushSize/2; 
			double y = e.getY() - brushSize/2;
			
			cursorBounds.moveTo(e.getX(), e.getY());
			if (currentTool.equals("Paint")) {
				canvasGraphicContext.fillOval(x, y, brushSize, brushSize);
			}else if (currentTool.equals("Erase")) {
				canvasGraphicContext.clearRect(x, y, brushSize, brushSize);
			}
		});
		
		
		canvas.setOnMouseMoved(e -> {
			cursorBounds.moveTo(e.getX(), e.getY());
		});
		
		
		canvas.setOnMouseClicked(e -> {
			double x = e.getX() - textSize/2; 
			double y = e.getY() + textSize/2;
			
			if (currentTool.equals("Text")) {
				canvasGraphicContext.setFont(new Font(textSize));
				canvasGraphicContext.fillText(selectedText, x, y);
			}
		});
		
		// Setup canvas
		canvas.setHeight((Main.mainPane.getHeight() - (toolPropertyPanel.getHeight() + (Main.mainControl.menuBar.getHeight()))));
		canvas.setWidth((Main.mainPane.getWidth() - (toolPropertyPanel.getWidth() + (Main.mainControl.menuBar.getWidth()))));
		canvas.heightProperty().bind(Main.mainPane.heightProperty().subtract(toolPropertyPanel.heightProperty().add(Main.mainControl.menuBar.heightProperty())));
		canvas.widthProperty().bind(Main.mainPane.widthProperty().subtract(toolsPanel.widthProperty()));
		
	}
	
	// Creates one of three available paint tools (paint, erase, text)
	private void createPaintTool(String toolName) {
		Button toolButton = new Button(toolName);
		toolButton.getStyleClass().add("paint-tools");
		
		toolButton.setOnAction(e -> setCurrentTool(toolName));
		
		toolsPanel.getChildren().add(toolButton);
	}
	
	private void setCurrentTool(String toolName) {
		currentTool = toolName;
		
		if (toolName == PAINT_TOOLS[0] || toolName == PAINT_TOOLS[1]) {
			canvas.setCursor(Cursor.OPEN_HAND);
			cursorBounds.setVisible(true);
		}else{
			canvas.setCursor(Cursor.DEFAULT);
			cursorBounds.setVisible(false);
		}
		
		createPropertyPanel(toolName);
	}

	private TextField createTextField(String fieldInput) {
		TextField recSize = new TextField(fieldInput);
		recSize.getStyleClass().add("paint-tools");
		return recSize;
	}
	
	private ColorPicker createColorPicker(Color input) {
		ColorPicker colPicker = new ColorPicker(input);
		colPicker.getStyleClass().addAll("color-pickers");
		
		colPicker.setOnAction(e -> {
			paintColor = colPicker.getValue();
			canvasGraphicContext.setFill(paintColor);
		});
		
		return colPicker;
	}
	
	private void createPropertyPanel(String toolName) {
		toolPropertyPanel.getChildren().clear();
		if (toolName.equals("Paint")) {
			ColorPicker colPicker = createColorPicker(paintColor);
			
			TextField recSize = createTextField(Double.toString(brushSize));
			recSize.setOnAction(e -> {
				setBrushSize(Double.parseDouble(recSize.getText()));
			});
			
			toolPropertyPanel.getChildren().addAll(colPicker,recSize);
		} else if (toolName.equals("Erase")) {
			TextField recSize = createTextField(Double.toString(brushSize));
			
			recSize.setOnAction(e -> {
				setBrushSize(Double.parseDouble(recSize.getText()));
			});
			
			toolPropertyPanel.getChildren().add(recSize);
		} else if (toolName.equals("Text")) {
			ColorPicker colPicker = createColorPicker(paintColor);
			TextField textInput = createTextField(selectedText);
			
			textInput.setOnAction(e -> {
				selectedText = textInput.getText();
			});
			
			TextField widthInput = createTextField(Double.toString(textSize));
			
			widthInput.setOnAction(e -> {
				textSize = Double.parseDouble(widthInput.getText());
			});
			
			toolPropertyPanel.getChildren().addAll(colPicker,textInput,widthInput);
		}
	}

	private void setBrushSize(double brushSize) {
		this.brushSize = brushSize;
		
		// For UX, update the cursorBounds size to indicate brush radius.
		cursorBounds.setRadius(brushSize/2);
	}

	public void putOnCanvasImage(Image image) {
		canvasGraphicContext.drawImage(image,0,0);
	}
	
}
