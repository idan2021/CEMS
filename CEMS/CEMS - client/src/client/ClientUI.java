package client;
import enums.window;
import javafx.application.Application;
import javafx.stage.Stage;
import utils.FXMLutil;


public class ClientUI extends Application {
	public static ClientHandler clientHandler; 
	public static Object userRole;
	

	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		FXMLutil.runFXML(window.ClientServerConnection.toString());

	}

}
