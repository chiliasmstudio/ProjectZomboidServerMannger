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

import com.chiliasmstudio.ProjectZomboidServerMannger.function.projectzomboid.CheckUpdateHandler;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


import static com.chiliasmstudio.ProjectZomboidServerMannger.function.discord.MainBot.initialization_Main;

public class main {
    private static final Logger mainLogger = LogManager.getLogger("main");
    public static CheckUpdateHandler[] checkUpdates = new CheckUpdateHandler[30];

    public static void main(String[] args) throws Exception {

        for (String arg : args) {
            // Set the logging level based on whether the -debug flag is used
            if ("-debug".equalsIgnoreCase(arg)) {
                Configurator.setRootLevel(Level.DEBUG);
                break;
            }
        }


        //SetUpLog4j();
        mainLogger.debug("This is a debug message.");
        mainLogger.info("This is an info message.");
        //mainLogger.error("This is an error message.");

        Config.LoadConfig(".//config//config.properties");
        initialization_Main(Config.DiscordToken);
        Thread.sleep(5000L);

        LoadServerConfig();
    }

    public static void LoadServerConfig() throws Exception {
        // Load the main configuration file


        File folder = new File(".//config//servers");
        File[] listOfFiles = folder.listFiles();
        boolean hasServerConfig = false; // Flag to check if any other config files are found

        // Iterate through the files and check for .properties files other than server_example.properties
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".properties") && !listOfFiles[i].getName().equals("server_example.properties")) {
                hasServerConfig = true; // Set flag to true if a valid config is found
                mainLogger.info("find server config: " + listOfFiles[i].getName());
                CheckUpdateHandler updater = new CheckUpdateHandler(listOfFiles[i].getName());
                checkUpdates[i] = updater;
            }
        }

        // If no server config files are found other than server_example.properties, throw an exception
        if (!hasServerConfig) {
            throw new Exception("No server config files found other than server_example.properties!");
        }

        // Start update checks for all the valid server configurations
        for (CheckUpdateHandler servers : checkUpdates) {
            if (servers != null) {
                // TEMP servers.start();
                Thread.sleep(1000L);
            }
        }
    }

    //Not use yet
    public static void SetUpLog4j() throws IOException {
        File configFile = new File("config/log4j2.xml");
        if (!configFile.exists()) {
            // If it does not exist, copy the resource file from the JAR
            try (InputStream resourceStream = Config.class.getClassLoader().getResourceAsStream("log4j2.xml")) {
                if (resourceStream == null) {
                    throw new FileNotFoundException("Resource log4j2.xml not found in JAR!");
                }

                // Create directories if they do not exist
                File configDir = new File(configFile.getParent());
                if (!configDir.exists()) {
                    configDir.mkdir();
                }

                // Copy the file from the JAR to the current directory
                Files.copy(resourceStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Config file copied from JAR resources.");
            } catch (IOException ex) {
                throw new IOException("Failed to copy log4j2.xml from JAR resources!", ex);
            }
        }

        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File("Config/log4j2.xml");
        context.setConfigLocation(file.toURI());
    }
}
