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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerConfig {
    /**
     * Load config value form config.properties.
     * @param configFileDir Directory of config.
     * @throws IOException When fail to load config.
     * @throws Exception When required argument is invalid.
    */
    public void LoadConfig(String configFileDir) throws Exception{
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configFileDir));
        } catch (IOException ex) {
            throw new IOException("Fail to load server config file!");
        }

        //-------- Basic -------
        ServerName = properties.getProperty("ServerName", "");
        if(ServerName == null || ServerName.isEmpty())
            throw new Exception("ServerName not found!");

        ServerIP = properties.getProperty("ServerIP", "");
        if(ServerIP == null || ServerIP.isEmpty())
            throw new Exception("ServerIP not found!");

        TimeZone = properties.getProperty("TimeZone", "");

        //-------- Rcon --------
        RconPort = properties.getProperty("RconPort", "");
        if(RconPort == null || RconPort.isEmpty())
            throw new Exception("RconPort not found!");

        RconPassword = properties.getProperty("RconPassword", "");
        if(RconPassword == null || RconPassword.isEmpty())
            throw new Exception("RconPassword not found!");

        //-------- Discord --------
        DiscordChannel = Long.valueOf(properties.getProperty("DiscordChannel", ""));

        //-------- Directory --------
        ServerDirectory = properties.getProperty("ServerDirectory", "");
        if(ServerDirectory == null || ServerDirectory.isEmpty())
            throw new Exception("ServerDirectory not found!");

        ServerStartupScrip = properties.getProperty("ServerStartupScrip", "");
        if(ServerStartupScrip == null || ServerStartupScrip.isEmpty())
            throw new Exception("ServerStartupScrip not found!");


    }


    //-------- Basic --------
    /** Server name. */
    @Getter
    private String ServerName = "";
    /** Server ip. */
    @Getter
    private String ServerIP = "";

    /** Server time zone. */
    @Getter
    private String TimeZone  = "";

    //-------- Rcon --------
    /** Rcon port. */
    @Getter
    private String RconPort = "";

    /** Rcon password. */
    @Getter
    private String RconPassword = "";

    //-------- Discord --------
    /** Discord Channel that send notify. (optional). */
    @Getter
    private Long DiscordChannel  = 0L;

    //-------- Directory --------
    /** Directory of server. */
    @Getter
    private String ServerDirectory  = "";

    /** Directory of server startup bat/sh file. */
    @Getter
    private String ServerStartupScrip  = "";



}