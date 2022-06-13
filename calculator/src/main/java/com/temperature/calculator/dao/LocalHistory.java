package com.temperature.calculator.dao;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LocalHistory {

    private static LocalHistory instance = null;
    private Map<String, List<Calculator>> history= new HashMap<>();

    private LocalHistory() {
        {
            if (instance != null) {
                throw new RuntimeException("Not allowed. Please use getInstance() method");
            }
        }
    }

    public static LocalHistory getInstance() {

        if(instance == null) {
            instance = new LocalHistory();
        }

        return instance;
    }

    public void addUserCalculation(String user, Calculator calculator){
        var userHistory = getUserHistory(user);
        userHistory.add(calculator);
    }

    public List<Calculator> getUserHistory(String user){
        var userHistory = history.get(user);
        if(userHistory == null){
            userHistory = new LinkedList<Calculator>();
            history.put(user, userHistory);
        }
        return userHistory;
    }

}