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

public class SettleCsvHandler {

    protected static Logger logger = LoggerFactory.getLogger(SettleCsvHandler.class);

    public boolean isSpecialRecord(final String trackNum, final String data){
        return false;
    }

    public List<ExcelData>  parseFile(final String dir1, final String dir2, final String prefix,
                                             int idx) {

        int prefixIdx = Constants.PREFIX_IDX;
        final String tmpFileName =  Utils.getExportDataFileName(dir1, prefix, prefixIdx);
        File tmpFile = new File(tmpFileName);
        String dir = dir2;
        if (tmpFile.exists()){
            dir = dir1;
        }

        List<ExcelData> rets = new ArrayList<>();
        for(; prefixIdx <= idx; prefixIdx++) {
            final String fileName =  Utils.getExportDataFileName(dir, prefix, prefixIdx);
            logger.info("parse " + fileName);
            File file = new File(fileName);
            CSVParser parser = null;
            try {
                parser = CSVParser.parse(file, Charset.forName("GB2312"), CSVFormat.EXCEL);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            rets.addAll(getRequiredDatas(parser));
        }
        return rets;
    }

    public Set<ExcelData> getRequiredDatas(CSVParser parser){
        Set<ExcelData> rets = new HashSet<>();
        if (parser == null) {
            return rets;
        }

        int accumulateCount = 0;
        for (CSVRecord csvRecord : parser) {
            // Accessing Values by Column Index
            final String trackNum = Utils.getCellData(csvRecord, 1);
            if (Utils.trackNumberIsTitle(trackNum)) {
                continue;
            }

            if (!Utils.isTrackNumber(trackNum)) {
                logger.warn("not a track number :=" + trackNum);
                continue;
            }

            final String checkType = Utils.getCellData(csvRecord, 3);
            if(isSpecialRecord(trackNum, checkType)){

                continue;
            }

            final String price = Utils.getCellData(csvRecord, 7);
            ExcelData data =  Utils.find(rets, trackNum);
            if (data == null){
                rets.add(new ExcelData(trackNum, price));
            } else{
                Utils.accumulateMount(data, price);
                if (accumulateCount < 10) {
                    logger.info("accumulate " + trackNum);
                    accumulateCount++;
                }
            }
        }
        return rets;
    }

    public static void main(String[] args){
        SettleCsvHandler handler = new SettleCsvHandler();
        List<ExcelData> datas = handler.parseFile(Constants.appDir + Constants.mainRaw,
                Constants.appDir + Constants.subRaw,"", 1);
        System.out.println(datas.size());

        int a = 0;
        for (ExcelData d : datas){
            System.out.println(d);
            a++;
            if (a == 10){
                break;
            }
        }
    }
}

