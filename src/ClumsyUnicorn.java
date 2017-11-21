import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class ClumsyUnicorn extends AbstractNegotiationParty {
    private final String description = "Clumsy Unicorn";

    private Bid lastReceivedOffer; // offer on the table
    private Bid myLastOffer;
    private ArrayList<Offer> receivedOffers;
    
    private double utilityThreshold;
    private Opponent op1;
    private Opponent op2;
    private Offer lastOffer;
    private double maxUtility;
    private double minUtility;
    private List<Issue> allIssues;
    
    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        receivedOffers = new ArrayList<Offer>() ;

        try {
            Bid minBid = this.utilitySpace.getMinUtilityBid();
            Bid maxBid = this.utilitySpace.getMaxUtilityBid();
            maxUtility = getUtility(maxBid);
            minUtility = getUtility(minBid);
            utilityThreshold = (maxUtility-minUtility)/2+minUtility;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        allIssues = info.getUtilitySpace().getDomain().getIssues();
    }

    /**
     * When this function is called, it is expected that the Party chooses one of the actions from the possible
     * action list and returns an instance of the chosen action.
     *
     * @param list
     * @return
     */
    @Override
    public Action chooseAction(List<Class<? extends Action>> list) {
        // According to Stacked Alternating Offers Protocol list includes
        // Accept, Offer and EndNegotiation actions only.
        double time = getTimeLine().getTime(); // Gets the time, running from t = 0 (start) to t = 1 (deadline).
        // The time is normalized, so agents need not be
        // concerned with the actual internal clock.
        
        double treshold = this.calcUtilityTreshold();
        System.out.println(treshold);
        
        // First half of the negotiation offering the max utility (the best agreement possible) for Example Agent
        if (time < 0.5) {
            return new Offer(this.getPartyId(), this.getMaxUtilityBid());
        } else if (time<0.9){

            // Accepts the bid on the table in this phase,
            // if the utility of the bid is higher than Example Agent's last bid.
            if (lastReceivedOffer != null
                    && myLastOffer != null
                    && this.utilitySpace.getUtility(lastReceivedOffer) > this.utilitySpace.getUtility(myLastOffer)) {

                return new Accept(this.getPartyId(), lastReceivedOffer);
            } else {
                // Offering a random bid
                do {
                    myLastOffer = generateRandomBid();
                }while (this.utilitySpace.getUtility(myLastOffer) < utilityThreshold ) ;
                return new Offer(this.getPartyId(), myLastOffer);
            }
        }else{
            return new Accept(this.getPartyId(),lastReceivedOffer);
        }
    }

    /**
     * This method is called to inform the party that another NegotiationParty chose an Action.
     * @param sender
     * @param act
     */
    @Override
    public void receiveMessage(AgentID sender, Action act) {
        super.receiveMessage(sender, act);

        if (act instanceof Offer) { // sender is making an offer
            Offer offer = (Offer) act;
            
            if (this.lastReceivedOffer != null) {
            	this.getOpponent(sender).addReject(this.lastOffer);
            }
            this.getOpponent(sender).addOffer(offer);
            
            receivedOffers.add(offer);

            // storing last received offer
            lastReceivedOffer = offer.getBid();
            this.lastOffer = offer;
        } else if(act instanceof Accept) {
        	this.getOpponent(sender).addAccept(this.lastOffer);
        }
    }

    /**
     * A human-readable description for this party.
     * @return
     */
    @Override
    public String getDescription() {
        return description;
    }

    private Bid getMaxUtilityBid() {
        try {
            return this.utilitySpace.getMaxUtilityBid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public double getOfferUtility(Bid bid){
//        return this.utilitySpace.getUtility(bid);
//    }
    
    public Bid generateRandomBidWithTreshold(double utilityThreshold) {
    	if(utilityThreshold>this.maxUtility) {
    		return generateRandomBidWithTreshold(this.maxUtility);
    	}
    	
        Bid randomBid;
        double utility;
        do {
            randomBid = generateRandomBid();
            try {
                utility = utilitySpace.getUtility(randomBid);
            } catch (Exception e){
                utility = 0.0;
            }
        }
        while (utility < utilityThreshold);
        return randomBid;
    }
    
    private double timePressure(double time) {
    	double ep = 0.9;
    	return 1 - Math.pow(time, (1/ep));
    }
    
    private double calcUtilityTreshold(){
    	double tp = this.timePressure(getTimeLine().getTime());
    	return (this.maxUtility * tp); 
    }
    
    private Opponent getOpponent(AgentID sender) {
    	if(this.op1 == null) {
    		this.op1 = new Opponent(sender);
    		return this.op1;
    	} else if (this.op1.agentId == sender) {
    		return this.op1;
    	} else if (this.op2 == null) {
    		this.op2 = new Opponent(sender);
    		return this.op2;
    	} else if (this.op2.agentId == sender) {
    		return this.op2; 
    	}
    	return this.op1;
    }
}