package client;

public class SettleFactory {
    public static SettleCsvHandler getSettle(final String dataDir, PropertiesReader pReader){
        if(dataDir.equals(Constants.mainRaw)){
            return new SettleCsvHandler();
        } else {//if(dataDir.equals(Constants.subRaw)){
            return new SubSettleCsvHandler(pReader);
        }
    }
}
