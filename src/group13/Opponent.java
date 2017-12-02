package group13;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.util.MathUtils;

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
	
	protected Opponent(AgentID id, List<GirIssue> issues) {
		this.agentId = id;
		
//		offers = new ArrayList<Offer>();
//		rejectedOffers = new ArrayList<Offer>();
//		acceptedOffers = new ArrayList<Offer>();
		
		bids = new ArrayList<Bid>();
		rejectedBids = new ArrayList<Bid>();
		acceptedBids = new ArrayList<Bid>();
		
		this.issues = new ArrayList<GirIssue>(issues);
	}
	
	//Add Offer Made by the agent
	protected void addOffer(Offer offer, int action, double time) {
		Bid bid = offer.getBid();
		
		switch (action) {
		    case -1:
		    	rejectedBids.add(bid);
		    case 0:
		    	bids.add(bid);
		    case 1:
		    	acceptedBids.add(bid);
		    default:
		}
		
		for(GirIssue issue : this.issues) {
			issue.addValue(bid.getValue(issue.number), action);
		}
	}
	
	protected double expectedUtility(Bid bid) {
		return (double)1;
	}
	
//	protected void calculateRates() {
//		int accepted = this.acceptedBids.size();
//		int made     = this.bids.size();
//		int rejected = this.rejectedBids.size();
//		int total    = (accepted + made + rejected);
//		
//		if(total == 0) {return;}
//		
//		for(GirIssue issue : this.issues) {
//			for(GirValue value : issue.girValues) {
//				value.rate = (double) (value.accepted - value.rejected)/total;
//			}
//			issue.girValues.sort(GirValue.rateComparator);
//			issue.weight = issue.girValues.get(0).rate;
//		}
//		System.out.println(this.agentId);
//		this.issues.sort(GirIssue.weightComparator);
//    	for(GirIssue girIssue : this.issues) {
//    		System.out.print("Name:" + girIssue.name + ",Weight: " + girIssue.weight + "||");
//    	}
//    	System.out.println(" ");
//	}
	
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
