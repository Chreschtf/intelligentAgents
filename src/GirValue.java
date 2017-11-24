import negotiator.issue.ISSUETYPE;

class GirValue {
	protected String valueDiscrete;
	protected int valueInt;
	protected ISSUETYPE type;
	protected int accepted;
	protected int rejected;
	protected double probability;
	
	protected GirValue(String value){
		this.valueDiscrete = value;
	}
	
	protected GirValue(int value){
		this.valueInt = value;
	}
}
