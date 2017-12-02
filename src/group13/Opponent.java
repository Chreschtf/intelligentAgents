package group13;

import java.util.ArrayList;
import java.util.List;
import negotiator.actions.Offer;
import negotiator.AgentID;
import negotiator.Bid;


public class Opponent {
	protected ArrayList<Offer> offers;
	protected ArrayList<Offer> rejectedOffers;
	protected ArrayList<Offer> acceptedOffers;
	
	protected ArrayList<Bid> bids;
	protected ArrayList<Bid> rejectedBids;
	protected ArrayList<Bid> acceptedBids;
	
	protected AgentID agentId;
	
	protected List<GirIssue> issues;
	
	protected Window w0;
	
	protected Opponent(AgentID id, List<GirIssue> issues) {
		this.agentId = id;
		
//		offers = new ArrayList<Offer>();
//		rejectedOffers = new ArrayList<Offer>();
//		acceptedOffers = new ArrayList<Offer>();
		
		bids         = new ArrayList<Bid>();
		rejectedBids = new ArrayList<Bid>();
		acceptedBids = new ArrayList<Bid>();
		
		this.issues  = new ArrayList<GirIssue>(issues);
	}
	
	//Add Action by the Opponent
	protected void addOffer(Offer offer, int action, double time) {
		Bid bid = offer.getBid();
		
		boolean window = false;
		
		switch (action) {
		    case -1:
		    	rejectedBids.add(bid);
		    
		    case 0:
		    	bids.add(bid);
		    	if((bids.size() % Window.size) == 0){window = true;}
		    case 1:
		    	acceptedBids.add(bid);
		    default:
		}
		
		for(GirIssue issue : this.issues) {
			issue.addValue(bid.getValue(issue.number), action);
		}
		
		if(window) {
			Window w1 = new Window(bids);
			if(this.w0 != null) {
				w1.compareWindows(w1, this, time);
			}
			this.w0 = w1;
		}
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
	
	protected double expectedUtility(Bid bid) {
		double utility = 0;
		for(GirIssue issue : this.issues) {
			GirValue value = issue.getValue(bid.getValue(issue.number));
			utility = utility + ( value.vi * issue.weight );
		}
		return utility;
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
