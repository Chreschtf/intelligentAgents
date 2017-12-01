package group13;

import negotiator.Bid;

import java.util.ArrayList;
import java.util.List;

public class Window {
	protected static int size = 30;
	protected List<GirIssue> issues;
	
	protected Window(List<Bid> bids) {
		this.issues = new ArrayList<GirIssue>(Agent13.model.issues);
		
		for(Bid bid : bids) {
			for(GirIssue issue : this.issues) {
				issue.addValue(bid.getValue(issue.number), 0);
			}
		}
		this.updateFrequencies();
	}
	
	protected void updateFrequencies() {
		for(GirIssue issue : this.issues) {
			for(GirValue value : issue.girValues) {
				value.freq = (double) (value.accepted)/Window.size;
			}
		}
	}
}
