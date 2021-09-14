package controller;

import java.net.URL;
import java.util.ResourceBundle;

import client.ClientHandler;
import client.ClientUI;
import enums.window;
import javafx.fxml.*;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import utils.FXMLutil;
import utils.SpecialCalls;

/**
 * <p>
 * <b>Explanation:</b> The class ClientServerConnectionController - first window that connect with host name +
 * port - client side next window - login to system
 *  <p>
 * @author DvirVahav
 */
public class ClientServerConnectionController implements Initializable {
	@FXML
	private Button connectButton;

	@FXML
	private Text incorrectInputTextField;
	@FXML
	private Stage serverWindow;

	@FXML
	private TextField portField;

	@FXML
	private TextField hostField;

	@FXML
	private ImageView serverLogoImage;

	/**
	 * <p>
	 * <b>Explanation:</b> Once user prssed something wrong and he wants to change the line with the mistakes- it becomes null.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		portField.setOnKeyTyped(event -> {
			incorrectInputTextField.setText(null);
		});

		hostField.setOnKeyTyped(KeyEvent -> {
			incorrectInputTextField.setText(null);
		});
		portField.setOnKeyReleased(KeyEvent -> {
			if (KeyEvent.getCode() == KeyCode.ENTER)
				connectButton.fire();
		});
		hostField.setOnKeyReleased(KeyEvent -> {
			if (KeyEvent.getCode() == KeyCode.ENTER)
				connectButton.fire();
		});

	}

	/**
	 * <p>
	 * <b>Explanation:</b> Once pressed the method will notify the user if port\host name fields are empty, once
	 * typed will check if there is host name and port and will try to establish connection. on success - will open login window.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on connect button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 * @catch in case of connection failure
	 */
	public void connectButton(ActionEvent event) {

		// fields are empty
		if (portField.getText().trim().isEmpty() || hostField.getText().trim().isEmpty())
			incorrectInputTextField.setText("Username or Password field is empty!");

		// send msg to server host name and port and wait for response
		else

		{

			try {
				// try and open new connection to server, in case it fail it will throw an
				// exception.
				ClientUI.clientHandler = new ClientHandler(hostField.getText(),
						(int) Integer.parseInt(portField.getText()));

				// if connection success it will open new login window
				FXMLutil.swapScene(event, window.Login.toString());

			} catch (Exception e) {
				SpecialCalls.customeError("Error", "Connection error", "Please contact your system administrator");
			}

		}
	}
}



