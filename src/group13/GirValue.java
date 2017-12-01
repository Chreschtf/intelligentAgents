package group13;
import java.util.Comparator;

import negotiator.issue.ISSUETYPE;
import negotiator.issue.ValueDiscrete;

class GirValue {
	protected String valueDiscrete;
	protected int valueInt;
	protected ISSUETYPE type;
	protected int accepted;
	protected int rejected;
	protected int offered;
	protected double probability;
	protected double rate;
	protected double freq;
	
	protected GirValue(String value){
		this.valueDiscrete = value;
	}
	
	protected GirValue(int value){
		this.valueInt = value;
	}
	
	protected void addValue(int action) {
		switch (action) {
	    case -1:
	    	this.rejected++;
	    case 0:
	    	this.offered++;
	    case 1:
	        this.accepted++;
	    default:
	    	break;
		}
	}
	
	@Override
	public String toString() {
		return "GirValue [valueDiscrete=" + valueDiscrete + ", accepted=" + accepted + 
			   ", rejected=" + rejected + ", rate=" + rate + "]";
	}
	
	public static Comparator<GirValue> rateComparator = new Comparator<GirValue>() {
		public int compare(GirValue value1, GirValue value2) {
			return Double.compare(value2.rate, value1.rate);
		}
	};
}
