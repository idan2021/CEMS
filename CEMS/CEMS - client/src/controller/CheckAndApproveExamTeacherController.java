package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import entity.StudentExam;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.Calculators;
import utils.FXMLutil;
import utils.SpecialCalls;

/**
 * <p>
 * <b>Explanation:</b> The class allows the teacher to pick student computerize exam, and approve it.
 * <p>
 * For moving the next scene: check and approve grade, we click 'Next'.  For going back to the teacher menu we click 'Back'.
 * <p>
 * @author OrSteiner
 */
public class CheckAndApproveExamTeacherController implements Initializable {

	@FXML
	private TableView<StudentExam> studentExamTableView;

	@FXML
	private TableColumn<StudentExam, String> studentIDcolumn;

	@FXML
	private TableColumn<StudentExam, String> examIDcolumn;

	@FXML
	private TableColumn<StudentExam, String> gradeColumn;

	@FXML
	private TableColumn<StudentExam, String> gradeStatusColumn;

	@FXML
	private Button backBtn;

	@FXML
	private Button nextBtnClicked;

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap the scene back to the teacher menu when we click the button 'back'.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'back'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@FXML
	void backBtnClicked(ActionEvent event) {
		try {
			FXMLutil.swapScene(event, window.TeacherMenu.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Global variables
	private ObservableList<StudentExam> studentExamList = FXCollections.observableArrayList();

	/**
	 * <p>
	 * <b>Explanation:</b> The method initiate the table view to the exams the specific teacher composed and did not got her approval yet.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author OrSteiner
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ClientUI.clientHandler
				.handleMessageFromClientUI("getExamsToApprove:::" + ClientUI.clientHandler.myDetails.getId());
		try {
		ArrayList<StudentExam> dataToPresent = SpecialCalls.callStudentExam((String) ClientHandler.returnMessage);
		studentExamList.addAll(dataToPresent);}
		catch(ArrayIndexOutOfBoundsException e){
			SpecialCalls.customeError("SORRY","0 Exams left to check","Come back later...");
			try {
				FXMLutil.swapScene(new ActionEvent(), window.TeacherMenu.toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// set all the desired columns into the TableView
		studentIDcolumn.setCellValueFactory(new PropertyValueFactory<StudentExam, String>("studentID"));
		examIDcolumn.setCellValueFactory(new PropertyValueFactory<StudentExam, String>("examID"));
		gradeColumn.setCellValueFactory(new PropertyValueFactory<StudentExam, String>("grade"));
		gradeStatusColumn.setCellValueFactory(new PropertyValueFactory<StudentExam, String>("approved"));
		studentExamTableView.setItems(studentExamList);
		// run a scan on the test to get info if students cheated
		String respond = Calculators.calculateSuspectedcopies();
		if(!respond.isEmpty()) {
			//ImageView iv = new ImageView(getClass().getResource("robotFace.png").toExternalForm());
			SpecialCalls.callNotification("Warning!", respond);
		}
			
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method send to the next scene the student test with the correct and wrong answers.
	 * <p>
	 * <b>Receive:</b> The method gets the even 'click' on next button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author Or Steiner
	 */
	@FXML
	void nextBtnClicked(ActionEvent event) {
		ArrayList<String> errorLog = new ArrayList<String>();
		StudentExam SE = studentExamTableView.getSelectionModel().getSelectedItem();
		if (SE == null) {
			errorLog.add("A Stuent exam must be selected in order to proceed");
			SpecialCalls.callErrorFrame(errorLog);
		} else {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(window.CheckAndApproveGrade.toString()));
			Parent root = null;
			try {
				root = loader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			CheckAndApproveGradeTeacherController CAGcontroller = loader.getController();
			CAGcontroller.displayExam(SE);

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}
	}

}
