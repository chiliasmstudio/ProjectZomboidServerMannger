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

package com.chiliasmstudio.ProjectZomboidServerMannger.lib.Rcon;

import com.chiliasmstudio.ProjectZomboidServerMannger.lib.Rcon.ex.AuthenticationException;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;

public class Rcon {

    private final Object sync = new Object();
    private final Random rand = new Random();

    private int requestId;
    private Socket socket;

    private Charset charset;

    /**
     * Create, connect and authenticate a new Rcon object
     *
     * @param host     Rcon server address
     * @param port     Rcon server port
     * @param password Rcon server password
     * @throws IOException
     * @throws AuthenticationException
     */
    public Rcon(String host, int port, byte[] password) throws IOException, AuthenticationException {
        // Default charset is utf8
        this.charset = Charset.forName("UTF-8");

        // Connect to host
        this.connect(host, port, password);
    }

    /**
     * Connect to a rcon server
     *
     * @param host     Rcon server address
     * @param port     Rcon server port
     * @param password Rcon server password
     * @throws IOException
     * @throws AuthenticationException
     */
    public void connect(String host, int port, byte[] password) throws IOException, AuthenticationException {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Host can't be null or empty");
        }

        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port is out of range");
        }

        // Connect to the rcon server
        synchronized (sync) {
            // New random request id
            this.requestId = rand.nextInt();

            // We can't reuse a socket, so we need a new one
            this.socket = new Socket(host, port);
        }

        // Send the auth packet
        RconPacket res = this.send(RconPacket.SERVERDATA_AUTH, password);

        // Auth failed
        if (res.getRequestId() == -1) {
            throw new AuthenticationException("Password rejected by server");
        }
    }

    /**
     * Disconnect from the current server
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        synchronized (sync) {
            this.socket.close();
        }
    }

    /**
     * Send a command to the server
     *
     * @param payload The command to send
     * @return The payload of the response
     * @throws IOException
     */
    public String command(String payload) throws IOException {
        if (payload == null || payload.trim().isEmpty()) {
            throw new IllegalArgumentException("Payload can't be null or empty");
        }

        RconPacket response = this.send(RconPacket.SERVERDATA_EXECCOMMAND, payload.getBytes());

        return new String(response.getPayload(), this.getCharset());
    }

    private RconPacket send(int type, byte[] payload) throws IOException {
        synchronized (sync) {
            return RconPacket.send(this, type, payload);
        }
    }

    public int getRequestId() {
        return requestId;
    }

    public Socket getSocket() {
        return socket;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

}
