package utility;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class CursorBounds extends Circle {

	public CursorBounds(Pane parent) {
		setMouseTransparent(true);
		setFill(new Color(0,0,0,0));
		setStroke(new Color(200/255, 200/255, 200/255, 1));
		setStrokeType(StrokeType.INSIDE);
		setStrokeWidth(2);
//		TODO: addCursorToGivenScene
		parent.getChildren().add(this);
	}
	
	public CursorBounds(Pane parent, double radius) {
		this(parent);
		setRadius(radius);
	}
	
	public CursorBounds(Pane parent, double radius, double x, double y) {
		this(parent, radius);
		setCenterX(x);
		setCenterY(y);
	}

	public void moveTo(double x,  double y) {
		setCenterX(Math.max(x, getRadius()));
		setCenterY(Math.max(y, getRadius()));
	}
	
	public void moveTo(double x, double y, double limitX, double limitY){
		moveTo(Math.min(x, (limitX - getRadius())), Math.min(y, (limitY - getRadius())));
	}
}
