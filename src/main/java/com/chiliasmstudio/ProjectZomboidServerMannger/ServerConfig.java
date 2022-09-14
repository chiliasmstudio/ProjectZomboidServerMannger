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

        ServerName = properties.getProperty("ServerName", "");
        if(ServerName == null || ServerName.isEmpty())
            throw new Exception("ServerName not found!");

        ServerIP = properties.getProperty("ServerIP", "");
        if(ServerIP == null || ServerIP.isEmpty())
            throw new Exception("ServerIP not found!");

    }


    /** Server name. */
    @Getter
    private String ServerName = "";
    /** Server IP. */
    @Getter
    public static String ServerIP = "";

    /** Server name. */
    @Getter
    private String RconPort = "";
    /** Server IP. */
    @Getter
    public static String Serv  = "";



}