package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import client.ClientHandler;
import client.ClientUI;
import entity.QuestionInExam;
import enums.window;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import utils.Calculators;
import utils.FXMLutil;
import utils.SpecialCalls;
import utils.stringSplitter;
/**
 * <p>
 * <b>Explanation:</b> The class computerize exam is the exam of the student. the student can see the one question from the test and move to next\previous question by the arrows.
 * <p>
 * Each question has 4 answers. The student pick one of the answers.
 * <p>
 * The student can see the notes from the teacher, the timer of the test, and the number of the questions in the test with the number of the current question. 
 * <p>
 * @author OrSteiner
 */
public class ComputerizedExamStudentController {

	@FXML
	private BorderPane mainStage;

	@FXML
	private TextArea noteForStudentsTxt;

	@FXML
	private Text questionContent;

	@FXML
	private RadioButton answer1Txt;

	@FXML
	private RadioButton answer2Txt;

	@FXML
	private RadioButton answer3Txt;

	@FXML
	private RadioButton answer4Txt;

	@FXML
	private Button examDoneBtn;

	@FXML
	private ImageView nextQuestionImg;

	@FXML
	private Text numberOfQuestionTxt;

	@FXML
	private ImageView previousQuestionImg;

	@FXML
	private Text timerTxt;

	@FXML
	private Button backToMainMenuBtn;

	// Global variables
	private static int questionNumber = 0;
	private static String[] answersArray;
	private ArrayList<QuestionInExam> QIEarrayList = new ArrayList<QuestionInExam>();
	public String caseOfSubmission = "";
	private String examId;
	// timer parameters
	private int seconds = 0;
	private int minutes = 0;
	private int hours = 0;
	private String timeString;
	private boolean flag = true;

	// Calculate and present the time passed
	private Timer timer = new Timer();
	private TimerTask task = new TimerTask() {
		
		/**
		 * <p>
		 * <b>Explanation:</b> The method run is the logic of the timer.
		 * <p>
		 * <b>Receive:</b> The method does not receive anything.
		 * <p>
		 * <b>Return:</b> The method does not return anything.
		 * <p>
		 * @author OrSteiner
		 */
		public void run() {
			if (minutes == 0 && hours > 0) {
				hours--;
				minutes = 60;
			}
			if (seconds == 0 && minutes > 0) {
				minutes--;
				seconds = 60;
				ClientUI.clientHandler.handleMessageFromClientUI("getExamChanges:::" + examId);
				String[] respond = stringSplitter.dollarSplit((String) ClientHandler.returnMessage);
				if (respond[0].equals("approved") && flag) {
					flag = false;
					updateClock(respond[1]);
				}
				if (respond[0].equals("locked")) {
					hours = 0;
					minutes = 0;
					seconds = 1;
				}
			}
			seconds--;
			timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
			timerTxt.setText(timeString);
			if (hours == 0 && minutes == 0 && seconds == 0)
				exemTimeOver();
		}

	};

