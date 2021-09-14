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
 * <b>Explanation:</b> The principal can watch the students exams: she can search and click twice to download exam at desired location.
 * <p>
 * @author DvirVahav
 */
public class GetACopyOfExamPrincipalController implements Initializable {

	@FXML
	private TableView<MyFile> examsViewTable;

	@FXML
	private TableColumn<MyFile, String> studentIDCol;
	@FXML
	private TableColumn<MyFile, String> subjectIDCol;
	@FXML
	private TableColumn<MyFile, String> courseIDCol;
	@FXML
	private TextField searchTexField;
	@FXML
	private TableColumn<MyFile, String> examIDCol;

	@FXML
	private TableColumn<MyFile, String> examTypeCol;

	@FXML
	private Button backButton;
	@FXML
	private ArrayList<MyFile> examArrayList;
	private ObservableList<MyFile> examObservalbleList = FXCollections.observableArrayList();

	/**
	 * <p>
	 * <b>Explanation:</b> The method Initialize the variables, set message for user to download desired exam by clicking twice,
	 * set message for user how to search, Set search filter by examID, studentID or exam type, set listener whenever user type in the search text filed, 
	 * sort the data received by the filter, update view table results, and when principal clicks twice it set of save file prompt for her to save the exam on desired location.
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

		ClientUI.clientHandler.handleMessageFromClientUI("pullAllExamFiles");

		MyFile[] files = ((MyFile[]) ClientHandler.returnMessage);

		for (MyFile file : files)
			examArrayList.add(file);

		studentIDCol.setCellValueFactory(new PropertyValueFactory<MyFile, String>("studentID"));
		examIDCol.setCellValueFactory(new PropertyValueFactory<MyFile, String>("examID"));
		examTypeCol.setCellValueFactory(new PropertyValueFactory<MyFile, String>("examType"));
		courseIDCol.setCellValueFactory(new PropertyValueFactory<MyFile, String>("examCourse"));
		subjectIDCol.setCellValueFactory(new PropertyValueFactory<MyFile, String>("examSubject"));
		

		examObservalbleList.addAll(examArrayList);

		examsViewTable.setItems(examObservalbleList);

		// set message for user to download desired exam by clicking twice
		examsViewTable.setTooltip(new Tooltip("Click twice to download the exam") {

		});
		// set message for user how to search
		searchTexField.setTooltip(new Tooltip("Search by student ID,exam type or examID") {

		});

		// Set search filter by examID, studentID or exam type
		FilteredList<MyFile> searchFilter = new FilteredList<>(examObservalbleList, b -> true);

		// set listener whenever user type in the search text filed
		searchTexField.textProperty().addListener((observa, old, neo) -> {
			searchFilter.setPredicate(myFile -> {

				// cases when the lines will be true= will be shown to user
				if (myFile.getExamID().contains(neo) || myFile.getStudentID().contains(neo)
						|| myFile.getExamType().contains(neo))
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
				// in case the manual exam is null
				if (examsViewTable.getSelectionModel().getSelectedItem().getSize() == 0) {
					SpecialCalls.customeError("Error", "File is not readable",
							"Either user didnt upload file or there was an issue uploading the exam");

				} else {
					String typeOfExamFilter, typeOfExamFilterEnd;

					// manual exams are docx file as described in the assignment, and computerized
					// is set into text file with the details.
					if (!examsViewTable.getSelectionModel().getSelectedItem().getExamType().equals("manual")) {
						typeOfExamFilter = "TXT files (*.txt)";
						typeOfExamFilterEnd = "*.txt";
					} else {
						typeOfExamFilter = "DOCX files (*.docx)";
						typeOfExamFilterEnd = "*.docx";
					}
					try {
						FileChooser fileChooser = new FileChooser();

						// Set extension filter for docx files only
						FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(typeOfExamFilter,
								typeOfExamFilterEnd);
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
	 * <b>Explanation:</b> The method swap the scene back to the principal menu when we click the button 'back'.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'back'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void backButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.PrincipalMenu.toString());
	}

}
