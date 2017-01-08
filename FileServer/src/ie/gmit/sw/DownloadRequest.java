package ie.gmit.sw;

import java.util.Date;

public class DownloadRequest extends Request{
	private static final long serialVersionUID = 1L;
	private String filename;
	
	public DownloadRequest(String command, String host, Date d) {
		super(command, host, d);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
