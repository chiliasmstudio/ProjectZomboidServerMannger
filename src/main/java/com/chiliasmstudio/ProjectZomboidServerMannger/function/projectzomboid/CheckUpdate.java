/*
 * < ProjectZomboidServerMannger - Project Zomboid server manage software >
 *     Copyright (C) 2022-2022 chiliasmstudio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.chiliasmstudio.ProjectZomboidServerMannger.function.projectzomboid;

import com.chiliasmstudio.ProjectZomboidServerMannger.Config;
import com.chiliasmstudio.ProjectZomboidServerMannger.ServerConfig;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CheckUpdate extends Thread{
    // Last check time.
    private long unixTimestamp = 0L;
    // Name of server.
    private String serverName = "";


    public CheckUpdate(String serverName){
        unixTimestamp = Instant.now().getEpochSecond();
        this.serverName = serverName;
        ServerConfig s = new ServerConfig();
        s.getServerName();
    }

    public void run(){
        try {

            try {
                ProcessBuilder server = new ProcessBuilder("cmd", "/c start StartServer64.bat").directory(new File("C:\\Users\\paul0\\Downloads\\workzone"));
                Process p = server.start();
            }catch (IOException e){
                e.printStackTrace();
            }

            System.out.println(unixTimestamp);
            SendLog(unixTimestamp);
            SendLog(formattedDate(unixTimestamp));
            System.out.println(formattedDate(unixTimestamp));

            Thread.sleep(2000L);
            JSONArray itemList = GetPublishedFileDetails(CheckUpdate.GetCollectionDetail(2857565347L));
            // Foreach workshop item
            for (int i = 0; i < itemList.length(); i++){
                try {
                    JSONObject item = itemList.getJSONObject(i);
                    //System.out.println(i + " " + item.get("title"));
                }catch (JSONException e){
                    System.out.println("Error on Check");
                }

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get and return workshop item ids in collection.
     * */
    public static ArrayList<Long> GetCollectionDetail(Long collectionID){
        // Send Post to steam api.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.steampowered.com/ISteamRemoteStorage/GetCollectionDetails/v1");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("key", Config.SteamKey));
        nvps.add(new BasicNameValuePair("format", "json"));
        nvps.add(new BasicNameValuePair("collectioncount", "1"));
        nvps.add(new BasicNameValuePair("publishedfileids[0]", collectionID.toString()));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        ArrayList<Long> itemList = new ArrayList<Long>();
        try{
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            System.out.println("GetCollectionDetail with " + response2.getCode() + " " + response2.getReasonPhrase());
            HttpEntity entity2 = response2.getEntity();
            // Response form steam as string.
            String response = EntityUtils.toString(response2.getEntity(), "UTF-8");

            // Return item ids in collection.
            JSONObject obj = new JSONObject(response);
            JSONArray collectiondetails = obj
                    .getJSONObject("response")
                    .getJSONArray("collectiondetails")
                    .getJSONObject(0)
                    .getJSONArray("children");
            System.out.println(collectionID + " include " + collectiondetails.length() + " items");
            for (int i = 0; i < collectiondetails.length(); i++)
            {
                String publishedfileid = collectiondetails.getJSONObject(i).getString("publishedfileid");
                itemList.add(Long.parseLong(publishedfileid));
            }
            EntityUtils.consume(entity2);
            return itemList;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error while GetCollectionDetail.");
        }

    }

    public static JSONArray GetPublishedFileDetails(ArrayList<Long> itemList){
        // Send Post to steam api.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.steampowered.com/ISteamRemoteStorage/GetPublishedFileDetails/v1/?");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("key", Config.SteamKey));
        nvps.add(new BasicNameValuePair("format", "json"));
        nvps.add(new BasicNameValuePair("itemcount", String.valueOf(itemList.size())));
        for (int i = 0; i < itemList.size(); i++)
            nvps.add(new BasicNameValuePair("publishedfileids[" + i +"]", itemList.get(i).toString()));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        try{
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            System.out.println("GetPublishedFileDetails with " + response2.getCode() + " " + response2.getReasonPhrase());
            HttpEntity entity2 = response2.getEntity();

            // Response form steam as string.
            String response = EntityUtils.toString(response2.getEntity(), "UTF-8");
            JSONObject obj = new JSONObject(response);
            JSONArray publishedfiledetails = obj
                    .getJSONObject("response")
                    .getJSONArray("publishedfiledetails");

            EntityUtils.consume(entity2);
            return publishedfiledetails;
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    /**
     * Return formatted time with yyyy/MM/dd HH:mm:ss
     * @param unixTimestamp Unix timestamp.
     * */
    public String formattedDate(Long unixTimestamp){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().toZoneId()));
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        Date date = Date.from(instant);
        return dateFormat.format(date);
    }

    /**
     * Send Message to console.
     * @param obj Message to send.
     * */
    public void SendLog(Object obj){
        System.out.println(obj);
    }}
