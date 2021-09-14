package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import entity.reportExamDetails;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import utils.FXMLutil;
import utils.SpecialCalls;
import utils.stringSplitter;

/**
 * <p>
 * <b>Explanation:</b> The class allows the principal to watch statistics by a chosen teacher, course or student.
 * The principal will click the 'Get Report' button to see the statistics or 'cancel' button for the principal menu.
 * <p>
 * @author DvirVahab
 */
public class ExamStatisticsPrincipalController implements Initializable {

	@FXML
	private NumberAxis barChartNumberAxis;

	@FXML
	private BarChart<String, Number> reportDistributionBarChart;

	@FXML
	private CategoryAxis barChartCategoryAxis;

	@FXML
	private TextField examsAvgTitle;

	@FXML
	private TextField completedExamsTitle;

	@FXML
	private TextField examsMedianTitle;

	@FXML
	private Text reportTypeTitle;
	@FXML
	private TableColumn<reportExamDetails, String> examIdCol;

	@FXML
	private TableColumn<reportExamDetails, String> examSubjectCol;

	@FXML
	private TableColumn<reportExamDetails, String> examCourseCol;

	@FXML
	private TableColumn<reportExamDetails, String> medianCol;

	@FXML
	private TableColumn<reportExamDetails, String> AvgCol;
	@FXML
	private Pane reportMedianPane;

	@FXML
	private CheckBox courseCheckbox;

	@FXML
	private ComboBox<String> courseSelection;

	@FXML
	private CheckBox teacherCheckbox;

	@FXML
	private ComboBox<String> teacherSelection;

	@FXML
	private CheckBox studentCheckbox;

	@FXML
	private ComboBox<String> studentSelection;

	@FXML
	private Button cancelButton;
	@FXML
	private BorderPane examStatisticsAnchorPane;
	@FXML
	private Button getReportButton;

	@FXML
	private TableView<reportExamDetails> typeReportTableView;

	// Variables
	private XYChart.Series<String, Number> disSerias;
	private String AllTeachers, AllStudents, AllCourses;
	private int totalMedian;
	private float totalAvg;
	private HashMap<String, String> teacherNameAndID = new HashMap<>();
	private HashMap<String, String> studentNameAndID = new HashMap<>();
	private HashMap<String, String> courseNameAndID = new HashMap<>();
	private ArrayList<String> errorLog = new ArrayList<String>();
	private ArrayList<reportExamDetails> typeReportTableViewArrayList = new ArrayList<reportExamDetails>();

	private ObservableList<String> courseSelectOL = FXCollections.observableArrayList();
	private ObservableList<String> teacherSelectOL = FXCollections.observableArrayList();
	private ObservableList<String> studentSelectOL = FXCollections.observableArrayList();
	private ObservableList<reportExamDetails> typeReportTableViewObservableList = FXCollections.observableArrayList();

	/**
	 * <p>
	 * <b>Explanation:</b> The method saving names and ID, names for display and ID for SQL query, save unnecessary entries to SQL.
	 * <p>
	 * <b>Receive:</b> The method gets hashmap of user name and ID , a list of users and a combo box of users with the type report that selected.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahab
	 */	
	private void selectionUpdate(HashMap<String, String> userNameAndID, ObservableList<String> userSelectOL,
			ComboBox<String> userSelectionToUpdate, String reportType) {
		ClientUI.clientHandler.handleMessageFromClientUI(reportType);
		String returnMsg = (String) ClientHandler.returnMessage;
		String[] userStr = stringSplitter.stringByComma(returnMsg);
		for (int i = 0; i < userStr.length; i += 2)
			userNameAndID.put(userStr[i + 1], userStr[i]); // id, first and last name
		userNameAndID.entrySet().forEach(entry -> {
			userSelectOL.add(entry.getKey());
		});
		userSelectionToUpdate.setItems(userSelectOL);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method initialize all selections + checkboxes from DB
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// initilable variables
		initVariables();

		AllTeachers = "AllTeachers:::";
		AllStudents = "AllStudents:::";
		AllCourses = "AllCourses:::";
		
		// update the selection of courses, teachers and students from database
		selectionUpdate(courseNameAndID, courseSelectOL, courseSelection, AllCourses);
		selectionUpdate(teacherNameAndID, teacherSelectOL, teacherSelection, AllTeachers);
		selectionUpdate(studentNameAndID, studentSelectOL, studentSelection, AllStudents);

// This option allow the user to select only one category 
		courseCheckbox.setOnAction(EventHandler -> {
			if (courseCheckbox.isSelected()) {
				teacherCheckbox.setDisable(true);
				studentCheckbox.setDisable(true);
				teacherSelection.getSelectionModel().clearSelection();
				studentSelection.getSelectionModel().clearSelection();
			} else {
				teacherCheckbox.setDisable(false);
				studentCheckbox.setDisable(false);
			}
		});
		teacherCheckbox.setOnAction(EventHandler -> {
			if (teacherCheckbox.isSelected()) {
				courseCheckbox.setDisable(true);
				studentCheckbox.setDisable(true);
				courseSelection.getSelectionModel().clearSelection();
				studentSelection.getSelectionModel().clearSelection();
			} else {
				courseCheckbox.setDisable(false);
				studentCheckbox.setDisable(false);
			}
		});
		studentCheckbox.setOnAction(EventHandler -> {
			if (studentCheckbox.isSelected()) {
				courseSelection.getSelectionModel().clearSelection();
				teacherSelection.getSelectionModel().clearSelection();
				teacherCheckbox.setDisable(true);
				courseCheckbox.setDisable(true);
			} else {
				teacherCheckbox.setDisable(false);
				courseCheckbox.setDisable(false);
			}
		});

	}


