/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author WEEBS
 * 
 * NAME                             LAST UPDATE                     VERSION         COMMENT
 * Yusril                           06 - Sep - 2023                     1.0         Initial Plugin
 */
public class ApproverActionDao {
    public boolean updateStatusLevyExemption(String recordId, String status, String name, String username, String action) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();

        boolean result = false;

        query
                .append(" UPDATE")
                .append(" app_fd_levm_lex_app")
                .append(" SET")
                .append(" c_status=?,")
                .append(" c_last_action=?,")
                .append(" dateModified=CURRENT_TIMESTAMP(),")
                .append(" modifiedBy=?,")
                .append(" modifiedByName=?")
                .append(" WHERE")
                .append(" id=?");

        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, status);
            ps.setString(2, action);
            ps.setString(3, username);
            ps.setString(4, name);
            ps.setString(5, recordId);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
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
    
    public boolean insertAuditTrailLevyExemption(String name, String username, String activityName, String parentId, String status, String remark) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        PreparedStatement ps = null;
        Connection con = null;
        StringBuilder query = new StringBuilder();
        int insertReturn;
        boolean result = false;

        query
                .append("INSERT INTO app_fd_levm_lex_app_audit (id, dateCreated, dateModified, createdBy, createdByName, modifiedBy, modifiedByName, c_activity_name, c_approval_date, c_parent_id, c_name, c_username, c_status, c_remark)")
                .append("VALUES (UUID(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), ?, ?, ?, ?, ?, DATE_FORMAT(CURRENT_TIMESTAMP(), '%b %d, %Y %r'), ?, ?, ?, ?, ?)");
        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, username);
            ps.setString(2, name);
            ps.setString(3, username);
            ps.setString(4, name);
            ps.setString(5, activityName);
            ps.setString(6, parentId);
            ps.setString(7, name);
            ps.setString(8, username);
            ps.setString(9, status);
            ps.setString(10, remark);
            LogUtil.info("action button", ps.toString());
            insertReturn = ps.executeUpdate();

            if (insertReturn > 0) {
                result = true;
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
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
    
    public boolean updateStatusLevyExemptionCriteria(String recordId, String status, String name, String username, String action) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();

        boolean result = false;

        query
                .append(" UPDATE")
                .append(" app_fd_levm_lex_criteria")
                .append(" SET")
                .append(" c_status=?,")
                .append(" c_last_action=?,")
                .append(" dateModified=CURRENT_TIMESTAMP(),")
                .append(" modifiedBy=?,")
                .append(" modifiedByName=?")
                .append(" WHERE")
                .append(" id=?");

        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, status);
            ps.setString(2, action);
            ps.setString(3, username);
            ps.setString(4, name);
            ps.setString(5, recordId);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
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

    public boolean insertAuditTrailLevyExemptionCriteria(String name, String username, String activityName, String parentId, String status, String remark) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        PreparedStatement ps = null;
        Connection con = null;
        StringBuilder query = new StringBuilder();
        int insertReturn;
        boolean result = false;

        query
                .append("INSERT INTO app_fd_levm_lex_crtr_audit (id, dateCreated, dateModified, createdBy, createdByName, modifiedBy, modifiedByName, c_activity_name, c_approval_date, c_parent_id, c_name, c_username, c_status, c_remark)")
                .append("VALUES (UUID(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), ?, ?, ?, ?, ?, DATE_FORMAT(CURRENT_TIMESTAMP(), '%b %d, %Y %r'), ?, ?, ?, ?, ?)");
        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, username);
            ps.setString(2, name);
            ps.setString(3, username);
            ps.setString(4, name);
            ps.setString(5, activityName);
            ps.setString(6, parentId);
            ps.setString(7, name);
            ps.setString(8, username);
            ps.setString(9, status);
            ps.setString(10, remark);
            LogUtil.info("action button", ps.toString());
            insertReturn = ps.executeUpdate();

            if (insertReturn > 0) {
                result = true;
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
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

    public boolean updateStatusInterestWaiver(String recordId, String status, String name, String username, String action) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        PreparedStatement ps = null;
        StringBuilder query = new StringBuilder();

        boolean result = false;

        query
                .append(" UPDATE")
                .append(" app_fd_levm_inw_app")
                .append(" SET")
                .append(" c_status=?,")
                .append(" c_last_action=?,")
                .append(" dateModified=CURRENT_TIMESTAMP(),")
                .append(" modifiedBy=?,")
                .append(" modifiedByName=?")
                .append(" WHERE")
                .append(" id=?");

        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, status);
            ps.setString(2, action);
            ps.setString(3, username);
            ps.setString(4, name);
            ps.setString(5, recordId);

            int i = ps.executeUpdate();
            if (i > 0) {
                result = true;
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
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
    
    public boolean insertAuditTrailWaiver(String name, String username, String activityName, String parentId, String status, String remark, String tableName) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        PreparedStatement ps = null;
        Connection con = null;
        StringBuilder query = new StringBuilder();
        int insertReturn;
        boolean result = false;

        query
                .append("INSERT INTO app_fd_"+tableName+" (id, dateCreated, dateModified, createdBy, createdByName, modifiedBy, modifiedByName, c_activity_name, c_approval_date, c_parent_id, c_name, c_username, c_status, c_remark)")
                .append("VALUES (UUID(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), ?, ?, ?, ?, ?, DATE_FORMAT(CURRENT_TIMESTAMP(), '%b %d, %Y %r'), ?, ?, ?, ?, ?)");
        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, username);
            ps.setString(2, name);
            ps.setString(3, username);
            ps.setString(4, name);
            ps.setString(5, activityName);
            ps.setString(6, parentId);
            ps.setString(7, name);
            ps.setString(8, username);
            ps.setString(9, status);
            ps.setString(10, remark);
            LogUtil.info("action button", ps.toString());
            insertReturn = ps.executeUpdate();

            if (insertReturn > 0) {
                result = true;
            }
        } catch (SQLException e) {
            LogUtil.error(this.getClass().getName(), e, "Error : " + e.getMessage());
        } finally {
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
    
    public String getStatus(String id, String tableProcessName) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        StringBuilder query = new StringBuilder();
        String result = "";

        query.append("SELECT c_status FROM app_fd_"+tableProcessName+" WHERE id = ? LIMIT 1");
        try {
            con = ds.getConnection();
            ps = con.prepareStatement(query.toString());
            ps.setString(1, id);
            rs = ps.executeQuery();

            while (rs.next()) {
                result = rs.getString(1);
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
}
