package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.controlsfx.control.CheckComboBox;

import client.ClientHandler;
import client.ClientUI;
import entity.Question;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utils.FXMLutil;
import utils.SpecialCalls;
import utils.stringSplitter;

/**
 * <p>
 * <b>Explanation:</b> The class allows the teacher to compose a question to the data base. the teacher select subject, one or a few courses that the question will be related to.
 * Then, she fill the contact of the question and the answers- one correct and 3 incorrect.
 * <p>
 * The teacher click 'Create' for adding the question to the data base or 'Back' for going back to the teacher menu. 
 * <p>
 * @author OrSteiner
 */
public class ComposeAQuestionTeacherController implements Initializable {

	@FXML
	private ComboBox<String> subjectSelection;

	@FXML
	private CheckComboBox<String> courseSelection;

    @FXML
    private TextArea questionContentTextField;

	@FXML
	private TextField correctAnsTextField;

	@FXML
	private TextField wrongAns1TextField;

	@FXML
	private TextField wrongAns2TextField;

	@FXML
	private TextField wrongAns3TextField;

	@FXML
	private Button cancelButton;

	@FXML
	private Button createButton;

	// Global variables
	private ObservableList<String> SubjectList = FXCollections.observableArrayList();
	private ObservableList<String> CourseList = FXCollections.observableArrayList();

	/**
	 * <p>
	 * <b>Explanation:</b> Initialize the courses according to subject clicked.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the combobox.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void initializeCourses(ActionEvent event) {
		// after subject selected clear the courses box
		courseSelection.getItems().clear();
		// ask server to return courses related to the subject selected
		ClientUI.clientHandler.handleMessageFromClientUI("course:::" + subjectSelection.getValue());
		String str = (String) ClientHandler.returnMessage;
		String[] string = stringSplitter.stringByComma(str);
		CourseList.addAll(string);
		// set new items to show in courses box
		courseSelection.getItems().addAll(CourseList);
		CourseList.removeAll(string);
	}

	/**
	 * <p>
	 * <b>Explanation:</b>  The method try to insert the new question into the database: she get the new question fields and check if those fields are valid.
	 * <p>
	 * If there are any miss fields or for for already exist question it will pop the error screen, if field are valid pop success window.
	 * <p>
	 *	Then, she blank the specific fields that just got into the database (except subject and courses) and pop the success frame.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on 'Create' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void CreateBtnClicked(ActionEvent event) throws IOException {
		// get the new question fields
		ArrayList<String> errorLog = new ArrayList<String>();
		String composer = ClientUI.clientHandler.myDetails.getId();
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
		String correctAnswer = correctAnsTextField.getText();
		if (correctAnswer.isBlank())
			errorLog.add("'Correct answer' field is empty\n");
		String subject = subjectSelection.getValue();
		if (subject == null)
			errorLog.add("A subject must be selected\n");
		String courses = courseSelection.getCheckModel().getCheckedItems().toString();
		courses = courses.substring(1, courses.length() - 1);
		if (courses.isBlank())
			errorLog.add("At least one course must be selected\n");
		// if there are any miss fields pop the error screen
		if (!errorLog.isEmpty())
			SpecialCalls.callErrorFrame(errorLog);
		// check if those fields are valid
		else {
			StringBuilder id = new StringBuilder();
			ClientUI.clientHandler.handleMessageFromClientUI("subjectID:::"+subject);
			id.append((String) ClientHandler.returnMessage);
			ClientUI.clientHandler.handleMessageFromClientUI("questionIDgenerator");
			id.append((String) ClientHandler.returnMessage);
			Question newQuestion = new Question(composer, subject, content, answer1, answer2, answer3, correctAnswer,
					courses, id.toString());
			ClientUI.clientHandler.handleMessageFromClientUI("insertQuestion:::" + newQuestion.toString());
			// if field are valid pop success window or error for already exist question
			String returnMsg = (String) ClientHandler.returnMessage;
			if (returnMsg.equals("success")) {
				// blank the specific fields that just got into the database (except subject and courses)
				questionContentTextField.clear();
				correctAnsTextField.clear();
				wrongAns1TextField.clear();
				wrongAns2TextField.clear();
				wrongAns3TextField.clear();
				// pop the success frame
				SpecialCalls.callSuccessFrame("Question successfuly added");
			} else {
				// return error with the reason the question exist
			}
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b>  Initialize the subjects.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the subjects to choose from
		ClientUI.clientHandler.handleMessageFromClientUI("subject");
		String returnMsg = (String) ClientHandler.returnMessage;
		String[] string = stringSplitter.stringBySpace(returnMsg);
		SubjectList.addAll(string);
		subjectSelection.setItems(SubjectList);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap the scene back to the last scene-teacher menu, when we click the button 'back'.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'back'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void cancelBtnClicked(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.TeacherMenu.toString());
	}
	

}
