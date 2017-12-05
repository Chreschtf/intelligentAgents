package group13;
import negotiator.Bid;

public class QOffer {
    private Bid bid;
    private double utility;
    private double qvalue;

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

}
