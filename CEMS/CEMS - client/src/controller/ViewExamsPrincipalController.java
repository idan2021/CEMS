package controller;

import java.io.IOException;
import java.net.URL;
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
 * <b>Explanation:</b> The class presents all the exams in the system
 * <p>
 * 
 * @author DvirVahab
 */
public class ViewExamsPrincipalController implements Initializable {

	@FXML
	private TableView<Exam> examsTableView;

	@FXML
	private TableColumn<Exam, String> subjectCol;

	@FXML
	private TableColumn<Exam, String> courseCol;

	@FXML
	private TextField searchTextField;

	@FXML
	private TableColumn<Exam, String> composerCol;

	@FXML
	private Button backButton;

	private ArrayList<Exam> examArrayList;
	private ArrayList<String> examsID;
	private ArrayList<String[]> examsData;

	private ArrayList<QuestionInExam> quesionInExamArrayList;
	private ObservableList<Exam> examObservalbleList = FXCollections.observableArrayList();;

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls all the exams data from DB and presents
	 * all the exams in a table by subject, course and the composer of the exam
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path
	 * for the root object and the resources that used to localize the root object.
	 * <p>
	 * <b>Return:</b> List of all the questions in the system
	 * <p>
	 * 
	 * @author DvirVahab
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
			}catch (Exception e) {
				
			}
		}
		
		sortAndSaveInExams(quesionInExamArrayList, examsID);
		examObservalbleList.addAll(examArrayList);

		// set all the desired columns into the TableView
		subjectCol.setCellValueFactory(new PropertyValueFactory<Exam, String>("subject"));
		courseCol.setCellValueFactory(new PropertyValueFactory<Exam, String>("course"));
		composerCol.setCellValueFactory(new PropertyValueFactory<Exam, String>("author"));

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
	 * <b>Explanation:</b> The method presents the exams details by the exam input
	 * in a pop-up window
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> The exams details to be presented
	 * <p>
	 * 
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
	 * <b>Explanation:</b> The return of the back button to the principal's menu
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> Principal's menu
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	@FXML
	void backButtonClick(ActionEvent event) {
		try {
			FXMLutil.swapScene(event, window.PrincipalMenu.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			SpecialCalls.customeError("Error", "Error returning back", e.getMessage());
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> work with the question in exams array list, examsID
	 * arraylist for matching question to exam, and examsData to add additional data
	 * to exam.
	 * <p>
	 * <b>Receive:</b> Questions that are related the exam and array list of exams
	 * ID
	 * <p>
	 * <b>Return:</b> Sorted questions in the exam
	 * <p>
	 * 
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
	 * <b>Explanation:</b>This is a private class used to compare between two exams
	 * by our rules.
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	private class questionInExamCompartor implements Comparator<QuestionInExam> {

		/**
		 * <p>
		 * <b>Explanation:</b> The method compares between 2 question to find out if
		 * they are from the same exam
		 * <p>
		 * <b>Receive:</b> The method receives 2 questions to compare
		 * <p>
		 * <b>Return:</b> 0 for compared, 1/(-1) for questions in different exams
		 * <p>
		 * 
		 * @author DvirVahab
		 */
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



