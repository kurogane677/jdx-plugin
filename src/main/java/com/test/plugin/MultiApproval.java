/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListActionDefault;
import org.joget.apps.datalist.model.DataListActionResult;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.service.WorkflowManager;

/**
 *
 * @author ASANI
 */
public class MultiApproval extends DataListActionDefault {
    
    public static String pluginName = "Test multiple Approve";

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return pluginName;
    }

    @Override
    public String getLinkLabel() {
        return pluginName;
    }

    @Override
    public String getHref() {
        return getPropertyString("href");
    }

    @Override
    public String getTarget() {
        return "post";
    }

    @Override
    public String getHrefParam() {
        return getPropertyString("hrefParam");
    }

    @Override
    public String getHrefColumn() {
        return getPropertyString("hrefColumn");
    }

    @Override
    public String getConfirmation() {
        String confirm = getPropertyString("confirmation");
        if (confirm == null || confirm.isEmpty()) {
            confirm = "You sure? ";
        }
        return confirm;
    }
    
    @Override
    public DataListActionResult executeAction(DataList dl, String[] strings) {
    DataListActionResult result = new DataListActionResult();
    Map<String, String> activityData = new HashMap();
    WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
            
//        LogUtil.info(pluginName, "Hello World");
        for (String key : strings){
            LogUtil.info(pluginName, key);
            activityData = this.getActivityData(key);
            workflowManager.activityVariable(activityData.get("activity_id"), "status", "Approved");
            workflowManager.assignmentForceComplete(activityData.get("process_def_id"), activityData.get("process_id"), activityData.get("activity_id"), "admin");
            result.setType(result.TYPE_REDIRECT);
        }
        return result;
    }
    
        
    public HashMap<String, String> getActivityData(String processRecordId) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        PreparedStatement ps = null;
        Connection con = null;
        StringBuilder query = new StringBuilder();
        ResultSet rs = null;

        HashMap<String, String> activityData = new HashMap();

        query
                .append("SELECT")
                .append(" s.Id as activity_id,")
                .append(" s.ProcessId as process_id,")
                .append(" s.PDefName as process_def_id,")
                .append(" s.Name as activity_name")
                .append(" FROM")
                .append(" wf_process_link wpl LEFT JOIN shkactivities s")
                .append(" ON wpl.processId=s.ProcessId")
                .append(" WHERE")
                .append(" wpl.parentProcessId=?")
                .append(" AND (s.State='1000003' OR s.State='1000001')");
        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, processRecordId);
            rs = ps.executeQuery();

            while (rs.next()) {
                activityData.put("activity_id", rs.getString("activity_id"));
                activityData.put("process_id", rs.getString("process_id"));
                activityData.put("process_def_id", rs.getString("process_def_id"));
                activityData.put("activity_name", rs.getString("activity_name"));
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
                }
            }
        }
        return activityData;
    }
    

    @Override
    public String getLabel() {
        return pluginName;
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        String formDefField = null;
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        LogUtil.info(pluginName,"property masuk");
        if (appDef != null) {
            String formJsonUrl = "[CONTEXT_PATH]/web/json/console/app/" + appDef.getId() + "/" + appDef.getVersion() + "/forms/options";
            formDefField = "{name:'formDefId',label:'@@form.defaultformoptionbinder.formId@@',type:'selectbox',required:'True',options_ajax:'" + formJsonUrl + "'}";
        } else {
            formDefField = "{name:'formDefId',label:'@@form.defaultformoptionbinder.formId@@',type:'textfield',required:'True'}";
        }
        Object[] arguments = new Object[]{formDefField};
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/datalist/multi-approval.json", arguments, true);
        return json;
    
//        return AppUtil.readPluginResource(getClassName(), "/properties/multi-approval.json", null, true, null);
    }

}