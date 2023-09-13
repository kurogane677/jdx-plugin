/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin.dao;

import com.test.plugin.model.EmailDetails;
import com.test.plugin.model.ForfDetails;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author WEEBS
 * 
 * 
 * 
 * NAME                             LAST UPDATE (DD/MM/YY)                VERSION         COMMENT
 * Yusril                           12/09/2023                              1.0         Initial PLUGIN
 */
public class EmailReminderDao {
    
    String pluginName = "HRDC - Levy Generate Email Reminder Forfeiture DAO";
    LocalDate today = LocalDate.now();
    LocalDate lastDayOnMonth = today.withDayOfMonth(today.getMonth().length(today.isLeapYear()));
    
    DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
    Connection con = null;
    Statement st = null;
    ResultSet rs = null;
    ForfDetails forfModel = new ForfDetails();
    EmailDetails emailModel = new EmailDetails();
    
    public boolean checkData(String id){
        boolean result = false;
        StringBuilder query = new StringBuilder();
        
        query
                .append("SELECT id,c_id_mycoid , c_total_amount ")
                .append(", (SELECT MAX(c_reminder_period) FROM app_fd_levm_stp_levy_forf ORDER BY dateCreated DESC) as c_reminder_period")
                .append(", (SELECT MAX(c_reminder_period_type) FROM app_fd_levm_stp_levy_forf ORDER BY dateCreated DESC) as c_reminder_period_type ")
                .append(", (SELECT MAX(c_no_of_reminder_per_period) FROM app_fd_levm_stp_levy_forf ORDER BY dateCreated DESC) as c_no_of_reminder_per_period ")
                .append(" FROM app_fd_levm_lef_crtr_emp")
                .append(" WHERE id='"+id+"'");
        
        try {
            con = ds.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(query.toString());
            LogUtil.info(pluginName, "Execute "+query.toString());
            
            if(rs.next()){
                forfModel.setId(rs.getString("id"));
                forfModel.setMyCoID(rs.getString("c_id_mycoid"));
                forfModel.setForfeitBalance(rs.getString("c_total_amount"));
                forfModel.setPeriod(rs.getString("c_reminder_period"));
                forfModel.setPeriodType(rs.getString("c_reminder_period_type"));
                forfModel.setNoPeriod(rs.getString("c_no_of_reminder_per_period"));
                LogUtil.info(pluginName, "Success Get Criteria Data!");
                result = true;
            }
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            try {
                con.close(); st.close();
                LogUtil.info(pluginName, "Close Connection To Check Data!");
            } catch (SQLException e) {
                LogUtil.error(pluginName, e, "");
            }
        }
        return result;
    }
    
