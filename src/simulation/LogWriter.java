package simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LogWriter {

	public void write(List<LogEntry> logEntries, final String fileName) {
		try (FileWriter writer = new FileWriter(fileName)) {
			StringBuilder builder = new StringBuilder();
			for (LogEntry entry : logEntries) {
				builder.append(entry.toString());
				builder.append("\n");
			}
			writer.append(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
