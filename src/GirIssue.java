import negotiator.issue.ISSUETYPE;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueReal;
import negotiator.issue.ValueInteger;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.utility.EvaluatorDiscrete;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class GirIssue {
	protected int number;
	protected double weight;
	protected ISSUETYPE type;
	protected List<GirValue> girValues;
	
	protected GirIssue(Issue issue, AdditiveUtilitySpace uSpace){
		this.number = issue.getNumber();
		this.weight = uSpace.getWeight(this.number);
		this.type   = issue.getType();
		
		this.girValues = new ArrayList<GirValue>();
		
		switch (this.type) {
		    case REAL:
		        IssueReal issueReal = (IssueReal) issue;
		        double upperRealBound = issueReal.getUpperBound();
		        double lowerRealBound = issueReal.getLowerBound();
	
		        // accessing to the old value
		        // ValueReal valueReal = (ValueReal) bid.getValue(issueNumber);
		        
		    case INTEGER:
		        IssueInteger issueInteger = (IssueInteger) issue;
		        int upperIntegerBound = issueInteger.getUpperBound();
		        int lowerIntegerBound = issueInteger.getLowerBound();
	
		        // accessing to the old value
		        // ValueInteger valueInteger = (ValueInteger) bid.getValue(issueNumber);
	
	
		    case DISCRETE:
		        IssueDiscrete issueDiscrete = (IssueDiscrete) issue;
		        List<ValueDiscrete> allValues = issueDiscrete.getValues();
		        
		        for(ValueDiscrete valueDiscrete : allValues) {
		        	girValues.add(new GirValue(valueDiscrete.getValue()));
		        }
		}
	}
	
	protected void addValue(Value value, int action) {
		switch (this.type) {
		    case REAL:
		        
		    case INTEGER:
	
		    case DISCRETE:
		        ValueDiscrete valueDiscrete = (ValueDiscrete)value;
		        String valueString = valueDiscrete.getValue();
		        
		        for(GirValue girValue : this.girValues) {
		        	System.out.println(girValue.valueDiscrete + " " + "Action " + action);
		        	
		        	if(girValue.valueDiscrete == valueString) {
		        		if(action >= 0) {
		        			System.out.println(girValue.valueDiscrete + "Accepted");
		        			girValue.accepted++;
		        		}else {
		        			System.out.println(girValue.valueDiscrete + "Rejected");
		        			girValue.rejected++;
		        		}
		        	}	
		        }
		}
	}
	
	protected static Comparator<GirIssue> weightComparator = new Comparator<GirIssue>() {
		public int compare(GirIssue i1, GirIssue i2) {
		   return Double.compare(i2.weight, i1.weight);
	   }
	};
	
	protected static Comparator<GirIssue> numberComparator = new Comparator<GirIssue>() {
		public int compare(GirIssue i1, GirIssue i2) {
		   return Integer.compare(i1.number, i2.number);
	   }
	};

}