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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    /**
     * Load config value form config.properties.
     * @param configFileDir Directory of config.
     * @throws IOException When fail to load config.
     * @throws Exception when
    */
    public static void LoadConfig(String configFileDir) throws Exception{
        Properties properties = new Properties();
        if(configFileDir==null||configFileDir.isEmpty())
            configFileDir = "config/config.properties";
        try {
            properties.load(new FileInputStream(configFileDir));
        } catch (IOException ex) {
            throw new IOException("Fail to load config file!");
        }

        Token = properties.getProperty("token", "");
        //If token not set
        if(Token ==null|| Token.isEmpty())
            throw new Exception("Token not found!");

    }
    /** Token of discord bot */
    public static String Token = "";

}
