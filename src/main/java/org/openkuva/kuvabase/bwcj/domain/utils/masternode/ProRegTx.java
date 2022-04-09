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
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SignatureException;

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IInput;
import org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction.Output;
import org.spongycastle.util.encoders.Base64;

public final class ProRegTx {
    int version = 1;
    int type = 0;
    int mode = 0;
    IInput[] inputs;
    String collateralId;
    int collateralIndex;
    String collateralPrivKey;
    String host;
    int port;
    String masternodePubKey;
    String ownerAddr;
    String voteAddr;
    String payAddr;
    int reward;
    NetworkParameters params;

    public ProRegTx(IInput[] inputs, String collateralId, int collateralIndex, String collateralPrivKey,  String host, int port, String masternodePubKey, String ownerAddr, String voteAddr, String payAddr, int reward, NetworkParameters params) {
        this.inputs = inputs;
        this.collateralId = collateralId;
        this.collateralIndex = collateralIndex;
        this.collateralPrivKey = collateralPrivKey;
        this.host = host;
        this.port = port;
        this.masternodePubKey = masternodePubKey;
        this.ownerAddr = ownerAddr;
        this.voteAddr = voteAddr;
        this.payAddr = payAddr;
        this.params = params;
        this.reward = reward;
    }

    private String getMessage(UnsafeByteArrayOutputStream  outputStream) throws IOException {
        long reward = this.reward * 100;
        String hash = Utils.HEX.encode(Sha256Hash.twiceOf(outputStream.toByteArray()).getReversedBytes());
        return this.payAddr + '|' +  Long.toString(reward)  + '|' + this.ownerAddr  + '|' + this.voteAddr + '|' + hash;
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
            ECKey ecKey = ECKey.fromPrivate(this.collateralPrivKey , true);
            String msg = this.getMessage(outputStream);
            String signatureBase64 = ecKey.signMessage(msg);

          try{
              ecKey.verifyMessage(msg, signatureBase64);
          }catch (SignatureException e){
              throw new SignatureException("Could not verify message", e);
          }

          byte[] signatureEncoded;
          try {
              signatureEncoded = Base64.decode(signatureBase64);
          } catch (RuntimeException var12) {
              throw new SignatureException("Could not decode base64", var12);
          }
          outputStream.write(Utils.HEX.decode("41"));
          outputStream.write(signatureEncoded);
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
            Utils.uint16ToByteStreamLE(this.type, outputStream);
            Utils.uint16ToByteStreamLE(this.mode, outputStream);

            Utils.writeInput(this.collateralId, this.collateralIndex, outputStream);
            Utils.writeIp(this.host, this.port, outputStream);

            Utils.writeAddress(this.params, this.ownerAddr, false, outputStream);
            outputStream.write(Utils.HEX.decode(this.masternodePubKey));
            Utils.writeAddress(this.params, this.voteAddr, false,outputStream);
            Utils.uint16ToByteStreamLE(this.reward * 100, outputStream);
            Utils.writeAddress(this.params, this.payAddr, true, outputStream);
            this.writeInputHash(outputStream);
            this.getSignMessage(signMode, outputStream);

            int n = outputStream.size();
            Utils.uint8ToByteStream(0x6a, outputStream1);
            if (n < 253) {
                Utils.uint8ToByteStream(76, outputStream1);
                Utils.uint8ToByteStream(n, outputStream1);
            } else {
                Utils.uint8ToByteStream(77, outputStream1);
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
