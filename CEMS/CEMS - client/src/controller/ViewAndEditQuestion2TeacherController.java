package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import utils.FXMLutil;
import utils.SpecialCalls;
import java.io.IOException;
import java.util.ArrayList;
import org.controlsfx.control.CheckComboBox;
import client.ClientHandler;
import client.ClientUI;
import entity.Question;
import enums.window;

/**
 * <p>
 * <b>Explanation:</b> The class presents all the question details
 * <p>
 * 
 * @author OrSteiner
 */
public class ViewAndEditQuestion2TeacherController {

	@FXML
	private ComboBox<String> subjectBox;

	@FXML
	private CheckComboBox<String> coursesCheckBoxs;

	@FXML
	private TextField questionContentTextField;

	@FXML
	private TextField correctAnswerTextField;

	@FXML
	private TextField wrongAns1TextField;

	@FXML
	private TextField wrongAns2TextField;

	@FXML
	private TextField wrongAns3TextField;

	@FXML
	private Button backBtn;

	@FXML
	private Button updateButton;

	// Global variables
	public Question questionToEdit;

	// calling this method from scene "viewAndEditQuestion1" with the selected
	private String subject;
	private String courses;

	/**
	 * <p>
	 * <b>Explanation:</b> The method displays the question and her answers
	 * <p>
	 * <b>Receive:</b> The search string the question inputs in the text box
	 * <p>
	 * <b>Return:</b> The search by the question input of details
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	// question
	public void displayQuestion(Question questionToEdit) {
		this.questionToEdit = questionToEdit;
		questionContentTextField.setText(questionToEdit.getContent());
		correctAnswerTextField.setText(questionToEdit.getCorrectAnswer());
		wrongAns1TextField.setText(questionToEdit.getWrongAnswer1());
		wrongAns2TextField.setText(questionToEdit.getWrongAnswer2());
		wrongAns3TextField.setText(questionToEdit.getWrongAnswer3());
		subject = questionToEdit.getSubject();
		courses = questionToEdit.getCourses();
		// subjectBox.setc

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The return of the back button to the principal's menu
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> Principal's menu
	 * <p>
	 * 
	 * @author Or Steiner
	 */
	@FXML
	void backBtnClicked(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.ViewAndEditQuestion1Teacher.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method updates the question and the details in the DB, uses a lot of the blocks from ComposeAQuestionController
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> The method updates in DB and receive a success pop-up
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void updateBtnClickd(ActionEvent event) {
		// get the new question fields
		ArrayList<String> errorLog = new ArrayList<String>();
		String id = questionToEdit.getId();
		String composer = questionToEdit.getComposer();
		String content = questionContentTextField.getText();
		if (content.isBlank())
			errorLog.add("Content field is empty\n");
		String answer1 = wrongAns1TextField.getText();
		if (answer1.isBlank())
			errorLog.add("'Wrong answer 1' field is empty\n");
		String answer2 = wrongAns2TextField.getText();
		if (answer2.isBlank())
			errorLog.add("'Wrong answer 2' field is empty\n");
		String answer3 = wrongAns3TextField.getText();
		if (answer3.isBlank())
			errorLog.add("'Wrong answer 3' field is empty\n");
		String correctAnswer = correctAnswerTextField.getText();
		if (correctAnswer.isBlank())
			errorLog.add("'Correct answer' field is empty\n");
		// if there are any miss fields pop the error screen
		if (!errorLog.isEmpty())
			SpecialCalls.callErrorFrame(errorLog);
		else {
			Question newQuestion = new Question(composer, subject, content, answer1, answer2, answer3, correctAnswer,
					courses, id);
			ClientUI.clientHandler.handleMessageFromClientUI("updateQuestion:::" + newQuestion.toString());
			// if field are valid pop success window or error for already exist question
			String returnMsg = (String) ClientHandler.returnMessage;
			if (returnMsg.equals("success")) {
				SpecialCalls.callSuccessFrame("Question successfuly updated");
			} else {
				// return error with the reason the question exist
			}
		}

	}

}
