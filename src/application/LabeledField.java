package application;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LabeledField extends VBox {

	String label;
	Label fieldLabel;
	Node field;
	
	public LabeledField(String label, Node field) {
		this.label = label;
		this.field = field;
		
		fieldLabel = new Label(label);
		fieldLabel.setStyle("-fx-text-fill: WHITE");
		
		getChildren().addAll(fieldLabel, field);
	}
}
