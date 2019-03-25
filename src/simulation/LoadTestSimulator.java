package simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import simulation.BehaviorModel.Behavior;
import simulation.BehaviorModel.MarkovState;
import simulation.BehaviorModel.Transition;
import simulation.TimeseriesForecast.BehaviorForecast;
import simulation.TimeseriesForecast.UserForecast;

public class LoadTestSimulator {

	private static final String INPUT_DIRECTORY_PATH = "C:\\Users\\ahi\\Desktop\\Universität\\Thesis\\Data\\50Categories\\mixMayJune\\forecast";
	private static final String OUTPUT_FILE_NAME = "sessionsTelescope.dat";
	private static final String KEY_PROPHET = "prophet";
	private static final String KEY_TELESCOPE = "telescope";
	private static final String END_STATE = "$";
	private static final long MILLISECONDS_OF_AN_HOUR = 3600000;
	
	public static void main(String[] args) {
		new LoadTestSimulator().simulateLoadTest();
	}
	
	private void simulateLoadTest() {
		CSVBehaviorModelReader behaviorModelReader = new CSVBehaviorModelReader();
		BehaviorModel model = behaviorModelReader.read(INPUT_DIRECTORY_PATH);
		
		CSVForecastReader forecastReader = new CSVForecastReader();
		TimeseriesForecast forecast = forecastReader.read(INPUT_DIRECTORY_PATH, KEY_TELESCOPE);
		
		List<LogEntry> logEntries = this.startSimulation(model, forecast);
		
		LogWriter logWriter = new LogWriter();
		logWriter.write(logEntries, INPUT_DIRECTORY_PATH + "\\" + OUTPUT_FILE_NAME);
		
		System.out.println("done");
	}
	
	private long getEndTimestamp(TimeseriesForecast forecast) {
		BehaviorForecast firstBehaviorForecast = forecast.getBehaviorForecast().get(0);
		long lastTimestamp = firstBehaviorForecast.getUserForecast().get(firstBehaviorForecast.getUserForecast().size() - 1).getTimestamp();
		
		return (lastTimestamp + 3600000 - 1);
	}
	
	private List<LogEntry> startSimulation(BehaviorModel model, TimeseriesForecast forecast) {
				
		long endTimestampOfForecast = this.getEndTimestamp(forecast);
		List<LogEntry> logEntries = new ArrayList<LogEntry>();
		
		for(int i = 0; i < model.getBehaviors().size(); i++) {
			Behavior behavior = model.getBehaviors().get(i);
			BehaviorForecast behaviorForecast = forecast.getBehaviorForecast().get(i);
					
			String strInitialState = behavior.getInitialState();
			MarkovState initialState = behavior.getMarkovStates().get(strInitialState);
			
			for(int hourIndex = 0; hourIndex < behaviorForecast.getUserForecast().size(); hourIndex++) {			
				UserForecast userForecast = behaviorForecast.getUserForecast().get(hourIndex);

				int numberOfUsers = (int) userForecast.getNumberOfUsers();
				
				for(int userIndex = 0; userIndex < numberOfUsers; userIndex++) {
					long currentDuration = 0;
					boolean stopAtIntervalBorder = false;
					if(hourIndex + 1 < behaviorForecast.getUserForecast().size()) {
						// first Parameter: Nutzer brechen früher ab (z.B. der erste Nutzer kann schon abbrechen), second Parameter: bei den letzten Nutzern abbrechen
						stopAtIntervalBorder = behaviorForecast.getUserForecast().get(hourIndex + 1).getNumberOfUsers() <= ((numberOfUsers - userIndex) * 0.125) + 1;				
					}
					//stopAtIntervalBorder = true;
					
					do {
						String sessionId = UUID.randomUUID().toString().replace("-", "");
						LogEntry entry = new LogEntry(sessionId);
						logEntries.add(entry);
						currentDuration = this.simulateMarkovChain(initialState, currentDuration, entry, userForecast.getTimestamp(), endTimestampOfForecast, stopAtIntervalBorder);
					} while(currentDuration < MILLISECONDS_OF_AN_HOUR);					
					
					long hourOverflow = currentDuration - MILLISECONDS_OF_AN_HOUR;					
					double numberOfHoursOverflows = (double)hourOverflow / MILLISECONDS_OF_AN_HOUR;					
					
					int hours = 1;
					for(hours = 1; hours < numberOfHoursOverflows && (hourIndex + hours < behaviorForecast.getUserForecast().size()); hours++) {
						behaviorForecast.getUserForecast().get(hourIndex + hours).subtractUsers(1);	
					}
					if(hourIndex + hours < behaviorForecast.getUserForecast().size()) {
						behaviorForecast.getUserForecast().get(hourIndex + hours).subtractUsers((numberOfHoursOverflows % 1));
					}
				}
			}
		}
		
		return logEntries;
	}
	
	private long simulateMarkovChain(MarkovState currentState, long currentDuration, LogEntry logEntry, long startTime, long endTimestampOfForecast, boolean stopAtIntervalBorder) {
		
		double randomProbability = Math.random();
		double sumProbability = 0.0;
		for(Transition transition : currentState.getTransitions()) {
			sumProbability += transition.getProbability();
			if(randomProbability <= sumProbability) {
				double thinkTimeMean = transition.getMean();
				currentDuration += thinkTimeMean;

				MarkovState targetState = transition.getTargetState();
				if(targetState.getId().equals(END_STATE)) {
					return currentDuration;
				}

				if(startTime + currentDuration >= endTimestampOfForecast) {
					logEntry.addRequestEntry(targetState.getId(), endTimestampOfForecast);
					return endTimestampOfForecast;
				} else {
					logEntry.addRequestEntry(targetState.getId(), startTime + currentDuration);					
				}			
				
				if(currentDuration >= MILLISECONDS_OF_AN_HOUR && stopAtIntervalBorder) {
					return currentDuration;
				}
				
				currentDuration = this.simulateMarkovChain(targetState, currentDuration, logEntry, startTime, endTimestampOfForecast, stopAtIntervalBorder);			
				break;
			}
		}
		
		return currentDuration;
	}

	
}
