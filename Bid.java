public class Bid {

    private Item item;
    private String bidder;
    private float bidAmount;

    public Bid(Item item, String bidder, float bidAmount){
        this.item = item;
        this.bidder = bidder;
        this.bidAmount = bidAmount;
    }



    @Override
    public String toString() {
        return this.bidder + " " +  this.bidAmount;
    }
}
