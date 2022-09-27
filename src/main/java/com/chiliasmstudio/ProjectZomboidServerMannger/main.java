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

import com.chiliasmstudio.ProjectZomboidServerMannger.function.projectzomboid.CheckUpdate;

import java.util.Scanner;

import static com.chiliasmstudio.ProjectZomboidServerMannger.function.discord.MainBot.initialization_Main;

public class main {
    public static void main(String[] args) throws Exception {
        Config.LoadConfig(null);
        initialization_Main(Config.DiscordToken);

        CheckUpdate updater1 = new CheckUpdate("TestNet 2");
        updater1.start();

        Scanner scan = new Scanner( System.in );

        while (true) {
            String message = scan.nextLine();
            System.out.printf( "string \"%s\" received...\n", message );
            System.out.flush();
            System.err.flush();
            if ( message.equals( "end" ) ) {
                break;
            }

        }

    }
}
