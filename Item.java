public class Item {

    private String name;
    private String description;
    private float price;
    private float currentMaxBid;
    private boolean ready = false; // Used for different thread lock functions

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

    /*
    //One way
    public synchronized void bidOnItem(float bidAmount) {
        //Figure this out..wait() locks this until its notified
        //so....wait() if someone is bidding
        //when finished bidding notify()
        long t1 = System.currentTimeMillis();
        while (System.currentTimeMillis() - t1 < 5000);
        this.currentMaxBid = bidAmount;
    }
    */

    //Another way
    public void bidOnItem(float bidAmount) {
        //Figure this out..wait() locks this until its notified
        //so....wait() if someone is bidding
        //when finished bidding notify()
        synchronized (this) {
            long t1 = System.currentTimeMillis();
            while (System.currentTimeMillis() - t1 < 5000) ;
            this.currentMaxBid = bidAmount;
        }
    }


    // Yet Another way(not quite working yet, It resists the items bid to the price
    /*
    public void bidOnItem(float bidAmount) {

        while(ready){
            try {
                wait();
            }catch (InterruptedException e){
                System.out.println(e);
            }
            this.currentMaxBid = bidAmount;
            ready = true;
            notify();
        }
    }
    */

    public Float getMaxBid() {
        return this.currentMaxBid;
    }

    public String toString() {
        return "\n" + this.name + "\n" + this.description + "\nPrice: $" + this.price + "\nMax Bid: $" + this.currentMaxBid;
    }


}