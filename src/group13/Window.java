package group13;

import negotiator.Bid;
//import org.apache.commons.math3.stat.inference.ChiSquareTest;

import java.util.ArrayList;
import java.util.List;

public class Window {
	protected static int size = 30;
	protected static double alpha = 0.1;
	protected static double beta  = 5;
	
	protected List<GirIssue> issues;
//	protected static ChiSquareTest chiSqTest = new ChiSquareTest();

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
			}
			issue.normaliseValues();
		}
	}
	
	protected void compareWindows(Window w0, Opponent op, double time) {
		double pval;
		boolean update;
		int updates = 0;
		
//		List<GirIssue> opIssues = new ArrayList<GirIssue>(op.issues);
		op.issues.sort(GirIssue.numberComparator);
		
		GirIssue i0;
		GirIssue i1;
		GirIssue io;
		
		double delta = this.calcDelta(time);
		
		for(int i = 0; i<this.issues.size(); i++) {
			update = false;
			
			i0 = w0.issues.get(i);
			i1 = this.issues.get(i);
			io = op.issues.get(i);
			
			pval = this.chiSquareTest(i0.getFreqs(), i1.getFreqs());
			
			if(pval>0.05) { //no issue change, weight is important
				update = true;
			}else {
				if(this.compareIssue(i0, i1, io) > 0){//higher utility, issue went up in importance 
					update = true;
				}
			}
			if(update) { 
				io.weight = io.weight + delta;
				updates++;
			}
		}//for each issue
		if(updates > 0) {GirIssue.normaliseWeights(op.issues);}
	}
	
	protected double calcDelta(double time) {
		return Window.alpha * (1 - (Math.pow(time, Window.beta)));
	}
	
	protected double chiSquareTest(double[] f0, double[] f1) {
		long[] f1long = new long[f1.length];
		
		for(int i = 0; i<f1.length; i++) {
			f1long[i] = (long)f1[i];
		}
		return (double)1;
//		return chiSqTest.chiSquareTest(f0, f1long);
	}
	
	protected int compareIssue(GirIssue i0, GirIssue i1, GirIssue io) {
		double u0 = i0.calcUtility(io);
		double u1 = i1.calcUtility(io);
		return Double.compare(u1, u0);
	}
}
