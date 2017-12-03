package group13;

import negotiator.Bid;
//import org.apache.commons.math3.stat.inference.ChiSquareTest;

//import java.util.ArrayList;
import java.util.List;

public class Window {
	protected static int size = 30;
	protected static double alpha = 0.1;
	protected static double beta  = 5;
	
	protected List<GirIssue> issues;
//	protected static ChiSquareTest chiSqTest = new ChiSquareTest();

	protected Window(List<Bid> bids) {
		this.issues = Opponent.copyDomainMap();
		
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
			issue.calcFrequencies(Window.size);
		}
	}
	
	protected void compareWindows(Window w0, Opponent op, double time) {
		double pval;
		boolean update;
		int updates = 0;
		
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
			
//			pval = this.chiSquareTest(i0.getFreqs(), i1.getFreqs());
			pval = this.freqTest(i0.getFreqs(), i1.getFreqs());
			
			if(pval<0.08) { //no issue change, weight is important
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
		if(updates > 0) {op.normaliseWeights();}
	}
	
	protected double calcDelta(double time) {
		return Window.alpha * (1 - (Math.pow(time, Window.beta)));
	}
	
//	protected double chiSquareTest(double[] f0, double[] f1) {
//		long[] f1long = new long[f1.length];
//		
//		for(int i = 0; i<f1.length; i++) {
//			f1long[i] = (long)f1[i];
//		}
//		return chiSqTest.chiSquareTest(f0, f1long);
//	}

	protected double freqTest(double[] f0, double[] f1) {
		double sum = 0;
		
		for(int i = 0; i<f1.length; i++) {
//			System.out.println(f0[i] + " " +f1[i]);
//			sum = sum + Math.pow( (f0[i] - f1[i]), 2);
			sum = sum + Math.abs(f0[i] - f1[i]);
		}
		sum = sum / f1.length;
//		System.out.println(sum);
		return sum;
	}
	
	protected int compareIssue(GirIssue i0, GirIssue i1, GirIssue io) {
		double u0 = i0.calcUtility(io);
		double u1 = i1.calcUtility(io);
		return Double.compare(u1, u0);
	}
}
