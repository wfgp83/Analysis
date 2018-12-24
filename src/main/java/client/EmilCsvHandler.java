package client;

import javafx.util.Pair;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmilCsvHandler {

    private static Logger logger = LoggerFactory.getLogger(EmilCsvHandler.class);

    public static EmilAccountData parseFile(final String appDir, final String subDir) {

        EmilAccountData accountData = new EmilAccountData();
        File folder = new File(appDir + subDir);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            logger.error("no direcory " + folder.getAbsolutePath());
            return accountData;
        }

        for (File f : listOfFiles){
            if (f.isDirectory()){
                continue;
            }
            logger.info("parseFile " + f.getAbsolutePath());
            CSVParser parser = null;
            try {
                parser = CSVParser.parse(f, Charset.forName("GB2312"), CSVFormat.EXCEL);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            Pair<Set<EmilData>, Set<EmilData>> rDatas = getRequiredDatas(parser);
            accountData.datas.getValue().getKey().addAll(rDatas.getKey());
            accountData.xDatas.getValue().getKey().addAll(rDatas.getValue());
        }

        return accountData;
    }

    public static Pair<Set<EmilData>, Set<EmilData>> getRequiredDatas(CSVParser parser){
        Set<EmilData> rets = new HashSet<>();
        Set<EmilData> xRets = new HashSet<>();
        if (parser == null) {
            return new Pair<>(rets, xRets);
        }
        for (CSVRecord csvRecord : parser) {
            // Accessing Values by Column Index
            final String trackNum = Utils.getCellData(csvRecord, 3);
            if (Utils.trackNumberIsTitle(trackNum)) {
                continue;
            }

            if (!Utils.isTrackNumber(trackNum)) {
                //logger.warn("not a track number :=" + trackNum);
                continue;
            }

            final String dateStr = Utils.getCellData(csvRecord, 0);
            final String customer = Utils.getCellData(csvRecord, 1);
            final String dstAddr = Utils.getCellData(csvRecord, 5);
            final String count = Utils.getCellData(csvRecord, 7);

            final String weight = Utils.getCellData(csvRecord, 8);
            final String offPrice = Utils.getCellData(csvRecord, 9);
            final String expressman = Utils.getCellData(csvRecord, 12);

            EmilData eD = new EmilData(dateStr, customer, trackNum, dstAddr, count, weight,
                    offPrice, expressman);
            boolean ret = true;
            if (customer.indexOf("x") != -1 || customer.indexOf("X") != -1){
                ret = xRets.add(eD);
            } else{
                ret = rets.add(eD);
            }
            if (!ret){
                logger.error("add " + trackNum + " failure, exist");
            }
        }

        return new Pair<>(rets, xRets);
    }

    public static void main(String[] args) throws Exception{
        EmilAccountData datas = EmilCsvHandler.parseFile(Constants.appDir, Constants.emilRaw);
        int a = 1;
        System.out.println("x:="+ datas.xDatas.getValue().getKey().size());
        System.out.println("b:=" + datas.datas.getValue().getKey().size());
        for (EmilData data : datas.xDatas.getValue().getKey()) {
            System.out.println(data.toString());
            a++;
            if(a == 10)
                break;;
        }
        System.out.println("==============");
        a = 0;
        for (EmilData data : datas.datas.getValue().getKey()) {
            System.out.println(data.toString());
            a++;
            if(a == 10)
                break;;
        }


        String ss = "罗正虎（东大楼新中洲）-承包区,朱建中X";
        System.out.println(ss.indexOf("X"));
    }
}


