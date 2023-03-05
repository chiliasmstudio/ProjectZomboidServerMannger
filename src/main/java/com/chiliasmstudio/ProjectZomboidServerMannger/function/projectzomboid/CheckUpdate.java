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
import com.chiliasmstudio.ProjectZomboidServerMannger.lib.Util.Steam.SteamAPI;
import com.chiliasmstudio.ProjectZomboidServerMannger.function.discord.MainBot;
import org.apache.commons.lang3.SystemUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
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
    private static ServerConfig serverConfig = new ServerConfig();
    
    public CheckUpdate(String configDir) throws Exception {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.LoadConfig("config//servers//"+configDir);
        this.serverConfig = serverConfig;
    }

    public void run(){
        try {
            // Start Project Zomboid server.
            try {
                ProcessBuilder server = null;
                if(serverConfig.isSSH()){
                    throw new RuntimeException("Error");
                } else if (SystemUtils.IS_OS_WINDOWS) {
                    server = new ProcessBuilder("cmd", "/c start "+serverConfig.getServerStartupScrip()).directory(new File(serverConfig.getServerDirectory()));
                } else if (SystemUtils.IS_OS_MAC) {
                    throw new RuntimeException("Error");
                } else if (SystemUtils.IS_OS_LINUX) {
                    server = new ProcessBuilder("xterm", "-e", "sh your_script.sh").directory(new File(serverConfig.getServerDirectory()));
                }

                Process p = server.start();
                MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage(serverConfig.getServerName()+" is booting now.").queue();
            }catch (IOException e){
                e.printStackTrace();
            }

            while (true){
                unixTimestamp = Instant.now().getEpochSecond();
                SendLog(unixTimestamp);
                SendLog(formattedDate(unixTimestamp));
                JSONArray itemList = SteamAPI.GetPublishedFileDetails(SteamAPI.GetCollectionDetail(2857565347L));
                // Foreach workshop item
                int error = 0;
                for (int i = 0; i < itemList.length(); i++){
                    try {
                        JSONObject item = itemList.getJSONObject(i);
                        if(item.getLong("time_updated")>unixTimestamp){
                            //TODO restart server
                        }
                        //System.out.println(i + " " + item.get("title"));
                    }catch (JSONException e){
                        error++;
                    }
                }
                SendLog(error + " items error on check");
                Thread.sleep(60000L);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }






    /**
     * Return formatted time with yyyy/MM/dd HH:mm:ss
     * @param unixTimestamp Unix timestamp.
     * */
    private String formattedDate(Long unixTimestamp){
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
    private void SendLog(Object obj){
        System.out.println("["+serverConfig.getServerName()+"] "+obj);
    }}
