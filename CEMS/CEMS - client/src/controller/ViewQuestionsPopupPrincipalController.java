package controller;

import java.io.IOException;

import entity.Question;
import enums.window;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utils.FXMLutil;

/**
 * <p>
 * <b>Explanation:</b> View question window - for principal receive information
 * from previous window - ViewQuestionsPrincipal
 * <p>
 * 
 * @author DvirVahab
 */
public class ViewQuestionsPopupPrincipalController {

	@FXML
	private TextField  composerIDText;

	@FXML
	private TextArea questionContnetTextField;

	@FXML
	private TextField  questionIDText;

	@FXML
	private TextArea wrongAnswer1TextField;

	@FXML
	private TextArea correctAnswerTextField;

	@FXML
	private TextArea wrongAnswer2TextField;

	@FXML
	private TextArea wrongAnswer3TextField;

	@FXML
	private TextField  coursesText;

	@FXML
	private TextField  subjectText;

	@FXML
	private Button backButton;

	/**
	 * <p>
	 * <b>Explanation:</b> The method presents the question details by the question
	 * input
	 * <p>
	 * <b>Receive:</b> Question type question
	 * <p>
	 * <b>Return:</b> The question details to be presented
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public void displayQuestion(Question q) {

		coursesText.setText(q.getCourses());
		subjectText.setText(q.getSubject());
		questionIDText.setText(q.getId());
		composerIDText.setText(q.getComposer());
		questionContnetTextField.setText(q.getContent());
		correctAnswerTextField.setText(q.getCorrectAnswer());
		wrongAnswer1TextField.setText(q.getWrongAnswer1());
		wrongAnswer2TextField.setText(q.getWrongAnswer2());
		wrongAnswer3TextField.setText(q.getWrongAnswer3());
	
	

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The return of the back button to the presentation of the
	 * questions list
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> View questions as principal
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	@FXML
	void backButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.viewQuestionsPrincipal.toString());

	}
}



