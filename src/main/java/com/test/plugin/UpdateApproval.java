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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

/**
 *
 * @author ASANI
 */
public class UpdateApproval extends DefaultApplicationPlugin {
    
    String pluginName = "Test Update Mapping";
    int intRow = 0, currRow = 0;

    @Override
    public Object execute(Map map) {
                
        PluginManager pluginManager = (PluginManager) map.get("pluginManager");
        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
        WorkflowAssignment workflowAssignment = (WorkflowAssignment) map.get("workflowAssignment");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        String submodule = getPropertyString("submodule");
        String row = wm.getProcessVariable(workflowAssignment.getProcessId(), "currLevel");
//        LogUtil.info(pluginName, row);
        currRow = Integer.valueOf(row);
//        LogUtil.info(pluginName, "Current Row" +currRow);
        String submoduleId = this.getModule(submodule);
        String id = appService.getOriginProcessId(workflowAssignment.getProcessId());
        List<String> approvers = new ArrayList<String>();    
        Map<String, String> activityData = new HashMap();
        
        
        try {
            approvers = this.getApprover(submoduleId);
            updateLevel(id, currRow);
        } catch (SQLException ex) {
            Logger.getLogger(UpdateApproval.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String apprv = String.valueOf(approvers.get(currRow));
        LogUtil.info(pluginName, apprv);
        wm.activityVariable(activityData.get("activity_id"), "approver", apprv);
        
        //Update for Next Level
        currRow++;
        String nextApprv = String.valueOf(approvers.get(currRow));
        LogUtil.info(pluginName, "Next: "+nextApprv);
        wm.activityVariable(activityData.get("activity_id"), "nextApprover", nextApprv);
//        String lastAppr = String.valueOf(approvers.get(approvers.size()-1));
//        wm.activityVariable(activityData.get("activity_id"), "lastApprover", lastAppr);
        wm.activityVariable(activityData.get("activity_id"), "maxLevel", ""+intRow);
        wm.activityVariable(activityData.get("activity_id"), "currLevel", ""+currRow);
        return false;
    }

    private void updateLevel(String activityId, Integer currLevel){
        
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs = null;
        String level = String.valueOf(currLevel);
        
        String query = "UPDATE app_fd_testbrow SET c_last_status ='"+level+"' WHERE id ='"+activityId+"'";
        
        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery(query);
            
            LogUtil.info(pluginName, "Data has been updated!");
            
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
    }
    
    private String getModule(String submodule){
        String result = "";
        
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs = null;
        
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
    
    private HashMap<String, String> getActivityData(String activityId){
        
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        PreparedStatement ps = null;
        Connection con = null;
        StringBuilder query = new StringBuilder();
        ResultSet rs = null;
        
        HashMap<String, String> result = new HashMap();
        
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
            ps.setString(1, activityId);
            rs = ps.executeQuery();
            
            
            while (rs.next()) {
                result.put("activity_id", rs.getString("activity_id"));
                result.put("process_id", rs.getString("process_id"));
                result.put("process_def_id", rs.getString("process_def_id"));
                result.put("activity_name", rs.getString("activity_name"));
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
        
        return result;
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
    
    @Override
    public String getName() {
        return pluginName;
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getVersion() {
        return "1.0";
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
    
}
