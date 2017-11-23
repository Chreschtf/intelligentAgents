
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
	
	protected Opponent(AgentID id) {
		this.agentId = id;
		
		offers = new ArrayList<Offer>();
		rejectedOffers = new ArrayList<Offer>();
		acceptedOffers = new ArrayList<Offer>();
		
		bids = new ArrayList<Bid>();
		rejectedBids = new ArrayList<Bid>();
		acceptedBids = new ArrayList<Bid>();
	}
	
	//Add Offer rejected by the agent
	protected void addReject(Offer offer) {
		rejectedOffers.add(offer);
		rejectedBids.add(offer.getBid());
	}
	
	//Add Offer Made by the agent
	protected void addOffer(Offer offer) {
		offers.add(offer);
		bids.add(offer.getBid());
	}
	
	//Add Offer made by the agent
	protected void addAccept(Offer offer) {
		acceptedOffers.add(offer);
		acceptedBids.add(offer.getBid());
		
	}
}
