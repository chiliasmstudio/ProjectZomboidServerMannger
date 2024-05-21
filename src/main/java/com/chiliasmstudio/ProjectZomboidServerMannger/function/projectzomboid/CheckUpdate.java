/*
 * < ProjectZomboidServerMannger - Project Zomboid server manage software >
 *     Copyright (C) 2022-2024 chiliasmstudio
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
import java.util.Collections;

public class CheckUpdate extends Thread {
    // Last check time.
    private long unixTimestamp = 0L;
    // Server config file.
    private ServerConfig serverConfig = new ServerConfig();

    public CheckUpdate(String configDir) throws Exception {
        serverConfig.LoadConfig("config//servers//" + configDir);
    }

    public void run() {
        try {
            // Try to boot server.
            if (!startServer())
                throw new RuntimeException();
            SendLog("Boot ok.");

            // Wait until server is start.
            Thread.sleep(serverConfig.getRestartTime() * 1000L);

            // Try rcon connect.
            SendCommand sendCommand = new SendCommand(serverConfig);
            if(!sendCommand.connect())
                throw new RuntimeException();
            SendLog("Rcon ok.");


            unixTimestamp = Instant.now().getEpochSecond();
            while (true) {

                //Check update
                JSONArray updateList = new JSONArray();
                JSONArray itemList = null;
                try {
                    SendLog(formattedDate(Instant.now().getEpochSecond()) + " (" + Instant.now().getEpochSecond() + ")" + " start check.");
                    itemList = SteamAPI.GetPublishedFileDetails(SteamAPI.GetCollectionDetail(2857565347L));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Foreach workshop item.
                boolean needRestart = false;
                int error = 0;
                if (itemList != null) {
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
                } else
                    SendLog("Error on check.");
                SendLog(error + " items error on check.");
                unixTimestamp = Instant.now().getEpochSecond();


                // Restart server.
                if (needRestart) {
                    MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage(serverConfig.getServerName() + " need reboot! restart in 10 minute.").queue();
                    sendCommand.sendMessage("Server need reboot! restart in 10 minute.");
                    MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage("Mod to update:").queue();
                    for (int i = 0; i < updateList.length(); i++) {
                        JSONObject element = updateList.getJSONObject(i);
                        String message = "https://steamcommunity.com/sharedfiles/filedetails/?id=" + element.get("publishedfileid");
                        MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage("<" + message + ">").setEmbeds(Collections.emptyList()).queue();
                    }


                    Thread.sleep(60 * 1000L);// 5 minute.
                    sendCommand.sendMessage("Server need reboot! restart in 4 minute.");
                    Thread.sleep(240 * 1000L);// 1 minute.
                    sendCommand.sendMessage("Server need reboot! restart in 1 minute.");
                    Thread.sleep(30 * 1000L);// 30 second.
                    sendCommand.sendMessage("Server need reboot! restart in 30 second.");
                    Thread.sleep(20 * 1000L);// 10 second.
                    sendCommand.sendMessage("Server need reboot! restart in 10 second.");

                    MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage(serverConfig.getServerName() + " rebooting!").queue();
                    SendLog("Stopping server.");

                    int tryClose = 0;
                    while (true) {
                        if (closeServer()) {
                            Thread.sleep(60 * 1000L);
                            SendLog("Server has stop.");
                            break;
                        } else {
                            Thread.sleep(3000L);
                            if (tryClose > 3)
                                throw new RuntimeException("Failed to execute code after 3 attempts");
                        }
                        tryClose++;
                    }

                    // Try to boot server.
                    if (!startServer())
                        throw new RuntimeException();
                    SendLog("Boot ok.");

                    // Wait until server is start.
                    Thread.sleep(serverConfig.getRestartTime() * 1000L);

                    // Try rcon connect.
                    sendCommand = new SendCommand(serverConfig);
                    if(!sendCommand.connect())
                        throw new RuntimeException();
                    SendLog("Rcon ok.");
                }


                Thread.sleep(serverConfig.getCheckFrequency() * 1000L);//
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);//TODO Log4j
        }
    }

    /**
     * Start the server and send Discord notification
     *
     * @return true if server is started successfully, false otherwise
     */
    private boolean startServer() {
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
            e.printStackTrace();//TODO Log4j
            return false;
        } catch (Exception e) {
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
    private boolean closeServer() {
        try {
            Rcon rcon = new Rcon(serverConfig.getServerIP(), serverConfig.getRconPort(), serverConfig.getRconPassword().getBytes());
            rcon.command("quit");
            rcon.disconnect();
            MainBot.bot_Main.getTextChannelById(serverConfig.getDiscordChannel()).sendMessage(serverConfig.getServerName() + " has stop.").queue();
            return true;
        } catch (IOException e) {
            // Unable to connect to the server
            e.printStackTrace();//TODO Log4j
            return false;
        } catch (AuthenticationException e) {
            // Authentication failed
            e.printStackTrace();//TODO Log4j
            return false;
        } catch (Exception e) {
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
