/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin;

import com.test.plugin.dao.ApproverActionDao;
import java.util.HashMap;
import java.util.Map;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListActionDefault;
import org.joget.apps.datalist.model.DataListActionResult;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.service.WorkflowManager;

/**
 *
 * @author WEEBS
 */
public class ApproverAction extends DataListActionDefault{
    
    String pluginName = "HRDC - Approver Layer 4";
    ApproverActionDao aad = new ApproverActionDao();

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return pluginName;
    }

    @Override
    public String getLinkLabel() {
        String label = getPropertyString("label");
        if (label == null || label.isEmpty()) {
            label = pluginName;
        }
        return label;
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
            confirm = "Are you sure?";
        }
        return confirm;
    }

    @Override
    public DataListActionResult executeAction(DataList dl, String[] strings) {
        DataListActionResult result = new DataListActionResult();
        LogUtil.info(pluginName, "Masuk Execute");
        
        String submodule = getPropertyString("submodule");
        String submitType = getPropertyString("submittype");
        String actorUsername = getPropertyString("actor_username");
        String actorFullname = getPropertyString("actor_fullname");
        String remarkMessage = getPropertyString("remark_message");
        String tableProcessName = getPropertyString("tableProcessName");
        String tableName = getPropertyString("tableNameAudit");
        String applicationStatus = "";
        String auditActivity = "";
        String auditStatus = "";
        String applicationLastAction = "";
        
        if(submitType.equalsIgnoreCase("Approved")){
            applicationStatus = "APPLICATION APPROVED";
            auditActivity = "Application Approved";
            auditStatus = "Approved";
            applicationLastAction = "Approved";
        }else if(submitType.equalsIgnoreCase("Rejected")){
            applicationStatus = "APPLICATION REJECTED";
            auditActivity = "Application Rejected";
            auditStatus = "Rejected";
            applicationLastAction = "Rejected";
        }else{
            applicationStatus = "APPLICATION RETURNED";
            auditActivity = "Application Returned";
            auditStatus = "Returned";
            applicationLastAction = "Returned";   
        }
        
        for(String key : strings){
            LogUtil.info(pluginName, "ID : "+key);
            LogUtil.info(pluginName, "submodule: " + submodule);
            LogUtil.info(pluginName, "actorUsername: " + actorUsername);
            LogUtil.info(pluginName, "actorFullname: " + actorFullname);
            LogUtil.info(pluginName, "remarkMessage: " + remarkMessage);
            LogUtil.info(pluginName, "Action : " + submitType);
            LogUtil.info(pluginName, "applicationStatus: " + applicationStatus);
            LogUtil.info(pluginName, "auditActivity: " + auditActivity);
            LogUtil.info(pluginName, "auditStatus: " + auditStatus);
            LogUtil.info(pluginName, "applicationLastAction: " + applicationLastAction);
            LogUtil.info(pluginName, "Submit Type: " + submitType);
            
            String appStatus = aad.getStatus(key, tableProcessName);
            LogUtil.info(pluginName, appStatus);
            
            if(appStatus.equalsIgnoreCase("PENDING LAST APPROVAL")){
                this.ActivityCompleted(key, submodule, actorFullname, actorUsername, remarkMessage, applicationStatus, auditActivity, auditStatus, applicationLastAction, tableName, submitType);
                result.setType(result.TYPE_REDIRECT);
                result.setMessage(remarkMessage);
            }else{
                result.setType(result.TYPE_ERROR);
                result.setMessage("Check application status or reload the page!");
            }
        }
        
        return result;
    }
    
    public void ActivityCompleted(String key, String submodule, String actorFullname, String actorUsername, String remarkMessage, String applicationStatus, String auditActivity, String auditStatus, String applicationLastAction, String tableName, String submitType) {
        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");

        Map<String, String> activityData = new HashMap();
        activityData = aad.getActivityData(key);

        workflowManager.activityVariable(activityData.get("activity_id"), "status", submitType);
        workflowManager.assignmentForceComplete(activityData.get("process_def_id"), activityData.get("process_id"), activityData.get("activity_id"), actorUsername);

        if (submodule.equalsIgnoreCase("levy exemption")) {
            if (aad.updateStatusLevyExemption(key, applicationStatus, actorFullname, actorUsername, applicationLastAction)) {
                LogUtil.info("action button lex", "st: true");
                aad.insertAuditTrailLevyExemption(actorFullname, actorUsername, auditActivity, key, auditStatus, remarkMessage);
            }
        } else if (submodule.equalsIgnoreCase("levy exemption criteria")) {
            if (aad.updateStatusLevyExemptionCriteria(key, applicationStatus, actorFullname, actorUsername, applicationLastAction)) {
                LogUtil.info("action button lexc", "st: true");
                aad.insertAuditTrailLevyExemptionCriteria(actorFullname, actorUsername, auditActivity, key, auditStatus, remarkMessage);
            }
        } else if (submodule.equalsIgnoreCase("interest waiver")) {
            if (aad.updateStatusInterestWaiver(key, applicationStatus, actorFullname, actorUsername, applicationLastAction)) {
                LogUtil.info("action button inw", "st: true");
                aad.insertAuditTrailWaiver(actorFullname, actorUsername, auditActivity, key, auditStatus, remarkMessage, tableName);
            }
        } 
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
        if (appDef != null) {
            String formJsonUrl = "[CONTEXT_PATH]/web/json/console/app/" + appDef.getId() + "/" + appDef.getVersion() + "/forms/options";
            formDefField = "{name:'formDefId',label:'@@form.defaultformoptionbinder.formId@@',type:'selectbox',required:'True',options_ajax:'" + formJsonUrl + "'}";
        } else {
            formDefField = "{name:'formDefId',label:'@@form.defaultformoptionbinder.formId@@',type:'textfield',required:'True'}";
        }
        Object[] arguments = new Object[]{formDefField};
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/approverActionSettings.json", arguments, true,null);
        return json;
//        return AppUtil.readPluginResource(getClassName(), "/properties/approverActionSettings.json", null, true, null);
    }
    
}