	/**
	 * <p>
	 * <b>Explanation:</b> The timer method is used to submit the exam because time's over
	 * <p>
	 * <b>Receive:</b> The method does not receive anything.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	public void exemTimeOver() {
		caseOfSubmission = "Exam Time Is Over";
		examDoneClicked(new ActionEvent());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method is used to add time to the timer.
	 * <p>
	 * <b>Receive:</b> The method gets the time to add that the principal approved.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	private void updateClock(String timeToAdd) {
		int TTA = Integer.parseInt(timeToAdd);
		hours += TTA / 60;
		minutes += TTA % 60;
		if (minutes > 60) {
			hours++;
			minutes -= 60;
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method initiate our exam form (happens once in exam): arrows and question number text, answersArray, the question and the timer to the exam duration. 
	 * <p>
	 * <b>Receive:</b> The method gets the examID.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	public void initializeExam(String examID) {
		// ask the server to return the questions in the exam
		examId = examID;
		ClientUI.clientHandler.handleMessageFromClientUI("getQuestionsInExam:::" + examID);
		QIEarrayList = SpecialCalls.callQuestionsInExam((String) ClientHandler.returnMessage);
		// special case: if there is only one question in the exam
		if (QIEarrayList.size() == 1) {
			previousQuestionImg.setVisible(false);
			nextQuestionImg.setVisible(false);
			examDoneBtn.setVisible(true);
		}
		// Initialize arrows and question number text
		previousQuestionImg.setVisible(false);
		numberOfQuestionTxt.setText("1 of " + QIEarrayList.size());
		// Initialize answersArray
		answersArray = new String[QIEarrayList.size()];
		for (int i = 0; i < QIEarrayList.size(); i++)
			answersArray[i] = "0";
		// Initialize the question
		presentQuestion(QIEarrayList.get(questionNumber), questionNumber);
		// Initialize the timer to the exam duration
		ClientUI.clientHandler.handleMessageFromClientUI("getExamDuration:::" + examID);
		String examDuration = (String) ClientHandler.returnMessage;
		minutes = Integer.parseInt(examDuration);
		while (minutes >= 60) {
			hours++;
			minutes -= 60;
		}
		timer.scheduleAtFixedRate(task, 1000, 1000);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method present the question and manage the answers the user selected: she set all the relevant data to the frame and randomize the order of the answers.
	 * <p>
	 * Then, the method use the answerArray to place the selected answer of the user (remember user answers)
	 * <p>
	 * <b>Receive:</b> The method gets an array of questions and the number of the question to present.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	public void presentQuestion(QuestionInExam question, int questionNumber) {
		// set all the relevant data to the frame
		// randomize the order of the answers
		String[] answersOrder = Calculators.calculateAnswersOrder(question.getQuestion().getCorrectAnswer(),
				question.getQuestion().getWrongAnswer1(), question.getQuestion().getWrongAnswer2(),
				question.getQuestion().getWrongAnswer3());
		questionContent.setText(question.getQuestion().getContent() + " (" + question.getPoints() + " Points)");
		answer1Txt.setText(answersOrder[0]);
		answer2Txt.setText(answersOrder[1]);
		answer3Txt.setText(answersOrder[2]);
		answer4Txt.setText(answersOrder[3]);
		noteForStudentsTxt.setText(question.getStudentComment());

		// use the answerArray to place the selected answer of the user (remember user
		// answers)
		if (answersArray[questionNumber].equals("0")) {
			clearSelectionExept(5);
		} else {
			if (answersArray[questionNumber] == answer1Txt.getText()) {
				clearSelectionExept(5);
				answer1Txt.setSelected(true);
			}
			if (answersArray[questionNumber] == answer2Txt.getText()) {
				clearSelectionExept(5);
				answer2Txt.setSelected(true);
			}
			if (answersArray[questionNumber] == answer3Txt.getText()) {
				clearSelectionExept(5);
				answer3Txt.setSelected(true);
			}
			if (answersArray[questionNumber] == answer4Txt.getText()) {
				clearSelectionExept(5);
				answer4Txt.setSelected(true);
			}
		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method is used to set new question by clicking the right arrow.
	 * <p>
	 * <b>Receive:</b> The method gets the event click on the picture of 'arrow'.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void presentNextQuestion(MouseEvent event) {
		questionNumber++;
		if (questionNumber > 0)
			previousQuestionImg.setVisible(true);
		numberOfQuestionTxt.setText((questionNumber + 1) + " of " + QIEarrayList.size());
		if (questionNumber + 1 == QIEarrayList.size()) {
			nextQuestionImg.setVisible(false);
			examDoneBtn.setVisible(true);
		}
		presentQuestion(QIEarrayList.get(questionNumber), questionNumber);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method is used to set previous question by clicking the left arrow.
	 * <p>
	 * <b>Receive:</b> The method gets the event click on the picture of 'arrow'.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void presentPreviousQuestion(MouseEvent event) {
		questionNumber--;
		if (questionNumber == 0)
			previousQuestionImg.setVisible(false);
		nextQuestionImg.setVisible(true);
		examDoneBtn.setVisible(false);
		if (questionNumber < QIEarrayList.size()) {
			numberOfQuestionTxt.setText((questionNumber + 1) + " of " + QIEarrayList.size());
			presentQuestion(QIEarrayList.get(questionNumber), questionNumber);
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method clears all the text beside the user selection and save his answers at appropriate place. 
	 * <p>
	 * <b>Receive:</b> The method gets the event click on radio button 1.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void answer1Selected(ActionEvent event) {
		// clear other options (same for answerSelected 2-4)
		clearSelectionExept(1);
		// save the answer of the student at the question number spot in the
		// answersArray (same for answerSelected 2-4)
		answersArray[questionNumber] = answer1Txt.getText();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method clears all the text beside the user selection and save his answers at appropriate place. 
	 * <p>
	 * <b>Receive:</b> The method gets the event click on radio button 2.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void answer2Selected(ActionEvent event) {
		clearSelectionExept(2);
		answersArray[questionNumber] = answer2Txt.getText();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method clears all the text beside the user selection and save his answers at appropriate place. 
	 * <p>
	 * <b>Receive:</b> The method gets the event click on radio button 3.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void answer3Selected(ActionEvent event) {
		clearSelectionExept(3);
		answersArray[questionNumber] = answer3Txt.getText();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method clears all the text beside the user selection and save his answers at appropriate place. 
	 * <p>
	 * <b>Receive:</b> The method gets the event click on radio button 4.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void answer4Selected(ActionEvent event) {
		clearSelectionExept(4);
		answersArray[questionNumber] = answer4Txt.getText();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method is used for clearing the selection radioButton.
	 * <p>
	 * <b>Receive:</b> The method gets the index of the answer that we want to keep.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * @author OrSteiner
	 */
	private void clearSelectionExept(int i) {

		switch (i) {
		case 1:
			answer4Txt.setSelected(false);
			answer3Txt.setSelected(false);
			answer2Txt.setSelected(false);
			break;
		case 2:
			answer1Txt.setSelected(false);
			answer3Txt.setSelected(false);
			answer4Txt.setSelected(false);
			break;
		case 3:
			answer1Txt.setSelected(false);
			answer2Txt.setSelected(false);
			answer4Txt.setSelected(false);
			break;
		case 4:
			answer1Txt.setSelected(false);
			answer2Txt.setSelected(false);
			answer3Txt.setSelected(false);
			break;
		default:
			answer1Txt.setSelected(false);
			answer2Txt.setSelected(false);
			answer3Txt.setSelected(false);
			answer4Txt.setSelected(false);
			break;
		}
	}
	// ********** END - LOGIC FOR SELECTED ANSWER ********

