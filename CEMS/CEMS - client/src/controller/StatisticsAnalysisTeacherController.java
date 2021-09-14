package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.FXMLutil;
import utils.SpecialCalls;
import utils.stringSplitter;

/**
 * <p>
 * <b>Explanation:</b> The class presents the exams details and it's statistics
 * by teacher
 * <p>
 * 
 * @author DvirVahab
 */
public class StatisticsAnalysisTeacherController implements Initializable {

	@FXML
	private TableView<reportExamDetails> typeReportTableView;

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
	private BarChart<String, Number> reportDistributionBarChart;

	@FXML
	private CategoryAxis barChartCategoryAxis;

	@FXML
	private NumberAxis barChartNumberAxis;

	@FXML
	private TextField completedExamsTitle;

	@FXML
	private TextField examsAvgTitle;

	@FXML
	private TextField examsMedianTitle;

	@FXML
	private Button backButton;
	
	// Variables
		private XYChart.Series<String, Number> disSerias;
		private int totalMedian;
		private float totalAvg;
		private ObservableList<reportExamDetails> typeReportTableViewObservableList = FXCollections.observableArrayList();
		private ArrayList<reportExamDetails> typeReportTableViewArrayList = new ArrayList<reportExamDetails>();

		/**
		 * <p>
		 * <b>Explanation:</b> The method display the exam statistics of the teacher by
		 * initializing variables and selected teacher
		 * <p>
		 * <b>Receive:</b> The method gets the location used to resolve relative path
		 * for the root object and the resources that used to localize the root object.
		 * <p>
		 * <b>Return:</b> List of all the statistics for the specific teacher in the
		 * system
		 * <p>
		 * 
		 * @author DvirVahab
		 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initVariables();
		teacherSelect();

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The return of the back button to the teacher’s menu
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> Teacher's menu
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	@FXML
	void backButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.TeacherMenu.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls all the statistics data from DB and
	 * presents all the statistics about the teacher and calculates the teacher
	 * average and median exams
	 * <p>
	 * <b>Return:</b> List of all the statistics for the specific exam in the system
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	private void teacherSelect() {

		ClientUI.clientHandler
				.handleMessageFromClientUI("ReportByTeacher:::" + ClientUI.clientHandler.myDetails.getId());
		String str = (String) ClientHandler.returnMessage;
		String[] teacherStr = stringSplitter.stringByComma(str);
		{
			
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
					break;
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
				else if (Integer.parseInt(x.getAvg()) < 100)
					distirbution[6]++;

				totalAvg += Integer.parseInt(x.getAvg());
			}
			// display results

			examsAvgTitle.setText(String.valueOf(totalAvg / typeReportTableViewArrayList.size()));
			completedExamsTitle.setText(String.valueOf(typeReportTableViewArrayList.size()));

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

		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method calculates the median of the all the students
	 * exams
	 * <p>
	 * <b>Receive:</b> Array list of exams
	 * <p>
	 * <b>Return:</b> The median of all of the teacher's exams
	 * <p>
	 * 
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
	 * <b>Explanation:</b> The method initialize all the details for the table to
	 * present
	 * <p>
	 * 
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
	 * <b>Explanation:</b> This is a private class used to compare between two exams
	 * by our rules.
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	private class examMedianCompartor implements Comparator<reportExamDetails> {

		/**
		 * <p>
		 * <b>Explanation:</b> The method compares between 2 reports to find out if they
		 * are the same
		 * <p>
		 * <b>Receive:</b> The method receives 2 reports to compare
		 * <p>
		 * <b>Return:</b> 0 for compared, 1/(-1) for different reports
		 * <p>
		 * 
		 * @author DvirVahab
		 */
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



