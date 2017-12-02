package group13;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.utility.EvaluatorDiscrete;
import negotiator.BidIterator;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;

import static java.lang.Math.min;

public class Agent13 extends AbstractNegotiationParty {
    private final String description = "Agent13";

    private Bid myLastOffer;
    private ArrayList<Offer> receivedOffers;
    
    protected static Opponent model;
    private Opponent op1;
    private Opponent op2;
    
    private Offer lastOffer;
    private double maxUtility;
    private double minUtility;
    private NegotiationInfo info;
 
    private List<GirIssue> issues;
    private PriorityQueue<QOffer> qValues;
    private PriorityQueue<QOffer> qValuesNotConsideredYet;
    private QOffer myLastQOffer;
    private boolean offerMade;
    private boolean lastOfferAcceptedBySucceedingAgent;
    private double epsilon = 0.4;
    private double waitPhase = 0.1;
    private double alpha = 0.1;
    private double gamma = 0.9;
    private double rewardReject = -2;
    private double rewardAcceptOnce = -1;

    
    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        receivedOffers = new ArrayList<Offer>() ;
        BidIterator bidIterator = new BidIterator(this.utilitySpace.getDomain());
        Comparator<QOffer> comparator = new QComparator();
        qValues = new PriorityQueue<QOffer>(comparator);
        qValuesNotConsideredYet = new PriorityQueue<QOffer>(comparator);


        while (bidIterator.hasNext()) {
            Bid bid = bidIterator.next();
            QOffer tmpQoffer = new QOffer(bid,this.getUtility(bid),this.getUtility(bid),alpha,gamma);
            qValuesNotConsideredYet.add(tmpQoffer);
        }

        QOffer qtemp= qValuesNotConsideredYet.poll();
        qValues.add(qtemp);

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

        offerMade=false;
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
        
        if(op1!=null) {
//        	this.op1.calculateRates();
        }
        if(op2!=null) {
//        	this.op2.calculateRates();
        }
        
        if (time<0.9){
            this.addQvalues();
        }

        //System.out.println(this.qValues.peek().getUtility());
        if (this.myLastQOffer!=null){
            if (lastOfferAcceptedBySucceedingAgent)
                myLastQOffer.updateQvalue(rewardAcceptOnce,this.qValues.peek().getQvalue());
            else
                myLastQOffer.updateQvalue(rewardReject,this.qValues.peek().getQvalue());
        }

        if (this.lastOffer!=null && this.myLastQOffer!=null ){
            try{
                double lastOfferUtility = this.getUtility(lastOffer.getBid());
                if (this.getUtility(lastOffer.getBid()) >=  this.myLastQOffer.getUtility()
                        && lastOfferUtility >= this.qValues.peek().getUtility()) {
                    this.qValues.add(myLastQOffer);
                    return new Accept(this.getPartyId(), this.lastOffer.getBid());
                }
            }catch (Exception e) {
                e.printStackTrace();
            } 
        }
        
        QOffer myNextQOffer = this.qValues.poll();
        offerMade=true;
        this.lastOffer = new Offer(this.getPartyId(),myNextQOffer.getBid());
        if (myLastQOffer!=null)
            this.qValues.add(myLastQOffer);
        myLastQOffer=myNextQOffer;
        return this.lastOffer;
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
                if (offerMade){
                    offerMade=false;
                    lastOfferAcceptedBySucceedingAgent=false;
                }
            	this.getOpponent(sender).addOffer(this.lastOffer, -1, getTimeLine().getTime());
            }
            this.getOpponent(sender).addOffer(offer, 0, getTimeLine().getTime());
            
            receivedOffers.add(offer);

            // storing last received offer
            this.lastOffer = offer;
           
        } else if(act instanceof Accept) {

            if (offerMade) {
                lastOfferAcceptedBySucceedingAgent=true;
                offerMade=false;
            }
        	
        	if(this.lastOffer != null) {
        		this.getOpponent(sender).addOffer(this.lastOffer, 1, getTimeLine().getTime());
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

    
    public Bid generateRandomBidWithTreshold(double utilityThreshold) {
    	if(utilityThreshold>this.maxUtility) {
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
    	Agent13.model = new Opponent();
    	
    	AbstractUtilitySpace utilitySpace = this.info.getUtilitySpace();
    	AdditiveUtilitySpace uSpace = (AdditiveUtilitySpace) utilitySpace;
    	
    	List<Issue>issues = uSpace.getDomain().getIssues();
    	
    	double w0 = 1/issues.size(); //Initial Weights
    	
    	for (Issue issue : issues) {
    		Agent13.model.issues.add(new GirIssue(issue, uSpace, w0));
    	}
    	
    	//Get Weights for our agent
    	this.issues = new ArrayList<GirIssue>(Agent13.model.issues);
    	for (GirIssue issue : this.issues) {
    		issue.weight = uSpace.getWeight(issue.number);
    	}
    }
    
    private Opponent getOpponent(AgentID sender) {
    	if(this.op1 == null) {
    		this.op1 = new Opponent(sender, Agent13.model.issues);
    		return this.op1;
    	} else if (this.op1.agentId == sender) {
    		return this.op1;
    	} else if (this.op2 == null) {
    		this.op2 = new Opponent(sender, Agent13.model.issues);
    		return this.op2;
    	} else if (this.op2.agentId == sender) {
    		return this.op2; 
    	}
    	return this.op1;
    }

    private void addQvalues(){
        double threshold = min(this.calcUtilityTreshold(),0.9);
        try {
            while (qValuesNotConsideredYet.size() != 0 && qValuesNotConsideredYet.peek().getUtility() >= threshold) {
                QOffer qtemp = qValuesNotConsideredYet.poll();
                qValues.add(qtemp);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}