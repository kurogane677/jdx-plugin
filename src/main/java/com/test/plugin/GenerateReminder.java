/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin;

import com.test.plugin.dao.EmailReminderDao;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;

/**
 *
 * @author WEEBS
 * 
 * NAME                             LAST UPDATE (DD/MM/YY)                VERSION         COMMENT
 * Yusril                           06/09/2023                              1.0         Initial PLUGIN
 */

public class GenerateReminder extends DefaultApplicationPlugin {
    
    String pluginName = "HRDC - Levy Generate Email Reminder Forfeiture";
    EmailReminderDao dao = new EmailReminderDao();

    @Override
    public Object execute(Map map) {
//        WorkflowUserManager wm = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        WorkflowAssignment wfa = (WorkflowAssignment) map.get("workflowAssignment");
        String id = appService.getOriginProcessId(wfa.getProcessId()); // get id data forf
        
        //Generate Email Reminder
        try {
            if(dao.checkData(id)){
                dao.insertEmailReminder();
            }   
        } catch (Exception e) {
            LogUtil.error(pluginName,e, "");
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
        return "";
//        return AppUtil.readPluginResource(getClassName(), "/properties/generate.json", null, true, null);
    }
    
}