	/**
	 * <p>
	 * <b>Explanation:</b> The method send the exam and calculate his write answer points. 
	 * In case the time is up: the method calculate the grade, save the grade to the DataBase and pop the grade to the student and back into the menu.
	 * <p>
	 * In case the user clicked "DONE": pop a decision frame to ask the user if he really want to submit, calculate the grade, save the grade to the DataBase,
	 * and pop the grade to the student and back into the menu.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'Done'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	private void examDoneClicked(ActionEvent event) {
		if (caseOfSubmission.equals("Exam Time Is Over")) {
			// in case the time is up
			Platform.runLater(() -> {
				SpecialCalls.callInfoFrame("Exam submitted automatically", "TIME IS UP !", "Exam Time Is Over...");
				// calculate the grade
				int grade = Calculators.calculateGrade(answersArray, QIEarrayList);
				StringBuilder answersArraytoStirng = new StringBuilder();
				for (int i = 0; i < answersArray.length; i++)
					answersArraytoStirng.append(answersArray[i] + "###");
				String str = answersArraytoStirng.substring(0, answersArraytoStirng.length() - 3);
				// save the grade to the DataBase
				ClientUI.clientHandler.handleMessageFromClientUI("insertStudentGrade:::"
						+ ClientUI.clientHandler.myDetails.getId() + "%%%" + examId + "%%%" + grade + "%%%" + str);
				// pop the grade to the student and back into the menu
				if (grade >= 55)
					SpecialCalls.callInfoFrame("You have passed the exam with the grade " + grade, "RESULT",
							"Congratulations!");
				if (grade < 55)
					SpecialCalls.callInfoFrame("You have failed the exam with the grade " + grade, "RESULT", "Failed!");
			});
			// hide all the answers and present the menu button
			answer4Txt.setVisible(false);
			answer3Txt.setVisible(false);
			answer2Txt.setVisible(false);
			answer1Txt.setVisible(false);
			nextQuestionImg.setVisible(false);
			previousQuestionImg.setVisible(false);
			questionContent.setVisible(false);
			numberOfQuestionTxt.setVisible(false);
			examDoneBtn.setVisible(false);
			backToMainMenuBtn.setVisible(true);
		} else {
			// in case the user clicked "DONE"

			// pop a decision frame to ask the user if he really want to submit
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("Submmiting can not be undone");
			alert.setContentText("Are you sure you want to subbmit?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				// calculate the grade
				timer.cancel();
				int grade = Calculators.calculateGrade(answersArray, QIEarrayList);
				StringBuilder answersArraytoStirng = new StringBuilder();
				for (int i = 0; i < answersArray.length; i++)
					answersArraytoStirng.append(answersArray[i] + "###");
				String str = answersArraytoStirng.substring(0, answersArraytoStirng.length() - 3);
				// save the grade to the DataBase
				ClientUI.clientHandler.handleMessageFromClientUI("insertStudentGrade:::"
						+ ClientUI.clientHandler.myDetails.getId() + "%%%" + examId + "%%%" + grade + "%%%" + str);
				// pop the grade to the student and back into the menu
				if (grade >= 55)
					SpecialCalls.callInfoFrame("You have passed the exam with the grade " + grade, "RESULT",
							"Congratulations!");
				if (grade < 55)
					SpecialCalls.callInfoFrame("You have failed the exam with the grade " + grade, "RESULT", "Failed!");
				try {
					FXMLutil.swapScene(event, window.StudentMenu.toString());
					timer.cancel();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				// keep working
			}
		}
	}

	// ****** END - EXAM TIME IS UP OR EXAM SUBMMITED

	/**
	 * <p>
	 * <b>Explanation:</b> If the time ended the method reveal just the 'back to main menu' button '.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'back'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void backToMainMenuClicked(ActionEvent event) {
		try {
			FXMLutil.swapScene(event, window.StudentMenu.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
