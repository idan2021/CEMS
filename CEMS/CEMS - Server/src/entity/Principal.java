package entity;

public class Principal extends User {

	public Principal(String id, String firstName, String lastName, String userName, String password, String email,
			String role) {
		super(id, firstName, lastName, userName, password, email);
		this.role = Role.principal;
	}
}
