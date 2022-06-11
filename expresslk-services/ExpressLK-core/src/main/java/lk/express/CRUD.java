package lk.express;

public enum CRUD {

	Create("create"), Read("view"), Update("edit"), Delete("delete");

	private String permission;

	CRUD(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}
}
