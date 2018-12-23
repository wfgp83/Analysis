package client;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class XlsResultWriter {

    private static Logger logger = LoggerFactory.getLogger(XlsResultWriter.class);

    public static String findPrice(String trackNum, List<ExcelData> datas){
        String price = "";
        for (ExcelData data: datas) {
            if (data.trackNumber.equals(trackNum)){
                if (price.isEmpty()){
                    price = data.price;
                }else{
                    logger.error("same record " + trackNum);
                }
            }
        }

        return price;
    }

    public static String addPrice(String p1, String p2){
        double weight = 0;
        try {
            weight = Double.valueOf(p1);
        } catch (NumberFormatException e){
            logger.error(e.getMessage());
        }

        double amount = 0;
        try {
            amount  = Double.valueOf(p2);
        } catch (NumberFormatException e){
            logger.error(e.getMessage());
        }

        return BigDecimal.valueOf(weight).add(BigDecimal.valueOf(amount))
                .setScale(2, RoundingMode.UP).toString();
    }

    public static void writeFile(String fileName, List<EmilData> emilData, List<ExcelData> datas) throws Exception {
        logger.info("write output file " + fileName);
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet");

        Row header = sheet.createRow(0);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("业务日期");

        headerCell = header.createCell(1);
        headerCell.setCellValue("客户名称");

        headerCell = header.createCell(2);
        headerCell.setCellValue("内单号");

        headerCell = header.createCell(3);
        headerCell.setCellValue("目的地");

        headerCell = header.createCell(4);
        headerCell.setCellValue("件数");

        headerCell = header.createCell(5);
        headerCell.setCellValue("重量");

        headerCell = header.createCell(6);
        headerCell.setCellValue("折扣价");

        headerCell = header.createCell(7);
        headerCell.setCellValue("取件人");

        headerCell = header.createCell(8);
        headerCell.setCellValue("成本价");

        headerCell = header.createCell(9);
        headerCell.setCellValue("利润");

        int rows = 0;
        String totalProfit = "0";
        String totalWeight = "0";
        int totalCount = 0;
        for (EmilData data: emilData) {
            rows++;
            Row row = sheet.createRow(rows);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(data.dateStr);
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(data.customer);
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(data.trackNum);
            {
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(data.dstAddr);
            }
            {
                Cell cell3 = row.createCell(4);
                cell3.setCellValue(data.count);
            }
            {
                Cell cell3 = row.createCell(5);
                cell3.setCellValue(data.weight);
            }
            {
                Cell cell3 = row.createCell(6);
                cell3.setCellValue(data.offPrice);
            }
            {
                Cell cell3 = row.createCell(7);
                cell3.setCellValue(data.expressMan);
            }
            String price = findPrice(data.trackNum, datas);
            {
                Cell cell3 = row.createCell(8);
                cell3.setCellValue(price);
            }
            String profit = addPrice(price, data.offPrice);
            {
                Cell cell3 = row.createCell(9);
                cell3.setCellValue(profit);
            }
            totalProfit = addPrice(totalProfit, profit);
            totalCount += Integer.valueOf(data.count);
            totalWeight = addPrice(totalWeight, data.weight);
        }
        Row row = sheet.createRow(++rows);
        Cell cell0 = row.createCell(0);
        cell0.setCellValue("合计");
        {
            Cell cell3 = row.createCell(4);
            cell3.setCellValue(totalCount);
        }
        {
            Cell cell3 = row.createCell(5);
            cell3.setCellValue(totalWeight);
        }

        {
            Cell cell3 = row.createCell(9);
            cell3.setCellValue(totalProfit);
        }

        FileOutputStream outputStream = new FileOutputStream(fileName);
        workbook.write(outputStream);
        workbook.close();
    }

    public static void writeTmpFile(String fileName, List<ExcelData> datas) throws Exception {
        logger.info("write output file " + fileName);
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet");

        Row header = sheet.createRow(0);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("内单号");

        headerCell = header.createCell(1);
        headerCell.setCellValue("成本价");


        int rows = 0;
        for (ExcelData data: datas) {
            rows++;
            Row row = sheet.createRow(rows);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(data.trackNumber);
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(data.price);
        }
        FileOutputStream outputStream = new FileOutputStream(fileName);
        workbook.write(outputStream);
        workbook.close();
    }
}
