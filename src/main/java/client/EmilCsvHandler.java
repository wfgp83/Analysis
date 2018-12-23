package client;

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

    public static List<EmilData> parseFile(final String appDir, final String subDir) {

        List<EmilData> rets = new ArrayList<>();
        File folder = new File(appDir + subDir);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            logger.error("no direcory " + folder.getAbsolutePath());
            return rets;
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
            rets.addAll(getRequiredDatas(parser));
        }

        return rets;
    }

    public static Set<EmilData> getRequiredDatas(CSVParser parser){
        Set<EmilData> rets = new HashSet<>();
        if (parser == null) {
            return rets;
        }
        for (CSVRecord csvRecord : parser) {
            // Accessing Values by Column Index
            final String trackNum = Utils.getCellData(csvRecord, 3);
            if (Utils.trackNumberIsTitle(trackNum)) {
                continue;
            }

            if (!Utils.isTrackNumber(trackNum)) {
                logger.warn("not a track number :=" + trackNum);
                continue;
            }

            final String dateStr = Utils.getCellData(csvRecord, 0);
            final String customer = Utils.getCellData(csvRecord, 1);
            final String dstAddr = Utils.getCellData(csvRecord, 5);
            final String count = Utils.getCellData(csvRecord, 7);

            final String weight = Utils.getCellData(csvRecord, 8);
            final String offPrice = Utils.getCellData(csvRecord, 9);
            final String expressman = Utils.getCellData(csvRecord, 12);

            boolean ret = rets.add(new EmilData(dateStr, customer, trackNum, dstAddr, count, weight,
                    offPrice, expressman));
            if (!ret){
                logger.error("add " + trackNum + " failure, exist");
            }
        }

        return rets;
    }

    public static void main(String[] args) throws Exception{
        List<EmilData> datas = EmilCsvHandler.parseFile(Constants.appDir, "emil_rawData");
        int a = 1;
        System.out.println(datas.size());
        for (EmilData data : datas) {
            System.out.println(data.toString());
            a++;
            if(a == 10)
                break;;
        }

        for (EmilData data : datas) {
            if (data.trackNum.equals("51244502301480")){
                System.out.println(data.toString());
                break;
            }
        }
    }
}
