/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.test.plugin.function;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ASANI
 */
public class ForfeitBalanceFunc {
    String pluginName = "HRDC - Levy Forfeit Balance";
    
    public String GenerateFinanceSystem(String url, String id_mycoid){
        String result = "";
        HttpURLConnection connection = null;
        try {
//            URL yang hit ke modul claim
            URL endpoint = new URL(url);
            connection = (HttpURLConnection) endpoint.openConnection();
            connection.getHeaderField("");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json"); //Untuk Header
//            Untuk Header Lain kalo ada
//            connection.setRequestProperty("hash", "hashedtext?");
//            connection.setRequestProperty("dateTime", "today");
//            connection.setRequestProperty("apiKey", "apiKey");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.close();
            
            InputStream input = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                response.append(line);
                response.append('\r');
            }
            reader.close();
            
            JSONObject obj = new JSONObject(response.toString());
            JSONArray data = obj.getJSONArray("data");
            
            for (int i = 0; i < data.length(); i++) {
//                Simpen status
                JSONObject mainObj = data.getJSONObject(i);
                if(mainObj.has("status")){
                    result = mainObj.getString("status");
                }
            }
           
        }catch(Exception e){
            LogUtil.error(pluginName, e,"");
        }
        finally {
            connection.disconnect();
        }
        return result;
    }
}
