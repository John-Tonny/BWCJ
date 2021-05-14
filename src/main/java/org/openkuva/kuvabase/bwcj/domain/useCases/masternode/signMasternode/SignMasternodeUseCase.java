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

package org.openkuva.kuvabase.bwcj.domain.useCases.masternode.signMasternode;

import org.bitcoinj.core.Utils;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.UnsafeByteArrayOutputStream;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.IBitcoreWalletServerAPI;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bitcoinj.core.Utils;

public class SignMasternodeUseCase implements ISignMasternodeUseCase {
    private long CLIENT_VERSION = 1000000;
    private long CLIENT_SENTINEL_VERSION = 1000000;
    private long CLIENT_MASTERNODE_VERSION = 1010191;

    private final IBitcoreWalletServerAPI bwsApi;

    public SignMasternodeUseCase(IBitcoreWalletServerAPI bwsApi) {
        this.bwsApi = bwsApi;
    }

    @Override
    public String execute(
            String coin,
            String txid,
            int vout,
            String signPrivKey,
            long pingHeight,
            String pingHash,
            String masternodeKey,
            String address,
            int port) {
        if (coin == null) {
            throw new IllegalStateException("no coin");
        }
        if (!coin.equalsIgnoreCase("vcl")) {
            throw new IllegalStateException("coin must be vcl");
        }
        if (txid == null) {
            throw new IllegalStateException("no txid");
        }
        if (vout < 0) {
            throw new IllegalStateException("vout is expected to be a positive integer");
        }
        if (signPrivKey == null) {
            throw new IllegalStateException("no signPrivKey");
        }
        if (pingHeight < 0) {
            throw new IllegalStateException("pingHeight can't be less than 0");
        }
        if (pingHash == null) {
            throw new IllegalStateException("no pingHash");
        }
        if (masternodeKey == null) {
            throw new IllegalStateException("no masternodeKey");
        }
        if (address == null) {
            throw new IllegalStateException("no address");
        }
        if (port < 0) {
            throw new IllegalStateException("no address");
        }

        UnsafeByteArrayOutputStream outputStream = new UnsafeByteArrayOutputStream();
        UnsafeByteArrayOutputStream outputStream1 = new UnsafeByteArrayOutputStream();
        UnsafeByteArrayOutputStream outputStream2 = new UnsafeByteArrayOutputStream();
        try {
            serialize_input(txid, vout, outputStream);
            hash_decode(pingHash, outputStream);
            Date now = new Date();
            Utils.uint64ToByteStreamLE(BigInteger.valueOf(now.getTime()/1000), outputStream);
            outputStream.write(Utils.HEX.decode("01"));
            Utils.uint32ToByteStreamLE(CLIENT_SENTINEL_VERSION / 1000000, outputStream);
            Utils.uint32ToByteStreamLE(CLIENT_MASTERNODE_VERSION / 1000000, outputStream);
            String pingSig = this.signMessage(Utils.HEX.encode(outputStream.toByteArray()), masternodeKey);

            serialize_input(txid, vout, outputStream1);
            get_address(address, port, outputStream1);
            byte[] signPubKey = ECKey.fromPrivate(signPrivKey, true).getPubKey();
            outputStream1.write(Utils.HEX.decode(Integer.toHexString(signPubKey.length)));
            outputStream1.write(signPubKey);
            byte[] pubKey = ECKey.fromPrivate(masternodeKey, true).getPubKey();
            outputStream1.write(Utils.HEX.decode(Integer.toHexString(pubKey.length)));
            outputStream1.write(pubKey);
            Utils.uint64ToByteStreamLE(BigInteger.valueOf(now.getTime()/1000), outputStream1);
            Utils.uint32ToByteStreamLE(31800, outputStream1);
            String msgSig = this.signMessage(Utils.HEX.encode(outputStream1.toByteArray()), signPrivKey);

            outputStream2.write(Utils.HEX.decode("01"));
            serialize_input(txid, vout, outputStream2);
            get_address(address, port, outputStream2);
            outputStream2.write(Utils.HEX.decode(Integer.toHexString(signPubKey.length)));
            outputStream2.write(signPubKey);
            outputStream2.write(Utils.HEX.decode(Integer.toHexString(pubKey.length)));
            outputStream2.write(pubKey);
            outputStream2.write(Utils.HEX.decode(Integer.toHexString(msgSig.length()/2)));
            outputStream2.write(Utils.HEX.decode(msgSig));
            Utils.uint64ToByteStreamLE(BigInteger.valueOf(now.getTime()/1000), outputStream2);
            Utils.uint32ToByteStreamLE(31800, outputStream2);
            serialize_input(txid, vout, outputStream2);
            hash_decode(pingHash, outputStream2);
            Utils.uint64ToByteStreamLE(BigInteger.valueOf(now.getTime()/1000), outputStream2);
            outputStream2.write(Utils.HEX.decode(Integer.toHexString(pingSig.length()/2)));
            outputStream2.write(Utils.HEX.decode(pingSig));
            outputStream2.write(Utils.HEX.decode("01"));
            Utils.uint32ToByteStreamLE(CLIENT_SENTINEL_VERSION , outputStream2);
            Utils.uint32ToByteStreamLE(CLIENT_MASTERNODE_VERSION , outputStream2);
            Utils.uint32ToByteStreamLE(0 , outputStream2);

            return Utils.HEX.encode(outputStream2.toByteArray());
        }catch(IOException e){
            return null;
        }
    }

    public static byte[] reverseArray(byte[] arr){
        for(int i=0;i<arr.length/2;i++){
            //两个数组元素互换
            byte temp = arr[i];
            arr[i] = arr[arr.length-i-1];
            arr[arr.length-i-1] = temp;
        }
        return arr;
    }

    public static void serialize_input(String txid, int vout, OutputStream outputStream) throws IOException {
        outputStream.write(reverseArray(Utils.HEX.decode(txid)));
        Utils.uint32ToByteStreamLE(vout, outputStream);
    }

    public static void hash_decode(String pingHash, OutputStream outputStream) throws  IOException {
        outputStream.write(reverseArray(Utils.HEX.decode(pingHash)));
    }

    public static void get_address(String address, int port, OutputStream outputStream) throws IOException {
        String[] strIp = address.split("\\.");
        byte[] btIp= new byte[4];
        for(int i=0;i<strIp.length;i++) {
            btIp[i] = (byte)Integer.parseInt(strIp[i]);
        }
        outputStream.write(Utils.HEX.decode("00000000000000000000ffff"));
        outputStream.write(btIp);
        Utils.uint16ToByteStreamBE(port, outputStream);
    }

    private String signMessage(String message, String masternodeKey) {
        return ECKey.fromPrivate(masternodeKey, true).signMessage1(message);
    }
}
