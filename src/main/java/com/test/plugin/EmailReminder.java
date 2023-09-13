package com.test.plugin;

import com.test.plugin.dao.EmailReminderDao;
import com.test.plugin.function.EmailReminderFunc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author WEBBS
 * 
 * NAME                             LAST UPDATE(DD/MM/YY)                   VERSION         COMMENT
 * Yusril                           12/09/2023                                1.0         Initial Files
 */

public class EmailReminder extends DefaultApplicationPlugin {
    
    String pluginName = "HRDC - Levy Email Reminder Forfeiture";
    EmailReminderDao dao = new EmailReminderDao();
    EmailReminderFunc func = new EmailReminderFunc();

    @Override
    public Object execute(Map map) {
    List<String> ids = new ArrayList<>();
        ids = dao.getReminderId();
        
        for(String id : ids){
            String emailto = dao.checkEmailUser(id);
            try {
                if(dao.getTemplateMail("ET15")){
                    func.execute(map, emailto);
                    if(dao.checkReminderData(id)){
                        dao.updateEmailReminder(id);
                    }
                }
                
            } catch (Exception e) {
                LogUtil.error(pluginName,e, "");
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
        return AppUtil.readPluginResource(getClassName(), "/properties/emailReminderSettings.json", null, true, null);
    }
    
}