    public boolean insertEmailReminder(){
        boolean result = false;
        
        StringBuilder query = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate nextremind = LocalDate.now();
        LocalDate endremind = LocalDate.now();
        int getStartDay = lastDayOnMonth.getDayOfMonth();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        UUID uuid = UUID.randomUUID();
        int daysdiff = 0;
        
        //TODO
        //Set END REMINDER
        if(forfModel.getPeriodType().equalsIgnoreCase("Month")){
            //TODO
            daysdiff = (getStartDay * Integer.valueOf(forfModel.getPeriod())) / (Integer.parseInt(forfModel.getNoPeriod()));
            nextremind = today.plusDays(Long.valueOf(daysdiff));
            endremind = today.plusMonths(Long.parseLong(forfModel.getPeriod()));
        }else if(forfModel.getPeriodType().equalsIgnoreCase("Year")){
            //TODO
            daysdiff = (getStartDay * Integer.parseInt(forfModel.getPeriod())) / (Integer.parseInt(forfModel.getNoPeriod()));
            nextremind = today.plusDays(Long.valueOf(daysdiff));
            endremind = today.plusYears(Long.valueOf(forfModel.getPeriod()));
        }
        
        query.append("INSERT INTO app_fd_levm_lef_email_rmndr ") 
//             .append(" (id, dateCreated, dateModified") 
//             .append(" createdBy, createdByName, modifiedBy, modifiedByName")
//             .append(" c_period , c_no_of_period, c_period_type, c_levy_balance")  
//             .append(" , c_reminder_date_start, c_next_reminder_date, c_end_of_reminder")  
//             .append(" , c_claimed, c_id_mycoid, c_parent_id)") 
             .append(" VALUES (")  
             .append("'"+uuid+"'")// ID
             .append(", CURDATE() ")// dateCreated
             .append(", CURDATE() ")// dateModified
             .append(", 'SYSTEM' ")// createdBy
             .append(", 'SYSTEM' ")// createdByName
             .append(", 'SYSTEM' ")// modifiedBy
             .append(", 'SYSTEM' ")// modifiedByName
             .append(", '"+forfModel.getPeriod()+"'  ")// c_period
             .append(", '"+forfModel.getNoPeriod()+"'  ")// c_no_period
             .append(", '"+forfModel.getPeriodType()+"'  ")// c_period_type
             .append(", '"+forfModel.getForfeitBalance()+"'  ")// c_levy_balance -> balance want to forfeit
             .append(", '"+String.valueOf(today)+"' ")// c_reminder_date_start
             .append(", '"+String.valueOf(nextremind)+"'  ")// c_next_reminder_date
             .append(", '"+String.valueOf(endremind)+"'  ")// c_end_of_reminder
             .append(", 'No'  ")// c_claimed
             .append(", '"+forfModel.getMyCoID()+"'  ")// c_id_mycoid
             .append(", '"+forfModel.getId()+"' ")// c_parent_id
             .append(")");
        
        try {
            con = ds.getConnection();
            st = con.createStatement();
            st.executeUpdate(query.toString());
            LogUtil.info(pluginName, "Execute "+query.toString());
            LogUtil.info(pluginName, "Successfully generate email reminder");
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            try {
                con.close(); st.close();
                LogUtil.info(pluginName, "Close Connection To Generate Email Reminder!");
            } catch (SQLException e) {
                LogUtil.error(pluginName, e, "");
            }
        }
        return result;
    }
    
    public List<String> getReminderId(){
        List<String> result = new ArrayList();
        StringBuilder query = new StringBuilder();
        
        query
                .append("SELECT afllce.* FROM app_fd_levm_lef_crtr_emp afllce")
                .append(" JOIN app_fd_levm_lef_email_rmndr afller ON afllce.id = afller.c_parent_id ")
                .append(" WHERE ")
                .append(" (afller.c_reminder_date_start = CURDATE() OR afller.c_next_reminder_date = CURDATE() AND ")
                .append(" STR_TO_DATE(afller.c_end_reminder_date,'%Y-%m-%d') >= CURDATE()) ")
                .append(" AND afller.c_claimed ='No' AND afllce.c_total_amount  > 0");
        try {
            con = ds.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(query.toString());
            
            while(rs.next()){
                result.add(rs.getString(1));
            }
            
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            try {
                con.close(); st.close(); rs.close();
                LogUtil.info(pluginName, "Close Connection To Send Email Reminder!");
            } catch (SQLException e) {
                LogUtil.error(pluginName, e, "");
            }
        }
        
        return result;
    }
    
    public String checkEmailUser(String id){
        String result = "";
        StringBuilder query = new StringBuilder();
        
        query
                .append("SELECT ")
                .append(" du.email ")
                .append(" FROM dir_user du join app_fd_levm_lef_email_rmndr afller ON ")
                .append(" afer.c_username = du.username ")
                .append(" WHERE ")
                .append(" (afller.c_reminder_date_start = '"+today+"' or afller.c_next_reminder_date = '"+today+"' ")
                .append(" AND STR_TO_DATE(afller.c_end_of_reminder,'%Y-%m-%d') >= '"+today+"') ")
                .append(" AND afller.id = '"+id+"' ")
                .append(" AND afller.c_claimed ='No' and afller.c_balance > 0");
        
        try {
            con = ds.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(query.toString());
            LogUtil.info(pluginName, "Execute "+query.toString());
            if(rs.next()){
                //TODO LIST
//                Langsung kirim email menggunakan plugin email!?
                result = rs.getString(1);
            }
            
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            try {
                con.close(); st.close(); rs.close();
                LogUtil.info(pluginName, "Close Connection To Check User Email!");
            } catch (SQLException e) {
                LogUtil.error(pluginName, e, "");
            }
        }
        
        return result;
    }
    
