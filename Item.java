public class Item {

    private String name;
    private String description;
    private float price;
    private float currentMaxBid;

    public Item(String name, String description, float price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.currentMaxBid = price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public synchronized void bidOnItem(float bidAmount) {
        //Figure this out..wait() locks this until its notified
        //so....wait() if someone is bidding
        //when finished bidding notify()
        this.currentMaxBid = bidAmount;
    }

    public Float getMaxBid() {
        return this.currentMaxBid;
    }

    public String toString() {
        return "\n" + this.name + "\n" + this.description + "\nPrice: " + this.price + "\nMax Bid: " + this.currentMaxBid;
    }

}