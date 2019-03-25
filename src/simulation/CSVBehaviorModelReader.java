package simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import simulation.BehaviorModel.Behavior;
import simulation.BehaviorModel.MarkovState;
import simulation.BehaviorModel.Transition;

public class CSVBehaviorModelReader {

	public BehaviorModel read(final String directory) {
		try {
			File[] files = new File(directory).listFiles();
			BehaviorModel model = new BehaviorModel();
			model.setBehaviors(new ArrayList<Behavior>());
			for (File file : files) {
				if (!(file.getPath().endsWith(".csv") && file.getName().startsWith("behaviormodel"))) {
					continue;
				}
				Behavior behavior = new Behavior();
				behavior.initMarkovStates();
				model.getBehaviors().add(behavior);
				try (CSVParser parser = new CSVParser(new BufferedReader(new FileReader(file)),
						CSVFormat.DEFAULT.withDelimiter(','))) {

					List<MarkovState> statesOfBehavior = new ArrayList<MarkovState>();
					for (CSVRecord record : parser) {
						if (behavior.getMarkovStates().isEmpty()) {
							for (int i = 1; i < record.size(); i++) {
								MarkovState state = new MarkovState();
								state.setId(record.get(i));
								state.setTransitions(new ArrayList<Transition>());
								behavior.addMarkovState(state);
								statesOfBehavior.add(state);
							}
						} else {
							String id = record.get(0);
							if (id.endsWith("*")) {
								id = id.replace("*", "");
								behavior.setInitialState(id);
							}
							MarkovState currentMarkovState = behavior.getMarkovStates().get(id);
							
							for (int i = 1; i < record.size(); i++) {
								String cell = record.get(i);
								String[] cellParts = cell.split(";");
								if (cellParts[0].equals("0.0")) {
									continue;
								}
								Transition transition = new Transition();
								transition.setProbability(Double.parseDouble(cellParts[0]));
								String thinkTime = cellParts[1].replace("n(", "").replace(")", "").trim();
								String[] meanAndDeviationOfThinkTime = thinkTime.split(" ");
								transition.setMean((double) Integer.parseInt(meanAndDeviationOfThinkTime[0]));
								transition.setDeviation((double) Integer.parseInt(meanAndDeviationOfThinkTime[1]));
								transition.setTargetState(statesOfBehavior.get(i - 1));
								currentMarkovState.getTransitions().add(transition);
							}
						}
					}
				}
			}

			return model;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
