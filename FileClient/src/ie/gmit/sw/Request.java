package ie.gmit.sw;

import java.io.Serializable;
import java.util.Date;

public class Request implements Serializable{
	private static final long serialVersionUID = 1L;
	private String command;
	private String host;
	private Date d;
	
	public Request(String command, String host, Date d){
		setCommand(command);
		setHost(host);
		setDate(d);
	}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Date getDate() {
		return d;
	}

	public void setDate(Date d) {
		this.d = d;
	}
}
