package strutter.helper;

public class MenuClass {
	
	private String menulable;
	private String link;
	private String target;
	private String permission;
	
	public MenuClass(String line) {
		String[] liner = line.split(",");
		setMenulable(liner[0]);
		setLink(liner[1]);
		setTarget(liner[2]);
		setPermission(liner[3]);
	}

	public String generate(String permission, String active) {
		
		String line = "<li><a href='" + getLink() + "' target='" + getTarget() + "'>" + getMenulable() + "</a></li>";
		return line;
	}
	
	
	public String getMenulable() {
		return menulable;
	}

	public void setMenulable(String menulable) {
		this.menulable = menulable;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
}