    public boolean checkReminderData(String id){
        boolean result = false;
        StringBuilder query = new StringBuilder();
        
        query
                .append("SELECT ")
                .append(" id, c_period, c_no_of_period , c_period_type , c_reminder_date_start, c_next_reminder_date , c_username , c_parent_id ")
                .append(" FROM app_fd_levm_lef_email_rmndr afller")
                .append(" WHERE ")
                .append(" (c_reminder_date_start = '"+today+"' or c_next_reminder_date = '"+today+"' AND ")
                .append(" STR_TO_DATE(c_end_of_reminder,'%Y-%m-%d') >= '"+today+"') ")
                .append(" and c_claimed ='No' and c_balance > 0")
                .append(" AND c_parent_id='"+id+"'");
        
        try {
            con = ds.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(query.toString());
            
            if(rs.next()){
                emailModel.setId(rs.getString("id"));
                emailModel.setPeriod(rs.getString("c_period"));
                emailModel.setNoOfPeriod(rs.getString("c_no_period"));
                emailModel.setPeriodType(rs.getString("c_period_type"));
                emailModel.setStartReminder(rs.getString("c_reminder_date_start"));
                emailModel.setNextReminder(rs.getString("c_next_reminder_date"));
                result = true;
            }
            
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            try {
                con.close(); st.close(); rs.close();
                LogUtil.info(pluginName, "Close Connection To Send Email Reminder!");
            } catch (SQLException e) {
                LogUtil.error(pluginName, e, "");
            }
        }
        
        return result;
    }
    
    public boolean updateEmailReminder(String id){
        boolean result = false;
        StringBuilder query = new StringBuilder();
        LocalDate nextremind = LocalDate.parse(emailModel.getNextReminder());
        int getStartDay = lastDayOnMonth.getDayOfMonth();
        int daysdiff = 0;
        
        if(emailModel.getPeriodType().equalsIgnoreCase("Month")){
            daysdiff = (getStartDay * Integer.valueOf(emailModel.getPeriod())) / (Integer.parseInt(emailModel.getNoOfPeriod()));
            nextremind = today.plusDays(Long.valueOf(daysdiff));
        }else if(emailModel.getPeriodType().equalsIgnoreCase("Year")){
            daysdiff = (getStartDay * Integer.valueOf(emailModel.getPeriod())) / (Integer.parseInt(emailModel.getNoOfPeriod()));
            nextremind = today.plusDays(Long.valueOf(daysdiff));
        }
        
        query
                .append("UPDATE app_fd_levm_lef_email_rmndr afller ")
                .append(" SET c_next_reminder_date = '"+String.valueOf(nextremind)+"' ")
                .append(" WHERE c_parent_id = '"+id+"'");
        
        try {
            con = ds.getConnection();
            st = con.createStatement();
            st.executeUpdate(query.toString());
            LogUtil.info(pluginName, "Executed "+query.toString());
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            try {
                con.close(); st.close(); rs.close();
                LogUtil.info(pluginName, "Close Connection To Update Email Reminder!");
            } catch (SQLException e) {
                LogUtil.error(pluginName, e, "");
            }
        }
        
        return result;
    }
    
    public boolean getTemplateMail(String id){
        boolean result = false;
//        int result = 0;
        StringBuilder query = new StringBuilder();
        
        query.append("SELECT c_subject , c_content FROM app_fd_levm_stp_levy_email aflsle WHERE id='"+id+"'");
        
        try {
            con = ds.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(query.toString());
            LogUtil.info(pluginName, "Execute "+query.toString());
            if(rs.next()){
                emailModel.setSubject(rs.getString("c_subject"));
                emailModel.setContent(rs.getString("c_content"));
                result = true;
            }
        } catch (SQLException e) {
            LogUtil.error(pluginName, e, "");
        } finally {
            try {
                con.close(); st.close();
                LogUtil.info(pluginName, "Close Connection To Generate Email Reminder!");
            } catch (SQLException e) {
                LogUtil.error(pluginName, e, "");
            }
        }
        
        return result;
    }
}
