/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin.model;

/**
 *
 * @author WEEBS
 */
public class EmailDetails {
    String id, start, next, period, period_type, no_period;
    String subject, content;
    
    public String getId(){
        return id;
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    public String getStartReminder(){
        return start;
    }
    public void setStartReminder(String start){
        this.start = start;
    }
    
    public String getNextReminder(){
        return next;
    }
    public void setNextReminder(String next){
        this.next = next;
    }
    
    public String getPeriod(){
        return period;
    }
    public void setPeriod(String period){
        this.period = period;
    }
    
    public String getPeriodType(){
        return period_type;
    }
    public void setPeriodType(String period_type){
        this.period_type = period_type;
    }
    
    public String getNoOfPeriod(){
        return no_period;
    }
    public void setNoOfPeriod(String no_period){
        this.no_period = no_period;
    }
    
    public String getSubject(){
        return subject;
    }
    
    public void setSubject(String subject){
        this.subject = subject;
    }
    
    public String getContent(){
        return content;
    }
    
    public void setContent(String content){
        this.content = content;
    }
}
