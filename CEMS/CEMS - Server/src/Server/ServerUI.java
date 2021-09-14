package Server;

import enums.window;
import javafx.application.Application;
import javafx.stage.Stage;
import utils.FXMLutil;

public class ServerUI extends Application {
	final public static int DEFAULT_PORT = 5555;

	static ServerHandler sv = null;

	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLutil.runFXML(window.ServerPort.toString());

	}

}
