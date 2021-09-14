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
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;

import client.ClientHandler;
import client.ClientUI;
import entity.Exam;
import entity.Question;
import entity.QuestionInExam;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.FXMLutil;
import utils.SpecialCalls;
/**
 * <p>
 * <b>Explanation:</b> The class allows the teacher to search an exam and see it by his subject name, course name, or the composer name,
 *  and use it by clicking 'use today' button or going back to the teacher menu by clicking the 'back' button.
 * <p>
 * @author OrSteiner
 */
public class ExamsLibraryTeacherController implements Initializable {

	@FXML
	private TableView<Exam> examsTableView;

	@FXML
	private TableColumn<Exam, String> subjectCol;

	@FXML
	private TableColumn<Exam, String> courseCol;

	@FXML
	private TableColumn<Exam, String> composerCol;
	
    @FXML
    private TableColumn<Exam, String> noteForTeacherColumn;

	@FXML
	private Button backButton;

	@FXML
	private TextField searchTextField;

	@FXML
	private Button useTodayBtn;

	private ArrayList<Exam> examArrayList;
	private ArrayList<String> examsID;
	private ArrayList<String[]> examsData;

	private ArrayList<QuestionInExam> quesionInExamArrayList;
	private ObservableList<Exam> examObservalbleList = FXCollections.observableArrayList();
	private String newID;

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
	void backButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.TeacherMenu.toString());

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method insert into exams table the chosen exam.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'Use Today'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void useTodayClicked(ActionEvent event) throws IOException {
		// get the user selection
		Exam E = examsTableView.getSelectionModel().getSelectedItem();
		if(E==null)
			SpecialCalls.customeError("Please select an exam first", null, null);
		else {
			// set the exam date for today
			LocalDate date = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			E.setDateOfExam(date.format(formatter));
			// set the exam composer to the user who took it
			E.setAuthor(ClientUI.clientHandler.myDetails.getId());
			E.setStartingTime("16:00");
			System.out.println(E.toString());
			StringBuilder newID = new StringBuilder();
			ClientUI.clientHandler.handleMessageFromClientUI("subjectIDAndcourseID:::"+E.getCourse());
			newID.append((String) ClientHandler.returnMessage);
			ClientUI.clientHandler.handleMessageFromClientUI("examIDgenerator");
			newID.append((String) ClientHandler.returnMessage);
			this.newID=newID.toString();
			 System.out.println(newID);
			E.setId(this.newID);
			// insert the question in the exam
			ArrayList<QuestionInExam> questionInExamToInsert = new ArrayList<QuestionInExam>();
			String insertQuestionInExamReturnMessage = "success";
			questionInExamToInsert.addAll(E.getQuestions());
			for(QuestionInExam QIE : questionInExamToInsert) {
				// set examID to the questions in the list
				QIE.setExamID(this.newID);
				System.out.println(QIE.toString());
				ClientUI.clientHandler.handleMessageFromClientUI("insertQuestionInExam:::" + QIE.toString());
				insertQuestionInExamReturnMessage = (String) ClientHandler.returnMessage;
				// if the message return was not "success" break the loop and present to error
				// message
				if (!insertQuestionInExamReturnMessage.equals("success"))
					break;
			}
			ClientUI.clientHandler.handleMessageFromClientUI("insertExam:::" + E.toString()+":::"+E.getQuestions().get(0).getStudentComment()+":::"+E.getQuestions().get(0).getTeacherComment());
			String insertExamReturnedMessage = (String) ClientHandler.returnMessage;
			if(insertExamReturnedMessage.equals("success")&&insertQuestionInExamReturnMessage.equals("success"))
				SpecialCalls.callSuccessFrame("Great! you are now able to start this exam in \"start my exam button\"");
		}
			
		
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method initialize variables, split all question in exam into quesioninExam array list, set all the desired columns into the TableView, Set search filter by subject,course or composer,
	 *  sort the data received by the filter and update view table results.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize variables
		examsData = new ArrayList<String[]>();
		examsID = new ArrayList<String>();
		examArrayList = new ArrayList<Exam>();
		quesionInExamArrayList = new ArrayList<QuestionInExam>();

		ClientUI.clientHandler.handleMessageFromClientUI("pullAllExams");
		String returnMsg = ((String) ClientHandler.returnMessage);
		// returning data: col 1-9 question, 10-13 questionInExam additional
		// fields,14-19 exam details of question
		String[] returnMsgArr = utils.stringSplitter.dollarSplit(returnMsg);
		// split all question in exam into quesioninExam array list
		for (int i = 0; i < returnMsgArr.length; i++) {

			String[] temp = utils.stringSplitter.stringByPrecent(returnMsgArr[i]);

			/*
			 * 1. add exam data to new String[] 2. add examID 3. will be used later on to
			 * match between examID,questionInExam and examData
			 */
			try {
				if (!examsID.contains(temp[12])) {
					examsData.add(new String[] { temp[13], temp[14], temp[15], temp[16], temp[17], temp[18] });
					examsID.add(temp[12]);
				}
				quesionInExamArrayList.add(new QuestionInExam(
						new Question(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], temp[7], temp[8]),
						Integer.parseInt(temp[9]), temp[10], temp[11], temp[12]));
			} catch (Exception e) {

			}
		}

		sortAndSaveInExams(quesionInExamArrayList, examsID);
		examObservalbleList.addAll(examArrayList);

		// set all the desired columns into the TableView
		subjectCol.setCellValueFactory(new PropertyValueFactory<Exam, String>("subject"));
		courseCol.setCellValueFactory(new PropertyValueFactory<Exam, String>("course"));
		composerCol.setCellValueFactory(new PropertyValueFactory<Exam, String>("author"));
		noteForTeacherColumn.setCellValueFactory(new PropertyValueFactory<Exam, String>("teacherComment"));
		// contentColumn.setCellValueFactory(new
		// PropertyValueFactory<Question,String>("content"));
		// correctAnswerColumn.setCellValueFactory(new
		// PropertyValueFactory<Question,String>("correctAnswer"));
		examsTableView.setItems(examObservalbleList);
		examsTableView.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2)
				try {

					examViewClick(event);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		});
		examsTableView.setTooltip(new Tooltip("Click twice for extended details about the exam") {

		});
		// set message for user how to search
		searchTextField.setTooltip(new Tooltip("Search by subject,course or composer") {

		});

		// Set search filter by subject,course or composer
		FilteredList<Exam> searchFilter = new FilteredList<>(examObservalbleList, b -> true);

		// set listener whenever user type in the search text filed
		searchTextField.textProperty().addListener((observa, old, neo) -> {
			searchFilter.setPredicate(exam -> {

				// cases when the lines will be true= will be shown to user
				if (exam.getSubject().toLowerCase().contains(neo.toLowerCase())
						|| exam.getCourse().toLowerCase().contains(neo.toLowerCase())
						|| exam.getAuthor().toLowerCase().contains(neo.toLowerCase()))
					return true;
				else
					return false;

			});
		});
		// sort the data received by the filter
		SortedList<Exam> sortedData = new SortedList<>(searchFilter);
		sortedData.comparatorProperty().bind(examsTableView.comparatorProperty());

		// update view table results
		examsTableView.setItems(sortedData);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method is moving me to watch a question scene by double clicking on her.
	 * <p>
	 * <b>Receive:</b> The method get the event 'click' on a question.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahab
	 */
	void examViewClick(MouseEvent event) throws IOException {
		Exam E = examsTableView.getSelectionModel().getSelectedItem();
		FXMLLoader loader = FXMLutil.fxmlCreator(window.viewExamsPopUpWindow.toString());
		Parent root = loader.load();
		ViewExamsPopupPrincipalController viewExamsPopupPrincipalController = loader.getController();
		viewExamsPopupPrincipalController.displayExam(E);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method work with the question in exams array list, examsID arraylist for matching
	 * question to exam, and examsData to add addtional data to exam.
	 * <p>
	 * <b>Receive:</b> The method gets 2 arraylists of questions per exam and of exams.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahab
	 */
	private void sortAndSaveInExams(ArrayList<QuestionInExam> questionInExams, ArrayList<String> examsID) {
		// sort
		Collections.sort(questionInExams, new questionInExamCompartor());
		ArrayList<QuestionInExam> temp = new ArrayList<>();

		for (int i = 0; i < examsID.size(); i++) {
			// iterate all question available and extract the ones who fit current exam ID
			temp.clear();
			for (QuestionInExam QIE : questionInExams)
				if (QIE.getExamID().equals(examsID.get(i)))
					temp.add(QIE);
			// add new exam: examID,date,lgenth,author,subject,course,question that
			// fitmstartTime
			examArrayList.add(new Exam(examsID.get(i), examsData.get(i)[0], examsData.get(i)[1], examsData.get(i)[2],
					examsData.get(i)[3], examsData.get(i)[4], temp, examsData.get(i)[5]));

		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b>This is a private class used to compare between two exams by our rules.
	 * <p>
	 * <b>Receive:</b> The method in the class get two questions.
	 * <p>
	 * <b>Return:</b> The method in the class returns 1 or -1 by the rules.
	 * <p>
	 * @author DvirVahab
	 */	
	private class questionInExamCompartor implements Comparator<QuestionInExam> {

		// override the compare() method
		public int compare(QuestionInExam s1, QuestionInExam s2) {
			if (s1.getExamID().equals(s2.getExamID()))
				return 0;
			else if (Integer.parseInt(s1.getExamID()) > Integer.parseInt(s2.getExamID()))
				return 1;
			else
				return -1;
		}

	}
	
}