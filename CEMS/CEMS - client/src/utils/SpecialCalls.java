package utils;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.controlsfx.control.Notifications;
import client.ClientHandler;
import client.ClientUI;
import entity.Exam;
import entity.ExamWaitingForApproval;
import entity.Question;
import entity.QuestionInExam;
import entity.StudentExam;
import entity.StudentsGradeInfo;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

/**
 * <p>
 * <b>Explanation:</b> The class adds some special functions you might find
 * useful to others
 * <p>
 * 
 * @author OrSteiner
 */
public class SpecialCalls {
	
	/**
	 * <p>
	 * <b>Explanation:</b> This method get all the exams from query and separates
	 * them
	 * <p>
	 * <b>Receive:</b> A query results of all the exams
	 * <p>
	 * <b>Return:</b> List of all the exams in DB
	 * <p>
	 * 
	 * @author IdanDaar
	 */
	public static ArrayList<Exam> callExams(String msg) {

		ArrayList<Exam> returnList = new ArrayList<Exam>();
		String[] examsString = stringSplitter.stringByPrecent(msg);
		String[] exams = new String[7];
		for (int i = 0; i < examsString.length; i++) {
			exams = stringSplitter.dollarSplit(examsString[i]);
			Exam e = new Exam(exams[0], exams[1], exams[2], exams[3], exams[4], exams[5],
					new ArrayList<QuestionInExam>(), exams[6]);
			if (!e.getStartingTime().equals("manual"))
				e.setStartingTime("computerized");
			e.setTimeInMinutes(e.getTimeInMinutes() + " minutes");
			returnList.add(e);
		}
		return returnList;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method create a costume error pop-up
	 * <p>
	 * <b>Receive:</b> title - error title, header - second header, content - the
	 * error itself
	 * <p>
	 * <b>Return:</b> The new pop-up window
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static void customeError(String title, String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method create a general error pop-up
	 * <p>
	 * <b>Return:</b> The new pop-up window
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static void generalError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error 0");
		alert.setHeaderText("Operation failed: System error. ");
		alert.setContentText("Please contact your system administrator");

		alert.showAndWait();

	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method create an error pop-up
	 * <p>
	 * <b>Receive:</b> errorLog to receive the errors combined to show
	 * <p>
	 * <b>Return:</b> The new pop-up window
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static void callErrorFrame(ArrayList<String> errorLog) {
		String errorLogString = errorLog.toString();
		// replace the chars we don't need from a errorLog
		errorLogString = errorLogString.replace("[", "");
		errorLogString = errorLogString.replace(",", "");
		errorLogString = errorLogString.replace("]", "");
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setHeaderText("failed");
		errorAlert.setHeaderText("Error!");
		errorAlert.setContentText("Please note the following errors:\n " + errorLogString);
		errorAlert.showAndWait();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method able to present success pop-up screen with
	 * specific message
	 * <p>
	 * <b>Receive:</b> message to show
	 * <p>
	 * <b>Return:</b> The new pop-up window
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static void callSuccessFrame(String mgsToShow) {
		Alert errorAlert = new Alert(AlertType.INFORMATION);
		errorAlert.setTitle("succeed");
		errorAlert.setHeaderText("Success!");
		errorAlert.setContentText(mgsToShow);
		errorAlert.showAndWait();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method able to present info pop-up screen with
	 * specific message
	 * <p>
	 * <b>Receive:</b> message to show, the title and the header
	 * <p>
	 * <b>Return:</b> The new pop-up window
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static void callInfoFrame(String mgsToShow, String title, String header) {
		Alert errorAlert = new Alert(AlertType.INFORMATION);
		errorAlert.setTitle(title);
		errorAlert.setHeaderText(header);
		errorAlert.setContentText(mgsToShow);
		errorAlert.showAndWait();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method able to pop up a notification message with
	 * the method arguments
	 * <p>
	 * <b>Receive:</b> title - error title, body of the notification
	 * <p>
	 * <b>Return:</b> The new pop-up notification
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static void callNotification(String title, String body) {
		Notifications notificationBuilder = Notifications.create().title(title).text(body)
				// .graphic(iv)
				.hideAfter(Duration.seconds(20)).position(Pos.TOP_CENTER);
		notificationBuilder.showWarning();

	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method use to translate a string of questions form
	 * DB into ArrayList
	 * <p>
	 * <b>Receive:</b> Message to split
	 * <p>
	 * <b>Return:</b> Question ArrayList with those questions
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static ArrayList<Question> callQuestions(String msg) {
		ArrayList<Question> returnList = new ArrayList<Question>();
		String[] questionsString = stringSplitter.stringByPrecent(msg);
		String[] OQ = new String[9];
		for (int i = 0; i < questionsString.length; i++) {
			OQ = stringSplitter.dollarSplit(questionsString[i]);
			Question q = new Question(OQ[0], OQ[6], OQ[1], OQ[2], OQ[3], OQ[4], OQ[5], OQ[7], OQ[8]);
			returnList.add(q);
		}
		return returnList;
	}





	/**
	 * <p>
	 * <b>Explanation:</b> This method use to translate a string of questionsInExam
	 * form DB into ArrayList, used in computerizedExamStudentController
	 * <p>
	 * <b>Receive:</b> QIEstring to return the question string split
	 * <p>
	 * <b>Return:</b> ArrayList of QIE to initialize exam from
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static ArrayList<QuestionInExam> callQuestionsInExam(String QIEstring) {
		ArrayList<QuestionInExam> returnList = new ArrayList<QuestionInExam>();
		String[] questionsString = stringSplitter.stringByPrecent(QIEstring);
		String[] OQ = new String[7];
		for (int i = 0; i < questionsString.length; i++) {
			OQ = stringSplitter.dollarSplit(questionsString[i]);
			QuestionInExam qie = new QuestionInExam(new Question("1", "2", OQ[2], OQ[3], OQ[4], OQ[5], OQ[6], "8", "9"),
					Integer.parseInt(OQ[0]), OQ[1], "12", "13");
			returnList.add(qie);
		}
		return returnList;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method use to translate a string of questionsInExam
	 * form DB into ArrayList, used in computerizedExamStudentController
	 * <p>
	 * <b>Receive:</b> studentExamString to return the question string split
	 * <p>
	 * <b>Return:</b> ArrayList of QIE to initialize exam from
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static ArrayList<StudentExam> callStudentExam(String studentExamString) {
		ArrayList<StudentExam> arrayToReturn = new ArrayList<StudentExam>();
		String[] str = stringSplitter.stringByPrecent(studentExamString);
		String[] SEparam = new String[6];
		for (int i = 0; i < str.length; i++) {
			SEparam = stringSplitter.dollarSplit(str[i]);
			String[] strSplit = SEparam[5].split("###");
			StudentExam SE = new StudentExam(SEparam[0], SEparam[1], SEparam[2], SEparam[3], SEparam[4], "", "",
					strSplit);
			arrayToReturn.add(SE);
		}
		return arrayToReturn;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method use to translate a string of grades form DB
	 * into ArrayList
	 * <p>
	 * <b>Receive:</b> message to split the grades
	 * <p>
	 * <b>Return:</b> ArrayList of split grades of students
	 * <p>
	 * 
	 * @author HaniEival
	 */
	public static ArrayList<StudentsGradeInfo> callStudentGradeInfo(String msg) {
		ArrayList<StudentsGradeInfo> returnList = new ArrayList<StudentsGradeInfo>();
		String[] stuGradeString = stringSplitter.stringByPrecent(msg);
		String[] stuGrade = new String[8];
		for (int i = 0; i < stuGradeString.length; i++) {
			stuGrade = stringSplitter.dollarSplit(stuGradeString[i]);
			
			if (stuGrade[5].equals("null"))
				stuGrade[5]=" ";
			if (stuGrade[6].equals("null"))
				stuGrade[6]=" ";
			StudentsGradeInfo sgi = new StudentsGradeInfo(stuGrade[1], "a", "b", "c", "d", "e", "f", stuGrade[0],
					stuGrade[2], stuGrade[3], stuGrade[7], stuGrade[4], stuGrade[5], stuGrade[6]);
			returnList.add(sgi);
		}
		return returnList;
	}
	

	/**
	 * <p>
	 * <b>Explanation:</b> the method returns all current exams using the response from the server.
	 * <p>
	 * the call is usually used by the {@link CurrentExamTeacherController} to update its table.
	 * <p>
	 * <b>Receive:</b> the method gets a message String from the server that was returned from a query.
	 * <p>
	 * <b>Return:</b> The method returns the exams organized in an ArrayList. 
	 * <p>
	 * @author EthanButorsky
	 */
	public static ArrayList<Exam> callCurrentExams(String msg) {
		ArrayList<Exam> returnList = new ArrayList<Exam>();
		String[] examAndExamOnline = stringSplitter.umpercentSplit(msg);
		String[] examsOnlineString = stringSplitter.sulamitSplit(examAndExamOnline[1]);
		ArrayList<String> examsOnlineArrayList = new ArrayList<>();
		for (String i : examsOnlineString) {
			examsOnlineArrayList.add(i);
		}
		String[] examsString = stringSplitter.stringByPrecent(examAndExamOnline[0]);
		String[] exams = new String[7];
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		LocalDateTime examEnd, examStart;
		if (!msg.isEmpty()) {
			for (int i = 0; i < examsString.length; i++) {
				exams = stringSplitter.dollarSplit(examsString[i]);
				Exam e = new Exam(exams[0], exams[1], exams[2], exams[3], exams[4], exams[5],
						new ArrayList<QuestionInExam>(), exams[6]);
				if (!e.getStartingTime().equals("manual"))
					e.setStartingTime("computerized");
				returnList.add(e);
			}
		}
		if (examsOnlineArrayList.size() == 0) {
			returnList.clear();
			return returnList;
		} else {
			for (int i = 0; i < returnList.size(); i++) {
				if (!examsOnlineArrayList.contains(returnList.get(i).getId())) {
					returnList.remove(i);
				}
			}
			return returnList;
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method returns all current exams awaiting new duration approval using the response from the server.
	 * <p>
	 * the call is usually used by the {@link ExamDurationApprovalPrincipalController} to update its table.
	 * <p>
	 * <b>Receive:</b> the method gets a message String from the server that was returned from a query.
	 * <p>
	 * <b>Return:</b> The method returns the exams organized in an ArrayList. 
	 * <p>
	 * @author EthanButorsky
	 */
	public static ArrayList<ExamWaitingForApproval> callExamsAwaitingApproval(String msg) {
		ArrayList<ExamWaitingForApproval> returnList = new ArrayList<ExamWaitingForApproval>();
		String[] examsString = stringSplitter.stringByPrecent(msg);
		String[] exams = new String[5];
		if (!msg.isEmpty()) {
			for (int i = 0; i < examsString.length; i++) {
				exams = stringSplitter.dollarSplit(examsString[i]);
				ExamWaitingForApproval e = new ExamWaitingForApproval(exams[0], exams[1], exams[2], exams[3], exams[4]);
				returnList.add(e);
			}
		}
		return returnList;
	}

}
