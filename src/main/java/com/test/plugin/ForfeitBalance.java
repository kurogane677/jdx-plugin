/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin;

import com.test.plugin.dao.ForfeitBalanceDao;
import com.test.plugin.function.ForfeitBalanceFunc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;

/**
 *
 * @author WEEBS
 * 
 * 
 * 
 * NAME                             LAST UPDATE (DD/MM/YY)                VERSION         COMMENT
 * Yusril                           13/09/2023                              1.0         Initial PLUGIN
 */
public class ForfeitBalance extends DefaultApplicationPlugin{
    
    String pluginName = "HRDC - Levy Forfeit amount";
    ForfeitBalanceDao dao = new ForfeitBalanceDao();
    ForfeitBalanceFunc func = new ForfeitBalanceFunc();

    @Override
    public Object execute(Map map) {
        List<String> id_mycoids = new ArrayList<>();
        id_mycoids = dao.checkTransactionUsers();
        String url = getPropertyString("endpoint");
        boolean execute = Boolean.valueOf(getPropertyString("execute"));
        
        if(!execute) return execute;
        
        for(String id_mycoid : id_mycoids){
            if(func.GenerateFinanceSystem(url, id_mycoid).equalsIgnoreCase("Success")){
                dao.updateDataTransaction(id_mycoid);
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return pluginName;
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getVersion() {
        return "1.0";
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getDescription() {
        return pluginName;
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getLabel() {
        return pluginName;
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getPropertyOptions() {
//        return "";
        return AppUtil.readPluginResource(getClassName(), "/properties/ForfeitBalanceSettings.json", null, true, null);
    }
    
}
