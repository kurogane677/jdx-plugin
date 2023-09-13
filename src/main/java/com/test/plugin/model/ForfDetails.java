/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin.model;

/**
 *
 * @author WEEBS
 */
public class ForfDetails {
    String id, mycoid, levybalance, period , period_type, no_of_period;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    
    public String getNoPeriod(){
        return no_of_period;
    }
    
    public void setNoPeriod(String no_of_period){
        this.no_of_period = no_of_period;
    }
    
    public String getMyCoID() {
        return mycoid;
    }

    public void setMyCoID(String mycoid) {
        this.mycoid = mycoid;
    }
    
    public String getForfeitBalance() {
        return levybalance;
    }

    public void setForfeitBalance(String levybalance) {
        this.levybalance = levybalance;
    }
}