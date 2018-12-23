package client;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CsvHandler {

    private static Logger logger = LoggerFactory.getLogger(CsvHandler.class);

    TrackNumberPage numberPage = new TrackNumberPage(Collections.emptyList());

    public List<String> parseFile(File file){
        logger.info("parseFile " + file.getAbsolutePath());
        CSVParser parser = null;
        try {
            parser = CSVParser.parse(file, Charset.forName("GB2312"), CSVFormat.EXCEL);
        } catch (Exception e){
          logger.error(e.getMessage());
        }
        return getTrackNumbers(parser);
    }

    public void parseFile(String appDir, String subDir){
        logger.info("parseFile " + appDir + subDir);
        File folder = new File(appDir + subDir);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            logger.error("no direcory " + folder.getAbsolutePath());
            return;
        }

        List<String> datas = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isDirectory()){
                continue;
            }
            datas.addAll(parseFile(file));
        }
        numberPage = new TrackNumberPage(datas);
    }

    public List<String> getTrackNumbers(CSVParser parser){
        List<String> datas = new ArrayList<>();
        if (parser == null){
            return datas;
        }

        int existCount = 0;
        for (CSVRecord csvRecord : parser) {
            // Accessing Values by Column Index
            final String trackNum = Utils.getCellData(csvRecord, 0);
            if (Utils.trackNumberIsTitle(trackNum)) {
                continue;
            }

            if (!Utils.isTrackNumber(trackNum)) {
                logger.warn("not a track number :=" + trackNum);
                continue;
            }

            if (datas.contains(trackNum)){
                if (existCount <10){
                    logger.error("add " + trackNum + " failure, exist.");
                }
                existCount++;
                continue;
            }
            datas.add(trackNum);
        }

        logger.info("exist count " + existCount);

        return datas;
    }

    public List<String> getTrackNumbers(){
        return numberPage.getTrackNumbers();
    }

    public void resetPage(List<String> trackNum){
        numberPage.trackNumbers.addAll(trackNum);
        numberPage.trackNumSize = numberPage.trackNumbers.size();
    }

    public boolean hasNext(){
        return numberPage.hasElement();
    }

    public String next(){
        return numberPage.getElement();
    }

    public int getTrackNumSize(){
        return numberPage.getTrackNumSize();
    }

    public static void main(String[] args) throws Exception {
        CsvHandler handler = new CsvHandler();
        handler.parseFile(Constants.appDir, Constants.mainRaw);
        while (handler.hasNext()){
            String numDatas = handler.next();
            System.out.println(numDatas);
        }
        System.out.println("total count : " + handler.getTrackNumSize());
    }
}
