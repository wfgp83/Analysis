package client;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static final int TWO_SECONDS = 2000;
    private PropertiesReader propertiesReader;
    private Robot robot;
    private Clipboard systemClipboard;

    public void initApp(String appConfDir) throws Exception {
        propertiesReader = new PropertiesReader(appConfDir);
        systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        robot = new Robot();
        robot.setAutoDelay(1000);
        robot.setAutoWaitForIdle(true);
    }

    public static void main(String... args) {
        long currTime = System.currentTimeMillis();
        final String appConfDir = Constants.appDir;
        final List<String> dirs = new ArrayList<>();
        dirs.add(appConfDir + Constants.mainRaw);
        dirs.add(appConfDir + Constants.subRaw);
        dirs.add(appConfDir + Constants.LOG);

        for (String dir : dirs) {
            try {
                FileUtils.deleteDirectory(new File(dir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String dir : dirs) {
            Utils.createDir(dir);
        }

        Main exportData = new Main();
        try {
            exportData.initApp(appConfDir);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }

        Constants.halt(appConfDir, "");

        Map<String, CsvHandler> handlers = new HashMap<>();
        for(String subDomain : Arrays.asList(Constants.subRaw, Constants.mainRaw)) {
            CsvHandler sub = new CsvHandler();
            sub.parseFile(Constants.appDir, subDomain);
            handlers.put(subDomain, sub);
        }
        List<EmilData> datas = EmilCsvHandler.parseFile(Constants.appDir, "emil_rawData");
        List<String> notMatchTrackNums = new ArrayList<>();
        for(EmilData d : datas){
            boolean isFound = false;
            for(Map.Entry<String, CsvHandler> entry : handlers.entrySet()){
                isFound = Constants.matchTrackNum(d.trackNum, entry.getValue().getTrackNumbers());
                if(isFound){
                    break;
                }
            }

            if(!isFound){
                notMatchTrackNums.add(d.trackNum);
            }
        }

        if (!notMatchTrackNums.isEmpty()) {
            int c = 0;
            for (String it : notMatchTrackNums){
                logger.info("Not match " + it);
                c++;
                if(c == 10){
                    break;
                }
            }
            logger.info("Emil data has " + notMatchTrackNums.size() + " item not match Q9");
            for(Map.Entry<String, CsvHandler> entry : handlers.entrySet()) {
                logger.info("Original " + entry.getKey() + " cout :" + entry.getValue().getTrackNumSize());
                entry.getValue().resetPage(notMatchTrackNums);
                logger.info("New " + entry.getKey() + " cout :" + entry.getValue().getTrackNumSize());
            }
        }

        List<ExcelData> excelDatas = new ArrayList<>();

        for(String subDomain : Arrays.asList(Constants.subRaw, Constants.mainRaw)) {
            final String domain = Constants.getAccount(subDomain);
            if (!subDomain.equals(Constants.subRaw)){
                JOptionPane.showMessageDialog(null, "Switch to " + domain + ", please");
            }
            Constants.halt(domain);


            exportData.sentTab();
            int fileNameSuffix = Constants.PREFIX_IDX - 1;
            while (handlers.get(subDomain).hasNext()) {
                fileNameSuffix++;
                final String trackNums = handlers.get(subDomain).next();
                final String exportSendDatafileName = Utils.getExportDataFileName(appConfDir + subDomain,
                        domain, fileNameSuffix);
                exportData.queryAndExport(trackNums, exportSendDatafileName);
            }

            List<ExcelData> excelData = SettleFactory.getSettle(subDomain, exportData.propertiesReader)
                    .parseFile(appConfDir + Constants.subRaw, appConfDir + Constants.mainRaw,
                            domain, fileNameSuffix);
            excelDatas.addAll(excelData);
        }

        {
            final long spendTime = (System.currentTimeMillis() - currTime) /1000;
            logger.info("Export data spend total time: " + spendTime +" seconds");
        }

        logger.info("Export record size := " + excelDatas.size());
        logger.info("Emil record size := " + datas.size());

        try{
            XlsResultWriter.writeTmpFile(appConfDir + "\\tmp.XLS", excelDatas);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        try {
            XlsResultWriter.writeFile(appConfDir + "\\result.XLS", datas, excelDatas);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        final long spendTime = (System.currentTimeMillis() - currTime) /1000;
        logger.info("Spend total time: " + spendTime +" seconds");

        JOptionPane.showMessageDialog(null, "Complete, spend total time "+ spendTime + " seconds");
    }

    private String displayMsg(int total, int unknown, int valid, int invalid, long spendTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("Total :").append(total).append("\n");
        sb.append("Unknown :").append(unknown).append("\n");
        sb.append("Valid :").append(valid).append("\n");
        sb.append("Invalid :").append(invalid).append("\n");
        sb.append("spendTime :").append(spendTime);
        return sb.toString();
    }

    public void queryAndExport(final String  trackNums,
                               String exportSendDatafileName) {

        File checkFile = Utils.createFile(exportSendDatafileName);
        delAndCpForTrackNumbers(trackNums);
        queryForSendTab();
        nameToClipboard(checkFile.getName());
        exportDataForSendTab(checkFile.exists());
    }

    private void writeTxtFile(final String outputFileName, List<String> requiredData) {
        writeTxtFile(outputFileName, Utils.joinElement(requiredData));
    }

    private void writeTxtFile(final String outputFileName, final String requiredData) {
        Utils.deleteFile(outputFileName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
            writer.write(requiredData);
            writer.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void exportData(MyPoint export, MyPoint excel, MyPoint save, MyPoint complete,
                           MyPoint override, boolean fileExist) {

        // export button
        robot.mouseMove(export.x, export.y);
        clickLeftMouse();

        // excel button
        robot.mouseMove(excel.x,excel.y);
        clickLeftMouse();

        int delay = propertiesReader.getDelayBetweenExportAndFileNameInSecond();
        if (delay != 0) {
            robot.delay(delay * 1000);
        }

        // file name
        ctrlV();

        // ok button
        robot.mouseMove(save.x,save.y);
        clickLeftMouse();

        if (fileExist) {
            // export success
            robot.mouseMove(override.x,override.y);
            clickLeftMouse();
        }

        delay = propertiesReader.getDelayBetweenExportFileNameAndOkInSecond();
        if (delay != 0) {
            robot.delay(delay * 1000);
        } else{
            robot.delay(100);
        }

        // export success
        robot.mouseMove(complete.x,complete.y);
        clickLeftMouse();
    }

    public void exportDataForSendTab(boolean fileExist) {
        MyPoint export = propertiesReader.getSendTabExportPoint();
        MyPoint excel = propertiesReader.getSendTabExportExccelStylePoint();
        MyPoint save = propertiesReader.getSendTabExportExccelButtonSavePoint();
        MyPoint complete = propertiesReader.getSendTabExportExccelButtonCompletePoint();
        MyPoint override = propertiesReader.getSendTabExportExccelButtonOverridePoint();
        exportData(export, excel, save, complete, override, fileExist);
    }

    public void nameToClipboard(String customerName) {
        StringSelection dataStr = new StringSelection(customerName);
        systemClipboard.setContents(dataStr, dataStr);
    }

    public void query(MyPoint point){
        robot.mouseMove(point.x, point.y);
        clickLeftMouse();
        int delay = propertiesReader.getDelayForQuery();
        if (delay == 0) {
            delay = 1;
        }
        robot.delay(delay * 1000);
    }

    public void queryForSendTab(){
        MyPoint point = propertiesReader.getSendTabQueryPoint();
        query(point);
    }

    public void delAndCp(MyPoint clearP, MyPoint point){
        robot.delay(40);
        robot.mouseMove(clearP.x, clearP.y);
        clickLeftMouse();

        robot.mouseMove(point.x,point.y);
        clickLeftMouse();
        ctrlV();
    }

    public void delAndCpForTrackNumbers(String datas){
        StringSelection dataStr = new StringSelection(datas);
        systemClipboard.setContents(dataStr, dataStr);
        MyPoint point = propertiesReader.getSendTabTrackNumbersPoint();
        MyPoint clearP = propertiesReader.getSendTabTrackNumbersClearPoint();
        delAndCp(clearP, point);
    }

    public void sentTab() {
        robot.delay(40);

        MyPoint point = propertiesReader.getSendTabPoint();
        robot.mouseMove(point.x, point.y);
        clickLeftMouse();
    }

    public void selectCheckboxQueryByTrackNumber(){
        robot.delay(40);

        MyPoint point = propertiesReader.getSendTabCheckboxTrackNumberPoint();
        robot.mouseMove(point.x, point.y);
        clickLeftMouse();
    }

    public void clickLeftMouse()
    {
        robot.delay(40);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(40);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(40);
    }

    public void ctrlV(){
        robot.delay(20);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.delay(20);
        robot.keyPress(KeyEvent.VK_V);
        robot.delay(20);
        robot.keyRelease(KeyEvent.VK_V);
        robot.delay(20);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(40);
    }
}
