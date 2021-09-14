package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import client.ClientUI;
import enums.window;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import utils.FXMLutil;

/**
 * <p>
 * <b>Explanation:</b> Student menu, get a copy of exam, view grdes, peform an exam scenes. 
 * <p>
 * @author Everyonev
 */
public class StudentMenuController implements Initializable {

	@FXML
	private Text userNameText;

    @FXML
    private ImageView examsButton;

    @FXML
    private ImageView gradesButton;

    @FXML
    private Button logOutButton;

	@FXML
	private AnchorPane gradesMenu;

	@FXML
	private Button viewGradeButton;

	@FXML
	private AnchorPane examsMenu;

	@FXML
	private Button performAnExamButton;

	@FXML
	private Button getACopyOfExamButton;
    
    @FXML
    private ImageView gradesOnHoverButton;
    
    @FXML
    private ImageView examsOnHoverButton;
	
	//Global variables
	final Tooltip viewGradeToolTip = new Tooltip();
	final Tooltip performAnExamToolTip = new Tooltip();
	final Tooltip getACopyOfExamToolTip = new Tooltip();

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'view grades' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'view grades' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author Everyone
	 */
	@FXML
	void SwapToViewGradeFrame(ActionEvent event) {
		try {
			FXMLutil.swapScene(event, window.ViewGradesStudent.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Perform An Exam' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Perform An Exam' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author Everyone
	 */
	@FXML
	void SwapToPerformAnExamFrame(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.PerformAnExam.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Get A Copy Of Exam' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Get A Copy Of Exam' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author Everyone
	 */
	@FXML
	void SwapToGetACopyOfExamFrame(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.GetACopyOfExam.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method initialize simple menu navigation.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author Everyone
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		userNameText.setText("Welcome " + ClientUI.clientHandler.myDetails.getFirstLast());
		// click "exams" to open statistics menu
		examsButton.setOnMouseClicked(event -> {
			examsButton.setVisible(false);
			examsOnHoverButton.setVisible(true);
			gradesButton.setVisible(true);
			gradesOnHoverButton.setVisible(false);
			examsMenu.setVisible(true);
			gradesMenu.setVisible(false);
		});

		// click "grades" to open exam menu
		gradesButton.setOnMouseClicked(event -> {
			examsButton.setVisible(true);
			examsOnHoverButton.setVisible(false);
			gradesButton.setVisible(false);
			gradesOnHoverButton.setVisible(true);
			examsMenu.setVisible(false);
			gradesMenu.setVisible(true);
		});
		// function exactly as exit button, save to duplicate code
		logOutButton.setOnAction(event -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("You are about to disconnect from server");
			alert.setHeaderText("Any unsaved changes will be deleted");
			alert.setContentText("Click OK to disconnect and Cancel otherwise");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {

				try {
					// update the server user is about to disconnect
					ClientUI.clientHandler.sendToServer("disconnect");

					// disconenct by closing the connection
					ClientUI.clientHandler.closeConnection();

					// return to login window
					FXMLutil.swapScene(event, window.Login.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		//set tool tips to the in menu selections
		viewGradeToolTip.setText("- View your grades before and after your teacher approval");
		viewGradeButton.setTooltip(viewGradeToolTip);
		performAnExamToolTip.setText("- Enter the code received from your teacher to start an exam");
		performAnExamButton.setTooltip(performAnExamToolTip);
		getACopyOfExamToolTip.setText("- Download your checked exam (manual exam)\n- View you exam answers (computerized exam)");
		getACopyOfExamButton.setTooltip(getACopyOfExamToolTip);
	}

}
