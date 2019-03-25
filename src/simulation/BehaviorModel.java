package simulation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Manuel Palenga
 *
 */
public class BehaviorModel {

	private List<Behavior> behaviors;
	
	public void setBehaviors(List<Behavior> behaviors) {
		this.behaviors = behaviors;
	}
	
	public List<Behavior> getBehaviors() {
		return behaviors;
	}
	
	public static class Behavior {
		
		private String name;
		
		private String initialState;
		
		private Double probability;
		
		private Map<String, MarkovState> markovStates;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getInitialState() {
			return initialState;
		}
		
		public void setInitialState(String initialState) {
			this.initialState = initialState;
		}
		
		public Double getProbability() {
			return probability;
		}
		
		public void setProbability(Double probability) {
			this.probability = probability;
		}
		
		public Map<String, MarkovState> getMarkovStates() {
			return markovStates;
		}
		
		public void addMarkovState(MarkovState state) {
			if(state.getId() == null) {
				throw new IllegalArgumentException("ID from MarkovState is not defined.");
			}
			markovStates.put(state.getId(), state);
		}
		
		public void initMarkovStates() {
			this.markovStates = new HashMap<String, MarkovState>();
		}
	}
	
	public static class MarkovState {
		
		private String id;
		
		private List<Transition> transitions;
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public List<Transition> getTransitions() {
			return transitions;
		}
		
		public void setTransitions(List<Transition> transitions) {
			this.transitions = transitions;
		}
		
	}
	
	public static class Transition {
		
		private MarkovState targetState;
		
		private Double probability;
		
		private Double mean;
		
		private Double deviation;

		public MarkovState getTargetState() {
			return targetState;
		}

		public void setTargetState(MarkovState targetState) {
			this.targetState = targetState;
		}

		public Double getProbability() {
			return probability;
		}

		public void setProbability(Double probability) {
			this.probability = probability;
		}

		public Double getMean() {
			return mean;
		}

		public void setMean(Double mean) {
			this.mean = mean;
		}

		public Double getDeviation() {
			return deviation;
		}

		public void setDeviation(Double deviation) {
			this.deviation = deviation;
		}
	}
	
}
