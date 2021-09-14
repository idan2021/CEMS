package controller;

import java.io.IOException;
import java.util.ArrayList;
import client.ClientHandler;
import client.ClientUI;
import entity.QuestionInExam;
import entity.StudentExam;
import enums.window;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import utils.FXMLutil;
import utils.SpecialCalls;

/**
 * <p>
 * <b>Explanation:</b> The class is the following frame of 'check and approve exam' to fill the teacher thoughts about the student grade.
 * <p>
 * After the teacher choose a computerize exam, she can see the questions, their right answers and the student answers.
 * <p> 
 * The teacher can write notes to the students about the test, or change his grade with an explenation why she did it.
 * <p>
 * @author OrSteiner
 */
public class CheckAndApproveGradeTeacherController {

	@FXML
	private Text examTitleTxt;

	@FXML
	private TableView<studentAndAnswers> contentTableView;

	@FXML
	private TableColumn<studentAndAnswers, String> questionContentColumn;

	@FXML
	private TableColumn<studentAndAnswers, String> correctAnswerColumn;

	@FXML
	private TableColumn<studentAndAnswers, String> studentAnswerColumn;

	@FXML
	private TextField studentNotesField;

	@FXML
	private Slider studentGradeSlider;

	@FXML
	private TextField studentGradeTxtFld;

	@FXML
	private Button backBtn;

	@FXML
	private Button approveBtn;

	@FXML
	private TextField changeGradeExplenation;

	// Global variables
	private ArrayList<QuestionInExam> questionArray = new ArrayList<QuestionInExam>();
	private StudentExam Se;
	private ArrayList<studentAndAnswers> SAA = new ArrayList<studentAndAnswers>();
	private ObservableList<studentAndAnswers> studentAndAnswersList = FXCollections.observableArrayList();

	/**
	 * <p>
	 * <b>Explanation:</b> The method Initialize the frame via the data that got from the 'check and approve exam' controller:
	 * <p>
	 * She get the questions of the exam, set the exam Title to the student details, initiate the student and answers array to present in the table view
	 * ,set all the desired columns into the TableView, and then initiate the slider and text field of the points the student reached.
	 * <p>
	 * <b>Receive:</b> The method gets all the answers of a student on a computerized exam.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	public void displayExam(StudentExam SE) {
		// get the questions of the exam
		Se = SE;
		ClientUI.clientHandler.handleMessageFromClientUI("getQuestionsInExam:::" + SE.getExamID());
		questionArray = SpecialCalls.callQuestionsInExam((String) ClientHandler.returnMessage);
		// set the exam Title to the student details
		examTitleTxt.setText(
				"The student " + SE.getStudentID() + " got " + SE.getGrade() + " in the exam " + SE.getExamID());
		// initiate the student and answers array to present in the table view
		int i = 0;
		System.out.println(Se.getStudentAnwers()[0]);
		for (QuestionInExam QIE : questionArray) {
			SAA.add(new studentAndAnswers(QIE.getQuestion().getContent(), QIE.getQuestion().getCorrectAnswer(),
					Se.getStudentAnwers()[i]));
			i++;
		}
		studentAndAnswersList.addAll(SAA);
		// set all the desired columns into the TableView
		questionContentColumn
				.setCellValueFactory(new PropertyValueFactory<studentAndAnswers, String>("questionContent"));
		correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<studentAndAnswers, String>("correctAnswer"));
		studentAnswerColumn.setCellValueFactory(new PropertyValueFactory<studentAndAnswers, String>("studentAnswer"));
		contentTableView.setItems(studentAndAnswersList);
		// initiate the slider and text field of the points the student reached
		studentGradeSlider.setValue(Integer.parseInt(Se.getGrade()));
		studentGradeTxtFld.setText(Integer.toString((int) studentGradeSlider.getValue()));
		studentGradeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				studentGradeTxtFld.setText(Integer.toString((int) studentGradeSlider.getValue()));
			}
		});

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method set the slider points according to student grades.
	 * <p>
	 * <b>Receive:</b> The method gets event to set the points.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void gradeChanged(ActionEvent event) {
		studentGradeSlider.setValue(Integer.parseInt(studentGradeTxtFld.getText()));
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This is a private class of the fields that we need on the table view.
	 * <p>
	 * @author OrSteiner
	 */
	public class studentAndAnswers {
		String questionContent;
		String correctAnswer;
		String studentAnswer;

		public studentAndAnswers(String questionContent, String correctAnswer, String studentAnswer) {
			super();
			this.questionContent = questionContent;
			this.correctAnswer = correctAnswer;
			this.studentAnswer = studentAnswer;
		}

		public String getQuestionContent() {
			return questionContent;
		}

		public void setQuestionContent(String questionContent) {
			this.questionContent = questionContent;
		}

		public String getCorrectAnswer() {
			return correctAnswer;
		}

		public void setCorrectAnswer(String correctAnswer) {
			this.correctAnswer = correctAnswer;
		}

		public String getStudentAnswer() {
			return studentAnswer;
		}

		public void setStudentAnswer(String studentAnswer) {
			this.studentAnswer = studentAnswer;
		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method allows to change the grade and to explain why-check if the fields are valid and update the exam in the data base.
	 * <p>
	 * In advance, the teacher can also just  approve the grade.
	 * <p>
	 * <b>Receive:</b> The method gets event 'click' on 'approve'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void approveBtnClicked(ActionEvent event) {
		ArrayList<String> errorLog = new ArrayList<String>();
		String studentNotes = "no_notes";
		String changeExplanation = "no_change";
		if (!studentNotesField.getText().isBlank())
			studentNotes = studentNotesField.getText();
		if (!changeGradeExplenation.getText().isBlank())
			changeExplanation = changeGradeExplenation.getText();
		// in case grade changed and explanation was not inserted
		if (!Se.getGrade().equals(studentGradeTxtFld.getText()) && changeGradeExplenation.getText().isBlank())
			errorLog.add("An explanation for the grade change must be filled");
		// if there are any miss fields pop the error screen
		if (!errorLog.isEmpty())
			SpecialCalls.callErrorFrame(errorLog);
		else {
			ClientUI.clientHandler.handleMessageFromClientUI("teacherGradeUpdate:::" + studentNotes + "%%%"
					+ studentGradeTxtFld.getText() + "%%%" + changeExplanation+"%%%"+Se.getStudentID()+"%%%"+Se.getExamID());
			SpecialCalls.callSuccessFrame("The grade was updated successfully");
			try {
				FXMLutil.swapScene(event, window.CheckAndApproveExam.toString());
			} catch (IOException e) {
				try {
					FXMLutil.swapScene(event, window.TeacherMenu.toString());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method turn back to the check and approve exam scene. 
	 * <p>
	 * <b>Receive:</b> The method gets event 'click' on 'back' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author Or Steiner
	 */
	@FXML
	void backBtnClicked(ActionEvent event) {
		try {
			FXMLutil.swapScene(event, window.CheckAndApproveExam.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
