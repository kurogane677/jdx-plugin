/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author WEEBS
 * 
 * NAME                             LAST UPDATE                     VERSION         COMMENT
 * Yusril                           06 - Sep - 2023                     1.0         Initial Files
 */

public class ApprovalMappingDao {
    
    String pluginName = "HRDC - Mapping Approval DAO"; 
    
    public String getModule(String submodule) throws SQLException{
        String result = "";
        
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        String query = ("SELECT id FROM app_fd_levm_stp_aprvl WHERE c_submodule ='"+submodule+"'");
        
        try {
            con = ds.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(query);
            
            LogUtil.info(pluginName,"Execute "+query);
            if(rs.next()){
                result = rs.getString(1);
            }
            LogUtil.info(pluginName,"Successfully Loaded the Module");
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            //Close any connection avaiable
            con.close(); st.close(); rs.close();
            LogUtil.info(pluginName, "Connection to Module closed!");
        }
        
        return result;
    }
    
    public HashMap<String, String> getActivityData(String processRecordId) throws SQLException {

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
                .append(" wf_process_link wpl LEFT JOIN SHKActivities s")
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
            
            LogUtil.info(pluginName,"Execute "+query.toString());
            LogUtil.info(pluginName,"Successfully Loaded the Activity Data");
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
            //Close any connection avaiable
            con.close(); ps.close(); rs.close();
            LogUtil.info(pluginName, "Connection to Activity Data closed!");
        }
        return activityData;
    }
    
    public List<String> getApprover(String id) throws SQLException {
        List<String> result = new ArrayList<String>();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();
        
        query.append("SELECT c_officer_name FROM app_fd_levm_stp_aprv_dtls WHERE c_detail_id ='"+id+"' AND c_status = 'Active' ORDER BY dateCreated ASC");
        try {
            con = ds.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(query.toString());

            while (rs.next()) {
                result.add(rs.getString(1));
            }
            LogUtil.info(pluginName,"Execute "+query.toString());
            LogUtil.info(pluginName,"Successfully Loaded the Approvers");
        } catch (SQLException ex) {
            LogUtil.error(this.getClass().getName(), ex, "Error : " + ex.getMessage());
        } finally {
            con.close(); st.close(); rs.close();
            LogUtil.info(pluginName, "Connection to Approver closed!");
        }
        return result;
    }
    
}
