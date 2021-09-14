package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ClientUI;
import entity.Exam;
import entity.QuestionInExam;
import entity.User.Role;
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

/**
 * <p>
 * <b>Explanation:</b> The class presents a pop-up for exam details
 * <p>
 * 
 * @author DvirVahab
 */
public class ViewExamsPopupPrincipalController implements Initializable {
	@FXML
	private TextField composerIDText;
	@FXML
	private TextField questionIDText;

	@FXML
	private TextArea questionContnetTextField;

	@FXML
	private TextArea wrongAnswer1TextField;

	@FXML
	private TextArea correctAnswerTextField;

	@FXML
	private TextArea wrongAnswer2TextField;

	@FXML
	private TextArea wrongAnswer3TextField;

	@FXML
	private TextField coursesText;

	@FXML
	private TextField subjectText;

	@FXML
	private Button backButton;

	@FXML
	private ComboBox<Integer> questionNumberComboBox;

	@FXML
	private TextField questionPointText;

	@FXML
	private TextField examIDText;

	@FXML
	private TextField examDateText;

	@FXML
	private TextField examDurationText;

	@FXML
	private TextField examComposerIDText;

	@FXML
	private TextField examSubjectText;

	@FXML
	private TextField examCourseText;
	private ArrayList<QuestionInExam> thisExam;
	private ArrayList<Integer> index;
	
	/**
	 * <p>
	 * <b>Explanation:</b> The return of the back button to view exams if the user
	 * is principal and exams library if the user is teacher
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> To the view exams if it is principal, if it is a teacher the
	 * back returns to exams library
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	@FXML
	void backButtonClick(ActionEvent event) {
		try {
			if(ClientUI.clientHandler.myDetails.getRole().equals(Role.principal))
				FXMLutil.swapScene(event, window.viewExamsPrincipal.toString());
				if(ClientUI.clientHandler.myDetails.getRole().equals(Role.teacher))
				FXMLutil.swapScene(event, window.ExamsLibrary.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			SpecialCalls.customeError("Error", "Error returning back", e.getMessage());
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method is extracting the exam data and displays it
	 * <p>
	 * <b>Receive:</b> The user clicks the exam he wants
	 * <p>
	 * <b>Return:</b> The exam data display
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public void displayExam(Exam e) {
		index=new ArrayList<>();
		
		// set comboxBox indexes by question length
		thisExam = e.getQuestions();
		
		//Initialize comboBox
		for(int i=0;i<thisExam.size();i++)
			index.add(i+1);
		
		ObservableList<Integer> indexOL = FXCollections.observableArrayList();
		indexOL.setAll(index);
		questionNumberComboBox.setItems(indexOL);
		
		// exam Details filled.
		examIDText.setText(e.getId());
		examDateText.setText(e.getDateOfExam());
		examDurationText.setText(e.getTimeInMinutes());
		examComposerIDText.setText(e.getAuthor());
		examCourseText.setText(e.getCourse());
		examSubjectText.setText(e.getSubject());

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method presents the exams details by the selected
	 * exam from the screen before
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path
	 * for the root object and the resources that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The details about the exam to display
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		questionNumberComboBox.setOnAction(event -> {
			             
			// question details
			questionPointText.setText(String.valueOf(thisExam.get(questionNumberComboBox.getValue()-1).getPoints()));
			questionIDText.setText(thisExam.get(questionNumberComboBox.getValue()-1).getQuestionID());
			composerIDText.setText(thisExam.get(questionNumberComboBox.getValue()-1).getQuestion().getComposer());
			subjectText.setText(thisExam.get(questionNumberComboBox.getValue()-1).getQuestion().getSubject());
			coursesText.setText(thisExam.get(questionNumberComboBox.getValue()-1).getQuestion().getCourses());

			// question itself
			questionContnetTextField.setText(thisExam.get(questionNumberComboBox.getValue()-1).getQuestionContent());
			wrongAnswer1TextField.setText(thisExam.get(questionNumberComboBox.getValue()-1).getQuestion().getWrongAnswer1());
			wrongAnswer2TextField.setText(thisExam.get(questionNumberComboBox.getValue()-1).getQuestion().getWrongAnswer2());
			wrongAnswer3TextField.setText(thisExam.get(questionNumberComboBox.getValue()-1).getQuestion().getWrongAnswer3());
			correctAnswerTextField.setText(thisExam.get(questionNumberComboBox.getValue()-1).getQuestion().getCorrectAnswer());

		});
	}

}



