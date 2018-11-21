public class Bid {

    private Item item;
    private int biddersSocketPort;
    private float bidAmount;

    public Bid(Item item, int biddersSocketPort, float bidAmount){
        this.item = item;
        this.biddersSocketPort = biddersSocketPort;
        this.bidAmount = bidAmount;
    }


    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getBiddersSocketPort() {
        return biddersSocketPort;
    }

    public void setBiddersSocketPort(int biddersSocketPort) {
        this.biddersSocketPort = biddersSocketPort;
    }

    public float getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(float bidAmount) {
        this.bidAmount = bidAmount;
    }

    @Override
    public String toString() {
        return this.biddersSocketPort + " bid $" +  this.bidAmount + " on " + this.item.getName();
    }
}