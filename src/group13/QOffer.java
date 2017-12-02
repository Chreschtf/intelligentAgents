package group13;
import negotiator.Bid;

public class QOffer {
    private Bid bid;
    private double utility;
    private double qvalue;
    private final double alpha = 0.1;
    private final double gamma = 0.9;

    public QOffer(Bid bid_,double utility_,double qvalue_){
        bid=bid_;
        utility=utility_;
        qvalue=qvalue_;
    }

    public Bid getBid(){
        return bid;
    }
    public double getUtility(){
        return utility;
    }

    public double getQvalue(){
        return qvalue;
    }

    public void updateQvalue(double reward,double maxQval){
        qvalue=(1-alpha)*qvalue + alpha*(reward+gamma*maxQval);
    }
}
