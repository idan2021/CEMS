package entity;

public class Teacher extends User {

	public Teacher(String id, String firstName, String lastName, String userName, String password, String email,
			String role) {
		super(id, firstName, lastName, userName, password, email);
		this.role = Role.teacher;
	}
}
