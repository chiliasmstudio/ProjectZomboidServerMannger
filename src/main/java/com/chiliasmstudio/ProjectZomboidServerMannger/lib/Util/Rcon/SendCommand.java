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

package com.chiliasmstudio.ProjectZomboidServerMannger.lib.Util.Rcon;

import com.chiliasmstudio.ProjectZomboidServerMannger.ServerConfig;
import com.chiliasmstudio.ProjectZomboidServerMannger.lib.Rcon.Rcon;
import com.chiliasmstudio.ProjectZomboidServerMannger.lib.Rcon.ex.AuthenticationException;

import java.io.IOException;

public class SendCommand {

    /**
     * Sends a message to the target server via Rcon.
     * Returns true if successful, false if there was an error.
     *
     * @param message The message to be sent
     * @param serverConfig The configuration of the target server
     * @return true if the message was successfully sent, false otherwise
     */
    public static boolean sendMessage(String message, ServerConfig serverConfig) {
        try {
            Rcon rcon = new Rcon(serverConfig.getServerIP(), serverConfig.getRconPort(), serverConfig.getRconPassword().getBytes());
            rcon.command("servermsg \"" + message + "\"");
            rcon.disconnect();
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
}
