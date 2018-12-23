package client;

import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.util.HashMap;
import java.util.Map;

public class SubSettleCsvHandler extends SettleCsvHandler{

    PropertiesReader pReader;
    Map<String, Integer> logCount = new HashMap<>();

    public SubSettleCsvHandler(PropertiesReader pReader){
        this.pReader = pReader;
        logger = LoggerFactory.getLogger(SubSettleCsvHandler.class);
    }

    public void LogIgnore(final String trackNum, final String datas){
        Integer v = logCount.get(datas);
        if (v == null){
            logCount.put(datas, 1);
        } else if(v <10){
            logger.info("Ignore " + trackNum +", " + datas);
            logCount.put(datas, v + 1);
        }
    }

    public boolean isSpecialRecord(final String trackNum, final String data){
        final String datas = "扣有偿中转";
        boolean ret = data.equals(datas) || data.equals(pReader.getIgnoreOne())
                ||data.equals(pReader.getIgnoreTwo()) || data.equals(pReader.getIgnoreThree());
        if(ret){
            LogIgnore(trackNum, data);
        }
        return ret;
    }
}
