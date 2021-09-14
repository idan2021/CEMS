package controller;

import java.io.IOException;
import java.util.ArrayList;
import entity.Question;
import entity.QuestionInExam;
import enums.window;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.FXMLutil;
import utils.SpecialCalls;

/**
 * <p>
 * <b>Explanation:</b> The class attachQuestion controller is the second frame of "compose an exam" frame.
 * <p>
 * The class allows the teacher to select a question, set the number of points to the question, and present to the teacher the remaining points for the exam. 
 * <p>
 * The teacher can click 'done' and return to compose an exam frame with the chosen question or 'back' to the same screen without the chosen question.
 * <p>
 * @author OrSteiner
 */
public class AttachQuestionController {

	@FXML
	private TableView<Question> questionTableView;

	@FXML
	private TableColumn<Question, String> subjectColumn;

	@FXML
	private TableColumn<Question, String> coursesColumn;

	@FXML
	private TableColumn<Question, String> contentColumn;

	@FXML
	private TableColumn<Question, String> correctAnswerColumn;

	@FXML
	private Slider setPointsSlider;

	@FXML
	private TextField pointsTxtFld;

	@FXML
	private Button backBtn;

	@FXML
	private Button doneButton;

	@FXML
	private Text remainingPointsText;

	// global variables
	private ObservableList<Question> questionsList = FXCollections.observableArrayList();
	ObservableList<QuestionInExam> questionsInExamList;

	/**
	 * <p>
	 * <b>Explanation:</b> The method gets the questions from the previous screen and present them to the teacher. 
	 * <p>
	 * <b>Receive:</b> The method gets array list of questions and the points the were summed until now for a test.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	public void displayQuestions(ArrayList<Question> questionArrayList, int pointsum,
			ObservableList<QuestionInExam> questionsInExamList) {
		this.questionsInExamList = questionsInExamList;
		questionsList.addAll(questionArrayList);
		// set all the desired columns into the TableView
		subjectColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("subject"));
		coursesColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("courses"));
		contentColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("content"));
		correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("correctAnswer"));
		questionTableView.setItems(questionsList);
		// initialize the points slider
		pointsTxtFld.setText(Integer.toString((int) setPointsSlider.getValue()));
		setPointsSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				pointsTxtFld.setText(Integer.toString((int) setPointsSlider.getValue()));
			}
		});
		remainingPointsText.setText(remainingPointsText.getText() + pointsum);
		setPointsSlider.setMax(pointsum);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method use to communicate with pointsSlider.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on points text field.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void pointsSetOnTxtFld(ActionEvent event) {
		setPointsSlider.setValue(Integer.parseInt(pointsTxtFld.getText()));
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method create a Question in exam object to place in the list of the previous frame.
	 * <p>
	 * She use flag that determines wether question is already in the exam, then she get the selected question 
	 * from table view and if there are any miss fields she pop the error screen.
	 * <p>
	 * In advance, she store all the errors in errorLog
	 * <p>
	 * At the end, if all fields are filled create QuestionInExam object with the user parameters and send them to the previous frame
	 * <p>
	 * <b>Receive:</b> The method gets the even 'click' on done button
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void doneBtnClicked(ActionEvent event) throws IOException {
		// flag that determines wether question is already in the exam
		boolean isInExam = false;
		// store all the errors in errorLog
		ArrayList<String> errorLog = new ArrayList<String>();
		// get the selected question from table view
		Question questionToAttach = questionTableView.getSelectionModel().getSelectedItem();
		if (questionToAttach == null)
			errorLog.add("A question must be selected\n");
		// get the selected points from user
		int points = (int) setPointsSlider.getValue();
		if (points == 0)
			errorLog.add("Points must be setted\n");
		// if there are any miss fields pop the error screen
		if (!errorLog.isEmpty())
			SpecialCalls.callErrorFrame(errorLog);
		else {
			// if question is already in exam turn on flag
			for (int i = 0; i < questionsInExamList.size(); i++)
				if (questionsInExamList.get(i).getQuestionID().equals(questionToAttach.getId()))
					isInExam = true;
			if (isInExam == false) {
				// if all fields are filled create QuestionInExam object with the user
				// parameters and send them to the previous frame
				QuestionInExam questionToSend = new QuestionInExam(questionToAttach, points, " ",
						" ", null);
				sendQuestionToComposeAnExam(questionToSend, event);
			} else {
				SpecialCalls.customeError("Question is already in exam", "Please pick another question.", "");
			}
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method create a call for Compose an exam frame with the question to send.
	 * <p>
	 * <b>Receive:</b> The method gets question to return and the event 'click' on send a question button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	private void sendQuestionToComposeAnExam(QuestionInExam questionToSend, ActionEvent event) throws IOException {
		// create a call for Compose an exam frame with the question to send
		FXMLLoader loader = new FXMLLoader(getClass().getResource(window.ComposeAnExam.toString()));
		Parent root = loader.load();
		ComposeAnExamTeacherController CAEcontroller = loader.getController();
		CAEcontroller.addQuestion(questionToSend);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap the scene back to the last scene-compose an exam, when we click the button 'back'.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'back'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void backBtnClicked(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.ComposeAnExam.toString());
	}

}
