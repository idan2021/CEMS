

package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.ClientHandler;
import client.ClientUI;
import entity.StudentsGradeInfo;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.FXMLutil;
import utils.SpecialCalls;
import utils.stringSplitter;

/**
 * <p>
 * <b>Explanation:</b> This class show the principal the grades of all the students. the principal choose the subject and the courses and then click 'show'.She can see all the grades.
 * The principal can also see if the grades were appended by the teacher and the notes that the teacher wrote to the students.
 * <p>
 * @author Hani Eival
 */
public class ViewGradesPrincipalController implements Initializable {

    @FXML
    private ComboBox<String> subjectSelection;

    @FXML
    private ComboBox<String> courseSelection;

    @FXML
    private TableView<StudentsGradeInfo> Grades;

    @FXML
    private TableColumn<StudentsGradeInfo, String> studentID;
    
    @FXML
    private TableColumn<StudentsGradeInfo, String> DateOfExam;

    @FXML
    private TableColumn<StudentsGradeInfo, String> Grade;

    @FXML
    private TableColumn<StudentsGradeInfo, String> Approve;
    
    @FXML
    private TableColumn<StudentsGradeInfo, String> Notes;
    
    @FXML
    private TableColumn<StudentsGradeInfo, String> ChangeReason;

    @FXML
    private Button cancelButton;

    @FXML
    private Button showButton;


	// Global variables
	private ObservableList<String> SubjectList = FXCollections.observableArrayList();
	private ObservableList<String> CourseList = FXCollections.observableArrayList();
	private ArrayList<String> errorLog = new ArrayList<String>();
	private ObservableList<StudentsGradeInfo> stuGrade = FXCollections.observableArrayList();

	/**
	 * <p>
	 * <b>Explanation:</b> Initialize the courses according to subject clicked & after another subject selected clear the courses box and the grades table.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the combobox.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author Hani Eival
	 */
	@FXML
		void initializeCourses(ActionEvent event) {
		courseSelection.getItems().clear();
		Grades.getItems().clear();
			// after subject selected clear the courses box
			ClientUI.clientHandler.handleMessageFromClientUI("course:::" + subjectSelection.getValue());
			String str = (String) ClientHandler.returnMessage;
			String[] string = stringSplitter.stringByComma(str);
			CourseList.addAll(string);
			// set new items to show in courses box
			courseSelection.setItems(CourseList);
			

		}
	/**
	 * <p>
	 * <b>Explanation:</b> Initialize the subjects.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author Hani Eival
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		subjectSelection.getItems().clear();
		Grades.getItems().clear();
		// Initialize the subjects to choose from
		ClientUI.clientHandler.handleMessageFromClientUI("subject");
		String str = (String) ClientHandler.returnMessage;
		String[] string = stringSplitter.stringBySpace(str);
		SubjectList.addAll(string);
		subjectSelection.setItems(SubjectList);
	}
	
	/**
	 * <p>
	 * <b>Explanation:</b> The method swap the scene back to the menu when we click the button 'cancel'.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'cancel'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author Hani Eival
	 */
	@FXML
	void cancelBtnClicked(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.PrincipalMenu.toString());
	}
	
	/**
	 * <p>
	 * <b>Explanation:</b> The method check that the user chose subject and course and then it shows on the table the grades of all the students of the subject and course that selected.
	 * The principal can see if the grades of the student were approved or not and if there are any notes on the grade from the teacher.  
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the 'show' button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author Hani Eival
	 */
	@FXML
	void showButtonClick(ActionEvent event) throws IOException {
		Grades.getItems().clear();
		System.out.println(subjectSelection.getValue());
		
		// check errors, cases that combo box is null.
		if (courseSelection.getValue() == null && subjectSelection.getValue() == null )
			errorLog.add("No option selected\n");

		else if (courseSelection.getValue() == null)
			errorLog.add("You need to select course\n");


		if (!errorLog.isEmpty()) {
			SpecialCalls.callErrorFrame(errorLog);
			errorLog.clear();
		}

		ArrayList<StudentsGradeInfo> stuGradeArr = new ArrayList<StudentsGradeInfo>();
		ClientUI.clientHandler.handleMessageFromClientUI(
				"allStudentGradePrincipal:::" + subjectSelection.getValue() + ":::" + courseSelection.getValue());
		stuGradeArr = SpecialCalls.callStudentGradeInfo((String) ClientHandler.returnMessage);
		stuGrade.addAll(stuGradeArr);
		studentID.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("id"));
		DateOfExam.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("dateOfExam"));
		Grade.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("grade"));
		Approve.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("approve"));
		Notes.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("notes"));
		ChangeReason.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("changeReason"));
		Grades.setItems(stuGrade);

	}


}