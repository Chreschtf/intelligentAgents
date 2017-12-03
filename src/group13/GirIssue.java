package group13;
import negotiator.issue.ISSUETYPE;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.AdditiveUtilitySpace;
//import negotiator.utility.EvaluatorDiscrete;
import java.util.Comparator;
import java.util.List;

import java.util.ArrayList;

public class GirIssue {
	protected int number;
	protected double weight;
	protected ISSUETYPE type;
	protected List<GirValue> girValues;
	protected String name;
	
	public GirIssue(Issue issue, AdditiveUtilitySpace uSpace, double w0){
		this.number = issue.getNumber();
		this.weight = w0;
		this.type   = issue.getType();
		this.name   = issue.getName();
		
		this.girValues = new ArrayList<GirValue>();
		
		switch (this.type) {
		    case REAL:
		    	break;
		    	
		    case INTEGER:	
		    	break;
		    	
		    case DISCRETE:
		        IssueDiscrete issueDiscrete = (IssueDiscrete) issue;
		        List<ValueDiscrete> allValues = issueDiscrete.getValues();
		        
		        for(ValueDiscrete valueDiscrete : allValues) {
		        	girValues.add(new GirValue(valueDiscrete.getValue()));
		        }
		        girValues.sort(GirValue.discreteComparator);
		        break;
		        
		    default:
		    	break;
		}
	}
	
	protected GirIssue(GirIssue issue) {
		this.number = issue.number;
		this.weight = issue.weight;
		this.type   = issue.type;
		this.name   = issue.name;
		
		this.girValues = new ArrayList<GirValue>();
		
		for(GirValue value : issue.girValues) {
			this.girValues.add(new GirValue(value));
		}
	}
	
	public void addValue(Value value, int action) {
		switch (this.type) {
		    case REAL:
		    	break;
		    	
		    case INTEGER:
		    	break;
		    	
		    case DISCRETE:
		        ValueDiscrete valueDiscrete = (ValueDiscrete)value;
		        String valueString = valueDiscrete.getValue();
		        
		        for(GirValue girValue : this.girValues) {
		        	if(girValue.valueDiscrete.equals(valueString)) {
		        		girValue.addValue(action);
		        	}
		        }
		        break;
		        
		    default:
		    	break;
		}
	}
	
	protected GirValue getValue(Value value) {
		switch (this.type) {
		    case REAL:
		    	break;
		    	
		    case INTEGER:
		    	break;
		    	
		    case DISCRETE:
		        ValueDiscrete valueDiscrete = (ValueDiscrete)value;
		        String valueString = valueDiscrete.getValue();
		        
		        for(GirValue girValue : this.girValues) {
		        	if(girValue.valueDiscrete.equals(valueString)) {
		        		return girValue;
		        	}
		        }
		        break;
		        
		    default:
		    	break;
		}
		return new GirValue("Fail"); //Just for compiling sake
	}
	
	protected void calcIV(int total) {
		double max = 0;
		
		//IV
		for(GirValue girValue : this.girValues) {
        	girValue.vi = (double)(1 + girValue.accepted)/(1 + total);
        	if(girValue.vi > max) {
        		max = girValue.vi;
        	}
        }
		
		//Normalise by the max
		for(GirValue girValue : this.girValues) {
        	girValue.vi = girValue.vi / max;
        }
	}
	
	protected void calcFrequencies(int total) {
		double sum = 0;
		
		//distribution frequency
		for(GirValue value : this.girValues) {
			value.freq = (double) (1 + value.accepted)/(1 + total);
			sum = sum + value.freq;
		}
		
		//normalise to sum 1
		for(GirValue value : this.girValues) {
			value.freq = value.freq/sum;
		}
	}
	
	protected double[] getFreqs() {
		double[] freqs = new double[this.girValues.size()];
		for(int i = 0; i < this.girValues.size(); i++) {
        	freqs[i] = girValues.get(i).freq;
        }
		return freqs;
	}
	
	protected double calcUtility(GirIssue opIssue) {
		double utility = 0;
		double freq;
		double vi;
		
		for(int i = 0; i < this.girValues.size(); i++) {
			freq = this.girValues.get(i).freq;
			vi   = opIssue.girValues.get(i).vi;
			utility = utility + ( freq * vi );
        }
		return utility;
	}
		
	public static Comparator<GirIssue> weightComparator = new Comparator<GirIssue>() {
		public int compare(GirIssue i1, GirIssue i2) {
		   return Double.compare(i2.weight, i1.weight);
	   }
	};
	
	public static Comparator<GirIssue> numberComparator = new Comparator<GirIssue>() {
		public int compare(GirIssue i1, GirIssue i2) {
		   return Integer.compare(i1.number, i2.number);
	   }
	};

	@Override
	public String toString() {
		return "GirIssue [name=" + name + ", number=" + number + ", weight=" + weight + "]";
	}
}
