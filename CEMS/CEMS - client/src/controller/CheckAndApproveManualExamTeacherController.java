package controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import entity.MyFile;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.FXMLutil;
import utils.SpecialCalls;
/**
 * <p>
 * <b>Explanation:</b> The class shows all the grades for menual exam that the teacher needs to approve. The teacher can search the exam by the name of the subject,course, exam type or exam date. 
 * <p>
 * The teacher choose a menual exam from the table view, set the grade for the selected exam and click 'approve grade'.
 * <p>
 * @author DvirVahav
 */
public class CheckAndApproveManualExamTeacherController implements Initializable {

	@FXML
	private TableView<MyFile> examsViewTable;
	@FXML
	private TableColumn<MyFile, String> studentIDCol;
	@FXML
	private TableColumn<MyFile, String> subjectNameCol;

	@FXML
	private TableColumn<MyFile, String> courseNameCol;

	@FXML
	private TableColumn<MyFile, String> examTypeCol;

	@FXML
	private TableColumn<MyFile, String> examDateCol;

	@FXML
	private Button backButton;
	@FXML
	private Button approveGradeButton;

	@FXML
	private TextField gradeTextField;

	@FXML
	private TextField searchTextField;
	private ArrayList<MyFile> examArrayList;
	private ObservableList<MyFile> examObservalbleList = FXCollections.observableArrayList();

	/**
	 * <p>
	 * <b>Explanation:</b> The method Initialize the frame via the data got from the 'check and approve exam' controller.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize variables

		examArrayList = new ArrayList<>();

		ClientUI.clientHandler.handleMessageFromClientUI(
				"pullAllManualExamFilesForTeacher:::" + ClientUI.clientHandler.myDetails.getId());

		MyFile[] files = ((MyFile[]) ClientHandler.returnMessage);

		for (MyFile file : files)
			examArrayList.add(file);
		
		studentIDCol.setCellValueFactory(new PropertyValueFactory<MyFile, String>("studentID"));
		subjectNameCol.setCellValueFactory(new PropertyValueFactory<MyFile, String>("examSubject"));
		courseNameCol.setCellValueFactory(new PropertyValueFactory<MyFile, String>("examCourse"));
		examTypeCol.setCellValueFactory(new PropertyValueFactory<MyFile, String>("examType"));
		examDateCol.setCellValueFactory(new PropertyValueFactory<MyFile, String>("examDate"));

		examObservalbleList.addAll(examArrayList);

		examsViewTable.setItems(examObservalbleList);

		// set message for user to download desired exam by clicking twice
		examsViewTable.setTooltip(new Tooltip("Click twice to download the exam") {

		});
		// set message for user how to search
		searchTextField.setTooltip(new Tooltip("Search by exam subject, exam course, exam type, exam date") {

		});

		// Set search filter by examID, studentID or exam type
		FilteredList<MyFile> searchFilter = new FilteredList<>(examObservalbleList, b -> true);

		// set listener whenever user type in the search text filed
		searchTextField.textProperty().addListener((observa, old, neo) -> {
			searchFilter.setPredicate(myFile -> {

				// cases when the lines will be true= will be shown to user
				if (myFile.getExamCourse().toLowerCase().contains(neo.toLowerCase())
						|| myFile.getExamSubject().toLowerCase().contains(neo.toLowerCase())
						|| myFile.getExamType().toLowerCase().contains(neo.toLowerCase())
						|| myFile.getExamDate().toLowerCase().contains(neo.toLowerCase()))
					return true;
				else
					return false;

			});
		});
		// sort the data received by the filter
		SortedList<MyFile> sortedData = new SortedList<>(searchFilter);
		sortedData.comparatorProperty().bind(examsViewTable.comparatorProperty());

		// update view table results
		examsViewTable.setItems(sortedData);

		// when principal clicks twice it set of save file prompt for her to save the
		// exam on desired location.
		examsViewTable.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				
				if (examsViewTable.getSelectionModel().getSelectedItem().getSize() == 0) {
					SpecialCalls.customeError("Error", "File is not readable", "Either user didnt upload file or there was an issue uploading the exam");

				} else {

					try {
						FileChooser fileChooser = new FileChooser();

						// Set extension filter for docx files only
						FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DOCX files (*.docx)",
								"*.docx");
						fileChooser.getExtensionFilters().add(extFilter);

						// Show save file dialog
						Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
						File theFile = fileChooser.showSaveDialog(window);
						FileOutputStream fos = new FileOutputStream(theFile); // get path
						BufferedOutputStream bos = new BufferedOutputStream(fos);
						bos.write(examsViewTable.getSelectionModel().getSelectedItem().getMybytearray(), 0,
								examsViewTable.getSelectionModel().getSelectedItem().getSize());
						bos.flush();
						fos.flush();
						bos.close();
					} catch (Exception e) {
					}

				}
			}
		});

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method is updating manual exam, teacher can download by click twice, check and then set grade to DB.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void approveGradeButtonClick(ActionEvent event) {
		int grade = Integer.parseInt(gradeTextField.getText());

		if (grade < 0 || grade > 100)
			SpecialCalls.customeError("Error", "Wrong input", "Please choose a number between 0-100");

		else {
			
			
			ClientUI.clientHandler.handleMessageFromClientUI(
					"teacherGradeUpdate:::" + "no_notes" + "%%%" + gradeTextField.getText() + "%%%" + "no_change"
							+ "%%%" + examsViewTable.getSelectionModel().getSelectedItem().getStudentID() + "%%%"
							+ examsViewTable.getSelectionModel().getSelectedItem().getExamID());
			
			SpecialCalls.callSuccessFrame("The grade was updated successfully");
			examObservalbleList.remove(examsViewTable.getSelectionModel().getSelectedItem());
			examsViewTable.setItems(examObservalbleList);
			
		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap the scene back to the teacher menu when we click the button 'back'.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'back'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void backButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.TeacherMenu.toString());
	}

}
