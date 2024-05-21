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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class Config {
    /**
     * Load config value form config.properties.
     *
     * @param configFileDir Directory of config.
     * @throws IOException When fail to load config.
     * @throws Exception   When required argument is invalid.
     */
    public static void LoadConfig(String configFileDir) throws Exception {
        Properties properties = new Properties();
        if (configFileDir == null || configFileDir.isEmpty())
            configFileDir = "config/config.properties";
        try {
            properties.load(new FileInputStream(configFileDir));
        } catch (IOException ex) {
            throw new IOException("Fail to load config file!");
        }

        DiscordToken = properties.getProperty("DiscordToken", "");
        if (DiscordToken == null || DiscordToken.isEmpty())
            throw new Exception("Discord Token not found!");

        SteamKey = properties.getProperty("SteamKey", "");
        if (SteamKey == null || SteamKey.isEmpty())
            throw new Exception("SteamKey not found!");

        String ServersLine = properties.getProperty("Servers", "");
        Servers.addAll(Arrays.asList(ServersLine.split(";")));
        if (Servers == null || Servers.isEmpty())
            throw new Exception("Servers not found!");


    }

    /**
     * Token of discord bot.
     */
    public static String DiscordToken = "";

    /**
     * Steam Web API key.
     */
    public static String SteamKey = "";

    /**
     * Servers to manage.
     */
    public static ArrayList<String> Servers = new ArrayList<>();

}
