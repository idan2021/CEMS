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
 * <b>Explanation:</b> This class show the student his grades. The student choose the subject and then click 'show'.
 * The student can also see if the grades were appended by the teacher and the notes that the teacher wrote them.
 * <p>
 * @author Hani Eival
 */
public class ViewGradesStudentController implements Initializable {

    @FXML
    private ComboBox<String> subjectSelection;

    @FXML
    private TableView<StudentsGradeInfo> Grades;

    @FXML
    private TableColumn<StudentsGradeInfo, String> Course;
    
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
	private ObservableList<StudentsGradeInfo> stuGrade = FXCollections.observableArrayList();
	private ArrayList<String> errorLog = new ArrayList<String>();
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
		FXMLutil.swapScene(event, window.StudentMenu.toString());
	}
	/**
	 * <p>
	 * <b>Explanation:</b> The method check that the user chose subject and then it shows on the table the grades of the student.
	 * The student can see if his grades were approved or not and if there are any notes on the grade from the teacher.  
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
		if (subjectSelection.getValue() == null )
			errorLog.add("You need to select subject\n");
		
		if (!errorLog.isEmpty()) {
			SpecialCalls.callErrorFrame(errorLog);
			errorLog.clear();
		}
		
		ArrayList<StudentsGradeInfo> stuGradeArr = new ArrayList<StudentsGradeInfo>();
		ClientUI.clientHandler.handleMessageFromClientUI(
				"studentGrade:::" + ClientUI.clientHandler.myDetails.getId() + ":::" + subjectSelection.getValue());
		stuGradeArr = SpecialCalls.callStudentGradeInfo((String) ClientHandler.returnMessage);
		stuGrade.addAll(stuGradeArr);
		Course.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("course"));
		DateOfExam.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("dateOfExam"));
		Grade.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("grade"));
		Approve.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("approve"));
		Notes.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("notes"));
		ChangeReason.setCellValueFactory(new PropertyValueFactory<StudentsGradeInfo, String>("changeReason"));
		
		
		Grades.setItems(stuGrade);
		
	}


}