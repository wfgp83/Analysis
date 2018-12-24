package client;

import javafx.util.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class EmilAccountData {
    public EmilAccountData(){
        datas = new Pair<>(Constants.mainRaw, new Pair<>(new ArrayList<>(), new ArrayList<>()));
        xDatas = new Pair<>(Constants.subRaw, new Pair<>(new ArrayList<>(), new ArrayList<>()));
    }
    public Pair<String, Pair<List<EmilData>, List<ExcelData>>> datas;
    public Pair<String, Pair<List<EmilData>, List<ExcelData>>> xDatas;

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Switch to  Account, please");
    }
}
