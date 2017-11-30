package group13;
import negotiator.Bid;

public class QOffer {
    private Bid bid;
    private double utility;
    private double qvalue;
    private double alpha;
    private double gamma;

    public QOffer(Bid bid_,double utility_,double qvalue_,double alpha_,double gamma_){
        bid=bid_;
        utility=utility_;
        qvalue=qvalue_;
        gamma=gamma_;
        alpha=alpha_;
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
