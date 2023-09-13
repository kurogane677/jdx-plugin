/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin.function;

import com.test.plugin.dao.EmailReminderDao;
import com.test.plugin.model.EmailDetails;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.ApplicationPlugin;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;

/**
 *
 * @author WEEBS
 * 
 * 
 * 
 * NAME                             LAST UPDATE (DD/MM/YY)                VERSION         COMMENT
 * Yusril                           12/09/2023                              1.0         Initial PLUGIN
 */
public class EmailReminderFunc {
    
    String pluginName = "HRDC - Levy Sent Email";
    EmailReminderDao dao = new EmailReminderDao();
    EmailDetails model = new EmailDetails();
    
        public Object execute(Map map, String mailTo){
            String message = this.changeSubject(model.getContent());
            
            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
            PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        
            Plugin plugin = pluginManager.getPlugin("org.joget.apps.app.lib.EmailTool");
     
//            Get default properties (SMTP setting) for email tool
            Map propertiesMap = AppPluginUtil.getDefaultProperties(plugin, "", appDef, null);
            propertiesMap.put("pluginManager", pluginManager);
            propertiesMap.put("appDef", appDef);
//          propertiesMap.put("request", request);
     
            ApplicationPlugin emailTool = (ApplicationPlugin) plugin;
     
//            send email
            propertiesMap.put("toSpecific", mailTo);
/*        Test Mail
        propertiesMap.put("subject", "This is a test email for " + mailTo);
        propertiesMap.put("message", "Email content for " + mailTo);
*/
            propertiesMap.put("subject", model.getSubject());
//            propertiesMap.put("UTF-8");
            propertiesMap.put("message", message);
         
//        set properties and execute the tool
            ((PropertyEditable) emailTool).setProperties(propertiesMap);
            emailTool.execute(propertiesMap);
            LogUtil.info(pluginName, "Successfully send email to "+mailTo);
     
        return null;
    }
        
        public String changeSubject(String message){
            String result = "";
            String[] hashedMessage = message.split("#");
            LocalDate date = LocalDate.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String today = date.format(dtf);
            
            for(String hashed : hashedMessage){
                if(hashed.equalsIgnoreCase("COMPANYNAME")){
                    message = message.replace("#"+hashed+"#", "SLPKNT22");
                    result = message;
                }else if(hashed.equalsIgnoreCase("COMPANYADDRESS")){
                    message = message.replace("#"+hashed+"#", "Jl. Bandung");
                    result = message;
                }else if(hashed.equalsIgnoreCase("datesend")){
                    message = message.replace("#"+hashed+"#", today);
                    result = message;
                }
            }
            return result;
        }
}
