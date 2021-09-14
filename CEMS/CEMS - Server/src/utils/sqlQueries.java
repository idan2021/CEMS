package utils;

import java.io.BufferedInputStream;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import entity.MyFile;

/**
 * <p>
 * <b>Explanation:</b> The class contains all the queries which used in the
 * project
 * <p>
 * 
 * @author Everyone
 */
public class sqlQueries {

	public static String pullAllTeachers(String[] clientMessageArray, Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement("SELECT ID,firstName,lastName from users WHERE personRole=\"Teacher\"");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				// ID,First Last,
				SqlQuery.append(rs.getString(1) + "," + rs.getString(2) + " " + rs.getString(3) + ",");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls exams questions and uses INNER JOIN of SQL. combining of two tables at once
	 * <p>
	 * <b>Receive:</b> examID for the search of the specific exam
	 * <p>
	 * <b>Return:</b> String of all the questions in a specific exam
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pullExamQuestions(String examID, Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement(
					"SELECT questioninexam.*,questions.* FROM questioninexam INNER JOIN questions ON questioninexam.questionID=questions.questionID WHERE examID =?");
			stmt.setString(1, examID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(2) + ":::"); // get points
				sqlResult.append(rs.getString(4) + ":::"); // get comment for students
				sqlResult.append(rs.getString(7) + ":::"); // question content
				sqlResult.append(rs.getString(8) + ":::"); // wrong answer 1
				sqlResult.append(rs.getString(9) + ":::"); // wrong answer 2
				sqlResult.append(rs.getString(10) + ":::"); // wrong answer 3
				sqlResult.append(rs.getString(11) + "%%%"); // correct answer
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls the questions data from DB and presents
	 * all the question in a table, used in computerize exam to initialize the timer
	 * <p>
	 * <b>Receive:</b> examID for the search of the specific exam
	 * <p>
	 * <b>Return:</b> String of the exam's length
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pullExamDuration(String examID, Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement("SELECT lengthOfExam FROM exams WHERE examID=?");
			stmt.setString(1, examID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1)); // get examDuration
				break;
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method inserts the student grade to the DB
	 * <p>
	 * <b>Receive:</b> String of details to insert
	 * <p>
	 * <b>Return:</b> "success" or "fail" as result of insertion
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pushStudentGrade(String details, Connection dBconnect) {
		PreparedStatement stmt;
		String[] relevantDetails = stringSplitter.stringByPrecent(details);
		try {
			stmt = dBconnect.prepareStatement(
					"INSERT INTO studentexams (studentID,examID,examType,grade,approved,studentAnswers) VALUES (?,?,\"computerized\",?,\"pending\",?)");
			stmt.setString(1, relevantDetails[0]);
			stmt.setString(2, relevantDetails[1]);
			stmt.setString(3, relevantDetails[2]);
			stmt.setString(4, relevantDetails[3]);
			int rs = stmt.executeUpdate();
			return rs == 1 ? "success" : "fail";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls all the exams that needs to be approved,
	 * used in check and approve exams to get the data relevant for the teacher
	 * <p>
	 * <b>Receive:</b> teacherID for the search of the specific teacher
	 * <p>
	 * <b>Return:</b> String of students details and exams
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pullExamsToApprove(String teacherID, Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement(
					"SELECT studentexams.* FROM studentexams INNER JOIN exams on studentexams.examID=exams.examID "
							+ "where exams.examComposer=? AND  studentexams.approved=\"pending\" AND studentexams.examType=\"computerized\"");
			stmt.setString(1, teacherID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1) + ":::"); // get studentID
				sqlResult.append(rs.getString(2) + ":::"); // get examID
				sqlResult.append(rs.getString(3) + ":::"); // get examType
				sqlResult.append(rs.getString(5) + ":::"); // get grade
				sqlResult.append(rs.getString(6) + ":::"); // get Approved status
				sqlResult.append(rs.getString(9) + "%%%"); // get students answers
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method updates the students grade in the DB, used in
	 * check and approve grade to apply teacher overview on student exam results
	 * <p>
	 * <b>Receive:</b> relevantDetails for the specific student to update
	 * <p>
	 * <b>Return:</b> "success" or "fail" as result of insertion
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String updateStudentGrade(String relevantDetails, Connection dBconnect) {
		PreparedStatement stmt;
		String[] updateDetails = stringSplitter.stringByPrecent(relevantDetails);
		try {
			stmt = dBconnect.prepareStatement(
					"UPDATE studentExams SET grade = ?, approved = \"yes\", noteForStudent=?, reasonForGradeChange=? "
							+ "WHERE studentID=? AND examID=?");
			stmt.setString(2, updateDetails[0]); // set grade
			stmt.setString(1, updateDetails[1]); // set noteForStudent
			stmt.setString(3, updateDetails[2]); // set reason
			stmt.setString(4, updateDetails[3]); // where studentID
			stmt.setString(5, updateDetails[4]); // where examID
			int rs = stmt.executeUpdate();
			return rs == 1 ? "success" : "fail";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls the student's exams, used in the
	 * calculator
	 * <p>
	 * <b>Return:</b> String of exams details of students
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pullStudentExamsDetails(Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement("SELECT studentID,examID,grade,studentAnswers FROM studentexams "
					+ "WHERE examType=\"computerized\" AND approved=\"pending\"");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1) + ":::");
				sqlResult.append(rs.getString(2) + ":::");
				sqlResult.append(rs.getString(3) + ":::");
				sqlResult.append(rs.getString(4) + "%%%");
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method returns the blob of the exam from the table
	 * examFiles, used in manualExamStudentController
	 * <p>
	 * <b>Receive:</b> examID to search for specific exam
	 * <p>
	 * <b>Return:</b> The manual exam file
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static MyFile pullManualExam(String examID, Connection DBconnect) {
		PreparedStatement stmt;
		try {
			stmt = DBconnect.prepareStatement("SELECT exam FROM examFiles WHERE examID=? ");
			stmt.setString(1, examID);
			ResultSet rs = stmt.executeQuery();
			MyFile temp = new MyFile("", "", "");
			while (rs.next()) {
				// Initialize new file. studentID, examID, examType
				InputStream fos = rs.getBinaryStream(1);
				BufferedInputStream bos = new BufferedInputStream(fos);
				temp.setSize(1024000);
				temp.initArray(temp.getSize());
				bos.read(temp.getMybytearray(), 0, temp.getSize());
				break;
			}
			return temp;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method uploads the student manual exam to the DB,
	 * used in manualExamStudentController
	 * <p>
	 * <b>Receive:</b> A file that the student submitted
	 * <p>
	 * <b>Return:</b> "success" or "fail" as result of insertion
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String submmintManualExam(MyFile file, Connection DBconnect) {
		InputStream is = null;
		if (((MyFile) file).getMybytearray() != null)
			is = new ByteArrayInputStream(((MyFile) file).getMybytearray());
		try {
			PreparedStatement pstmt = DBconnect
					.prepareStatement("INSERT INTO studentexams (studentID,examID,examType,exam,grade,approved) VALUES "
							+ "(?,?,\"manual\",?,\"N\",\"pending\")");
			System.out.println(file.getStudentID() + "-----" + file.getExamID());
			pstmt.setBlob(3, is);
			pstmt.setString(1, file.getStudentID());
			pstmt.setString(2, file.getExamID());
			int rs = pstmt.executeUpdate();
			return rs == 1 ? "success" : "failed";
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return null;

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method check for correct login details
	 * <p>
	 * <b>Receive:</b> userName and password
	 * <p>
	 * <b>Return:</b> error when userName\password is incorrect or type of user if
	 * needed
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String askForLoginDetails(String[] clientMessageArray, Connection DBconnect) {
		PreparedStatement stmt;

		try {
			/* check if user name exist */
			stmt = DBconnect.prepareStatement("SELECT * FROM users WHERE userName=?");
			StringBuilder SqlQuery = new StringBuilder();
			stmt.setString(1, clientMessageArray[1]);
			ResultSet messageFromDB = stmt.executeQuery();

			if (!messageFromDB.next())
				return "Username is incorrect! please try again..";
			else {

				/* check if password exist */
				stmt = DBconnect.prepareStatement("SELECT * FROM users WHERE userName=? AND userPassword=?");
				stmt.setString(1, clientMessageArray[1]);
				stmt.setString(2, clientMessageArray[2]);
				messageFromDB = stmt.executeQuery();
				if (!messageFromDB.next())
					return "Password is incorret! please try again..";

				// login success, sending name,last name and role
				for (int i = 1; i < 8; i++) {
					SqlQuery.append(messageFromDB.getString(i));
					SqlQuery.append(":::");
				}

				return "successfull:::" + SqlQuery.toString();
			}
			// stmt.executeUpdate("UPDATE course SET semestr=\"W08\" WHERE num=61309");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String askForSpesificExams(String[] ClientMessageArray, String column, Connection DBconnect) {
		Statement stmt;
		System.out.println("SELECT DISTINCT " + column + "  FROM exams " + "WHERE ExamSubject='" + ClientMessageArray[1]
				+ "' AND ExamCourse='" + ClientMessageArray[2] + "' AND ExamYear='" + ClientMessageArray[3] + "'");
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT DISTINCT " + column + "  FROM exams " + "WHERE ExamSubject='"
					+ ClientMessageArray[1] + "' AND ExamCourse='" + ClientMessageArray[2] + "' AND ExamYear='"
					+ ClientMessageArray[3] + "'");
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + " ");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
			// stmt.executeUpdate("UPDATE course SET semestr=\"W08\" WHERE num=61309");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method returns all the subjects in the DB
	 * <p>
	 * <b>Return:</b> String of all the subjects in the DB
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pullSubjects(Connection DBconnect) {
		PreparedStatement stmt;
		try {
			StringBuilder SqlQuery = new StringBuilder();
			stmt = DBconnect.prepareStatement("SELECT DISTINCT subjectName FROM subjectsAndCourses");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + " ");
			}
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method returns the courses by the subject that been
	 * selected
	 * <p>
	 * <b>Receive:</b> The sunjectName for the filter of courses
	 * <p>
	 * <b>Return:</b> The courses names that attached to the selected subject
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String pullCourses(String[] clientMessageArray, Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement("SELECT courseName from subjectsandcourses WHERE subjectName =?");
			stmt.setString(1, clientMessageArray[1]);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + ",");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method inserts new question to the DB, used in
	 * composeAQuestinTeacherController
	 * <p>
	 * <b>Receive:</b> String that contains the question details
	 * <p>
	 * <b>Return:</b> "success" or "fail" as result of insertion
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String PushQuestion(String clientMessageArray, Connection dBconnect) {
		PreparedStatement stmt;
		String[] questionDetails = stringSplitter.stringByQuestionParameters(clientMessageArray);
		try {
			stmt = dBconnect.prepareStatement(
					"INSERT INTO questions (questionComposer, questionSubject,questionContent,wrongAnswer1,wrongAnswer2,wrongAnswer3,correctAnswer,questionCourses,questionID)\r\n"
							+ "VALUES (?,?,?,?,?,?,?,?,?)");
			stmt.setString(1, questionDetails[1]);
			stmt.setString(2, questionDetails[2]);
			stmt.setString(3, questionDetails[3]);
			stmt.setString(4, questionDetails[4]);
			stmt.setString(5, questionDetails[5]);
			stmt.setString(6, questionDetails[6]);
			stmt.setString(7, questionDetails[7]);
			stmt.setString(8, questionDetails[8]);
			stmt.setString(9, questionDetails[9]);
			int rs = stmt.executeUpdate();
			return rs == 1 ? "success" : "fail";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String pullSubjectsByUser(String name, Connection dBconnect) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method pull all the quesstions the teacher composed
	 * <p>
	 * <b>Receive:</b> composerID which is the ID of the teacher that create the
	 * question
	 * <p>
	 * <b>Return:</b> String of all the questions the teacher composed
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pullTeacherQuestions(String composerId, Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement("SELECT * FROM questions WHERE questionComposer=?");
			stmt.setString(1, composerId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1) + ":::");
				sqlResult.append(rs.getString(2) + ":::");
				sqlResult.append(rs.getString(3) + ":::");
				sqlResult.append(rs.getString(4) + ":::");
				sqlResult.append(rs.getString(5) + ":::");
				sqlResult.append(rs.getString(6) + ":::");
				sqlResult.append(rs.getString(7) + ":::");
				sqlResult.append(rs.getString(8) + ":::");
				sqlResult.append(rs.getString(9) + "%%%");
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method returns the subject ID by the subject name
	 * <p>
	 * <b>Receive:</b> subject to get the subject
	 * <p>
	 * <b>Return:</b> The subjects ID
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pullSubjectID(String subject, Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement("SELECT DISTINCT subjectID FROM subjectsAndCourses WHERE subjectName=?");
			stmt.setString(1, subject);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1));
				break;
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method updates existing question as got from
	 * viewAndEditQuestion2Controller
	 * <p>
	 * <b>Receive:</b> A string of question to update in DB
	 * <p>
	 * <b>Return:</b> "success" or "fail" as result of insertion
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String updateQuestion(String questionToUpdate, Connection dBconnect) {
		PreparedStatement stmt;
		String[] questionDetails = stringSplitter.stringByQuestionParameters(questionToUpdate);
		try {
			stmt = dBconnect.prepareStatement(
					"UPDATE questions SET questionComposer =?, questionContent = ? , wrongAnswer1 = ?, wrongAnswer2 = ?, wrongAnswer3 = ? , correctAnswer = ? , questionSubject = ?, questionCourses =?\r\n"
							+ "WHERE questionID= ?");
			stmt.setString(1, questionDetails[1]);
			stmt.setString(2, questionDetails[3]);
			stmt.setString(3, questionDetails[4]);
			stmt.setString(4, questionDetails[5]);
			stmt.setString(5, questionDetails[6]);
			stmt.setString(6, questionDetails[7]);
			stmt.setString(7, questionDetails[2]);
			stmt.setString(8, questionDetails[8]);
			stmt.setString(9, questionDetails[9]);
			int rs = stmt.executeUpdate();
			return rs == 1 ? "success" : "fail";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method pull all questions, used in
	 * ComposeAnExamController
	 * <p>
	 * <b>Return:</b> All the questions in the DB
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pullAllQuestions(Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement("SELECT * FROM questions");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1) + ":::");
				sqlResult.append(rs.getString(2) + ":::");
				sqlResult.append(rs.getString(3) + ":::");
				sqlResult.append(rs.getString(4) + ":::");
				sqlResult.append(rs.getString(5) + ":::");
				sqlResult.append(rs.getString(6) + ":::");
				sqlResult.append(rs.getString(7) + ":::");
				sqlResult.append(rs.getString(8) + ":::");
				sqlResult.append(rs.getString(9) + "%%%");
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method pull all teachers details, used in
	 * PrincipalMenuController
	 * <p>
	 * <b>Return:</b> String with teachers details
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String pullAllTeachers(Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement("SELECT ID,firstName,lastName from users WHERE personRole=\"Teacher\"");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				// ID,First Last,
				SqlQuery.append(rs.getString(1) + "," + rs.getString(2) + " " + rs.getString(3) + ",");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method pull all students details, used in
	 * PrincipalMenuController
	 * <p>
	 * <b>Return:</b> String with students details
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String pullAllStudents(String[] clientMessageArray, Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement("SELECT ID,firstName,lastName from users WHERE personRole=\"Student\"");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + "," + rs.getString(2) + " " + rs.getString(3) + ",");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method returns all the courses attached to selected
	 * subject
	 * <p>
	 * <b>Receive:</b> The subjectNAme
	 * <p>
	 * <b>Return:</b> The courses details
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pullAllCourses(String[] clientMessageArray, Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect
					.prepareStatement("SELECT subjectID,courseID,subjectName,courseName fROM subjectsandcourses");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				// subject and courseID combined| subjectName|courseName
				SqlQuery.append(
						rs.getString(1) + rs.getString(2) + "," + rs.getString(3) + ": " + rs.getString(4) + ",");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pulls reports related to teacher
	 * <p>
	 * <b>Receive:</b> teacherID to pull the reports related
	 * <p>
	 * <b>Return:</b> The reports related to the teacher
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String pullReportByTeacher(String[] clientMessageArray, Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement(
					"SELECT e.examID,e.examSubject,e.examCourse,es.median,es.average FROM exams e,examstats es WHERE e.examID=es.examID && es.median!=\"0\" && es.average!=\"0\" && e.examComposer=?");
			stmt.setString(1, clientMessageArray[1]);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + "%%%" + rs.getString(2) + "%%%" + rs.getString(3) + "%%%"
						+ rs.getString(4) + "%%%" + rs.getString(5) + ",");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pulls reports of exams related to course
	 * <p>
	 * <b>Receive:</b> examsID for the search of course
	 * <p>
	 * <b>Return:</b> The exams reports related to the specific course
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String pullReportByCourse(String[] clientMessageArray, Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement(
					"SELECT e.examID,e.examSubject,e.examCourse,es.median,es.average FROM exams e,examstats es WHERE e.examID=es.examID && es.median!=\"0\" && es.average!=\"0\" && e.examID LIKE '"
							+ clientMessageArray[1] + "%'");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + "%%%" + rs.getString(2) + "%%%" + rs.getString(3) + "%%%"
						+ rs.getString(4) + "%%%" + rs.getString(5) + ",");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pulls reports related to specific student
	 * <p>
	 * <b>Receive:</b> studentID for the search of student
	 * <p>
	 * <b>Return:</b> The reports related to the specific student
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String pullReportByStudent(String[] clientMessageArray, Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement(
					"SELECT e.examID,e.examSubject,e.examCourse,se.grade,es.average FROM exams e,examstats es,studentexams se WHERE e.examID=se.examID && es.median!=\"0\" && es.average!=\"0\" &&  e.examID=es.examID && se.examID=es.examID && se.studentID=?");

			stmt.setString(1, clientMessageArray[1]);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + "%%%" + rs.getString(2) + "%%%" + rs.getString(3) + "%%%"
						+ rs.getString(4) + "%%%" + rs.getString(5) + ",");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pull all exams for principal view at view
	 * exams section
	 * <p>
	 * <b>Return:</b> String of all the exams in DB
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String pullAllExams(Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement(
					"select q.questionComposer,q.questionSubject,q.questionContent,q.wrongAnswer1,q.wrongAnswer2,q.wrongAnswer3,q.correctAnswer,q.questionCourses,q.questionID,qi.points,e.studentComment ,e.teacherComment, qi.examID, e.dateOfExam,e.lengthOfExam,e.examComposer,e.examSubject,e.examCourse,e.startingTime from questioninexam qi, questions q,exams e where q.questionID=qi.questionID AND qi.examID=e.examID");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + "%%%" + rs.getString(2) + "%%%" + rs.getString(3) + "%%%"
						+ rs.getString(4) + "%%%" + rs.getString(5) + "%%%" + rs.getString(6) + "%%%" + rs.getString(7)
						+ "%%%" + rs.getString(8) + "%%%" + rs.getString(9) + "%%%" + rs.getString(10) + "%%%"
						+ rs.getString(11) + "%%%" + rs.getString(12) + "%%%" + rs.getString(13) + "%%%"
						+ rs.getString(14) + "%%%" + rs.getString(15) + "%%%" + rs.getString(16) + "%%%"
						+ rs.getString(17) + "%%%" + rs.getString(18) + "%%%" + rs.getString(19) + ":::");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pull all the users in DB
	 * <p>
	 * <b>Return:</b> String of all the students in DB
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String pullAllUsers(Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement(
					"select u.ID,u.firstName,u.lastName,u.userName,u.email,u.personRole from users u");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4)
						+ " " + rs.getString(5) + " " + rs.getString(6) + ",");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String pullAllCourses(Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement(
					"select  c.subjectID,c.courseID,c.subjectName,c.courseName from subjectsandcourses c");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + "%%%" + rs.getString(2) + "%%%" + rs.getString(3) + "%%%"
						+ rs.getString(4) + ",");
			}
			System.out.println(SqlQuery.toString());
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pull all the manual exams files in DB
	 * <p>
	 * <b>Return:</b> String of all the files of all the exams in DB
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static MyFile[] pullAllExamFiles(Connection DBconnect) {
		ArrayList<MyFile> studentExamFiles = new ArrayList<>();
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement(
					"select se.studentID,se.examID,se.examType,se.exam,e.examSubject,e.examCourse  from studentexams se,exams e where se.examType=\"manual\" AND se.examID=e.examID");
			ResultSet rs = stmt.executeQuery();

			// This query take care of all manual exams
			while (rs.next()) {

				// Initialize new file. studentID, examID, examType
				MyFile temp = new MyFile(rs.getString(1), rs.getString(2), rs.getString(3));
				// for table view use
				temp.setExamSubject(rs.getString(5));
				temp.setExamCourse(rs.getString(6));

				InputStream fos = rs.getBinaryStream(4);
				BufferedInputStream bos = new BufferedInputStream(fos);

				temp.setSize(1024000);

				temp.initArray(temp.getSize());
				try {
					bos.read(temp.getMybytearray(), 0, temp.getSize());
				} catch (Exception e) {
					temp.setSize(0);
				}
				studentExamFiles.add(temp);

			}

			// This query take care of all computerized exam. at first take users answer,
			// and in inner query takes exam question and correct answer
			stmt = DBconnect.prepareStatement(
					"select u.ID,se.examID,se.examType,se.studentAnswers,e.examSubject,e.examCourse from exams e, questions q, users u, studentexams se,questioninexam qie where examType=\"computerized\" AND u.ID=se.studentID AND qie.examID=se.examID AND qie.questionID=q.questionID GROUP BY studentID,examID");
			rs = stmt.executeQuery();

			while (rs.next()) {

				// Initialize new file. studentID, examID, examType
				MyFile temp = new MyFile(rs.getString(1), rs.getString(2), rs.getString(3));

				// for table view use
				temp.setExamSubject(rs.getString(5));
				temp.setExamCourse(rs.getString(6));

				StringBuilder examFile = new StringBuilder();

				// Get questions and answers for this specific exam by the id received from
				// extrnal query
				PreparedStatement ExtractExamQuestionsAndAnswers;
				StringBuilder examQuestionsAndAnswers = new StringBuilder();

				ExtractExamQuestionsAndAnswers = DBconnect.prepareStatement(
						"select q.questionContent,q.correctAnswer from questioninexam qe,questions q where  q.questionID=qe.questionID AND qe.examID=?");

				ExtractExamQuestionsAndAnswers.setString(1, rs.getString(2));
				ResultSet ExtractExamQuestionsAndAnswersResultSet = ExtractExamQuestionsAndAnswers.executeQuery();

				// Extract all inner query data
				while (ExtractExamQuestionsAndAnswersResultSet.next()) {
					examQuestionsAndAnswers.append(ExtractExamQuestionsAndAnswersResultSet.getString(1) + ":::"
							+ ExtractExamQuestionsAndAnswersResultSet.getString(2) + "%%%");
				}

				// Save into string array
				String[] examQuestionsAndAnswersStr = stringSplitter
						.stringByPrecent(examQuestionsAndAnswers.toString());
				examFile.append("StudentID: " + rs.getString(1) + " ExamID: " + rs.getString(2) + "\n");

				// User answers extraction, notice its from external rsultSet
				String[] answers = rs.getString(4).split("###");

				// Combine external and inner result set into file with the user exam answer and
				// questions.
				for (int i = 0; i < answers.length; i++)
					examFile.append("\nQuestion: " + stringSplitter.dollarSplit(examQuestionsAndAnswersStr[i])[0]
							+ "\nCorrect answer: " + stringSplitter.dollarSplit(examQuestionsAndAnswersStr[i])[1]
							+ "\nUser answer " + (i + 1) + ": " + answers[i] + "\n");

				// save into file
				InputStream fos = new ByteArrayInputStream(examFile.toString().getBytes());
				BufferedInputStream bos = new BufferedInputStream(fos);

				temp.setSize(1024000);

				temp.initArray(temp.getSize());
				bos.read(temp.getMybytearray(), 0, temp.getSize());

				// add this to list
				studentExamFiles.add(temp);

			}
			System.out.println(SqlQuery.toString());
			MyFile[] myExamFilesArr = new MyFile[studentExamFiles.size()];
			for (int i = 0; i < myExamFilesArr.length; i++)
				myExamFilesArr[i] = studentExamFiles.get(i);

			return myExamFilesArr;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pull all the exams that performs today by
	 * system date
	 * <p>
	 * <b>Receive:</b> The composerID to get only the exams related to the teacher
	 * <p>
	 * <b>Return:</b> String of all the exams performs today related to the teacher
	 * <p>
	 * 
	 * @author IdanDaar
	 */
	public static String pullTodayExams(String composerID, Connection DBconnect) {
		PreparedStatement stmt;
		LocalDate date = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement("SELECT * FROM exams WHERE dateOfExam=? AND examComposer=?");
			stmt.setString(1, date.format(formatter));
			stmt.setString(2, composerID);
			ResultSet rs = stmt.executeQuery();
			System.out.println(rs);
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + ":::");
				SqlQuery.append(rs.getString(2) + ":::");
				SqlQuery.append(rs.getString(3) + ":::");
				SqlQuery.append(rs.getString(4) + ":::");
				SqlQuery.append(rs.getString(5) + ":::");
				SqlQuery.append(rs.getString(6) + ":::");
				SqlQuery.append(rs.getString(7) + "%%%");
			}
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pull all the exams details
	 * <p>
	 * <b>Receive:</b> examsID to get only the specific exam details
	 * <p>
	 * <b>Return:</b> String of all the exam details
	 * <p>
	 * 
	 * @author IdanDaar
	 */
	public static String pullTestDetails(String examID, Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement("SELECT examSubject, examCourse FROM exams WHERE examID=?");
			stmt.setString(1, examID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + " ");
				SqlQuery.append(rs.getString(2) + " ");
				SqlQuery.append("test about to begin...");
			}
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method returns string of the course ID and subject
	 * ID by the course name, used in specialCalls.generateExamID
	 * <p>
	 * <b>Receive:</b> courseName for specific course in DB
	 * <p>
	 * <b>Return:</b> String of the courseID and sujectID
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pullSubjectIDAndCourseID(String courseName, Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement("SELECT courseID , subjectID FROM subjectsAndCourses WHERE courseName=?");
			stmt.setString(1, courseName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(2) + rs.getString(1));
				break;
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method inserts new exam details to the DB, used in
	 * ComposeAnExamController
	 * <p>
	 * <b>Receive:</b> examString to insert to DB and composerID for the composer
	 * detail
	 * <p>
	 * <b>Return:</b> "success" or "fail" as result of insertion
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pushExam(String examString, String clientMessageArray, String clientMessageArray2,
			Connection dBconnect) {
		PreparedStatement stmt;
		String[] examDetails = stringSplitter.stringByExamParameters(examString);
		for (String s : examDetails)
			try {
				stmt = dBconnect.prepareStatement("INSERT INTO exams VALUES (?,?,?,?,?,?,?,?,?)");
				stmt.setString(1, examDetails[1]);
				stmt.setString(2, examDetails[2]);
				stmt.setString(3, examDetails[3]);
				stmt.setString(4, examDetails[4]);
				stmt.setString(5, examDetails[5]);
				stmt.setString(6, examDetails[6]);
				stmt.setString(7, examDetails[7]);
				stmt.setString(8, clientMessageArray);
				stmt.setString(9, clientMessageArray2);
				int rs = stmt.executeUpdate();
				return rs == 1 ? "success" : "fail";
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method inserts use of question in a specific exam to
	 * the DB, used in ComposeAnExamController
	 * <p>
	 * <b>Receive:</b> questionInExamString to attach question to exam and to insert
	 * to DB
	 * <p>
	 * <b>Return:</b> "success" or "fail" as result of insertion
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String pushQuestionInExam(String questionInExamString, Connection dBconnect) {
		PreparedStatement stmt;
		String[] questionInExamDetails = stringSplitter.stringByQuestionInExamParameters(questionInExamString);
		try {
			stmt = dBconnect.prepareStatement("INSERT INTO questionInExam VALUES (?,?,?,?,?)");
			stmt.setString(1, questionInExamDetails[1]);
			stmt.setString(2, questionInExamDetails[2]);
			stmt.setString(3, questionInExamDetails[3]);
			stmt.setString(4, questionInExamDetails[4]);
			stmt.setString(5, questionInExamDetails[5]);
			int rs = stmt.executeUpdate();
			return rs == 1 ? "success" : "fail";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> upload exam file into the studnetExams table
	 * <p>
	 * <b>Receive:</b> file to update the exam file
	 * <p>
	 * <b>Return:</b> "success" or "fail" as result of insertion
	 * <p>
	 * 
	 * @author OrSteiner & DvirVahab
	 */
	public static String updateBlob(MyFile file, Connection DBconnect) {
		InputStream is = new ByteArrayInputStream(((MyFile) file).getMybytearray());
		try {
			PreparedStatement pstmt = DBconnect.prepareStatement("INSERT INTO examfiles (examID,exam) VALUES (?,?)");
			pstmt.setBlob(2, is);
			pstmt.setString(1, file.getExamID());
			int rs = pstmt.executeUpdate();
			return rs == 1 ? "success" : "failed";
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return null;

	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pulls all the student files by studentID
	 * <p>
	 * <b>Receive:</b> studentID to pull only his exam file
	 * <p>
	 * <b>Return:</b> All the student exams
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static MyFile[] pullAllStudentExamFiles(String[] clientMessageArray, Connection DBconnect) {
		ArrayList<MyFile> studentExamFiles = new ArrayList<>();
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement(
					"select e.examID, e.examSubject,e.examCourse,se.examType, e.dateOfExam, se.exam from exams e, studentexams se where se.examType=\"manual\" AND e.examID=se.examID AND se.studentID=?");
			stmt.setString(1, clientMessageArray[1]);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				// Initialize new file studentID,ExamID, examType
				MyFile temp = new MyFile(clientMessageArray[1], rs.getString(1), rs.getString(4));

				// used setters in order not to change constructor for others use.
				temp.setExamCourse(rs.getString(3));
				temp.setExamDate(rs.getString(5));
				temp.setExamSubject(rs.getString(2));

				InputStream fos = rs.getBinaryStream(6);
				BufferedInputStream bos = new BufferedInputStream(fos);

				temp.setSize(1024000);
				temp.initArray(temp.getSize());

				try {
					bos.read(temp.getMybytearray(), 0, temp.getSize());
				} catch (Exception e) {
					temp.setSize(0);
				}
				studentExamFiles.add(temp);

			}
			// This query take care of all computerized exam. at first take users answer,
			// and in inner query takes exam question and correct answer
			stmt = DBconnect.prepareStatement(
					"select u.ID,se.examID,se.examType,se.studentAnswers,e.examCourse,e.dateOfExam,e.examSubject from exams e, questions q, users u, studentexams se,questioninexam qie where examType=\"computerized\" AND se.studentID=? AND u.ID=se.studentID AND qie.examID=se.examID AND e.examID=se.examID AND qie.questionID=q.questionID GROUP BY studentID,examID");
			stmt.setString(1, clientMessageArray[1]);
			rs = stmt.executeQuery();

			while (rs.next()) {

				// Initialize new file. studentID, examID, examType
				MyFile temp = new MyFile(rs.getString(1), rs.getString(2), rs.getString(3));
				temp.setExamCourse(rs.getString(5));
				temp.setExamDate(rs.getString(6));
				temp.setExamSubject(rs.getString(7));
				StringBuilder examFile = new StringBuilder();

				// Get questions and answers for this specific exam by the id received from
				// extrnal query
				PreparedStatement ExtractExamQuestionsAndAnswers;
				StringBuilder examQuestionsAndAnswers = new StringBuilder();

				ExtractExamQuestionsAndAnswers = DBconnect.prepareStatement(
						"select q.questionContent,q.correctAnswer from questioninexam qe,questions q where  q.questionID=qe.questionID AND qe.examID=?");

				ExtractExamQuestionsAndAnswers.setString(1, rs.getString(2));
				ResultSet ExtractExamQuestionsAndAnswersResultSet = ExtractExamQuestionsAndAnswers.executeQuery();

				// Extract all inner query data
				while (ExtractExamQuestionsAndAnswersResultSet.next()) {
					examQuestionsAndAnswers.append(ExtractExamQuestionsAndAnswersResultSet.getString(1) + ":::"
							+ ExtractExamQuestionsAndAnswersResultSet.getString(2) + "%%%");
				}

				// Save into string array
				String[] examQuestionsAndAnswersStr = stringSplitter
						.stringByPrecent(examQuestionsAndAnswers.toString());
				examFile.append("StudentID: " + rs.getString(1) + " ExamID: " + rs.getString(2) + "\n");

				// User answers extraction, notice its from external rsultSet
				String[] answers = rs.getString(4).split("###");

				// Combine external and inner result set into file with the user exam answer and
				// questions.
				for (int i = 0; i < answers.length; i++)
					examFile.append("\nQuestion: " + stringSplitter.dollarSplit(examQuestionsAndAnswersStr[i])[0]
							+ "\nCorrect answer: " + stringSplitter.dollarSplit(examQuestionsAndAnswersStr[i])[1]
							+ "\nUser answer " + (i + 1) + ": " + answers[i] + "\n");

				// save into file
				InputStream fos = new ByteArrayInputStream(examFile.toString().getBytes());
				BufferedInputStream bos = new BufferedInputStream(fos);

				temp.setSize(1024000);

				temp.initArray(temp.getSize());
				bos.read(temp.getMybytearray(), 0, temp.getSize());

				// add this to list
				studentExamFiles.add(temp);

			}
			System.out.println(SqlQuery.toString());
			MyFile[] myExamFilesArr = new MyFile[studentExamFiles.size()];
			for (int i = 0; i < myExamFilesArr.length; i++) {
				myExamFilesArr[i] = studentExamFiles.get(i);

			}

			return myExamFilesArr;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pulls all the student grades by studentID,
	 * subjectName and courseName
	 * <p>
	 * <b>Receive:</b> studentID to pull the grades related to him, subject and
	 * course names to pull to the related course
	 * <p>
	 * <b>Return:</b> String of all the grades of the student in course
	 * <p>
	 * 
	 * @author HaniEival
	 */
	public static String pullAllGradesForStudent(String studentID, String subjectName, Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement(
					"SELECT e.dateOfExam, s.studentID,s.examID,s.grade,s.approved,s.noteForStudent,s.reasonForGradeChange, e.examCourse from exams e, studentexams s WHERE e.examID=s.examID AND s.studentID=? AND e.examSubject=?");
			stmt.setString(1, studentID);
			stmt.setString(2, subjectName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1) + ":::");
				sqlResult.append(rs.getString(2) + ":::");
				sqlResult.append(rs.getString(3) + ":::");
				sqlResult.append(rs.getString(4) + ":::");
				sqlResult.append(rs.getString(5) + ":::");
				sqlResult.append(rs.getString(6) + ":::");
				sqlResult.append(rs.getString(7) + ":::");
				sqlResult.append(rs.getString(8) + "%%%");
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method pulls all the students grades by subjectName
	 * and courseName
	 * <p>
	 * <b>Receive:</b> subject and course names to pull to the related course
	 * <p>
	 * <b>Return:</b> String of all the exams grades of the students in course
	 * <p>
	 * 
	 * @author HaniEival
	 */
	public static String pullAllGradesForPrincipal(String subjectName, String courseName, Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement(
					"SELECT e.dateOfExam, s.studentID,s.examID,s.grade,s.approved,s.noteForStudent,s.reasonForGradeChange, e.examCourse from exams e, studentexams s WHERE e.examID=s.examID AND e.examSubject=? AND e.examCourse=?");
			stmt.setString(1, subjectName);
			stmt.setString(2, courseName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1) + ":::");
				sqlResult.append(rs.getString(2) + ":::");
				sqlResult.append(rs.getString(3) + ":::");
				sqlResult.append(rs.getString(4) + ":::");
				sqlResult.append(rs.getString(5) + ":::");
				sqlResult.append(rs.getString(6) + ":::");
				sqlResult.append(rs.getString(7) + ":::");
				sqlResult.append(rs.getString(8) + "%%%");
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> this method checks if the student have ever tested before
	 * in the given exam
	 * <p>
	 * <b>Receive:</b> studentID and examID to check if the student tested before
	 * <p>
	 * <b>Return:</b> True if the student tested and false if its his first time
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static boolean isTestedBefore(String studentID, String examID, Connection dBconnect) {
		PreparedStatement stmt;
		try {
			stmt = dBconnect.prepareStatement("SELECT examType FROM studentexams WHERE studentID=? AND examID=?");
			stmt.setString(1, studentID);
			stmt.setString(2, examID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getString(1).equals("computerized"))
					return true;
				if (rs.getString(1).equals("manual"))
					return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 
	 * <p>
	 * <b>Explanation:</b> the method pushes an exam to the examsawaitingdurationapproval table in the DB in order to get an approval from the principal.
	 * <p>
	 * the query is usually used by the {@link CurrentExamTeacherController}.
	 * <p>
	 * <b>Receive:</b> the method gets a connection object to the DB and a String containing the pushed exam details.
	 * <p>
	 * <b>Return:</b> The method returns a "success" or "fail" String accordingly and a NULL pointer on a SQLException.
	 * <p>
	 * @author EthanButorsky
	 */
	public static String PushExamsToDurationApprovalTable(String clientMessageArray, Connection dBconnect) {
		PreparedStatement stmt;
		String[] examDetails = stringSplitter.stringByExamsAwaitingDurationApprovalParameters(clientMessageArray);
		try {
			stmt = dBconnect.prepareStatement(
					"INSERT INTO examsawaitingdurationapproval (examID, subject, course, durationToAdd, causeOfChange)\r\n"
							+ "VALUES (?,?,?,?,?)");
			stmt.setString(1, examDetails[1]);
			stmt.setString(2, examDetails[2]);
			stmt.setString(3, examDetails[3]);
			stmt.setString(4, examDetails[4]);
			stmt.setString(5, examDetails[5]);
			int rs = stmt.executeUpdate();
			return rs == 1 ? "success" : "fail";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method pulls all the exams from the examsawaitingdurationapproval table in the DB that are awaiting duration approval (examStatus = 'pending')
	 * <p>
	 * the query is usually used by the ExamDurationApprovalPrincipalController.
	 * <p>
	 * <b>Receive:</b> the method gets a connection object to the DB.
	 * <p>
	 * <b>Return:</b> The method returns a string of all the relevant exams specified above where fields are separated by ":::" and rows by "%%%".
	 * <p>
	 * @author EthanButorsky
	 */
	public static String PullExamsAwaitingApproval(Connection DBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = DBconnect
					.prepareStatement("SELECT * FROM examsawaitingdurationapproval WHERE examStatus=\"pending\"");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1) + ":::");
				sqlResult.append(rs.getString(2) + ":::");
				sqlResult.append(rs.getString(3) + ":::");
				sqlResult.append(rs.getString(4) + ":::");
				sqlResult.append(rs.getString(5) + "%%%");
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 
	 * <p>
	 * <b>Explanation:</b> the method removes an exam from the examsawaitingdurationapproval table in the DB with the given id.
	 * <p>
	 * the query is usually used by the {@link ExamDurationApprovalPrincipalController}.
	 * <p>
	 * <b>Receive:</b> the method gets a connection object to the DB and a String containing the to be Deleted exam ID.
	 * <p>
	 * <b>Return:</b> The method returns a "new duration has been disapproved." or "there was an error disapproving the exam." String accordingly and a NULL pointer on a SQLException.
	 * <p>
	 * @author EthanButorsky
	 */
	public static String RemoveExamsAwaitingApproval(String clientMessageArray, Connection DBconnect) {
		PreparedStatement stmt;
		try {
			stmt = DBconnect.prepareStatement("DELETE FROM examsawaitingdurationapproval WHERE examID =?");
			stmt.setString(1, clientMessageArray);
			int rs = stmt.executeUpdate();
			return (rs == 1 ? "new duration has been disapproved." : "there was an error disapproving the exam.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 
	 * <p>
	 * <b>Explanation:</b> the method finds the exam with the given ID in the examsawaitingdurationapproval table in the DB and sets its examStatus field as 'approved'.
	 * <p>
	 * the query is usually used by the {@link ExamDurationApprovalPrincipalController}.
	 * <p>
	 * <b>Receive:</b> the method gets a connection object to the DB and a String containing the chosen exam ID.
	 * <p>
	 * <b>Return:</b> The method returns a "good" or "bad" String accordingly and a NULL pointer on a SQLException.
	 * <p>
	 * @author Ethan Butorsky
	 */
	public static String extendExamDuration(String ExamID, Connection dBconnect) {
		PreparedStatement stmt;
		try {
			stmt = dBconnect.prepareStatement(
					"UPDATE examsawaitingdurationapproval SET" + " examStatus=\"approved\" WHERE examID=?");
			stmt.setString(1, ExamID);
			int rs = stmt.executeUpdate();
			return (rs == 1 ? "good" : "bad");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method extract all manual files for teacher to
	 * check,approve and set grade.
	 * <p>
	 * <b>Receive:</b> The exam composer to check the related exams
	 * <p>
	 * <b>Return:</b> The exam files, array of manual files to be opened at teacher
	 * pending approval controller
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static MyFile[] pullAllManualExamFilesForTeacher(String[] clientMessageArray, Connection DBconnect) {
		ArrayList<MyFile> studentExamFiles = new ArrayList<>();
		PreparedStatement stmt;
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement(
					"SELECT e.examID, e.examSubject,e.examCourse,se.examType, e.dateOfExam, se.exam,se.studentID FROM studentexams se,exams e where e.examID=se.examID AND se.examType=\"manual\" AND examComposer=? AND se.approved=\"pending\"");
			stmt.setString(1, clientMessageArray[1]);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				// Initialize new file studentID,ExamID, examType
				MyFile temp = new MyFile(rs.getString(7), rs.getString(1), rs.getString(4));

				// used setters in order not to change constructor for others use.
				temp.setExamCourse(rs.getString(3));
				temp.setExamDate(rs.getString(5));
				temp.setExamSubject(rs.getString(2));

				InputStream fos = rs.getBinaryStream(6);
				BufferedInputStream bos = new BufferedInputStream(fos);

				temp.setSize(1024000);
				temp.initArray(temp.getSize());
				try {
					bos.read(temp.getMybytearray(), 0, temp.getSize());
				} catch (Exception e) {
					temp.setSize(0);
				}
				studentExamFiles.add(temp);

			}
			System.out.println(SqlQuery.toString());
			MyFile[] myExamFilesArr = new MyFile[studentExamFiles.size()];
			for (int i = 0; i < myExamFilesArr.length; i++) {
				myExamFilesArr[i] = studentExamFiles.get(i);

			}

			return myExamFilesArr;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method finds the exam with the given ID in the examsawaitingdurationapproval table in the DB in order to check exam duration changes.
	 * <p>
	 * the query is usually used by the {@link ComputerizedExamStudentController} and {@link ManualExamStudentController}.
	 * <p>
	 * <b>Receive:</b> the method gets a connection object to the DB and a String containing the chosen exam ID.
	 * <p>
	 * <b>Return:</b> The method returns a string containing the examStatus and duration to add and a NULL pointer on a SQLException.
	 * <p>
	 * @author Ethan Butorsky
	 */
	public static String checkExamDurationChanged(String examID, Connection dBconnect) {
		PreparedStatement stmt;
		StringBuilder sqlResult = new StringBuilder();
		boolean flag = false;
		try {
			stmt = dBconnect.prepareStatement(
					"SELECT examStatus,durationToAdd FROM examsawaitingdurationapproval WHERE examID=?");
			stmt.setString(1, examID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1) + ":::");
				sqlResult.append(rs.getString(2));
				break;
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** v
	 * <p>
	 * <b>Explanation:</b> the method finds the exam with the given ID in the exams table in the DB.
	 * <p>
	 * if the exam is not in the examsawaitingdurationapproval table in the DB it inserts it to the table with a 'locked' ExamStatus. if it's in the table then it just updates the ExamStatus to 'locked'.
	 * <p>
	 * the query is usually used by the {@link CurrentExamTeacherController}.
	 * <p>
	 * <b>Receive:</b> the method gets a connection object to the DB and a String containing the chosen exam ID.
	 * <p>
	 * <b>Return:</b> The method returns a "good" or "bad" String accordingly and a NULL pointer on a SQLException.
	 * <p>
	 * @author EthanButorsky
	 */
	public static String lockExam(String examID, Connection dBconnect) {
		PreparedStatement stmt;
		try {
			stmt = dBconnect.prepareStatement("SELECT * FROM examsawaitingdurationapproval WHERE examID=?");
			stmt.setString(1, examID);
			int rs;
			ResultSet result = stmt.executeQuery();
			if (!result.next()) {
				stmt = dBconnect.prepareStatement(
						"INSERT INTO examsawaitingdurationapproval (examID, subject, course, durationToAdd, causeOfChange,examStatus) "
								+ "VALUES (?,\"NA\",\"NA\",\"NA\",\"NA\",\"locked\")");
				System.out.println("not in table");
				stmt.setString(1, examID);
				rs = stmt.executeUpdate();

			} else {
				stmt = dBconnect.prepareStatement(
						"UPDATE examsawaitingdurationapproval SET examStatus=\"locked\" WHERE examID=?");
				stmt.setString(1, examID);
				rs = stmt.executeUpdate();
			}

			return (rs == 1 ? "good" : "bad");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method finds all the exam with the given composer ID in the exams table in the DB that their status is not 'locked' in the examsawaitingdurationapproval table in the DB.
	 * <p>
	 * the query is usually used by the {@link CurrentExamTeacherController}.
	 * <p>
	 * <b>Receive:</b> the method gets a connection object to the DB and a String containing the chosen composer ID.
	 * <p>
	 * <b>Return:</b> The method returns a string containing returned exams and their fields from the query or a NULL pointer on a SQLException.
	 * <p>
	 * @author EthanButorsky
	 */
	public static String pullCurrentExams(String composerID, Connection DBconnect) {
		PreparedStatement stmt;
		LocalDate date = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		StringBuilder SqlQuery = new StringBuilder();
		try {
			stmt = DBconnect.prepareStatement(
					"SELECT * FROM exams WHERE dateOfExam=? AND examComposer=? AND examID != ALL (SELECT examID FROM examsawaitingdurationapproval WHERE examStatus = 'locked')");
			stmt.setString(1, date.format(formatter));
			stmt.setString(2, composerID);
			ResultSet rs = stmt.executeQuery();
			System.out.println(rs);
			while (rs.next()) {
				SqlQuery.append(rs.getString(1) + ":::");
				SqlQuery.append(rs.getString(2) + ":::");
				SqlQuery.append(rs.getString(3) + ":::");
				SqlQuery.append(rs.getString(4) + ":::");
				SqlQuery.append(rs.getString(5) + ":::");
				SqlQuery.append(rs.getString(6) + ":::");
				SqlQuery.append(rs.getString(7) + "%%%");
			}
			return SqlQuery.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method updates the exam statistics
	 * <p>
	 * <b>Receive:</b> The exam statistics to insert
	 * <p>
	 * <b>Return:</b> "success" or "fail" as result of insertion
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String updateExamStats(String string, Connection dBconnect) {
		PreparedStatement stmt, stmt2;
		StringBuilder sqlResult = new StringBuilder();
		boolean flag = false;
		try {
			stmt2 = dBconnect.prepareStatement("SELECT * from examstats where examID=?");
			stmt2.setString(1, string);
		
			ResultSet rs2 = stmt2.executeQuery();
			int counter = 0;
			while (rs2.next()) {
				rs2.getString(1);
				counter++;
				
			}
			if (counter == 0) {
				stmt = dBconnect
						.prepareStatement("INSERT INTO examstats () VALUES (?,?,?) ON DUPLICATE KEY UPDATE examID=?");
				stmt.setString(1, string);
				stmt.setString(4, string);
				stmt.setString(2, "0");
				stmt.setString(3, "0");
				int rs = stmt.executeUpdate();
				return "success";
			}
			else
				return "This exam is already performed today";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	} 

	

	/**
	 * <p>
	 * <b>Explanation:</b> This method pull the exam instructions to present to the
	 * student before exam
	 * <p>
	 * <b>Receive:</b> The examID for the comments related to the exam
	 * <p>
	 * <b>Return:</b> String of the exam instructions
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String pullTestInstuctions(String examID, Connection dBconnect) {
		PreparedStatement stmt;
		System.out.println(examID);
		StringBuilder sqlResult = new StringBuilder();
		try {
			stmt = dBconnect.prepareStatement("SELECT studentComment FROM exams WHERE examID=?");
			stmt.setString(1, examID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				sqlResult.append(rs.getString(1));
				break;
			}
			return sqlResult.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <p>
	 * <b>Explanation:</b> This method updates the examID by getting the old ID and replacing it
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String getAndUpdateExamID(Connection dBconnect) {
		PreparedStatement currentStmt, update;
		StringBuilder current = new StringBuilder();
		try {
			currentStmt = dBconnect.prepareStatement("SELECT examSerial FROM generator");
			ResultSet rs = currentStmt.executeQuery();
			while (rs.next())
			current.append(rs.getString(1));
			// add 1
			System.out.println(current);
			
			int currentInt = Integer.parseInt(current.toString());
			System.out.println(currentInt);
			currentInt++;
			

			update = dBconnect.prepareStatement("update generator set examSerial =?");
			
			update.setString(1, String.valueOf(currentInt));
			update.executeUpdate();
			
			return String.valueOf(currentInt);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method updates the QuestionID by getting the old ID and replacing it
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	public static String getAndUpdateQuestionID(Connection dBconnect) {
		PreparedStatement currentStmt, update;
		StringBuilder current = new StringBuilder();
		try {
			currentStmt = dBconnect.prepareStatement("SELECT questionSerial FROM generator");
			ResultSet rs = currentStmt.executeQuery();
			while (rs.next())
			current.append(rs.getString(1));
			// add 1

			int currentInt = Integer.parseInt(current.toString());
			System.out.println(currentInt);
			currentInt++;

			update = dBconnect.prepareStatement("update generator set questionSerial =?");
			update.setString(1, String.valueOf(currentInt));
			update.executeUpdate();
			return String.valueOf(currentInt);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
