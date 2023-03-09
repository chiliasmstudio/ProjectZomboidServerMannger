/*
 * < ProjectZomboidServerMannger - Project Zomboid server manage software >
 *     Copyright (C) 2022-2023 chiliasmstudio
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

import com.chiliasmstudio.ProjectZomboidServerMannger.ServerConfig;
import com.chiliasmstudio.ProjectZomboidServerMannger.lib.Rcon.Rcon;
import com.chiliasmstudio.ProjectZomboidServerMannger.lib.Rcon.ex.AuthenticationException;
import com.chiliasmstudio.ProjectZomboidServerMannger.lib.Util.Rcon.SendCommand;
import com.chiliasmstudio.ProjectZomboidServerMannger.lib.Util.Steam.SteamAPI;
import com.chiliasmstudio.ProjectZomboidServerMannger.function.discord.MainBot;
import org.apache.commons.lang3.SystemUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class CheckUpdate extends Thread {
    // Last check time.
    private long unixTimestamp = 0L;
    // Server config file.
    private static ServerConfig serverConfig = new ServerConfig();

    public CheckUpdate(String configDir) throws Exception {
        serverConfig.LoadConfig("config//servers//" + configDir);
    }

    public void run() {
        try {
            if (!startServer())
                throw new RuntimeException();
            unixTimestamp = Instant.now().getEpochSecond();
            while (true) {
                JSONArray updateList = new JSONArray();
                SendLog(formattedDate(unixTimestamp) + " (" + unixTimestamp + ")" + " start check.");
                JSONArray itemList = SteamAPI.GetPublishedFileDetails(SteamAPI.GetCollectionDetail(2857565347L));
                // Foreach workshop item
                boolean needRestart = false;
                int error = 0;
                for (int i = 0; i < itemList.length(); i++) {
                    try {
                        JSONObject item = itemList.getJSONObject(i);
                        if (item.getLong("time_updated") > unixTimestamp) {
                            SendLog("Need update: " + item.get("title"));
                            updateList.put(item);
                            needRestart = true;
                        }
                    } catch (JSONException e) {
                        error++;
                    }
                }
                SendLog(error + " items error on check.");
                if (needRestart) {
                    MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage(serverConfig.getServerName() + " need reboot! restart in 5 minute.").queue();
                    SendCommand.sendMessage("Server need reboot! restart in 5 minute.",serverConfig);
                    MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage("Mod to update:").queue();
                    for (int i = 0; i < updateList.length(); i++) {
                        JSONObject element = updateList.getJSONObject(i);
                        String message = "https://steamcommunity.com/sharedfiles/filedetails/?id=" + element.get("publishedfileid");
                        MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage("<"+message + ">").setEmbeds(Collections.emptyList()).queue();
                    }
                    Thread.sleep(60*1000L);// 4 minute.
                    SendCommand.sendMessage("Server need reboot! restart in 4 minute.",serverConfig);
                    Thread.sleep(60*1000L);// 3 minute.
                    SendCommand.sendMessage("Server need reboot! restart in 3 minute.",serverConfig);
                    Thread.sleep(60*1000L);// 2 minute.
                    SendCommand.sendMessage("Server need reboot! restart in 2 minute.",serverConfig);
                    Thread.sleep(60*1000L);// 1 minute.
                    SendCommand.sendMessage("Server need reboot! restart in 1 minute.",serverConfig);
                    Thread.sleep(30*1000L);// 30 second.
                    SendCommand.sendMessage("Server need reboot! restart in 30 second.",serverConfig);
                    Thread.sleep(20*1000L);// 10 second.
                    SendCommand.sendMessage("Server need reboot! restart in 10 second.",serverConfig);
                    Thread.sleep(5*1000L);// 5 second.
                    SendCommand.sendMessage("Server need reboot! restart in 5 second.",serverConfig);
                    Thread.sleep(1*1000L);// 4 second.
                    SendCommand.sendMessage("Server need reboot! restart in 4 second.",serverConfig);
                    Thread.sleep(1*1000L);// 3 second.
                    SendCommand.sendMessage("Server need reboot! restart in 3 second.",serverConfig);
                    Thread.sleep(1*1000L);// 2 second.
                    SendCommand.sendMessage("Server need reboot! restart in 2 second.",serverConfig);
                    Thread.sleep(1*1000L);// 1 second.
                    SendCommand.sendMessage("Server need reboot! restart in 1 second.",serverConfig);
                    Thread.sleep(3*1000L);
                    MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage(serverConfig.getServerName() + " rebooting!").queue();
                    SendLog("Stopping server.");
                    if (closeServer()) {
                        Thread.sleep(30 * 1000L);
                        SendLog("Server has stop.");
                        SendLog("Rebooting server.");
                        if (!startServer())
                            throw new RuntimeException();
                    } else
                        throw new RuntimeException();
                    Thread.sleep(serverConfig.getRestartTime() * 1000L);
                }
                unixTimestamp = Instant.now().getEpochSecond();
                Thread.sleep(serverConfig.getCheckFrequency() * 1000L);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Start the server and send Discord notification
     *
     * @return true if server is started successfully, false otherwise
     */
    private static boolean startServer() {
        try {
            ProcessBuilder server = null;
            if (serverConfig.isSSH()) {
                throw new RuntimeException("Error");
            } else if (SystemUtils.IS_OS_WINDOWS) {
                server = new ProcessBuilder("cmd", "/c start " + serverConfig.getServerStartupScrip()).directory(new File(serverConfig.getServerDirectory()));
            } else if (SystemUtils.IS_OS_MAC) {
                throw new RuntimeException("Error");
            } else if (SystemUtils.IS_OS_LINUX) {
                server = new ProcessBuilder("xterm", "-e", "sh your_script.sh").directory(new File(serverConfig.getServerDirectory()));
            }

            Process p = server.start();
            MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage(serverConfig.getServerName() + " is booting now.").queue();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Sends a "quit" command to the server via Rcon and disconnects.
     * Returns true if successful, false if there was an error.
     *
     * @return true if the quit command was successfully sent, false otherwise
     */
    private static boolean closeServer() {
        try {
            Rcon rcon = new Rcon(serverConfig.getServerIP(), serverConfig.getRconPort(), serverConfig.getRconPassword().getBytes());
            rcon.command("quit");
            rcon.disconnect();
            MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage(serverConfig.getServerName() + " has stop.").queue();
            return true;
        } catch (IOException e) {
            // Unable to connect to the server
            e.printStackTrace();
            return false;
        } catch (AuthenticationException e) {
            // Authentication failed
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Returns a formatted date string with the timezone set in serverConfig.
     *
     * @param unixTimestamp the Unix timestamp to format
     * @return the formatted date string
     */
    private String formattedDate(Long unixTimestamp) {
        ZoneId zoneId;
        if (serverConfig.getTimeZone().equalsIgnoreCase("Auto"))
            zoneId = ZoneId.systemDefault();
        else
            zoneId = ZoneId.of(serverConfig.getTimeZone());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(zoneId);
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        return formatter.format(instant);
    }

    /**
     * Send Message to console.
     *
     * @param obj Message to send.
     */
    private void SendLog(Object obj) {
        System.out.println("[" + serverConfig.getServerName() + "] " + obj);
    }
}
