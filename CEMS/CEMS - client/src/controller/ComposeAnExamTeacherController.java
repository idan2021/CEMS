package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import entity.Exam;
import entity.Question;
import entity.QuestionInExam;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.FXMLutil;
import utils.SpecialCalls;
import utils.stringSplitter;

/**
 * <p>
 * <b>Explanation:</b> The class allows to the teacher to compose a computerize
 * exam and to other teachers to use them.
 * <p>
 * The teacher select subject, course, exam duration for the test, his date and
 * time. The teacher click the button 'add new question', and then we transfer
 * to attach question window.
 * <p>
 * The teacher goes back to these screen with the chosen question, leave
 * comments to the students or for other teachers if she wants, and then she
 * click :
 * <p>
 * 'Create' for creating this exam
 * <p>
 * 'Remove question for removing an unwanted question that she chose.
 * <p>
 * 'back' for going back to teacher menu.
 * <p>
 * 
 * @author OrSteiner
 */

public class ComposeAnExamTeacherController implements Initializable {

	@FXML
	private ComboBox<String> subjectSelection;

	@FXML
	private ComboBox<String> courseSelection;

	@FXML
	private TextField timeInMinTextField;

	@FXML
	private Button addNewQuestion;

	@FXML
	private DatePicker examDatePicker;

	@FXML
	private TextField startTimeTextfield;

	@FXML
	private TableView<QuestionInExam> examTableView;

	@FXML
	private TableColumn<QuestionInExam, String> QuestionIDColumn;

	@FXML
	private TableColumn<QuestionInExam, String> QuestionContentColumn;

	@FXML
	private TableColumn<QuestionInExam, Integer> pointsColumn;

	@FXML
	private TextField commentToTeacherTextField;

	@FXML
	private TextField commentToStudentTextField;

	@FXML
	private Button backBtn;

	@FXML
	private Button discardQuestionBtn;

	@FXML
	private Button createButton;

	@FXML
	private Text totalPointsTxtFld;

	// Global variables
	private ObservableList<String> SubjectList = FXCollections.observableArrayList();
	private ObservableList<String> CourseList = FXCollections.observableArrayList();
	// static variables to keep user selection
	private static ArrayList<QuestionInExam> questionsToAdd = new ArrayList<QuestionInExam>();
	private static String timeInMin;
	private static String startTime;
	private static LocalDate mydate;
	private static String subjectSelect;
	private static String courseSelect;

