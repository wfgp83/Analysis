package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class Constants {
    private static Logger logger = LoggerFactory.getLogger(Constants.class);

    public static String appDir = "d:\\wfgp_util\\finance\\";
    public static String mainRaw = "main_rawData";
    public static String subRaw = "sub_rawData";
    public static String q9Raw = "Q_rawData";

    public static String emilRaw = "emil_rawData";

    public static String LOG = "log";

    //public static String tabFileNamePrefix = "sendTab";

    public static boolean matchTrackNum(String trackNum, List<String> trackNumbers){
        for (String s : trackNumbers) {
            if (s.equals(trackNum))
                return true;
        }
        return false;
    }

    public static int PREFIX_IDX = 1;


    public static void halt(String appDir, String subDir){
        logger.info("Confirm put Emil data to " + appDir +
                "\\" + emilRaw + "  directory, and export one file from Q9 to " + appDir +
                "\\" + q9Raw + "directory." +
                "\nThen press any keyboard key to start ...");

        Scanner scan=new Scanner(System.in);
        scan.nextLine();
        try {
            Thread.sleep(2);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }
    }

    public static void halt(String account){
        logger.info("Confirm go to Q9 "+ account +" account,\nThen press any keyboard key to start ...");

        Scanner scan=new Scanner(System.in);
        scan.nextLine();
        try {
            Thread.sleep(2);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }
    }

    public static String getAccount(String msg){
        if(msg.equals(Constants.mainRaw)){
            return "main";
        } else{
            return "sub";
        }
    }
}
