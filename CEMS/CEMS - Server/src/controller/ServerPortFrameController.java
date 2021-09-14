package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import Server.ServerHandler;


public class ServerPortFrameController implements Initializable {
	private ServerHandler echoServer;
	@FXML
	private Button btnConnect;
	@FXML
	private Text numOfClientLog;
	@FXML
	private TextField portxt;

	@FXML
	private Button btnExit;

	@FXML
	private Label portName;

	@FXML
	private TextArea clientConnectedLog;

	@FXML
	private Button btnDisconnect;

	@FXML
	private TextArea txtLog;

	ObservableList<String> list;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		txtLog.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER)
				btnConnect.fire();
		
		});
                               
                                          
	 
		 
	    }
	private String getport() {
		return portxt.getText();
	}

	@FXML
	void connectToServer(ActionEvent event) {
		String p;

		p = getport();
		if (p.trim().isEmpty()) {
			printToLog("You must enter a port number");
		} else {
		
			this.echoServer = ServerHandler.getInstance(Integer.valueOf(p), this);
			this.echoServer.startListening();
		}
	}

	/**
	 * @author DvirVahav
	 * @apiNote Disconnect without exiting the window
	 * @param event
	 * @throws IOException
	 * @throws SQLException
	 */

	@SuppressWarnings("static-access")
	@FXML
	void disconnectFromServer(ActionEvent event) throws IOException, SQLException {
		this.echoServer.DBconnect.close();
		this.echoServer.close();

	}

	public void numOfClientLog(String text) {
		numOfClientLog.setText(text);
	}

	public void printToLog(String text) {
		txtLog.appendText(text + "\n");
	}

	public void printToclientConnectedTextField(String text) {
		clientConnectedLog.appendText(text + "\n");
	}

	/**
	 * Exit window with warning
	 * 
	 * @author DvirVahav
	 * @param event
	 * @throws Exception
	 */
	public void getExitBtn(ActionEvent event) throws Exception {

		if (this.echoServer != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("You are about to disconnect the server");
			alert.setHeaderText("Clients connected to server will be disconnected");
			alert.setContentText("Click OK to approve and Cancel otherwise");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				btnDisconnect.fire();
				System.exit(0);
			} else {
				event.consume();
			}
		} else
			System.exit(0);

	}

}


