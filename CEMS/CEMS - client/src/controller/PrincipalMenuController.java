package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import client.ClientHandler;
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
import utils.SpecialCalls;
/**
 * <p>
 * <b>Explanation:</b> Principal menu, view grades,exams,questions,courses,user, copy of exams, exam statistics & exam duration approval.
 * <p>
 * @author DvirVahav
 */
public class PrincipalMenuController implements Initializable {

    @FXML
    private Text welcomeTitle;

    @FXML
    private ImageView examsButton;

    @FXML
    private ImageView examsMark;

    @FXML
    private ImageView viewDataButton;

    @FXML
    private Button logOutButton;
    
    @FXML
    private AnchorPane ViewDataAnchorPane;

    @FXML
    private Button viewExamsButton;

    @FXML
    private Button getACopyOfExamButton;

    @FXML
    private Button viewQuestionButton;

    @FXML
    private Button viewUsersButton;

    @FXML
    private Button viewCoursesButton;

    @FXML
    private Button viewGradeInfoButton;

    @FXML
    private AnchorPane examsAnchorPane;

    @FXML
    private Button examDurationApprovalButton;

    @FXML
    private Button examStatisticsButton;
    
    @FXML
	private ImageView durationApprovalMark;
    
    @FXML
    private ImageView examsOnHoverButton;
    
    @FXML
    private ImageView viewDataOnHoverButton;
    

	
	//Global variables
	final Tooltip tatisticAnalysisToolTip = new Tooltip();
	final Tooltip viewQuestionToolTip = new Tooltip();
	final Tooltip viewUsersToolTip = new Tooltip();
	final Tooltip getACopyOfExamToolTip = new Tooltip();
	final Tooltip viewExamsToolTip = new Tooltip();
	final Tooltip viewGradeInfoToolTip = new Tooltip();
	final Tooltip examDurationApprovalToolTip = new Tooltip();

	/**
	 * <p>
	 * <b>Explanation:</b> The method operate the buttons and initializes all the test that are waiting for an extension of time.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		welcomeTitle.setText("Welcome " + ClientUI.clientHandler.myDetails.getFirstLast());
		// click "viewData" to open exam menu
		viewDataButton.setOnMouseClicked(event -> {
			viewDataButton.setVisible(false);
			viewDataOnHoverButton.setVisible(true);
			examsButton.setVisible(true);
			examsOnHoverButton.setVisible(false);
			examsAnchorPane.setVisible(false);
			ViewDataAnchorPane.setVisible(true);
		});
		examsButton.setOnMouseClicked(event -> {
			viewDataButton.setVisible(true);
			viewDataOnHoverButton.setVisible(false);
			examsButton.setVisible(false);
			examsOnHoverButton.setVisible(true);
			examsAnchorPane.setVisible(true);
			ViewDataAnchorPane.setVisible(false);
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

					// disconnect by closing the connection
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
		tatisticAnalysisToolTip.setText("- Watch general statistic information");
		examStatisticsButton.setTooltip(tatisticAnalysisToolTip);
		viewQuestionToolTip.setText("- Watch any question available in the Database");
		viewQuestionButton.setTooltip(viewQuestionToolTip);
		viewUsersToolTip.setText("- Watch any user exist in the Database");
		viewUsersButton.setTooltip(viewUsersToolTip);
		getACopyOfExamToolTip.setText("- Get a copy of an exam in the Database");
		getACopyOfExamButton.setTooltip(getACopyOfExamToolTip);
		viewExamsToolTip.setText("- Watch any exam available in the Database");
		viewExamsButton.setTooltip(viewExamsToolTip);
		viewGradeInfoToolTip.setText("- Watch all of the student grades available in the Database");
		viewGradeInfoButton.setTooltip(viewGradeInfoToolTip);
		examDurationApprovalToolTip.setText("- Approve teachers request to extend running exams");
		examDurationApprovalButton.setTooltip(examDurationApprovalToolTip);
		
		//set question mark warnings for new exams awaiting duration approval on/off.
		ClientUI.clientHandler.handleMessageFromClientUI("examsAwaitingApproval:::");
		if(SpecialCalls.callExamsAwaitingApproval((String) ClientHandler.returnMessage).isEmpty()) {
			examsMark.setVisible(false);
			durationApprovalMark.setVisible(false);
		}
		else {
			examsMark.setVisible(true);
			durationApprovalMark.setVisible(true);
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'view Questions' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'view Questions' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void viewQuestionButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.viewQuestionsPrincipal.toString());
	}
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Exam Statistics' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Exam Statistics' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void examStatisticsClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.ExamStatistics.toString());
	}
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'view exams' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'view exams' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void viewExamsButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.viewExamsPrincipal.toString());
	}
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'view users' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'view users' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void viewUsersButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.viewUsersPrincipal.toString());
	}
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'view Courses' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'view Courses' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void viewCoursesButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.viewCoursesPrincipal.toString());
	}
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'get A Copy Of Exam' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'get A Copy Of Exam' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void getACopyOfExamButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.getACopyOfExamPrincipal.toString());
	}
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'View Grades Principal' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'View Grades' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void viewGradeInfoButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.ViewGradesPrincipal.toString());
	}
	
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'exam duration approval' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'exam duration approval' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
    @FXML
    void swapToExamDurationApproval(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.ExamDurationApproval.toString());
    }
    

}



