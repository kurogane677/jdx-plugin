/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin;

import com.test.plugin.dao.ApprovalMappingDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

/**
 *
 * @author WEEBS
 */

public class ApprovalMapping extends DefaultApplicationPlugin {

    String pluginName = "HRDC - Mapping Approval";
    ApprovalMappingDao apmd = new ApprovalMappingDao();
    
    @Override
    public Object execute(Map map) {
        
        LogUtil.info(pluginName, "Masuk Execute");
        PluginManager pluginManager = (PluginManager) map.get("pluginManager");
        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        String id = appService.getOriginProcessId(workflowAssignment.getProcessId());
        String submodule = getPropertyString("submodule");
        List<String> approvers = new ArrayList<String>();    
        Map<String, String> activityData = new HashMap();
        String submoduleId = "";
        
        LogUtil.info(pluginName,"Reading Data Access Object");
        try {
            submoduleId = apmd.getModule(submodule);
            activityData = apmd.getActivityData(id);
            approvers = apmd.getApprover(submoduleId);
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        }
        
        LogUtil.info(pluginName,"Pass Data Access Object");
        
//        for (int i = 0; i < approvers.size(); i++) {
            wm.activityVariable(activityData.get("activity_id"), "levy_approver", approvers.get(0));
            wm.activityVariable(activityData.get("activity_id"), "levy_approver_two", approvers.get(1));
            wm.activityVariable(activityData.get("activity_id"), "levy_approver_three", approvers.get(2));
            wm.activityVariable(activityData.get("activity_id"), "levy_approver_four", approvers.get(3));
//        }

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
        return AppUtil.readPluginResource(this.getClassName(), "/properties/approvalMappingSettings.json");
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
