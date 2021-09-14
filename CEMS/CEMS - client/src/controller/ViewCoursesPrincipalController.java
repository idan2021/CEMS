package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import entity.Course;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.FXMLutil;

/**
 * <p>
 * <b>Explanation:</b> The class presents all the courses
 * <p>
 * 
 * @author DvirVahab
 */
public class ViewCoursesPrincipalController implements Initializable {
	@FXML
	private TextField searchTextField;

	@FXML
	private TableView<Course> coursesTableView;

	@FXML
	private TableColumn<Course, String> subjectIDCol;

	@FXML
	private TableColumn<Course, String> subjectNameCol;

	@FXML
	private TableColumn<Course, String> courseIDCol;

	@FXML
	private TableColumn<Course, String> courseNameCol;

	@FXML
	private Button backButton;
	private ArrayList<Course> courseArrayList;
	private ObservableList<Course> courseObservalbleList = FXCollections.observableArrayList();

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls all the courses data from DB and
	 * presents all the courses in a table
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path
	 * for the root object and the resources that used to localize the root object.
	 * <p>
	 * <b>Return:</b> List of all the courses in the system
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize variables
		courseArrayList = new ArrayList<>();
		ClientUI.clientHandler.handleMessageFromClientUI("pullAllCourses");
		String returnMsg = ((String) ClientHandler.returnMessage);

		// split users row from data base
		String[] returnMsgArr = utils.stringSplitter.stringByComma(returnMsg);
		for (int i = 0; i < returnMsgArr.length; i++) {
			String[] temp = utils.stringSplitter.stringByPrecent(returnMsgArr[i]);
			courseArrayList.add(new Course(temp[0], temp[1], temp[2], temp[3]));
		}

		subjectIDCol.setCellValueFactory(new PropertyValueFactory<Course, String>("subjectID"));
		subjectNameCol.setCellValueFactory(new PropertyValueFactory<Course, String>("subjectName"));
		courseIDCol.setCellValueFactory(new PropertyValueFactory<Course, String>("courseID"));
		courseNameCol.setCellValueFactory(new PropertyValueFactory<Course, String>("courseName"));

		courseObservalbleList.addAll(courseArrayList);
		coursesTableView.setItems(courseObservalbleList);
		// set message for user how to search
		searchTextField.setTooltip(new Tooltip("Search by subject,course or composer") {

		});

		// Set search filter by subject,course or composer
		FilteredList<Course> searchFilter = new FilteredList<>(courseObservalbleList, b -> true);

		// set listener whenever user type in the search text filed
		searchTextField.textProperty().addListener((observa, old, neo) -> {
			searchFilter.setPredicate(course -> {

				// cases when the lines will be true= will be shown to user
				if (course.getSubjectID().toLowerCase().contains(neo.toLowerCase())
						|| course.getSubjectName().toLowerCase().contains(neo.toLowerCase())
						|| course.getCourseID().toLowerCase().contains(neo.toLowerCase())
						|| course.getCourseName().toLowerCase().contains(neo.toLowerCase()))
					return true;
				else
					return false;

			});
		});
		// sort the data received by the filter
		SortedList<Course> sortedData = new SortedList<>(searchFilter);
		sortedData.comparatorProperty().bind(coursesTableView.comparatorProperty());

		// update view table results
		coursesTableView.setItems(sortedData);
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
	void backButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.PrincipalMenu.toString());
	}

}



