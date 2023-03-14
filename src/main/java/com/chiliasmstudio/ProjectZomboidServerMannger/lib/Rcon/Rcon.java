/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 Marin Verstraete
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
