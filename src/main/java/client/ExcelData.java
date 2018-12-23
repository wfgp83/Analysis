package client;

public class ExcelData {
    public String trackNumber;
    public String price;

    public ExcelData(String trackNumber, String price) {
        this.trackNumber = trackNumber;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExcelData excelData = (ExcelData) o;

        return trackNumber.equals(excelData.trackNumber);
    }

    @Override
    public int hashCode() {
        return trackNumber.hashCode();
    }

    @Override
    public String toString() {
        return "ExcelData{" +
                "trackNumber='" + trackNumber + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
