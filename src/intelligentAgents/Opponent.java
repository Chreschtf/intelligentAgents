package intelligentAgents;
import java.util.ArrayList;
import negotiator.actions.Offer;
import negotiator.AgentID;
import negotiator.Bid;

public class Opponent {
	public ArrayList<Bid> offers;
	public ArrayList<Bid> rejectedOffers;
	public ArrayList<Bid> acceptedOffers;
	
	public AgentID agentId;
	
	public Opponent(AgentID id) {
		this.agentId = id;
	}
}
