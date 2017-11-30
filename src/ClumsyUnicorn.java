import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.utility.EvaluatorDiscrete;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class ClumsyUnicorn extends AbstractNegotiationParty {
    private final String description = "Clumsy Unicorn";

    private Bid myLastOffer;
    private ArrayList<Offer> receivedOffers;
    
    private Opponent op1;
    private Opponent op2;
    private Offer lastOffer;
    private double maxUtility;
    private double minUtility;
    private NegotiationInfo info;
    private List<GirIssue> girIssues;
    private List<GirIssue> clumsyIssues;
    
    private double epsilon = 0.4;
    private double waitPhase = 0.1;
    
    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        receivedOffers = new ArrayList<Offer>() ;

        try {
            Bid minBid = this.utilitySpace.getMinUtilityBid();
            Bid maxBid = this.utilitySpace.getMaxUtilityBid();
            maxUtility = getUtility(maxBid);
            minUtility = getUtility(minBid);
//            utilityThreshold = (maxUtility-minUtility)/2+minUtility;
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.info = info;
        this.mapDomain();
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
        
        if(op1!=null) {
//        	this.op1.print();
        	this.op1.calculateRates();
//        	this.op1.print2();
        }
        if(op2!=null) {
//        	this.op1.print();
        	this.op2.calculateRates();
//        	this.op2.print2();
        }
        
        if (time >= 0.9) {
        	return new Accept(this.getPartyId(),this.lastOffer.getBid());
        }
        
        double treshold = this.calcUtilityTreshold();
        
        if (this.lastOffer != null 
        	&& this.utilitySpace.getUtility(this.lastOffer.getBid()) > treshold) { 
            return new Accept(this.getPartyId(), this.lastOffer.getBid());
        } else {
        	this.lastOffer = new Offer(this.getPartyId(), this.generateRandomBidWithTreshold(treshold));
        	return this.lastOffer;
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
            
            if (this.lastOffer != null) {
            	this.getOpponent(sender).addReject(this.lastOffer);
            }
            this.getOpponent(sender).addOffer(offer);
            
            receivedOffers.add(offer);

            // storing last received offer
            this.lastOffer = offer;
           
        } else if(act instanceof Accept) {
        	if(this.lastOffer != null) {
        		this.getOpponent(sender).addAccept(this.lastOffer);
        	}
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
    	if(utilityThreshold>=this.maxUtility) {
    		return this.getMaxUtilityBid();
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
    	if (time <= this.waitPhase) {return 1;}
    	return 1 - Math.pow(time, (1/this.epsilon));
    }
    
    private double calcUtilityTreshold(){
    	double tp = this.timePressure(getTimeLine().getTime());
    	double treshold = (this.maxUtility * tp);
    	return treshold; 
    }
    
    private void mapDomain() {
    	this.girIssues = new ArrayList<GirIssue>();
    	
    	AbstractUtilitySpace utilitySpace = this.info.getUtilitySpace();
    	AdditiveUtilitySpace uSpace = (AdditiveUtilitySpace) utilitySpace;
    	
    	List<Issue>issues = uSpace.getDomain().getIssues();
    	
    	double w0 = 1/issues.size();
    	
    	for (Issue issue : issues) {
    		this.girIssues.add(new GirIssue(issue, uSpace, w0));
    	}
    }
    
    private Opponent getOpponent(AgentID sender) {
    	if(this.op1 == null) {
    		this.op1 = new Opponent(sender, this.girIssues);
    		return this.op1;
    	} else if (this.op1.agentId == sender) {
    		return this.op1;
    	} else if (this.op2 == null) {
    		this.op2 = new Opponent(sender, this.girIssues);
    		return this.op2;
    	} else if (this.op2.agentId == sender) {
    		return this.op2; 
    	}
    	return this.op1;
    }
}