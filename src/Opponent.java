
import java.util.ArrayList;
import java.util.List;

import negotiator.actions.Offer;
import negotiator.issue.Issue;
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
		
		offers = new ArrayList<Offer>();
		rejectedOffers = new ArrayList<Offer>();
		acceptedOffers = new ArrayList<Offer>();
		
		bids = new ArrayList<Bid>();
		rejectedBids = new ArrayList<Bid>();
		acceptedBids = new ArrayList<Bid>();
		
		this.issues = new ArrayList<GirIssue>(issues);
	}
	
	//Add Offer rejected by the agent
	protected void addReject(Offer offer) {
		Bid bid = offer.getBid();
		
		rejectedOffers.add(offer);
		rejectedBids.add(bid);
		
		System.out.println(this.agentId);
		for(GirIssue issue : this.issues) {
			issue.addValue(bid.getValue(issue.number), -1);
		}
	}
	
	//Add Offer Made by the agent
	protected void addOffer(Offer offer) {
		Bid bid = offer.getBid();
		
		offers.add(offer);
		bids.add(bid);
		
		for(GirIssue issue : this.issues) {
			issue.addValue(bid.getValue(issue.number), 0);
		}
	}
	
	//Add Offer Accepted by the agent
	protected void addAccept(Offer offer) {
		Bid bid = offer.getBid();
		
		acceptedOffers.add(offer);
		acceptedBids.add(bid);
		
		System.out.println(this.agentId);
		for(GirIssue issue : this.issues) {
			issue.addValue(bid.getValue(issue.number), 1);
		}
	}
}
