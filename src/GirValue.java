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

	@Override
	public String toString() {
		return "GirValue [valueDiscrete=" + valueDiscrete + ", accepted=" + accepted + ", rejected=" + rejected + "]";
	}
}