	/**
	 * <p>
	 * <b>Explanation:</b>Initialize the set of questions for presentation, and from
	 * there the teacher can select a question for the test: We ask server to return
	 * all the questions in DB, take the server data and create questions arrayList
	 * to place into the table view in "AttachQuestion" frame, and create the
	 * AttachQuestion scene with the Questions ArrayList.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on 'add new question'
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void initializeAttachQuestionFrame(ActionEvent event) throws IOException {
		// save the user selection of the following variables
		int PointsSum = 0;
		for (QuestionInExam QIE : questionsToAdd)
			PointsSum += QIE.getPoints();
		if (PointsSum == 100) {
			SpecialCalls.customeError("Points Capacity Reached", "Can't add any more questions",
					"Remove a question to add another one.");
		} else {
			timeInMin = timeInMinTextField.getText();
			startTime = startTimeTextfield.getText();
			mydate = examDatePicker.getValue();
			subjectSelect = subjectSelection.getValue();
			courseSelect = courseSelection.getValue();
			ArrayList<Question> questionArrayList = new ArrayList<Question>();
			// ask server to return all the questions in DB
			ClientUI.clientHandler.handleMessageFromClientUI("pullAllQuestions");
			// take the server data and create questions arrayList to place into the
			// Table view in "AttachQuestion" frame
			questionArrayList = SpecialCalls.callQuestions((String) ClientHandler.returnMessage);
			// create the AttachQuestion scene with the Questions ArrayList
			FXMLLoader loader = new FXMLLoader(getClass().getResource(window.AttachQuestion.toString()));
			Parent root = loader.load();
			AttachQuestionController AQcontroller = loader.getController();
			ObservableList<QuestionInExam> questionsInExamList = FXCollections.observableArrayList();
			questionsInExamList.addAll(questionsToAdd);
			AQcontroller.displayQuestions(questionArrayList, 100 - PointsSum, questionsInExamList);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> Initialize the courses according to subject clicked:
	 * after subject selected clear the courses box, the method ask server to return
	 * courses related to the subject selected and set the new items to show in
	 * courses box.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the combobox.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * 
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
		courseSelection.setItems(CourseList);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> Initialize subject box to choose from : set the user
	 * selection of the variables, initialize the table view of the chosen questions
	 * in exam and set all the desired columns into the TableVie. Then, the method
	 * checks if the sum of points equals 100, initialize the subjects to choose
	 * from and the date of exam to todays date.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path
	 * for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// set the user selection of the following variables
		timeInMinTextField.setText(timeInMin);
		startTimeTextfield.setText(startTime);
		examDatePicker.setValue(mydate);
		subjectSelection.setValue(subjectSelect);
		courseSelection.setValue(courseSelect);
		// Initialize the table view of the chosen questions in exam
		ObservableList<QuestionInExam> questionsInExamList = FXCollections.observableArrayList();
		questionsInExamList.addAll(questionsToAdd);
		// set all the desired columns into the TableView
		QuestionIDColumn.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("questionID"));
		QuestionContentColumn.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("questionContent"));
		pointsColumn.setCellValueFactory(new PropertyValueFactory<QuestionInExam, Integer>("points"));
		examTableView.setItems(questionsInExamList);
		Integer PointsSum = 0;
		// check if the sum of points equals 100
		for (QuestionInExam QIE : questionsToAdd) {
			PointsSum += QIE.getPoints();
		}
		totalPointsTxtFld.setText("Total points: " + PointsSum);
		// clear the tableView from last use of compose an exam
		// ******** uses here *********
		// Initialize the subjects to choose from
		ClientUI.clientHandler.handleMessageFromClientUI("subject");
		String returnMsg = (String) ClientHandler.returnMessage;
		String[] string = stringSplitter.stringBySpace(returnMsg);
		SubjectList.addAll(string);
		subjectSelection.setItems(SubjectList);
		// Initialize the date of exam to todays date
		examDatePicker.setValue(LocalDate.now());

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method get question from the AttachQuestion frame,
	 * used to add question into static arrayList that will save those
	 * questionsInExamõ
	 * <p>
	 * She set the user selection of the variables, stack the questionInExam as got
	 * from AttachQuestion frame and initialize the table view of the chosen
	 * questions in exam.
	 * <p>
	 * Then, she set all the desired columns into the TableView, update the total
	 * points, and check if the sum of points equals 100.
	 * <p>
	 * <b>Receive:</b> The method gets thae question the we attached to this exam
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public void addQuestion(QuestionInExam questionToSend) {
		// set the user selection of the following variables
		timeInMinTextField.setText(timeInMin);
		startTimeTextfield.setText(startTime);
		examDatePicker.setValue(mydate);
		subjectSelection.setValue(subjectSelect);
		courseSelection.setValue(courseSelect);
		// stack the questionInExam as got from AttachQuestion frame
		questionsToAdd.add(questionToSend);
		// Initialize the table view of the chosen questions in exam
		ObservableList<QuestionInExam> questionsInExamList = FXCollections.observableArrayList();
		questionsInExamList.addAll(questionsToAdd);
		// set all the desired columns into the TableView
		QuestionIDColumn.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("questionID"));
		QuestionContentColumn.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("questionContent"));
		pointsColumn.setCellValueFactory(new PropertyValueFactory<QuestionInExam, Integer>("points"));
		examTableView.setItems(questionsInExamList);
		// update the total points
		ArrayList<QuestionInExam> questionInExamToInsert = new ArrayList<QuestionInExam>();
		questionInExamToInsert.addAll(examTableView.getItems());
		Integer PointsSum = 0;
		// check if the sum of points equals 100
		for (QuestionInExam QIE : questionInExamToInsert) {
			PointsSum += QIE.getPoints();
		}
		totalPointsTxtFld.setText("Total points: " + PointsSum);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method:
	 * <p>
	 * 1. Try to insert the new question in exam (sql - questionInExam table)
	 * <p>
	 * 2. Try to create new exam (sql - exams table)
	 * <p>
	 * 3. if one can not be done launch an error frame
	 * <p>
	 * <b>Receive:</b> The method gets event 'click' on 'create' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void createBtnClicked(ActionEvent event) throws IOException {
		// get the exam fields (subject, course, duration, date)
		// for tests - for each consume check if the field fulfilled
		ArrayList<String> errorLog = new ArrayList<String>();
		String composer = ClientUI.clientHandler.myDetails.getId();
		String subject = subjectSelection.getValue();
		if (subject == null || subject.equals("subject"))
			errorLog.add("Subject must be selected\n");
		String course = courseSelection.getValue();
		if (course == null || course.equals("course"))
			errorLog.add("Course must be selected\n");
		String examDuration = timeInMinTextField.getText();
		try {
		if (examDuration == null || Integer.parseInt(examDuration) < 1 || Integer.parseInt(examDuration) > 600)
			errorLog.add("Exam duration must be filled or time duartion is incorrecrt \n");
		}catch(NumberFormatException nfe) {
			errorLog.add("Exam duration must be numbers");
		}
		String examStartTime = startTimeTextfield.getText();
		if (examStartTime == null)
			errorLog.add("Starting time must be selected");
		LocalDate myDate = examDatePicker.getValue();
		String examDate = myDate.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
		if (examDate.isBlank())
			errorLog.add("Exam date must be picked\n");

		// ***********************
		// get the questionInExam fields (questionID, points, teacherNote, studentsNote)
		// for tests - for each question in the table view sum the points to reach 100
		// points
		ArrayList<QuestionInExam> questionInExamToInsert = new ArrayList<QuestionInExam>();
		questionInExamToInsert.addAll(examTableView.getItems());
		Integer PointsSum = 0;
		// check if the sum of points equals 100
		for (QuestionInExam QIE : questionInExamToInsert) {
			PointsSum += QIE.getPoints();
		}
		if (PointsSum < 100)
			errorLog.add("Exam total points is below 100");
		else if (PointsSum > 100)
			errorLog.add("Exam total points is above 100");
		// if any error collected on the way, run an error frame with those mistakes
		if (!errorLog.isEmpty())
			SpecialCalls.callErrorFrame(errorLog);
		// ***********************
		// All tests are done, now to insert the questionInExam's and Exam
		else {
			// generate examID and bring other parameters that received (and tested) from
			// before
			StringBuilder examID = new StringBuilder();
			ClientUI.clientHandler.handleMessageFromClientUI("subjectIDAndcourseID:::" + course);
			examID.append((String) ClientHandler.returnMessage);
			ClientUI.clientHandler.handleMessageFromClientUI("examIDgenerator");
			examID.append((String) ClientHandler.returnMessage);

			// send the server the newExam to insert
			String teacherComment = commentToTeacherTextField.getText();
			String studentComment = commentToStudentTextField.getText();
			if (teacherComment.isBlank())
				teacherComment = " ";
			if (studentComment.isBlank())
				studentComment = " ";
			questionInExamToInsert.get(0).setStudentComment(teacherComment);
			questionInExamToInsert.get(0).setTeacherComment(studentComment);
			System.out.println(questionInExamToInsert.get(0).getStudentComment());
			Exam newExam = new Exam(examID.toString(), examDate, examDuration, composer, subject, course,
					questionInExamToInsert, examStartTime);
			System.out.println(newExam.toString());
			// send the server the newExam to insert
			ClientUI.clientHandler.handleMessageFromClientUI(
					"insertExam:::" + newExam.toString() + ":::" + studentComment + ":::" + teacherComment);
			// get return message from server to indicate if the insertion succeed
			String insertExamReturnedMessage = (String) ClientHandler.returnMessage;
			// for each QuestionInExamToInsert send the server the QuestionInExam object
			// toString()
			String insertQuestionInExamReturnMessage = null;
			for (QuestionInExam QIE : questionInExamToInsert) {
				// set examID to the questions in the list
				QIE.setExamID(examID.toString());
				ClientUI.clientHandler.handleMessageFromClientUI("insertQuestionInExam:::" + QIE.toString());
				insertQuestionInExamReturnMessage = (String) ClientHandler.returnMessage;
				// if the message return was not "success" break the loop and present to error
				// message
				if (!insertQuestionInExamReturnMessage.equals("success"))
					break;
			}
			// collect the answers from the two insertions and return suitable message
			if (insertQuestionInExamReturnMessage.equals("success") && insertExamReturnedMessage.equals("success")) {
				questionsToAdd.clear();
				SpecialCalls.callSuccessFrame("Exam successfuly added");
			} else {
				// return error with the reason the question exist

			}
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method is zeroing any other field and swap the scene
	 * back to the teacher menu when we click the button 'back'.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'back'.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void backBtnClicked(ActionEvent event) throws IOException {
		// zeroing any other field
		courseSelect = "course";
		subjectSelect = "subject";
		timeInMin = "";
		questionsToAdd.clear();
		FXMLutil.swapScene(event, window.TeacherMenu.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method remove the selected question from the list:
	 * she initialize the table view of the chosen questions in exam, set all the
	 * desired columns into the TableView,update the total points and check if the
	 * sum of points equals 100.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on discard question.
	 * <p>
	 * <b>Return:</b> The method does not return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void discardQuestionClicked(ActionEvent event) {
		questionsToAdd.remove(examTableView.getSelectionModel().getFocusedIndex());
		// Initialize the table view of the chosen questions in exam
		ObservableList<QuestionInExam> questionsInExamList = FXCollections.observableArrayList();
		questionsInExamList.addAll(questionsToAdd);
		// set all the desired columns into the TableView
		QuestionIDColumn.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("questionID"));
		QuestionContentColumn.setCellValueFactory(new PropertyValueFactory<QuestionInExam, String>("questionContent"));
		pointsColumn.setCellValueFactory(new PropertyValueFactory<QuestionInExam, Integer>("points"));
		examTableView.setItems(questionsInExamList);
		// update the total points
		ArrayList<QuestionInExam> questionInExamToInsert = new ArrayList<QuestionInExam>();
		questionInExamToInsert.addAll(examTableView.getItems());
		Integer PointsSum = 0;
		// check if the sum of points equals 100
		for (QuestionInExam QIE : questionInExamToInsert) {
			PointsSum += QIE.getPoints();
		}
		totalPointsTxtFld.setText("Total points: " + PointsSum);
	}

}
