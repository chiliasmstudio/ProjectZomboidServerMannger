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

package com.chiliasmstudio.ProjectZomboidServerMannger;

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
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class CheckUpdate {//2857565347

    public static void main(){
        // List of workshop item id
        ArrayList<Long> ItemList = new ArrayList<Long>();
    }

    /**
     * Get and return workshop item ids in collection.
     * */
    public static ArrayList<Long> GetCollectionDetail(Long collectionID){
        // Send Post to steam api.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.steampowered.com/ISteamRemoteStorage/GetCollectionDetails/v1");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("format", "json"));
        nvps.add(new BasicNameValuePair("key", Config.SteamKey));
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

    public static void GetPublishedFileDetails(ArrayList<Long> itemList){
        // Send Post to steam api.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(String.valueOf("https://api.steampowered.com/ISteamRemoteStorage/GetPublishedFileDetails/v1/?key="+Config.SteamKey));
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("format", "json"));
        nvps.add(new BasicNameValuePair("itemcount", String.valueOf(itemList.size())));
        for (int i = 0; i < itemList.size(); i++)
            nvps.add(new BasicNameValuePair("publishedfileids[" + i +"]", itemList.get(i).toString()));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        try{
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            System.out.println("GetCollectionDetail with " + response2.getCode() + " " + response2.getReasonPhrase());
            HttpEntity entity2 = response2.getEntity();
            // Response form steam as string.
            String response = EntityUtils.toString(response2.getEntity(), "UTF-8");

            JSONObject obj = new JSONObject(response);
            JSONArray publishedfiledetails = obj
                    .getJSONObject("response")
                    .getJSONArray("publishedfiledetails");
            for (int i = 0; i < itemList.size(); i++){
                try {
                    publishedfiledetails.getJSONObject(0).get("title");
                    System.out.println(i + " " + publishedfiledetails.getJSONObject(i).get("title"));
                }catch (JSONException e){
                   System.out.println("Error on Check");
                }

            }

            //System.out.println(response);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity2);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
