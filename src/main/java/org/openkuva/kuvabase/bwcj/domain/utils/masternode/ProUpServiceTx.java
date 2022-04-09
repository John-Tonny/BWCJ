/*
 * Copyright (c)  2018 One Kuva LLC, known as OpenKuva.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted (subject to the limitations in the disclaimer
 * below) provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *
 *      * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 *      * Neither the name of the One Kuva LLC, known as OpenKuva.org nor the names of its
 *      contributors may be used to endorse or promote products derived from this
 *      software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY
 * THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.openkuva.kuvabase.bwcj.domain.utils.masternode;

import org.bitcoinj.core.*;
import org.bitcoinj.core.ECKey.*;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IInput;
import org.spongycastle.util.encoders.Base64;

import java.io.IOException;
import java.security.SignatureException;

public final class ProUpServiceTx {
    int version = 1;
    int mode = 0;
    IInput[] inputs;
    String proTxHash;
    String host;
    int port;

    String masternodePrivKey;
    String payAddr;
    NetworkParameters params;

    String sig;

    public ProUpServiceTx(IInput[] inputs, String proTxHash, String host, int port, String masternodePrivKey, String payAddr, NetworkParameters params) {
        this.inputs = inputs;
        this.proTxHash = proTxHash;
        this.host = host;
        this.port =port;
        this.masternodePrivKey = masternodePrivKey;
        this.payAddr = payAddr;
        this.params = params;
        this.sig = null;
    }

    public void setSig(String sig){
        this.sig = sig;
    }

    private void writeInputHash(UnsafeByteArrayOutputStream outputStream) throws IOException {
        UnsafeByteArrayOutputStream outputStream1 = new UnsafeByteArrayOutputStream();
        try {
            for (int i = 0; i < this.inputs.length; i++) {
                Utils.writeInput(this.inputs[i].getTxid(), this.inputs[i].getVout(), outputStream1);
            }
            outputStream.write(Sha256Hash.twiceOf(outputStream1.toByteArray()).getBytes());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public String serialize(UnsafeByteArrayOutputStream outputStream, boolean useSig) throws  IOException {
        try {
            Utils.uint16ToByteStreamLE(this.version, outputStream);
            Utils.writeHash(this.proTxHash, outputStream);
            Utils.writeIp(this.host, this.port, outputStream);
            Utils.writeAddress(this.params, this.payAddr, false, outputStream);
            this.writeInputHash(outputStream);
            if(this.sig != null && useSig){
                outputStream.write(Utils.HEX.decode(this.sig));
            }
            return Utils.HEX.encode(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMessageHash() {
        UnsafeByteArrayOutputStream outputStream = new UnsafeByteArrayOutputStream();
        UnsafeByteArrayOutputStream outputStream1 = new UnsafeByteArrayOutputStream();
        try {
            this.serialize(outputStream, false);
            outputStream1.write(Sha256Hash.twiceOf(outputStream.toByteArray()).getReversedBytes());
            return Utils.HEX.encode(outputStream1.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getScripts1() {
        UnsafeByteArrayOutputStream outputStream = new UnsafeByteArrayOutputStream();
        UnsafeByteArrayOutputStream outputStream1 = new UnsafeByteArrayOutputStream();
        try {
            this.serialize(outputStream, true);

            int n = outputStream.size();
            Utils.uint8ToByteStream((byte)0x6a, outputStream1);
            if (n < 253) {
                Utils.uint8ToByteStream((byte)76, outputStream1);
                Utils.uint8ToByteStream((byte)n, outputStream1);
            } else {
                Utils.uint8ToByteStream((byte)77, outputStream1);
                Utils.uint16ToByteStreamLE(n, outputStream1);
            }
            outputStream1.write(outputStream.toByteArray());
            return Utils.HEX.encode(outputStream1.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