	/**
	 * <p>
	 * <b>Explanation:</b> The method initialize the report vars.
	 * <p>
	 * <b>Receive:</b> The method get the event 'click' on the button 'back'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahab
	 */	
	@FXML
	void reportBackButtonClick(ActionEvent event) {
		medianCol.setText("Median");
		AvgCol.setText("Avg");
		initVariables();
		reportMedianPane.setVisible(false);
		examStatisticsAnchorPane.setVisible(true);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method swap the scene back to the principal menu when we click the button 'cancel'.
	 * <p>
	 * <b>Receive:</b> The method gets the event 'click' on the button 'cancel'.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author DvirVahav
	 */
	@FXML
	void cancelButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.PrincipalMenu.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method check errors, cases that check box is not checked and/or selection is empty and lead the system to calculate the report by the principal choice. 
	 * <p>
	 * <b>Receive:</b> The method get the event 'click' on the 'Get Report' button.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * @author DvirVahab
	 */	
	@FXML
	void getReportButtonClick(ActionEvent event) {

		// sql query done by server to update values of exams avg and median
		updateAvgAndMEdianInDataBase();

		// Initialize as on student its different

		medianCol.setText("Median");
		AvgCol.setText("Avg");

		// check errors, cases that check box is not checked and/or selection is empty.
		if (!teacherCheckbox.isSelected() && !courseCheckbox.isSelected() && !studentCheckbox.isSelected())
			errorLog.add("No option selected\n");

		if (teacherCheckbox.isSelected() && teacherSelection.getValue() == null)
			errorLog.add("Teacher selection is empty\n");

		if (studentCheckbox.isSelected() && studentSelection.getValue() == null)
			errorLog.add("Student selection is empty\n");

		if (courseCheckbox.isSelected() && courseSelection.getValue() == null)
			errorLog.add("Course selection is empty\n");

		if (!errorLog.isEmpty()) {
			SpecialCalls.callErrorFrame(errorLog);
			errorLog.clear();
		} else {

			// Teachers report selected successfully
			if (teacherCheckbox.isSelected()) {
				initVariables();
				teacherSelect();
			}
			// Courses report selected successfully
			else if (courseCheckbox.isSelected()) {
				initVariables();
				courseSelect();

			}
			// Students report selected successfully
			else if (studentCheckbox.isSelected()) {
				initVariables();
				studentSelect();

			}
		}

	}
	
	/**
	 * <p>
	 * <b>Explanation:</b> The method update the average and the median.
	 * <p>
	 * <b>Receive:</b> The method doesn't get anything.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * @author DvirVahab
	 */
	private void updateAvgAndMEdianInDataBase() {
		ClientUI.clientHandler.handleMessageFromClientUI("updateAvgAndMedian:::");
		String response = (String) ClientHandler.returnMessage;
		if (response.equals("fail"))
			SpecialCalls.customeError("Error", "Error updaing statistics data",
					"Please contact you system administartor");

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method calculate the statistics by a specific chosen student:
	 * arrange all new reportExamDeatils extracted from DB to colums,calculate total average for specific teacher exams, 
	 * display results, calculate Median, and set data on chartBar.
	 * <p>
	 * <b>Receive:</b> The method doesn't get anything.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * @author DvirVahab
	 */	
	private void studentSelect() {

		medianCol.setText("Grade");
		AvgCol.setText("Exam Average");
		ClientUI.clientHandler
				.handleMessageFromClientUI("ReportByStudent:::" + studentNameAndID.get(studentSelection.getValue()));
		String str = (String) ClientHandler.returnMessage;
		String[] studentStr = stringSplitter.stringByComma(str);
		

			for (int i = 0; i < studentStr.length; i++) {
				// extract a line
				String[] temp = stringSplitter.stringByPrecent(studentStr[i]);
				// split the line into new object
				try {
					typeReportTableViewArrayList
							.add(new reportExamDetails(temp[0], temp[1], temp[2], temp[3], temp[4]));
				} catch (Exception e) {
					SpecialCalls.customeError("Error", "No data to display",
							"Student has no data to display or there was a porblem extracting data, please contact system administrator");
					return;
				}

			}
			// Initialize
			
			// Arrange all new reportExamDeatils extracted from DB to colums
			examIdCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("examID"));
			examSubjectCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("examSubject"));
			examCourseCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("examCourse"));
			medianCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("median"));
			AvgCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("avg"));
			typeReportTableViewObservableList.setAll(typeReportTableViewArrayList);
			typeReportTableView.setItems(typeReportTableViewObservableList);

			// calculate total average for specific teacher exams

			int[] distirbution = new int[7];
			for (reportExamDetails x : typeReportTableViewArrayList) {
				if (Integer.parseInt(x.getMedian()) < 70)
					distirbution[0]++;
				else if (Integer.parseInt(x.getMedian()) < 75)
					distirbution[1]++;
				else if (Integer.parseInt(x.getMedian()) < 80)
					distirbution[2]++;
				else if (Integer.parseInt(x.getMedian()) < 85)
					distirbution[3]++;
				else if (Integer.parseInt(x.getMedian()) < 90)
					distirbution[4]++;
				else if (Integer.parseInt(x.getMedian()) < 95)
					distirbution[5]++;
				else if (Integer.parseInt(x.getMedian()) <= 100)
					distirbution[6]++;

				totalAvg += Integer.parseInt(x.getMedian());
			}

			// display results
			examsAvgTitle.setText(String.valueOf(totalAvg / typeReportTableViewArrayList.size()));
			completedExamsTitle.setText(String.valueOf(typeReportTableViewArrayList.size()));
			reportTypeTitle.setText("Exam statistics for student: " + studentSelection.getValue());

			// calculate median
			calculateMedian(typeReportTableViewArrayList);
			examsMedianTitle.setText(String.valueOf(totalMedian));

			// set data on chartBar
			reportDistributionBarChart.getData().clear();
			disSerias = new XYChart.Series<>();
			disSerias.getData().add(new XYChart.Data<>("0-69.9", distirbution[0]));
			disSerias.getData().add(new XYChart.Data<>("70-74", distirbution[1]));
			disSerias.getData().add(new XYChart.Data<>("75-79", distirbution[2]));
			disSerias.getData().add(new XYChart.Data<>("80-84", distirbution[3]));
			disSerias.getData().add(new XYChart.Data<>("85-89", distirbution[4]));
			disSerias.getData().add(new XYChart.Data<>("90-94", distirbution[5]));
			disSerias.getData().add(new XYChart.Data<>("95-100", distirbution[6]));
			reportDistributionBarChart.getData().add(disSerias);
		  
			

		    examStatisticsAnchorPane.setVisible(false);
			reportMedianPane.setVisible(true);

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method calculate the statistics by a specific chosen course:
	 * arrange all new reportExamDeatils extracted from DB to colums,calculate total average for specific teacher exams, 
	 * display results, calculate Median, and set data on chartBar.
	 * <p>
	 * <b>Receive:</b> The method doesn't get anything.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * @author DvirVahab
	 */	
	private void courseSelect() {

		System.out.println(courseNameAndID.get(courseSelection.getValue()));
		ClientUI.clientHandler
				.handleMessageFromClientUI("ReportByCourse:::" + courseNameAndID.get(courseSelection.getValue()));

		String str = (String) ClientHandler.returnMessage;
		String[] courseStr = stringSplitter.stringByComma(str);
		

			for (int i = 0; i < courseStr.length; i++) {
				// extract a line
				String[] temp = stringSplitter.stringByPrecent(courseStr[i]);
				// split the line into new object
				try {
					typeReportTableViewArrayList
							.add(new reportExamDetails(temp[0], temp[1], temp[2], temp[3], temp[4]));
				} catch (ArrayIndexOutOfBoundsException e) {
					SpecialCalls.customeError("Error", "No data to display",
							"Course has no data to display or there was a porblem extracting data, please contact system administrator");
					return;
				}

			}
			// Arrange all new reportExamDeatils extracted from DB to colums
			examIdCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("examID"));
			examSubjectCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("examSubject"));
			examCourseCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("examCourse"));
			medianCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("median"));
			AvgCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("avg"));
			typeReportTableViewObservableList.setAll(typeReportTableViewArrayList);
			typeReportTableView.setItems(typeReportTableViewObservableList);

			// calculate total average for specific teacher exams
			totalAvg = 0;
			totalMedian = 0;
			int[] distirbution = new int[7];
			for (reportExamDetails x : typeReportTableViewArrayList) {
				if (Integer.parseInt(x.getAvg()) < 70)
					distirbution[0]++;
				else if (Integer.parseInt(x.getAvg()) < 75)
					distirbution[1]++;
				else if (Integer.parseInt(x.getAvg()) < 80)
					distirbution[2]++;
				else if (Integer.parseInt(x.getAvg()) < 85)
					distirbution[3]++;
				else if (Integer.parseInt(x.getAvg()) < 90)
					distirbution[4]++;
				else if (Integer.parseInt(x.getAvg()) < 95)
					distirbution[5]++;
				else if (Integer.parseInt(x.getAvg()) <= 100)
					distirbution[6]++;

				totalAvg += (int) Integer.parseInt(x.getAvg());
			}

			// display results

			examsAvgTitle.setText(String.valueOf(totalAvg / typeReportTableViewArrayList.size()));
			completedExamsTitle.setText(String.valueOf(typeReportTableViewArrayList.size()));
			reportTypeTitle.setText("Exam statistics for course: " + courseSelection.getValue());
			// calculate Median
			calculateMedian(typeReportTableViewArrayList);
			examsMedianTitle.setText(String.valueOf(totalMedian));

			// set data on chartBar
			reportDistributionBarChart.getData().clear();
			disSerias = new XYChart.Series<>();
			disSerias.getData().add(new XYChart.Data<>("0-69.9", distirbution[0]));
			disSerias.getData().add(new XYChart.Data<>("70-74", distirbution[1]));
			disSerias.getData().add(new XYChart.Data<>("75-79", distirbution[2]));
			disSerias.getData().add(new XYChart.Data<>("80-84", distirbution[3]));
			disSerias.getData().add(new XYChart.Data<>("85-89", distirbution[4]));
			disSerias.getData().add(new XYChart.Data<>("90-94", distirbution[5]));
			disSerias.getData().add(new XYChart.Data<>("95-100", distirbution[6]));
			reportDistributionBarChart.getData().add(disSerias);
			examStatisticsAnchorPane.setVisible(false);
			examStatisticsAnchorPane.setVisible(false);
			reportMedianPane.setVisible(true);
			reportMedianPane.setVisible(true);

		

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method calculate the statistics by a specific chosen teacher:
	 * arrange all new reportExamDeatils extracted from DB to colums,calculate total average for specific teacher exams, 
	 * display results, calculate Median, and set data on chartBar.
	 * <p>
	 * <b>Receive:</b> The method doesn't get anything.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * @author DvirVahab
	 */	
	private void teacherSelect() {

		ClientUI.clientHandler
				.handleMessageFromClientUI("ReportByTeacher:::" + teacherNameAndID.get(teacherSelection.getValue()));
		String str = (String) ClientHandler.returnMessage;
		String[] teacherStr = stringSplitter.stringByComma(str);
		

			for (int i = 0; i < teacherStr.length; i++) {
				// extract a line
				String[] temp = stringSplitter.stringByPrecent(teacherStr[i]);
				// split the line into new object
				try {
					typeReportTableViewArrayList
							.add(new reportExamDetails(temp[0], temp[1], temp[2], temp[3], temp[4]));
				} catch (ArrayIndexOutOfBoundsException e) {
					SpecialCalls.customeError("Error", "No data to display",
							"Teacher has no data to display or there was a porblem extracting data, please contact system administrator");
					return;
				}

			}
			// Arrange all new reportExamDeatils extracted from DB to colums
			examIdCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("examID"));
			examSubjectCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("examSubject"));
			examCourseCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("examCourse"));
			medianCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("median"));
			AvgCol.setCellValueFactory(new PropertyValueFactory<reportExamDetails, String>("avg"));
			typeReportTableViewObservableList.setAll(typeReportTableViewArrayList);
			typeReportTableView.setItems(typeReportTableViewObservableList);

			// calculate total average for specific teacher exams

			int[] distirbution = new int[7];
			for (reportExamDetails x : typeReportTableViewArrayList) {
				if (Integer.parseInt(x.getAvg()) < 70)
					distirbution[0]++;
				else if (Integer.parseInt(x.getAvg()) < 75)
					distirbution[1]++;
				else if (Integer.parseInt(x.getAvg()) < 80)
					distirbution[2]++;
				else if (Integer.parseInt(x.getAvg()) < 85)
					distirbution[3]++;
				else if (Integer.parseInt(x.getAvg()) < 90)
					distirbution[4]++;
				else if (Integer.parseInt(x.getAvg()) < 95)
					distirbution[5]++;
				else if (Integer.parseInt(x.getAvg()) <= 100)
					distirbution[6]++;

				totalAvg += (int) Integer.parseInt(x.getAvg());
			}
			// display results

			examsAvgTitle.setText(String.valueOf(totalAvg / typeReportTableViewArrayList.size()));
			completedExamsTitle.setText(String.valueOf(typeReportTableViewArrayList.size()));
			reportTypeTitle.setText("Exam statistics for teacher: " + teacherSelection.getValue());
			// calculate Median
			calculateMedian(typeReportTableViewArrayList);
			examsMedianTitle.setText(String.valueOf(totalMedian));

			// set data on chartBar
			reportDistributionBarChart.getData().clear();
			disSerias = new XYChart.Series<>();
			disSerias.getData().add(new XYChart.Data<>("0-69.9", distirbution[0]));
			disSerias.getData().add(new XYChart.Data<>("70-74", distirbution[1]));
			disSerias.getData().add(new XYChart.Data<>("75-79", distirbution[2]));
			disSerias.getData().add(new XYChart.Data<>("80-84", distirbution[3]));
			disSerias.getData().add(new XYChart.Data<>("85-89", distirbution[4]));
			disSerias.getData().add(new XYChart.Data<>("90-94", distirbution[5]));
			disSerias.getData().add(new XYChart.Data<>("95-100", distirbution[6]));
			reportDistributionBarChart.getData().add(disSerias);
			examStatisticsAnchorPane.setVisible(false);
			examStatisticsAnchorPane.setVisible(false);
			reportMedianPane.setVisible(true);
			reportMedianPane.setVisible(true);

		
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method is calculating the median statistics.
	 * <p>
	 * <b>Receive:</b> The method  array list of exams.
	 * <p>
	 * <b>Return:</b> The method  doesn't return anything.
	 * <p>
	 * @author DvirVahab
	 */	
	private void calculateMedian(ArrayList<reportExamDetails> exams) {
		// sort
		Collections.sort(exams, new examMedianCompartor());

		// case array is 1 or 2 long
		if (exams.size() == 2) {
			totalMedian = exams.get(1).getMedianNum();

		} else if (exams.size() == 1) {
			totalMedian = exams.get(0).getMedianNum();

			// in case its even
		} else if (exams.size() % 2 == 0) {
			totalMedian = exams.get(exams.size() / 2 + 1).getMedianNum();
			// in case its odd
		} else {
			totalMedian = exams.get(exams.size() / 2).getMedianNum();
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method clear all the variables.
	 * <p>
	 * <b>Receive:</b> The method doesn't get anything.
	 * <p>
	 * <b>Return:</b> The method dosen't return anything.
	 * <p>
	 * @author DvirVahab
	 */	
	public void initVariables() {
		typeReportTableViewArrayList.clear();
		typeReportTableViewObservableList.clear();
		totalAvg = 0;
		totalMedian = 0;
	}
	
	/**
	 * <p>
	 * <b>Explanation:</b>This is a private class used to compare between two exams by our rules.
	 * <p>
	 * <b>Receive:</b> The method in the class get two questions.
	 * <p>
	 * <b>Return:</b> The method in the class returns 1 or -1 by the rules.
	 * <p>
	 * @author OrSteiner
	 */
	private class examMedianCompartor implements Comparator<reportExamDetails> {

		// override the compare() method
		public int compare(reportExamDetails s1, reportExamDetails s2) {
			if (s1.getMedian().equals(s2.getMedian()))
				return 0;
			else if (Integer.parseInt(s1.getMedian()) > Integer.parseInt(s2.getMedian()))
				return 1;
			else
				return -1;
		}
	}
}
