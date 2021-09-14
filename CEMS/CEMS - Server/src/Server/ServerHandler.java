
package Server;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import controller.ServerPortFrameController;
import entity.MyFile;
import entity.User;
import ocsf.server.*;
import utils.entityCreator;
import utils.sqlQueries;
import utils.stringSplitter;

/**
 * <p>
 * <b>Explanation:</b> This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 * <p>
 * 
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class ServerHandler extends AbstractServer {

	// DB connection object
	Object connectedClientLock;
	public static Connection DBconnect;
	public static ServerHandler instance;
	public ServerPortFrameController serverUI;

	// connected users information
	public static ArrayList<User> connectedUsers;
	public static HashMap<HashMap<String, String>, ArrayList<String>> onlineExamsAndRegisteredStudens;
	public static ArrayList<String> onlineExams;

	// Class variables *************************************************

	/**
	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;

	// Constructors ****************************************************

	public ServerHandler(int port, ServerPortFrameController serverUI) {
		super(port);
		this.serverUI = serverUI;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> requests an instance of server handler with predetermined
	 * info
	 * <p>
	 * 
	 * @return instance of this class
	 */
	public static ServerHandler getInstance(int port, ServerPortFrameController serverWindow) {

		if (instance == null) {
			instance = new ServerHandler(port, serverWindow);
		}
		return instance;
	}

	// Instance methods ************************************************

	/**
	 * *
	 * <p>
	 * <b>Explanation:</b> This method handles any messages received from the
	 * client. get the first word of the string to navigate between choices
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {

		this.serverUI.printToLog("Message received: " + msg + " from " + client);
		// question examp

		String[] ClientMessageArray = null;

		if (msg instanceof String) {
			ClientMessageArray = stringSplitter.dollarSplit((String) msg);
			try {
				messageIdentifer(ClientMessageArray, client);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// used to upload exam files from students
		if (msg instanceof MyFile) {
			String respond = null;
			if (((MyFile) msg).getUserType().equals("teacher"))
				respond = sqlQueries.updateBlob((MyFile) msg, DBconnect);
			if (((MyFile) msg).getUserType().equals("student"))
				respond = sqlQueries.submmintManualExam((MyFile) msg, DBconnect);
			try {
				client.sendToClient(respond);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method deals with the controllers messages.
	 * <p>
	 * 
	 * @author Everyone
	 */
	private void messageIdentifer(String[] ClientMessageArray, ConnectionToClient client) throws IOException {

		switch (ClientMessageArray[0]) {

		/*
		 * DvirVahav Disconnect - connected client field in serverUI is updated.
		 * synchronized numOfClient field so it will be correctly displayed.
		 */

		case "disconnect":

			serverUI.printToclientConnectedTextField(client.getName() + " disconnected");
			synchronized (connectedClientLock) {
				serverUI.numOfClientLog(Integer.toString(this.getNumberOfClients() - 1));
			}
			break;
		/*
		 * DvirVahav - login - deals with connection to server, check DB and return
		 * appropriate msg to the client.
		 */
		case "login":
			Thread[] connectedClient = this.getClientConnections();
			for (Thread i : connectedClient) {
				if (i.getName().equals(ClientMessageArray[1])) {
					client.sendToClient("User " + ClientMessageArray[1] + " is already connected!");
					return;
				}
			}

			String login = (String) sqlQueries.askForLoginDetails(ClientMessageArray, DBconnect);
			String[] userName = login.split(":::");
			if (userName[0].equals("successfull")) {
				// set client specific thread to his ID - then can use it for extracting
				// information from the client.

				client.setName(userName[4]);
				switch (userName[7]) {
				case "Student":
					connectedUsers.add(entityCreator.studentCreator(userName));
					break;

				case "Teacher":
					connectedUsers.add(entityCreator.teacherCreator(userName));
					break;

				case "Principal":
					connectedUsers.add(entityCreator.principalCreator(userName));
					break;

				}

				serverUI.printToclientConnectedTextField(
						userName[2] + " " + userName[3] + " " + "[" + userName[7] + "]" + " is connected");
				synchronized (connectedClientLock) {
					serverUI.numOfClientLog(Integer.toString(this.getNumberOfClients()));
				}
				this.serverUI.printToLog(client.getName() + " Connected");
			}
			// sendToAllClients(rs);
			client.sendToClient(login);
			break;

		case "subject":
			String subjects = (String) sqlQueries.pullSubjects(DBconnect);
			client.sendToClient(subjects);
			break;

		case "subjectByUser":
			String subjectByuser = (String) sqlQueries.pullSubjectsByUser(client.getName(), DBconnect);

			break;
		case "course": {
			String courses = (String) sqlQueries.pullCourses(ClientMessageArray, DBconnect);
			client.sendToClient(courses);
			break;
		}

		/*
		 * DvirVahav - get all available courses, for prinicipal data
		 */
		case "AllCourses": {
			String courses = (String) sqlQueries.pullAllCourses(ClientMessageArray, DBconnect);
			client.sendToClient(courses);
			break;
		}
		/*
		 * 
		 * DvirVahav - get all available teacher, for principal data
		 */
		case "AllTeachers": {
			String courses = (String) sqlQueries.pullAllTeachers(ClientMessageArray, DBconnect);
			client.sendToClient(courses);
			break;
		}
		/*
		 * DvirVahav - get all available student, for principal data
		 */
		case "AllStudents": {
			String courses = (String) sqlQueries.pullAllStudents(ClientMessageArray, DBconnect);
			client.sendToClient(courses);
			break;
		}

		case "pullAllManualExamFilesForTeacher": {
			MyFile[] examFiles = sqlQueries.pullAllManualExamFilesForTeacher(ClientMessageArray, DBconnect);
			client.sendToClient(examFiles);
			break;
		}
		case "pullAllExamFiles": {
			MyFile[] examFiles = sqlQueries.pullAllExamFiles(DBconnect);
			client.sendToClient(examFiles);
			break;
		}
		case "pullAllStudentExamFiles": {

			MyFile[] examFiles = sqlQueries.pullAllStudentExamFiles(ClientMessageArray, DBconnect);
			client.sendToClient(examFiles);
			break;
		}

		// select u.ID,u.firstName,u.lastName,u.userName,u.email,u.personRole from users

		case "pullAllUsers": {
			String users = (String) sqlQueries.pullAllUsers(DBconnect);
			client.sendToClient(users);
			break;
		}
		case "pullAllCourses": {
			String users = (String) sqlQueries.pullAllCourses(DBconnect);
			client.sendToClient(users);
			break;
		}

		/*
		 * DvirVahav - exams report for courses
		 */
		case "ReportByCourse": {
			String courses = (String) sqlQueries.pullReportByCourse(ClientMessageArray, DBconnect);
			client.sendToClient(courses);
			break;
		}
		case "ReportByTeacher": {
			String teachers = (String) sqlQueries.pullReportByTeacher(ClientMessageArray, DBconnect);
			client.sendToClient(teachers);
			break;
		}
		case "ReportByStudent": {
			String students = (String) sqlQueries.pullReportByStudent(ClientMessageArray, DBconnect);
			client.sendToClient(students);
			break;
		}
		case "insertQuestion": {
			String respond = (String) sqlQueries.PushQuestion(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "pullQuestions": {
			String teacherQeustions = (String) sqlQueries.pullTeacherQuestions(ClientMessageArray[1], DBconnect);
			client.sendToClient(teacherQeustions);
			break;
		}
		case "subjectID": {
			String subjectID = (String) sqlQueries.pullSubjectID(ClientMessageArray[1], DBconnect);
			client.sendToClient(subjectID);
			break;
		}
		case "updateQuestion": {
			String respond = (String) sqlQueries.updateQuestion(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "examIDgenerator": {
			String respond = (String) sqlQueries.getAndUpdateExamID(DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "questionIDgenerator": {
			String respond = (String) sqlQueries.getAndUpdateQuestionID(DBconnect);
			client.sendToClient(respond);
			break;
		}
		/*
		 * DvirVahav - pull out all questions for principal view
		 */
		case "pullAllQuestions": {
			String respond = (String) sqlQueries.pullAllQuestions(DBconnect);
			client.sendToClient(respond);
			break;
		}
		/*
		 * DvirVahav - pull out all exams for principal view
		 */
		case "pullAllExams": {
			String respond = (String) sqlQueries.pullAllExams(DBconnect);
			client.sendToClient(respond);
			break;
		}

		case "getCurrentExams": {
			String respond = (String) sqlQueries.pullCurrentExams(ClientMessageArray[1], DBconnect);
			StringBuilder newRespond = new StringBuilder();
			newRespond.append(respond);
			newRespond.append("&&&"); // split between all exams Ethan extract to the ones who is online
			for (String i : onlineExams) {

				newRespond.append(i);
				newRespond.append("###");
			}

			client.sendToClient(newRespond.toString());
			break;
		}

		case "returmExamIDByCode": {
			String examIDByCode = "fail";
			for (HashMap<String, String> i : onlineExamsAndRegisteredStudens.keySet())
				if (i.containsValue(ClientMessageArray[1])) {

					examIDByCode = (i.toString().substring(1, i.toString().length() - 6));
				}
			client.sendToClient(examIDByCode);
			break;
		}

		case "checkIfExamExists": {
			String isOk = "fail";
			for (HashMap<String, String> i : onlineExamsAndRegisteredStudens.keySet())
				if (i.containsValue(ClientMessageArray[1])) {
					isOk = "success";

				}
			client.sendToClient(isOk);
			break;
		}

		case "updateStudentID": {
			this.serverUI.printToLog(ClientMessageArray[1] + ClientMessageArray[2] + ClientMessageArray[3]);
			StringBuilder errorLog = new StringBuilder();
			HashMap<String, String> test = new HashMap<>();
			test.put(ClientMessageArray[1], ClientMessageArray[2]); // first is examID, second is the key to the exam

			// if exam exists and code is correct
			if (onlineExamsAndRegisteredStudens.containsKey(test)) {
				// if student is not registered

				if (!onlineExamsAndRegisteredStudens.get(test).contains(ClientMessageArray[3])) {

					onlineExamsAndRegisteredStudens.get(test).add(ClientMessageArray[3]);

					this.serverUI.printToLog(onlineExamsAndRegisteredStudens.toString());
					client.sendToClient("success");
				} else
					// errorLog.append
					this.serverUI.printToLog("student already registered!");
			}

			else {
				// errorLog.append
				this.serverUI.printToLog("ExamID or examcode is incorrect");
				// client.sendToClient(errorLog.toString());
			}
			break;
		}
		/*
		 * Save starting exams on relevant tables in DB & save in online exams hash map
		 * saved in server handler for relevant controllers use. -DvirVahav
		 */
		case "updateExamStats": {
			String respond = (String) sqlQueries.updateExamStats(ClientMessageArray[1], DBconnect);
			if (respond.equals("This exam is already performed today")) {
				client.sendToClient("This exam is already performed today");
				break;

			} else {
				HashMap<String, String> test = new HashMap<>();

				test.put(ClientMessageArray[1], ClientMessageArray[2]); // first is examID, second is the key to the
																		// exam
				// check if we have already exam online if not - add one with the code
				if (!onlineExamsAndRegisteredStudens.containsKey(test)) {
					//

					// set window time of 10 minutes
					Timer timer = new Timer();
					TimerTask task = new TimerTask() {
						// timer logic
						@Override
						public void run() {
							System.out.println("yes");
							closeEntrance(test, ClientMessageArray[1]);
						};
					};
					timer.schedule(task, 10 * 60 * 1000);

					onlineExams.add(ClientMessageArray[1]);
					onlineExamsAndRegisteredStudens.put(test, new ArrayList<>());
					this.serverUI.printToLog(onlineExamsAndRegisteredStudens.toString());
					client.sendToClient("success");
				}
				// do nothing, probably teacher mistaken and started exam that is already
				// started. (we can return statement).
				else

					client.sendToClient("This exam is already started");
				
				break;
			}
		}
		case "updateAvgAndMedian": {
			if (UpdateExamsAvgAndMedian(DBconnect).equals("fail"))
				client.sendToClient("fail");
			else
				client.sendToClient("success");

			break;
		}

		case "testDetails": {
			String details = (String) sqlQueries.pullTestDetails(ClientMessageArray[1], DBconnect);
			client.sendToClient(details);
			break;
		}
		case "todayExams": {
			String todayExams = (String) sqlQueries.pullTodayExams(ClientMessageArray[1], DBconnect);
			client.sendToClient(todayExams);
			break;
		}
		case "subjectIDAndcourseID": {
			String subjectIDAndCourseID = (String) sqlQueries.pullSubjectIDAndCourseID(ClientMessageArray[1],
					DBconnect);
			client.sendToClient(subjectIDAndCourseID);
			break;
		}
		case "insertExam": {
			String respond = (String) sqlQueries.pushExam(ClientMessageArray[1], ClientMessageArray[2],
					ClientMessageArray[3], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "insertQuestionInExam": {
			String respond = (String) sqlQueries.pushQuestionInExam(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "getQuestionsInExam": {
			String respond = (String) sqlQueries.pullExamQuestions(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "getExamDuration": {
			String respond = (String) sqlQueries.pullExamDuration(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "insertStudentGrade": {
			String respond = (String) sqlQueries.pushStudentGrade(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "getExamsToApprove": {
			String respond = (String) sqlQueries.pullExamsToApprove(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "teacherGradeUpdate": {
			String respond = (String) sqlQueries.updateStudentGrade(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "getPendingStudentExam": {
			String respond = (String) sqlQueries.pullStudentExamsDetails(DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "getManualExamFile": {
			MyFile respond = sqlQueries.pullManualExam(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		/**
		 * Author Hani Eival
		 */
		case "allStudentGradePrincipal": {
			String studentGrade = (String) sqlQueries.pullAllGradesForPrincipal(ClientMessageArray[1],
					ClientMessageArray[2], DBconnect);
			client.sendToClient(studentGrade);
			break;
		}

		/**
		 * Author Hani Eival
		 */
		case "studentGrade": {
			String studentGrade = (String) sqlQueries.pullAllGradesForStudent(ClientMessageArray[1],
					ClientMessageArray[2], DBconnect);
			client.sendToClient(studentGrade);
			break;
		}
		case "isTested": {
			boolean respond = sqlQueries.isTestedBefore(ClientMessageArray[1], ClientMessageArray[2], DBconnect);
			client.sendToClient(respond);
			break;
		}
		/**
		 * @author Ethan
		 */
		case "requestChangeExamDuration": {
			String respond = (String) sqlQueries.PushExamsToDurationApprovalTable(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "examsAwaitingApproval": {
			String respond = (String) sqlQueries.PullExamsAwaitingApproval(DBconnect);
			client.sendToClient(respond);
			break;
		}

		case "disapproveExamDuration": {
			String respond = (String) sqlQueries.RemoveExamsAwaitingApproval(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "extendExamDuration": {
			String respond = (String) sqlQueries.extendExamDuration(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "getExamChanges": {
			String respond = (String) sqlQueries.checkExamDurationChanged(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "lockExam": {
			String respond = (String) sqlQueries.lockExam(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}
		case "getTestInstructions": {
			System.out.println(ClientMessageArray[1]);
			String respond = (String) sqlQueries.pullTestInstuctions(ClientMessageArray[1], DBconnect);
			client.sendToClient(respond);
			break;
		}

		default:
			client.sendToClient("fail");
			break;
		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method overrides the one in the superclass. Called
	 * when the server starts listening for connections.
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	protected void serverStarted() {
		// initilaze connectUsers array

		connectedClientLock = new Object();
		connectedUsers = new ArrayList<User>();
		// Connected to DB
		this.serverUI.printToLog("Server listening for connections on port " + getPort());
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			this.serverUI.printToLog("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			this.serverUI.printToLog("Driver definition failed");
		}

		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?serverTimezone=IST", "root",
					"123");
			DBconnect = conn;
			// Connection conn =
			// DriverManager.getConnection("jdbc:mysql://192.168.3.68/test","root","Root");
			this.serverUI.printToLog("SQL connection succeed");
			// createTableCourses(conn);
			// printCourses(conn);
		} catch (SQLException ex) {/* handle any errors */
			this.serverUI.printToLog("SQLException: " + ex.getMessage());
			this.serverUI.printToLog("SQLState: " + ex.getSQLState());
			this.serverUI.printToLog("VendorError: " + ex.getErrorCode());
		}

		// not complete, needs to understand what to do with the exam code
		onlineExams = new ArrayList<String>();
		onlineExamsAndRegisteredStudens = new HashMap<>();

		flushFileOfExamCodeAndLockDBTable();
	}

	public void closeEntrance(HashMap<String, String> examIDAndCode, String examID) {// Removes the code for entrance

		HashMap<String, String> temp = new HashMap<>();
		temp.put(examID, "N123");
		if (onlineExamsAndRegisteredStudens.containsKey(examIDAndCode)) {
			// save the students currently doing the test
			ArrayList<String> studentList = onlineExamsAndRegisteredStudens.get(examIDAndCode);
			// remove the entry so we can change the key of the exam
			onlineExamsAndRegisteredStudens.remove(examIDAndCode);

			// set the new code so users cannot enter the exam anymore.
			onlineExamsAndRegisteredStudens.put(temp, studentList);

		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b> Flush exam code file for reuse next time + flush DB with
	 * locked exam files
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	private void flushFileOfExamCodeAndLockDBTable() {

		// flush all locked exams
		try {
			PreparedStatement stmt = DBconnect.prepareStatement("truncate examsawaitingdurationapproval");
			this.serverUI.printToLog("Flush all lock exams");
		} catch (Exception e) {

		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method update the exams average and median
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String UpdateExamsAvgAndMedian(Connection dBconnect) {
		PreparedStatement stmt, stmt2, stmt3;
		try {
			stmt = dBconnect.prepareStatement(
					"update examstats set average = round((select avg(se.grade) as gradeAVG from studentexams se where se.examID=examstats.examID AND se.grade!=\"N\" group by se.examID) )");
			stmt2 = dBconnect.prepareStatement(
					"UPDATE examstats SET median = ( SELECT grade FROM (WITH RankedTable AS ( SELECT examID, grade, ROW_NUMBER() OVER (PARTITION BY examID ORDER BY grade) AS Rnk, COUNT(*) OVER (PARTITION BY examID) AS Cnt FROM studentexams where grade!=\"N\") SELECT examID, grade FROM RankedTable WHERE Rnk = Cnt / 2 + 1 ) e WHERE  e.examID = examstats.examID )");
			stmt3 = dBconnect.prepareStatement("UPDATE examstats SET median = average WHERE median is NULL");

			// update average
			stmt.executeUpdate();

			// update median
			stmt2.executeUpdate();

			// edge case when there is only 1 exam, update it also as average
			stmt3.executeUpdate();

			return "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method overrides the one in the superclass. Called
	 * when the server stops listening for connections.
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	protected void serverStopped() {

		this.serverUI.printToLog("Server has stopped listening for connections.");
		this.serverUI.printToLog("SQL has been disconnected.");
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method starts listening to the server to start
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public void startListening() {
		try {
			this.listen();
		} catch (Exception e) {

			this.serverUI.printToLog("Error - Could not listen for connections");

		}
	}

}
//End of ServerHandler class
