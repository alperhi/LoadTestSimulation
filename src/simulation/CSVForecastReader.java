package simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import simulation.TimeseriesForecast.BehaviorForecast;
import simulation.TimeseriesForecast.UserForecast;

public class CSVForecastReader {

	public TimeseriesForecast read(final String directory, final String filePrefix) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			File[] files = new File(directory).listFiles();
			TimeseriesForecast timeseriesForecast = new TimeseriesForecast();
			for (File file : files) {
				if (!(file.getPath().endsWith(".csv") && file.getName().startsWith(filePrefix))) {
					continue;
				}
				try (CSVParser parser = new CSVParser(new BufferedReader(new FileReader(file)),
						CSVFormat.DEFAULT.withDelimiter(','))) {
					
					boolean firstLine = true;
					BehaviorForecast behaviorForecast = new BehaviorForecast();
					timeseriesForecast.getBehaviorForecast().add(behaviorForecast);
					
					for (CSVRecord record : parser) {
						if(firstLine) {
							firstLine = false;
							continue;
						}
						long timestamp = dateFormat.parse(record.get(0)).getTime();
						UserForecast userForecast = new UserForecast(Double.parseDouble(record.get(1)), timestamp);
						behaviorForecast.getUserForecast().add(userForecast);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			return timeseriesForecast;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
