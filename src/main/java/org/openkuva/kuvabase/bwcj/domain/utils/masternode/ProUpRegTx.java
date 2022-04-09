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

public final class ProUpRegTx {
    int version = 1;
    int mode = 0;
    IInput[] inputs;
    String proTxHash;

    String ownerPrivKey;
    String masternodePubKey;
    String voteAddr;
    String payAddr;
    NetworkParameters params;


    public ProUpRegTx(IInput[] inputs, String proTxHash, String ownerPrivKey, String masternodePubKey, String voteAddr, String payAddr, NetworkParameters params) {
        this.inputs = inputs;
        this.proTxHash = proTxHash;
        this.ownerPrivKey = ownerPrivKey;
        this.masternodePubKey = masternodePubKey;
        this.voteAddr = voteAddr;
        this.payAddr = payAddr;
        this.params = params;
    }

    private String getMessage(UnsafeByteArrayOutputStream  outputStream) throws IOException {
        return Utils.HEX.encode(outputStream.toByteArray());
    }

    public void getSignMessage(boolean sigMode, UnsafeByteArrayOutputStream outputStream) throws IOException, SignatureException {
        if(outputStream == null) {
            outputStream = new UnsafeByteArrayOutputStream();
        }
        try {
            if(!sigMode){
                byte[] data = {0};
                outputStream.write(data);
                return;
            }
            ECKey ecKey = ECKey.fromPrivate(this.ownerPrivKey , true);
            String msg = this.getMessage(outputStream);
            String signature = ecKey.signMessage1(msg);
            try{
                ecKey.verifyMessage1(msg, signature);
            }catch (SignatureException e){
                throw new SignatureException("Could not verify message", e);
            }
            if(signature.length() != 130){
                throw new SignatureException("Could not sign message");
            }
            Utils.uint8ToByteStream(65, outputStream);
            outputStream.write(Utils.HEX.decode(signature));
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
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

    public String getOutScripts(boolean signMode) throws IOException, SignatureException {
        UnsafeByteArrayOutputStream outputStream = new UnsafeByteArrayOutputStream();
        UnsafeByteArrayOutputStream outputStream1 = new UnsafeByteArrayOutputStream();
        try {
            Utils.uint16ToByteStreamLE(this.version, outputStream);
            Utils.writeHash(this.proTxHash, outputStream);
            Utils.uint16ToByteStreamLE(this.mode, outputStream);

            outputStream.write(Utils.HEX.decode(this.masternodePubKey));

            Utils.writeAddress(this.params, this.voteAddr, false, outputStream);
            Utils.writeAddress(this.params, this.payAddr, true, outputStream);
            this.writeInputHash(outputStream);
            this.getSignMessage(signMode, outputStream);

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
        }catch (SignatureException e){
            throw  new SignatureException(e);
        }
    }
}
