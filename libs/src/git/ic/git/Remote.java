package ic.git;


public class Remote {

	public final String name;

	public final String url;

	@Override public boolean equals(Object obj) {
		if (obj instanceof Remote) { final Remote remote = (Remote) obj;
			return name.equals(remote.name);
		}
		else return false;
	}

	@Override public int hashCode() {
		return name.hashCode();
	}

	public Remote(String name, String url) {
		this.name = name;
		this.url = url;
	}

}
