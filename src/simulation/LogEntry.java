package simulation;

import java.util.ArrayList;
import java.util.List;

public class LogEntry {

	private List<RequestEntry> requestEntries;
	private String sessionId;
	
	public LogEntry(String sessionId) {
		this.sessionId = sessionId;
		this.requestEntries = new ArrayList<LogEntry.RequestEntry>();
	}
	
	public void addRequestEntry(String category, long startTime){
		requestEntries.add(new RequestEntry(category, startTime));
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public List<RequestEntry> getRequestEntries() {
		return requestEntries;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(sessionId + ";");
		for(RequestEntry entry : requestEntries) {
			builder.append(entry.toString());
		}
		return builder.toString();
	}
	
	public static class RequestEntry {
		
		private String category;
		private long startTime;
		
		public RequestEntry(String category, long startTime) {
			this.category = category;
			this.startTime = startTime;
		}
		
		@Override
		public String toString() {
			
			StringBuilder builder = new StringBuilder();
			
			builder.append("\"" + category + "\"" + ":");
			builder.append(startTime + "000000:");
			builder.append(startTime + "000000:");
			builder.append("/" + category + ":");
			builder.append("8080" + ":");
			builder.append("127.0.0.1" + ":");
			builder.append("HTTP/1.1" + ":");
			builder.append("GET" + ":");
			builder.append("<no-query-string>" + ":");
			builder.append("UTF-8" + ";");
			
			return builder.toString();
		}
		
		
		
	}
	
}
