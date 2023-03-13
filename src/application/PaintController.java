package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
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
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import utility.CursorBounds;
import utility.Vector2D;

public class PaintController implements ControllerInterface {
	
	private final static String[] PAINT_TOOLS = {"Paint","Erase","Text"};
	
	private final static String INITIAL_DIR_PATH = "C:\\\\Users\\\\User\\\\Documents\\\\Testing";
	private final static double  DEFAULT_BRUSH_SIZE = 10.0;
	private final static double MAX_DIST_BEFORE_PATH = 4;
	
	private Color paintColor = new Color((25/255), (25/255), (25/255), 1);
	private String currentTool;
	private String selectedText = "Text";
	private double textSize = 15;
	private double brushSize;
	
	private CursorBounds cursorBounds;
	private Vector2D lastDragPos;
	
	@FXML private BorderPane mainScene;
	@FXML private HBox toolsPanel;
	@FXML private VBox toolPropertyPanel;
	@FXML private Canvas canvas;
	@FXML private Pane canvas_anchor;
	@FXML private AnchorPane canvas_tile;
	
	private GraphicsContext canvasGraphicContext;
	
	@Override
	public void open() {
		Main.mainPane.setCenter(mainScene);
		createPropertyPanel(PAINT_TOOLS[0]);
		mainScene.setVisible(true);
	}
	
	@Override
	public void close() {
		mainScene.setVisible(false);
	}

	public void onSave() {
		if (mainScene.isVisible()) {
			try {
				FileChooser fileChooser = new FileChooser();
				
				// Create extensionfilter to only accept png files
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
					System.out.println("Saving failed :( :" + e);
			}
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
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
			double xWithout = e.getX();
			double yWithout = e.getY();
			double x = e.getX() - brushSize/2; 
			double y = e.getY() - brushSize/2;
			
			cursorBounds.moveTo(e.getX(), e.getY(), canvas.getWidth(), canvas.getHeight());
			if (currentTool.equals("Paint")) {
				if (lastDragPos.distanceTo(xWithout, yWithout) < MAX_DIST_BEFORE_PATH) {
					canvasGraphicContext.fillOval(x, y, brushSize, brushSize);
					lastDragPos.setTo(xWithout, yWithout);
				}else {
					canvasGraphicContext.moveTo(lastDragPos.x, lastDragPos.y);
					canvasGraphicContext.lineTo(xWithout, yWithout);
				
					canvasGraphicContext.stroke();
					lastDragPos.setTo(xWithout, yWithout);
				}
				
			}else if (currentTool.equals("Erase")) {
				canvasGraphicContext.clearRect(x, y, brushSize, brushSize);
			}
		});
		
		canvas.setOnMouseReleased(e -> {
			System.out.println("Exited");
			canvasGraphicContext.closePath();
			canvasGraphicContext.beginPath();
			lastDragPos = null;
		});
		
		
		canvas.setOnMouseMoved(e -> {
			cursorBounds.moveTo(e.getX(), e.getY(),  canvas.getWidth(),  canvas.getHeight());
		});
		
		canvas.setOnMousePressed(e -> {
			double x = e.getX(); 
			double y = e.getY();
			
			
			if (currentTool.equals("Text")) {
				canvasGraphicContext.setFont(new Font(textSize));
				canvasGraphicContext.fillText(selectedText, x - textSize/2, y + textSize/2);
			}else if (currentTool.equals("Paint")) {
				canvasGraphicContext.fillOval(x - brushSize/2, y - brushSize/2, brushSize, brushSize);
				lastDragPos = new Vector2D(x, y);
			}else if (currentTool.equals("Erase")) {
				canvasGraphicContext.clearRect(x - brushSize/2, y - brushSize/2, brushSize, brushSize);
			}
		});
		
		
		// Initialize canvas to 0x0
		setCanvasWidthAndHeight(0, 0);
		
		canvasGraphicContext.setLineCap(StrokeLineCap.ROUND);
		canvasGraphicContext.setLineJoin(StrokeLineJoin.ROUND);
		
		canvas_tile.minWidthProperty().bind(canvas.widthProperty());
		canvas_tile.minHeightProperty().bind(canvas.heightProperty());
		canvas_tile.maxWidthProperty().bind(canvas.widthProperty());
		canvas_tile.maxHeightProperty().bind(canvas.heightProperty());
		canvas_anchor.toBack();	
	}
	
	// Creates one of three available paint tools (paint, erase, text)
	private void createPaintTool(String toolName) {
		Button toolButton = new Button(toolName);
		toolButton.getStyleClass().add("paint-tools");
		
		toolButton.setOnAction(e -> setCurrentTool(toolName));
		
		toolsPanel.getChildren().add(toolButton);
	}
	
	private void setCurrentTool(String toolName) {
		if (currentTool != null && currentTool.equals(toolName))
			return;
		
		currentTool = toolName;
		
		toolsPanel.getChildren().forEach((node) -> {
			if (((Button) node).getText().equals(toolName)){
				node.getStyleClass().add("selected-button");
			}else {
				node.getStyleClass().remove("selected-button");
			}
		});
		
		if (toolName == PAINT_TOOLS[0] || toolName == PAINT_TOOLS[1]) {
			canvas.setCursor(Cursor.NONE);
			cursorBounds.setVisible(true);
		}else{
			canvas.setCursor(Cursor.TEXT);
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
			canvasGraphicContext.setStroke(paintColor);
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
			
			toolPropertyPanel.getChildren().addAll(colPicker, textInput, widthInput);
		}
	}

	private void setBrushSize(double brushSize) {
		this.brushSize = brushSize;
		
		canvasGraphicContext.setLineWidth(brushSize);
		// For UX, update the cursorBounds size to indicate brush radius.
		cursorBounds.setRadius(brushSize/2);
	}

	public void setCanvasWidthAndHeight(double width, double height) {
		canvas.setWidth(width);
		canvas.setHeight(height);
	}
	
	public void putOnCanvasImage(Image image) {
		double nCanvasWidth = Math.max(image.getWidth(), canvas.getWidth());
		double nCanvasHeight = Math.max(image.getHeight(), canvas.getHeight());
		
		Vector2D maxDim = getMaxCanvasDimensions();
		if (nCanvasWidth > maxDim.x || nCanvasHeight > maxDim.y) {
			Main.displayErrorAlert("Image too big!", "The opened image cannot fit within the canvas!");
			return;
		}
		
		// When the user has not yet defined the canvas.
		if (!mainScene.isVisible())
			setCanvasWidthAndHeight(nCanvasWidth, nCanvasHeight);
			
		// Do we have to resize canvas to fit image.
		if (nCanvasWidth > canvas.getWidth() || nCanvasHeight > canvas.getHeight()) {
			if (Main.displayConfirmAlert("Canvas needs to be resized", "The canvas must be resized for the image to fit (new dimension:(" + (int) nCanvasWidth + "," + (int) nCanvasHeight + "), do you wish to resize it?")) {
				setCanvasWidthAndHeight(nCanvasWidth, nCanvasHeight);
			}else {
				System.out.println("User denied to resize the canvas!");
				return;
			}
		}
		
		canvasGraphicContext.drawImage(image,0,0);
	}

	public Vector2D getMaxCanvasDimensions() {
		// TODO: Window title bar not accounted for in height calc.
		Rectangle2D bounds = Screen.getPrimary().getBounds();
		return new Vector2D(bounds.getMaxX() - toolsPanel.getPrefWidth(), bounds.getMaxY() - toolPropertyPanel.getPrefHeight() - Main.mainControl.menuBar.getHeight());
	}
	
}
