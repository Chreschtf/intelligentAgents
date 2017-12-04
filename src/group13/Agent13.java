package group13;
import misc.Range;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.BidIterator;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;

import static java.lang.Math.min;

public class Agent13 extends AbstractNegotiationParty {
    private final String description = "Agent13";

    private ArrayList<Offer> receivedOffers;
    
    private Opponent op1;
    private Opponent op2;
    
    private Offer lastOffer;
    private double maxUtility;
 
    private List<GirIssue> issues;
    private PriorityQueue<QOffer> qValues;
    
    SortedOutcomeSpace SOS;
    
    private QOffer myLastQOffer;
    private boolean offerMade;
    private boolean lastOfferAcceptedBySucceedingAgent;
    private double epsilon = 0.2;
    private double waitPhase = 0.1;
    private double rewardReject = -2;
    private double rewardAcceptOnce = -1;
    private double timeStep=10/180;
    private double timeThreshold = 0.1;
    private double minUtility;
    private double maxMinUtilityDistCoefficient=0.5;

    private double roundsSinceLastUpdate = 0;
    private double roundThreshold = 150;

    
    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        receivedOffers = new ArrayList<Offer>() ;
//        BidIterator bidIterator = new BidIterator(this.utilitySpace.getDomain());
        Comparator<QOffer> comparator = new QComparator();
        qValues = new PriorityQueue<QOffer>(Comparator.comparing(QOffer::getQvalue)
                                                    .thenComparing(QOffer::getUtility)
        .reversed());
//        qValuesNotConsideredYet = new PriorityQueue<QOffer>(comparator);
//
//
//        while (bidIterator.hasNext()) {
//            Bid bid = bidIterator.next();
//            QOffer tmpQoffer = new QOffer(bid,this.getUtility(bid),this.getUtility(bid));
//            qValuesNotConsideredYet.add(tmpQoffer);
//        }

//        QOffer qtemp= qValuesNotConsideredYet.poll();
//        qValues.add(qtemp);

        try {
            Bid minBid = this.utilitySpace.getMinUtilityBid();
            Bid maxBid = this.utilitySpace.getMaxUtilityBid();
            maxUtility = getUtility(maxBid);
            minUtility = getUtility(minBid);
            minUtility = (maxUtility-minUtility)*maxMinUtilityDistCoefficient+minUtility;
            System.out.println("Min Utility =" + this.minUtility);
            
            this.SOS = new SortedOutcomeSpace(this.getUtilitySpace());
            
            //has to go
            Range range = new Range(this.minUtility,this.maxUtility);
            List<BidDetails> bidDetails = this.SOS.getBidsinRange(range);
            System.out.println("Bids =" + bidDetails.size());
            
            range = new Range(0,1);
            bidDetails = this.SOS.getBidsinRange(range);
            System.out.println("Bids =" + bidDetails.size());
            //
            
            QOffer tmpQoffer = new QOffer(maxBid,maxUtility,maxUtility);
            qValues.add(tmpQoffer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Opponent.mapDomain(info);
        this.getWeights(info);

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
        roundsSinceLastUpdate+=1;
        if (roundsSinceLastUpdate>roundThreshold){
            this.addNashQvalues();
            roundsSinceLastUpdate=0;
        }

        //System.out.println(this.qValues.peek().getUtility());
        if (this.myLastQOffer!=null){
        	double maxQvalue=this.myLastQOffer.getQvalue();
        	if (this.qValues.peek()!= null) {
        		maxQvalue = this.qValues.peek().getQvalue();
        	}
            if (lastOfferAcceptedBySucceedingAgent)
                myLastQOffer.updateQvalue(rewardAcceptOnce,maxQvalue);
            else
                myLastQOffer.updateQvalue(rewardReject,maxQvalue);
        }

        if (this.lastOffer!=null && this.myLastQOffer!=null && this.qValues.peek()!=null){
            try{
                double lastOfferUtility = this.getUtility(lastOffer.getBid());
                if (lastOfferUtility >=  this.myLastQOffer.getUtility()
                        && lastOfferUtility >= this.qValues.peek().getUtility()) {
                    this.qValues.add(myLastQOffer);
                    if (lastOfferUtility<minUtility) {
                        System.out.println("###############################");
                        System.out.println("below threshold: " + lastOfferUtility);
                        System.out.println("threshold: " + minUtility);
                        System.out.println("###############################");
                    }
                    return new Accept(this.getPartyId(), this.lastOffer.getBid());
                }
            }catch (Exception e) {
                e.printStackTrace();
            } 
        }
        
        if (myLastQOffer!=null)
            this.qValues.add(myLastQOffer);
        QOffer myNextQOffer = this.qValues.poll();
        //if(this.qValues.peek()!=null) {
        //	myNextQOffer = this.qValues.poll();
        //}
        offerMade=true;
        this.lastOffer = new Offer(this.getPartyId(),myNextQOffer.getBid());
        
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

//    private Bid getMaxUtilityBid() {
//        try {
//            return this.utilitySpace.getMaxUtilityBid();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    
//    public Bid generateRandomBidWithTreshold(double utilityThreshold) {
//    	if(utilityThreshold>this.maxUtility) {
//    		return this.getMaxUtilityBid();
//    	}
//    	
//        Bid randomBid;
//        double utility;
//        do {
//            randomBid = generateRandomBid();
//            try {
//                utility = utilitySpace.getUtility(randomBid);
//            } catch (Exception e){
//                utility = 0.0;
//            }
//        }
//        while (utility < utilityThreshold);
//        return randomBid;
//    }
    
    private double timePressure(double time) {
    	if (time <= this.waitPhase) {return 1;}
    	return 1 - Math.pow(time, (1/this.epsilon));
    }
    
    private double calcUtilityTreshold(){
    	double tp = this.timePressure(getTimeLine().getTime());
    	double treshold = (this.maxUtility * tp);
    	return Math.max(treshold,this.minUtility);
    }
    
    private void getWeights(NegotiationInfo info) {
    	AbstractUtilitySpace utilitySpace = info.getUtilitySpace();
    	AdditiveUtilitySpace uSpace = (AdditiveUtilitySpace) utilitySpace;
    	
    	//Get Weights for our agent
    	this.issues = Opponent.copyDomainMap();
    	for (GirIssue issue : this.issues) {
    		issue.weight = uSpace.getWeight(issue.number);
    	}
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

    private void addNashQvalues(){
        this.qValues.clear();
        double threshold = min(this.calcUtilityTreshold(),(this.maxUtility * 0.9));
        Range range = new Range(threshold,1);
        List<BidDetails> bidDetails = this.SOS.getBidsinRange(range);
        for (BidDetails bidd : bidDetails){
            Bid bid = bidd.getBid();
            double nashUtility = 1;
            
            double ourUtility = this.getUtility(bid);
            nashUtility*=ourUtility;
            
            if(op1 != null) {
                double op1Utility=op1.expectedUtility(bid);
                nashUtility*=op1Utility;
            }
            if(op2 != null) {
            	double op2Utility=op2.expectedUtility(bid);
            	nashUtility*=op2Utility;
            }
            
            QOffer qtemp = new QOffer(bid,ourUtility,nashUtility);
            this.qValues.add(qtemp);
        }
        this.myLastQOffer = null;

    }
}