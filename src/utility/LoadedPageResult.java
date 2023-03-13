package utility;

import application.ControllerInterface;
import javafx.scene.Node;

public class LoadedPageResult {

	public Node root;
	public ControllerInterface control;

	public LoadedPageResult(Node root, ControllerInterface control) {
		this.root = root;
		this.control = control;
	}

}
