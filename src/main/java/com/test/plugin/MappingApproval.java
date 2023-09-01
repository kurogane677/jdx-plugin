/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;

/**
 *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
 * @author ASANI
 */
public class MappingApproval extends DefaultApplicationPlugin {
    
    String pluginName = "Test Multiple Approval";
    String pluginClass = getClassName();
    int intRow = 0, currRow = 0;

    @Override
    public Object execute(Map map) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        
        PluginManager pluginManager = (PluginManager) map.get("pluginManager");
        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        String submodule = getPropertyString("submodule");
        String submoduleId = this.getModule(submodule);
//        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        
        String id = appService.getOriginProcessId(workflowAssignment.getProcessId());
//        LogUtil.info(pluginName, id);
        List<String> approvers = new ArrayList<String>();    
        Map<String, String> activityData = new HashMap();
        activityData = this.getActivityData(id);
        String lastAppr = "";
        
        currRow = this.getLevel(id);
        LogUtil.info(pluginName, "Pass Current Row");
        try {
            approvers = this.getApprover(submoduleId);
        } catch (SQLException ex) {
            Logger.getLogger(MappingApproval.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        LogUtil.info(pluginName, "Pass Approver Row");
        String apprv = String.valueOf(approvers.get(currRow));
        String nextApprv = String.valueOf(approvers.get(currRow+1));
        
        wm.activityVariable(activityData.get("activity_id"), "approver", apprv);
        wm.activityVariable(activityData.get("activity_id"), "nextApprover", nextApprv);
        
//        for(String approver : approvers){
//            LogUtil.info(pluginName, approver);
//            if(intRow == currRow){
//                wm.activityVariable(activityData.get("activity_id"), "approver", approver);
//            }else{
//                wm.activityVariable(activityData.get("activity_id"), "nextApprover", approver);
//            }
//            intRow++;
//        }
        currRow++;
        lastAppr = String.valueOf(approvers.get(approvers.size()-1));
//        LogUtil.info(pluginName, "Max Level "+intRow);
        wm.activityVariable(activityData.get("activity_id"), "maxLevel", ""+intRow);
        wm.activityVariable(activityData.get("activity_id"), "currLevel", ""+currRow);
        wm.activityVariable(activityData.get("activity_id"), "lastApprover", lastAppr);
        
        return false;
    }
    
    private String getModule(String submodule){
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        PreparedStatement ps = null;
        Connection con = null;
//        StringBuilder query = new StringBuilder();
        ResultSet rs = null;
        String result = "";
        
        String query = "SELECT id FROM app_fd_testpath WHERE c_field2 = '"+submodule+"'";
        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
        
            if(rs.next()){
                result = rs.getString(1);
            }
            
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
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
        
        return result;
    }

    public Integer getLevel(String id){
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        Integer result = 0;
        PreparedStatement ps = null;
        Connection con = null;
//        StringBuilder query = new StringBuilder();
        ResultSet rs = null;
        
        String query = "SELECT c_last_status FROM app_fd_testbrow WHERE id='"+id+"'";
        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            
            if(rs.next()){
                result = Integer.valueOf(rs.getString("c_last_status"));
            }
            
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
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
    public String getName() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return pluginName;
    }

    @Override
    public String getVersion() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return "1.0.0";
    }

    @Override
    public String getDescription() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return pluginName;
    }

    @Override
    public String getLabel() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return pluginName;
    }

    @Override
    public String getClassName() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return AppUtil.readPluginResource(getClassName(), "/properties/approverMappingSettings.json", null, true, null);
    }


    private List<String> getApprover(String id) throws SQLException {
        List<String> result = new ArrayList<String>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();

            String query = "SELECT "
                    + " c_userapprov "
                    + " FROM app_fd_testpath_dtl WHERE c_detail_id = '"+id+"' "
                    + " ORDER BY dateCreated ASC";
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                result.add(rs.getString(1));
                intRow++;
            }

        } catch (SQLException ex) {
            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
            }
        }
        return result;
    }
}
