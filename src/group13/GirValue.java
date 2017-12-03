package group13;
import java.util.Comparator;
import negotiator.issue.ISSUETYPE;

class GirValue {
	protected String valueDiscrete;
	protected int valueInt;
	protected ISSUETYPE type;
	protected int offered;
	protected double freq;
	protected double vi; //issue valuation
	protected int accepted;
	protected int rejected;
	protected double probability;
	protected double rate;
	
	protected GirValue(String value){
		this.valueDiscrete = value;
	}
	
	protected GirValue(int value){
		this.valueInt = value;
	}
	
	
	
	protected GirValue(GirValue value) {
		this.valueDiscrete = value.valueDiscrete;
		this.valueInt      = value.valueInt;
		this.type          = value.type;
		
//		this.freq          = value.freq;
//		this.vi            = value.vi;
//		
//		this.offered       = value.offered;
//		this.accepted      = value.accepted;
//		this.rejected      = value.rejected;
		
//		this.probability = probability;
//		this.rate = rate;
	}

	protected void addValue(int action) {
		switch (action) {
		    case -1:
		    	this.rejected++;
		    	break;
		    case 0:
		    	this.offered++;
		    	break;
		    case 1:
		        this.accepted++;
		        break;
		    default:
		}
	}
	
	public String toString() {
		return "GirValue [valueDiscrete=" + valueDiscrete + ", offered=" + offered + 
				", freq=" + freq + ", vi=" + vi + "]";
	}

	public static Comparator<GirValue> discreteComparator = new Comparator<GirValue>() {
		public int compare(GirValue v1, GirValue v2) {

			String d1 = v1.valueDiscrete.toUpperCase();
			String d2 = v2.valueDiscrete.toUpperCase();

			//ascending order
			return d1.compareTo(d2);
		}
	};
	
//	public static Comparator<GirValue> rateComparator = new Comparator<GirValue>() {
//		public int compare(GirValue value1, GirValue value2) {
//			return Double.compare(value2.rate, value1.rate);
//		}
//	};
}
