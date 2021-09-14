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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import utils.FXMLutil;
import utils.SpecialCalls;
/**
 * <p>
 * <b>Explanation:</b> teacher menu, include every buttons to the scenes that related to the teacher.
 * <p>
 * @author OrSteiner
 */
public class TeacherMenuController implements Initializable {

	@FXML
	private BorderPane mainStage;

	@FXML
	private Text WelcomeUserName;


    @FXML
    private ImageView statisticsBtn;

    @FXML
    private ImageView questionsBtn;

    @FXML
    private Button logOutButton;

    @FXML
    private ImageView examsBtn;

	@FXML
	private VBox examMenu;
	
	@FXML
	private Button checkAndApproveManualExamButton;
	
	@FXML
	private Button ComposeExamBtn;

	@FXML
	private Button CheckNApproveExamsBtn;

	@FXML
	private Button startExamBtn;

	@FXML
	private Button currentExamBtn;

	@FXML
	private Button examLibraryBtn;

	@FXML
	private VBox statisticsMenu;

	@FXML
	private Button StatisticAnalysisBtn;

	@FXML
	private VBox questionMenu;

	@FXML
	private Button composeQuestionBtn;

	@FXML
	private Button EditNViewQeustionBtn;

	@FXML
	private Button UploadManualExam;
	
    @FXML
    private ImageView statisticsOnHoverBtn;

    @FXML
    private ImageView questionsOnHoverBtn;

    @FXML
    private ImageView examsOnHoverBtn;
    

	// Global variables
	final Tooltip currentExamToolTip = new Tooltip();
	final Tooltip checkAndApprove = new Tooltip();
	final Tooltip composeExam = new Tooltip();
	final Tooltip composeQuestion = new Tooltip();
	final Tooltip editAndView = new Tooltip();
	final Tooltip startExam = new Tooltip();
	final Tooltip statisticAnalysis = new Tooltip();
	final Tooltip uploadManualExam = new Tooltip();

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Current Exam' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Current Exam' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void SwapToCurrentExamFrame(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.CurrentExam.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Check And Approve Exam' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Check And Approve Exam' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void swapToCheckNApproveExamsFrame(ActionEvent event) throws IOException {
		// add to window.java
		FXMLutil.swapScene(event, window.CheckAndApproveExam.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Compose An Exam' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Compose An Exam' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void swapToComposeExamFrame(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.ComposeAnExam.toString());
	}

	// click "questions" -> "compose question" to open compose question window
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Compose A Question' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Compose A Question' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void swapToComposeQuestionFrame(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.ComposeAQuestionTeacher.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'View And Edit Question1' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'View And Edit Question1' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void swapToEditNViewQeustionFrame(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.ViewAndEditQuestion1Teacher.toString());
	}
	
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Exams Library' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Exams Library' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void swapToExamLibraryFrame(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.ExamsLibrary.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Start Exam Window' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Start Exam Window' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void swapToStartExamFrame(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.StartExamWindowTeacher.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Statistics Analysis' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Statistics Analysis' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void swapToStatisticAnalysisFrame(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.StatisticsAnalysisTeacher.toString());
	}
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Upload Manual Exam' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Upload Manual Exam' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void swapToUploadManualExamrame(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.UploadManualExam.toString());
	}
	
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap scene to 'Check And Approve Manual Exam' scene.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'Check And Approve Manual Exam' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	   @FXML
	    void checkAndApproveManualExamButtonClick(ActionEvent event) throws IOException {
		   FXMLutil.swapScene(event, window.CheckAndApproveManualExam.toString());
	    }
		/**
		 * <p>
		 * <b>Explanation:</b> The method initialize simple menu navigation.
		 * <p>
		 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
		 * <p>
		 * <b>Return:</b> The method does not return anything. 
		 * <p>
		 * @author OrSteiner
		 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// set the upper panel the correct user
		// --should be changed by the user name

		WelcomeUserName.setText("Welcome " + ClientUI.clientHandler.myDetails.getFirstLast());
		// click "statistics" to open statistics menu
		statisticsBtn.setOnMouseClicked(event -> {
			examsBtn.setVisible(true);
			examsOnHoverBtn.setVisible(false);
			questionsBtn.setVisible(true);
			questionsOnHoverBtn.setVisible(false);
			statisticsBtn.setVisible(false);
			statisticsOnHoverBtn.setVisible(true);
			updateAvgAndMEdianInDataBase();
			examMenu.setVisible(false);
			questionMenu.setVisible(false);
			statisticsMenu.setVisible(true);
		});
		
		// click "exams" to open exam menu
		examsBtn.setOnMouseClicked(event -> {
			examsBtn.setVisible(false);
			examsOnHoverBtn.setVisible(true);
			questionsBtn.setVisible(true);
			questionsOnHoverBtn.setVisible(false);
			statisticsBtn.setVisible(true);
			statisticsOnHoverBtn.setVisible(false);
			statisticsMenu.setVisible(false);
			questionMenu.setVisible(false);
			examMenu.setVisible(true);
		});
		// click "questions" to open question menu
		questionsBtn.setOnMouseClicked(event -> {
			examsBtn.setVisible(true);
			examsOnHoverBtn.setVisible(false);
			questionsBtn.setVisible(false);
			questionsOnHoverBtn.setVisible(true);
			statisticsBtn.setVisible(true);
			statisticsOnHoverBtn.setVisible(false);
			statisticsMenu.setVisible(false);
			examMenu.setVisible(false);
			questionMenu.setVisible(true);
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

		// set tool tips to the in menu selections
		currentExamToolTip
				.setText("- Watch running exams\n- Lock exam\n- Ask the principal to extend the exam duration");
		currentExamBtn.setTooltip(currentExamToolTip);
		checkAndApprove
				.setText("- Approve computerized exams grades\n- If there is any suspicous forms we'll let you know");
		CheckNApproveExamsBtn.setTooltip(checkAndApprove);
		composeExam.setText("- Compose a computerized exam using questions from CEMS Data Base");
		ComposeExamBtn.setTooltip(composeExam);
		composeQuestion.setText("- Create a question to use in future exams");
		composeQuestionBtn.setTooltip(composeQuestion);
		editAndView.setText("- View or Edit a question you created before");
		EditNViewQeustionBtn.setTooltip(editAndView);
		startExam.setText("- Start an exam by giving a 4 digit code to send the students");
		startExamBtn.setTooltip(startExam);
		uploadManualExam.setText("- Compose a manual exam by uploading exam file and attach details");
		UploadManualExam.setTooltip(uploadManualExam);
	}
	/**
	 * <p>
	 * <b>Explanation:</b> The method update the statistics avg and nedian in the data base
	 * <p>
	 * <b>Receive:</b> The method does not get anything. 
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	private void updateAvgAndMEdianInDataBase() {
		ClientUI.clientHandler.handleMessageFromClientUI("updateAvgAndMedian:::");
		String response = (String) ClientHandler.returnMessage;
		if (response.equals("fail"))
			SpecialCalls.customeError("Error", "Error updaing statistics data",
					"Please contact you system administartor");

	}

}
