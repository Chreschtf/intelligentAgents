package group13;

import negotiator.Bid;
import org.apache.commons.math3.stat.inference.ChiSquareTest;

import java.util.ArrayList;
import java.util.List;

public class Window {
	protected static int size = 30;
	protected List<GirIssue> issues;
	protected static ChiSquareTest chiSqTest = new ChiSquareTest();
	
	protected Window(List<Bid> bids) {
		this.issues = new ArrayList<GirIssue>(Agent13.model.issues);
		
		for(Bid bid : bids) {
			for(GirIssue issue : this.issues) {
				issue.addValue(bid.getValue(issue.number), 0);
			}
		}
		this.updateFrequencies();
		this.issues.sort(GirIssue.numberComparator);
	}
	
	protected void updateFrequencies() {
		for(GirIssue issue : this.issues) {
			for(GirValue value : issue.girValues) {
				value.freq = (double) (1 + value.accepted)/(1 + Window.size);
				value.freq = this.normalize(value.freq);
			}
		}
	}
	
	protected void compareWindows(Window w0) {
		for(int i = 0; i<this.issues.size(); i++) {
			double pval = this.chiSquareTest(w0.issues.get(i).getFreqs(), this.issues.get(i).getFreqs());
		}
	}
	
	protected double chiSquareTest(double[] f0, double[] f1) {
		long[] f1long = new long[f1.length];
		
		for(int i = 0; i<f1.length; i++) {
			f1long[i] = (long)f1[i];
		}
		return chiSqTest.chiSquareTest(f0, f1long);
	}
	
	protected double normalize(double value) {
		double low  = 0;
		double high = 1;
		double normHigh = 1;
		double normLow  = 0;
		
	    return ((value - low) / (high - low)) * (normHigh - normLow) + normLow;
	}
}
