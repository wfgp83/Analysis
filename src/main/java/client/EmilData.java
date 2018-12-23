package client;

public class EmilData {
    public String dateStr;
    public String customer;
    public String trackNum;
    public String dstAddr;
    public String count;
    public String weight;
    public String offPrice;
    public String expressMan;

    public EmilData(String dateStr, String customer, String trackNum, String dstAddr,
                    String count, String weight, String offPrice, String expressMan) {
        this.dateStr = dateStr;
        this.customer = customer;
        this.trackNum = trackNum;
        this.dstAddr = dstAddr;
        this.count = count;
        this.weight = weight;
        this.offPrice = offPrice;
        this.expressMan = expressMan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmilData emilData = (EmilData) o;

        return trackNum.equals(emilData.trackNum);
    }

    @Override
    public int hashCode() {
        return trackNum.hashCode();
    }

    @Override
    public String toString() {
        return "EmilData{" +
                "dateStr='" + dateStr + '\'' +
                ", customer='" + customer + '\'' +
                ", trackNum='" + trackNum + '\'' +
                ", dstAddr='" + dstAddr + '\'' +
                ", count='" + count + '\'' +
                ", weight='" + weight + '\'' +
                ", offPrice='" + offPrice + '\'' +
                ", expressMan='" + expressMan + '\'' +
                '}';
    }
}
