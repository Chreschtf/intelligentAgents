
import java.util.ArrayList;
import java.util.List;

import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.AgentID;
import negotiator.Bid;


public class Opponent {
	public ArrayList<Offer> offers;
	public ArrayList<Offer> rejectedOffers;
	public ArrayList<Offer> acceptedOffers;
	
	public ArrayList<Bid> bids;
	public ArrayList<Bid> rejectedBids;
	public ArrayList<Bid> acceptedBids;
	
	public AgentID agentId;
	
	public Opponent(AgentID id) {
		this.agentId = id;
		
		offers = new ArrayList<Offer>();
		rejectedOffers = new ArrayList<Offer>();
		acceptedOffers = new ArrayList<Offer>();
		
		bids = new ArrayList<Bid>();
		rejectedBids = new ArrayList<Bid>();
		acceptedBids = new ArrayList<Bid>();
	}
	
	//Add Offer rejected by the agent
	public void addReject(Offer offer) {
		rejectedOffers.add(offer);
		rejectedBids.add(offer.getBid());
		
		List<Issue> issues = offer.getBid().getIssues();
		for(int i = 0; i < issues.size(); i++) {
			issues.get(i);
		}
	}
	
	//Add Offer Made by the agent
	public void addOffer(Offer offer) {
		offers.add(offer);
		bids.add(offer.getBid());
	}
	
	//Add Offer made by the agent
	public void addAccept(Offer offer) {
		acceptedOffers.add(offer);
		acceptedBids.add(offer.getBid());
		
	}
}
