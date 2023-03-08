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

package com.chiliasmstudio.ProjectZomboidServerMannger;

import com.chiliasmstudio.ProjectZomboidServerMannger.function.projectzomboid.CheckUpdate;
import net.dv8tion.jda.api.entities.Guild;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;

import static com.chiliasmstudio.ProjectZomboidServerMannger.function.discord.MainBot.initialization_Main;

public class main {
    public static CheckUpdate[] checkUpdates = new CheckUpdate[30];

    public static void main(String[] args) throws Exception {
        // TODO Log4j
        if (SystemUtils.IS_OS_WINDOWS) {
            System.out.println("[INFO]Operating system: Windows");
        } else if (SystemUtils.IS_OS_MAC) {
            System.out.println("[INFO]Operating system: macOS");
        } else if (SystemUtils.IS_OS_LINUX) {
            System.out.println("[INFO] Operating system: Linux");
        } else {
            System.out.println("[ERROR]Unknown operating system");
            throw new RuntimeException("Unknown operating system");
        }
        Config.LoadConfig(null);
        initialization_Main(Config.DiscordToken);
        Thread.sleep(5000L);


        File folder = new File(".//config//servers");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".properties") && !listOfFiles[i].getName().equals("server_example.properties")) {
                System.out.println("[Config] find server config: " + listOfFiles[i].getName());// TODO Log4j
                CheckUpdate updater = new CheckUpdate(listOfFiles[i].getName());
                checkUpdates[i] = updater;
            }
        }
        for (CheckUpdate servers : checkUpdates) {
            if (servers != null) {
                servers.start();
                Thread.sleep(1000L);
            }
        }

        /*
        Scanner scan = new Scanner( System.in );

        while (true) {
            String message = scan.nextLine();
            System.out.printf( "string \"%s\" received...\n", message );
            System.out.flush();
            System.err.flush();
            if ( message.equals( "end" ) ) {
                break;
            }

        }*/

    }
}
