package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import enums.window;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.FXMLutil;
import utils.entityCreator;



/**
 * <p>
 * <b>Displayed:</b> Login - user will type in user name and password and click
 * login.
 * <p>
 * <b>Method:</b> "sendRequestToServer" will take the prompt string received and
 * check in server if its exist + role.
 * <p>
 * <b>Next window:</b> Appropriate window will appear by user role
 * ->(Teacher\Student\Principal)
 * <p>
 * <b>Failed prompt:</b> Will present "password\ user incorrect" at login
 * screen.
 * @author DvirVahav
 */

public class LoginController implements Initializable {

	@FXML
	private TextField usernameField;

	@FXML
	private Button loginButton;

	@FXML
	private TextField passwordField;

	@FXML
	private Text incorrectInputTextField;
	
    @FXML
    private ImageView ImageSlide;
    
    @FXML
    private VBox loginVbox;

	@Override
	/**
	 * <p>
	 * <b>Explanation:</b> The method initialize listeners to textFeilds in case user type "ENTER" so it will try to login.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	public void initialize(URL location, ResourceBundle resources) {
		// loginButton = new Button();
		usernameField.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER)
				loginButton.fire();
		});

		passwordField.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER)
				loginButton.fire();
		});

		usernameField.setOnKeyTyped(KeyEvent -> {
			incorrectInputTextField.setText(null);
		});

		passwordField.setOnKeyTyped(KeyEvent -> {
			incorrectInputTextField.setText(null);
		});
		
		ImageSlide.toBack();
		TranslateTransition slide = new TranslateTransition();
		slide.setDuration(Duration.seconds(20.0));
		slide.setNode(ImageSlide);
		slide.setToX(-200);
		slide.play();
		loginVbox.toFront();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> Event click on Login Sends string with user and password details to server in case its incorrect,
	 *  text field is updated accordingly showing the user exactly what he need to change.
	 * <p>
	 * <b>Receive:</b> The method get event 'click' on the 'Login' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	public void loginButtonClicked(ActionEvent event) {
		if (usernameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()) {
			incorrectInputTextField.setText("Username or Password field is empty!");
		} else {

			// check DB for user name and password
			StringBuilder loginString = new StringBuilder();
			loginString.append("login:::" + usernameField.getText() + ":::" + passwordField.getText());
			ClientUI.clientHandler.handleMessageFromClientUI(loginString.toString());
			String returnMsg = (String) ClientHandler.returnMessage;
			String[] returnMsgArr = returnMsg.split(":::");
			//// user name or password are incorrect
			if (!returnMsgArr[0].equals("successfull"))
				incorrectInputTextField.setText(returnMsg);
			// details are correct, open new Menu(Teacher\Student\Principal
			else {
			
				switch (returnMsgArr[7]) {
				case "Student":
					ClientUI.clientHandler.myDetails = entityCreator.studentCreator(returnMsgArr);
					try {
						FXMLutil.swapScene(event, window.StudentMenu.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				case "Teacher":
					ClientUI.clientHandler.myDetails = entityCreator.teacherCreator(returnMsgArr);
					try {
						FXMLutil.swapScene(event, window.TeacherMenu.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				case "Principal":

					ClientUI.clientHandler.myDetails = entityCreator.principalCreator(returnMsgArr);
					try {
						FXMLutil.swapScene(event, window.PrincipalMenu.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
				}
				
			}

		}
	}

}



