package utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import Server.ServerHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * <p>
 * <b>Explanation:</b> The class is for FXML tools; create,run & swap between
 * fxml scenes.
 * <p>
 * 
 * @author DvirVahav & OrSteiner
 */
public class FXMLutil {

	public static FXMLLoader fxmlCreator(String windowName) throws IOException {

		return new FXMLLoader(FXMLutil.class.getResource(windowName));

	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method is for the connection between the FXML and
	 * the controller
	 * <p>
	 * <b>Receive:</b> The windowName to switch to
	 * <p>
	 * <b>Return:</b> The new window to run the FXML on
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static Stage runFXML(String windowName) throws IOException {
		FXMLLoader windowFXML = FXMLutil.fxmlCreator(windowName); // create the FXMLLoder by window

		Parent root = windowFXML.load(); // load it into root
		Stage stageWindow = new Stage(); // set new stage
		stageWindow.setScene(new Scene(root)); // put fxml root scene to stage

		stageWindow.setTitle("CEMS");
		stageWindow.setResizable(false);
		stageWindow.show();
		if (windowName == enums.window.ServerPort.toString()) {

			stageWindow.setOnCloseRequest(EventHandler -> {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("You are about to disconnect from server");
				alert.setHeaderText("Any unsaved changes will be deleted");
				alert.setContentText("Click OK to disconnect and Cancel otherwise");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					try {
						if (ServerHandler.getInstance(0, null).isListening()) {
							ServerHandler.DBconnect.close();
							ServerHandler.getInstance(0, null).close();
						}
						

					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.exit(0);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.exit(0);
					}
				} else {
					EventHandler.consume();
				}

			});
		}
		return stageWindow;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method to change scene to a new one
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch and the windowName to
	 * switch to
	 * <p>
	 * <b>Return:</b> New scene to show
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static void swapScene(ActionEvent event, String windowName) throws IOException {
		FXMLLoader loginFXML = FXMLutil.fxmlCreator(windowName);
		Parent tableViewParent = loginFXML.load();
		Scene tableViewScene = new Scene(tableViewParent);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(tableViewScene);

		/*
		 * in case of exit - disconnect client from server, in case its server it will
		 * disconnect db and server. (Client with no change)
		 */

		
		window.show();

	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method hides old scene, show new scene (at the same
	 * scene)
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch and the windowName to
	 * switch to
	 * <p>
	 * <b>Return:</b> New scene to show
	 * <p>
	 * 
	 * @author DvirVahav
	 */
	public static void hideOldShowNew(ActionEvent event, String windowName) throws IOException {
		FXMLLoader loginFXML = FXMLutil.fxmlCreator(windowName);
		Parent tableViewParent = loginFXML.load();
		Scene tableViewScene = new Scene(tableViewParent);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.hide();
		window.setScene(tableViewScene);
		window.show();

	}

}
