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

package com.chiliasmstudio.ProjectZomboidServerMannger;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerConfig {
    /**
     * Load config value form config.properties.
     *
     * @param configFileDir Directory of config.
     * @throws IOException When fail to load config.
     * @throws Exception   When required argument is invalid.
     */
    public void LoadConfig(String configFileDir) throws Exception {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configFileDir));
        } catch (IOException ex) {
            throw new IOException("Fail to load server config file!");
        }

        //-------- Basic -------
        ServerName = properties.getProperty("ServerName", "");
        if (ServerName == null || ServerName.isEmpty())
            throw new Exception("ServerName not found!");

        ServerIP = properties.getProperty("ServerIP", "");
        if (ServerIP == null || ServerIP.isEmpty())
            throw new Exception("ServerIP not found!");

        SteamCollections = Long.valueOf(properties.getProperty("SteamCollections", ""));
        if (SteamCollections < 0)
            throw new Exception("SteamCollections not found!");

        TimeZone = properties.getProperty("TimeZone", "");

        RestartTime = Long.valueOf(properties.getProperty("RestartTime", ""));
        if (RestartTime <= 0)
            throw new Exception("RestartTime not found!");

        CheckFrequency = Long.valueOf(properties.getProperty("CheckFrequency", ""));
        if (CheckFrequency <= 0)
            throw new Exception("CheckFrequency not found!");

        StartCommand = properties.getProperty("StartCommand", "");
        if (StartCommand.isEmpty())
            throw new Exception("StartCommand not found!");

        //-------- SSH --------
        SSH = Boolean.parseBoolean(properties.getProperty("SSH", ""));
        if (SSH) {
            SSH_Command = properties.getProperty("SSH_Command", "");
            if (SSH_Command == null || SSH_Command.isEmpty())
                throw new Exception("SSH_Command not found!");
        }


        //-------- Rcon --------
        RconPort = Integer.parseInt(properties.getProperty("RconPort", ""));
        if (RconPort < 0 || RconPort > 65535)
            throw new Exception("RconPort not found!");

        RconPassword = properties.getProperty("RconPassword", "");
        if (RconPassword == null || RconPassword.isEmpty())
            throw new Exception("RconPassword not found!");

        //-------- Discord --------
        DiscordChannel = Long.valueOf(properties.getProperty("DiscordChannel", ""));

        //-------- Directory --------
        ServerDirectory = properties.getProperty("ServerDirectory", "");
        if (ServerDirectory == null || ServerDirectory.isEmpty())
            throw new Exception("ServerDirectory not found!");

        ServerStartupScrip = properties.getProperty("ServerStartupScrip", "");
        if (ServerStartupScrip == null || ServerStartupScrip.isEmpty())
            throw new Exception("ServerStartupScrip not found!");


    }


    //-------- Basic --------
    /**
     * Server name.
     */
    @Getter
    private String ServerName = "";
    /**
     * Server ip.
     */
    @Getter
    private String ServerIP = "";

    /**
     * Steam collections id.
     */
    @Getter
    private Long SteamCollections = -1L;

    /**
     * Server time zone.
     */
    @Getter
    private String TimeZone = "";

    /**
     * Restart time.
     */
    @Getter
    private Long RestartTime = -1L;

    /**
     * CheckFrequency.
     */
    @Getter
    private Long CheckFrequency = -1L;

    /**
     * Server time zone.
     */
    @Getter
    private String StartCommand = "";

    //-------- SSH --------

    /**
     * Using ssh.
     */
    @Getter
    private boolean SSH = false;

    /**
     * Command to run in ssh
     */
    @Getter
    private String SSH_Command = "";

    //-------- Rcon --------
    /**
     * Rcon port.
     */
    @Getter
    private int RconPort = -1;

    /**
     * Rcon password.
     */
    @Getter
    private String RconPassword = "";

    //-------- Discord --------
    /**
     * Discord Channel that send notify. (optional).
     */
    @Getter
    private Long DiscordChannel = 0L;

    //-------- Directory --------
    /**
     * Directory of server.
     */
    @Getter
    private String ServerDirectory = "";

    /**
     * Directory of server startup bat/sh file.
     */
    @Getter
    private String ServerStartupScrip = "";


}