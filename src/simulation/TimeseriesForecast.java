package simulation;

import java.util.ArrayList;
import java.util.List;

public class TimeseriesForecast {

	private List<BehaviorForecast> behaviorForecast = new ArrayList<TimeseriesForecast.BehaviorForecast>();

	public List<BehaviorForecast> getBehaviorForecast() {
		return behaviorForecast;
	}

	public static class BehaviorForecast {

		private List<UserForecast> userForecast = new ArrayList<UserForecast>();

		public List<UserForecast> getUserForecast() {
			return userForecast;
		}
	}

	public static class UserForecast {

		private double numberOfUsers;
		private long timestamp;

		public UserForecast(final double numberOfUsers, final long timestamp) {
			this.numberOfUsers = numberOfUsers;
			this.timestamp = timestamp;
		}
		
		public void subtractUsers(double users) {
			this.numberOfUsers -= users;
		}

		public double getNumberOfUsers() {
			return numberOfUsers;
		}

		public long getTimestamp() {
			return timestamp;
		}
	}

}
