package group13;

import java.util.ArrayList;
import java.util.List;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.parties.NegotiationInfo;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;
//import negotiator.issue.Issue;
//import negotiator.parties.NegotiationInfo;
//import negotiator.utility.AbstractUtilitySpace;
//import negotiator.utility.AdditiveUtilitySpace;
import negotiator.AgentID;
import negotiator.Bid;


public class Opponent {	
	protected ArrayList<Bid> bids;
	protected ArrayList<Bid> rejectedBids;
	protected ArrayList<Bid> acceptedBids;
	
	protected AgentID agentId;
	
	protected List<GirIssue> issues;
	
	protected static List<GirIssue> domainMap;
	
	protected Window w0;
	
	protected Opponent() {				
		this.bids         = new ArrayList<Bid>();
		this.rejectedBids = new ArrayList<Bid>();
		this.acceptedBids = new ArrayList<Bid>();
		
		this.issues  = new ArrayList<GirIssue>();
	}
	
	protected static void mapDomain(NegotiationInfo info) {
    	AbstractUtilitySpace utilitySpace = info.getUtilitySpace();
    	AdditiveUtilitySpace uSpace = (AdditiveUtilitySpace) utilitySpace;
    	
    	List<Issue>issues = uSpace.getDomain().getIssues();
    	
    	double w0 = (double)1/issues.size(); //Initial Weights
    	
    	Opponent.domainMap = new ArrayList<GirIssue>();
    	
    	for (Issue issue : issues) {
    		Opponent.domainMap.add(new GirIssue(issue, uSpace, w0));
    	}
    }
	
	protected static List<GirIssue> copyDomainMap() {
		List<GirIssue> map = new ArrayList<GirIssue>();
		
		for(GirIssue issue : domainMap) {
			map.add(new GirIssue(issue));
		}
		return map;
	}
	
	protected Opponent(AgentID id) {
		this.agentId = id;
		
		this.bids         = new ArrayList<Bid>();
		this.rejectedBids = new ArrayList<Bid>();
		this.acceptedBids = new ArrayList<Bid>();
		
		this.issues = Opponent.copyDomainMap();
	}
	
	//Add Action by the Opponent
	protected void addOffer(Offer offer, int action, double time) {
		Bid bid = offer.getBid();
		
		boolean window = false;
		
		switch (action) {
		    case -1:
		    	rejectedBids.add(bid);
		    	break;
		    	
		    case 0:
		    	bids.add(bid);
		    	if((bids.size() % Window.size) == 0){window = true;}
		    	break;
		    	
		    case 1:
		    	acceptedBids.add(bid);
		    	break;
		    	
		    default:
		    	break;
		}
		
		for(GirIssue issue : this.issues) {
			issue.addValue(bid.getValue(issue.number), action);
		}
		
		if(window) {
			this.newWindow(time);
		}
	}
	
	protected void newWindow(double time) {
		int total = bids.size();
		
		for(GirIssue issue : this.issues) {issue.calcIV(total);}
		

		Window w1 = new Window(bids.subList( (total - Window.size), (total - 1)));
		
		if(this.w0 != null) {
			w1.compareWindows(this.w0, this, time);
		}
		this.w0 = w1;
		this.print2();
	}
	
	protected double expectedUtility(Bid bid) {
		double utility = 0;
		for(GirIssue issue : this.issues) {
			GirValue value = issue.getValue(bid.getValue(issue.number));
			utility = utility + ( value.vi * issue.weight );
		}
		return utility;
	}
	
	protected void normaliseWeights() {
		double sum = 0;
		
		for(GirIssue issue : this.issues) {
			sum = sum + issue.weight;
        }
		
		for(GirIssue issue : this.issues) {
			issue.weight = issue.weight/sum;
        }
	}
	
	protected void print() {
		System.out.println(this.agentId);
		for(GirIssue issue : issues) {
			for(GirValue value : issue.girValues) {
				System.out.println(value.toString());
			}
		}
		System.out.println(" ");
	}
	
	protected void print2() {
		System.out.println(this.agentId);
		for(GirIssue issue : issues) {
			System.out.println(issue.name + ": " +issue.weight);
		}
		System.out.println(" ");
	}
}
