/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;

/**
 *
 * @author ASANI
 */
public class ForfeitBalanceDao {
    
    String pluginName = "HRDC - Levy Forfeit Balance DAO";
    
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    Connection con = null;
    Statement st = null;
    ResultSet rs = null;
    
    public List<String> checkTransactionUsers(){
        
        List<String> result = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        
        query
                .append("SELECT afllce.c_id_mycoid")
//                .append(" , afllce.c_status , afllce.c_end_of_warning_period , afllce.c_file_id ")
//                .append(" , (SELECT ")
//                .append(" CASE")
//                .append("	WHEN afllce.c_status = 'New' THEN 'New'")
//                .append("	WHEN afllce.c_status = 'Forfeited' THEN 'Forfeited'")
//                .append("	WHEN afllce.c_status = 'Exempted' THEN 'Exempted'")
//                .append("	WHEN afllf.c_status = 'APPLICATION APPROVED' THEN 'Approved'")
//                .append("	ELSE afllf.c_status")
//                .append(" END")
//                .append(" ) AS status")
                .append(" FROM app_fd_levm_lef_crtr_emp afllce ")
                .append(" JOIN app_fd_levm_lef_feapp afllf ON afllf.c_forfeiture_file_id = afllce.c_file_id AND afllce.c_id_mycoid = afllf.c_employer_id_mycoid ")
                .append(" JOIN app_fd_levm_stp_levy_forf aflslf ON afllce.c_forfeiture_criteria_id = aflslf.id")
                .append("(")
                .append(" WHERE STR_TO_DATE(afllce.c_end_of_warning_period, '%Y-%m-%d') = CURDATE() ")
//                .append(" AND STR_TO_DATE(afllce.c_last_claim_date , '%Y-%m-%d') BETWEEN STR_TO_DATE(afllce.c_approved_date  , '%Y-%m-%d') ")
//                .append(" AND STR_TO_DATE(afllce.c_end_of_warning_period , '%Y-%m-%d'))")
                .append(" AND (afllce.c_status != 'Forfeited' AND afllce.c_status != 'Exempted')");
        try {
            con = ds.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(query.toString());
            LogUtil.info(pluginName, "Execute "+query.toString());
            
            while(rs.next()){
                result.add(rs.getString(1));
            }
            
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            try {
                con.close(); st.close(); rs.close();
                LogUtil.info(pluginName, "Close Connection To Check Transaction Id!");
            } catch (SQLException e) {
                LogUtil.error(pluginName, e, "");
            }
        }
        return result;
    }
    
    public void updateDataTransaction(String id){
        StringBuilder query = new StringBuilder();
        
        query.append("UPDATE app_fd_levm_lef_email_rmndr afller SET afller.c_levy_balance = 0.00 WHERE c_parent_id = '"+id+"'");
        try {
            con = ds.getConnection();
            st = con.createStatement();
            st.executeUpdate(query.toString());
            LogUtil.info(pluginName, "Execute "+query.toString());
            LogUtil.info(pluginName, "Successfully update data Transaction ID "+id);
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            try {
                con.close(); st.close(); rs.close();
                LogUtil.info(pluginName, "Close Connection To Balance Update Id!");
            } catch (SQLException e) {
                LogUtil.error(pluginName, e, "");
            }
        }
    }
    
    public void updateStatusTransaction(String id){
        StringBuilder query = new StringBuilder();
        
        query.append("UPDATE app_fd_levm_lef_crtr_emp afllce SET c_status='Forfeited' , c_levy_balance_after = 0.00 WHERE id = '"+id+"'");
        try {
            con = ds.getConnection();
            st = con.createStatement();
            st.executeUpdate(query.toString());
            LogUtil.info(pluginName, "Execute "+query.toString());
            LogUtil.info(pluginName, "Successfully update status Transaction ID "+id);
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            try {
                con.close(); st.close(); rs.close();
                LogUtil.info(pluginName, "Close Connection To Update SStatus!");
            } catch (SQLException e) {
                LogUtil.error(pluginName, e, "");
            }
        }
    }
}
