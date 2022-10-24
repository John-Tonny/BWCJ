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

package org.openkuva.kuvabase.bwcj.domain.utils;

import static org.bitcoinj.core.NetworkParameters.ID_MAINNET;
import static org.bitcoinj.core.NetworkParameters.ID_TESTNET;

import org.bitcoinj.core.Utils;
import org.bitcoinj.core.Base58;
import org.openkuva.kuvabase.bwcj.data.entity.gson.wallet.JoinWalletSecret;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.wallet.IJoinWalletSecret;

public final class CommUtils {
    private CommUtils() {
    }

    public static String BuildJoinWalletSecret(String walletId, String walletPrivateKey, String coin, String network) {
        String walletId1 = walletId.replace("-", "");
        String widBase58 = Base58.encode(Utils.HEX.decode(walletId1));
        for(int i = widBase58.length(); i<22; i++){
            widBase58 += "0";
        }
        String ret = "";
        if(network.compareToIgnoreCase("livenet")==0){
            ret = "L" + coin;
        }else{
            ret = "T" + coin;
        }

        String walletPrivateKey1 = walletPrivateKey + "01";
        String wprivBase58 = Base58.encodeChecked(128, Utils.HEX.decode(walletPrivateKey1));

        return widBase58 + wprivBase58 + ret;
    }

    public static IJoinWalletSecret ParseWalletSecret(String secret) {
        String walletId = Utils.HEX.encode(Base58.decode(secret.substring(0,22)));
        String walletPrivateKey = Utils.HEX.encode(Base58.decodeChecked(secret.substring(22, 74)));
        String network = secret.substring(74,75);
        if(network.compareTo("L")==0){
            network = "livenet";
        }else{
            network="testnet";
        }

        String coin = secret.substring(75, secret.length());
        String walletId1 = walletId.substring(0,8) + "-" + walletId.substring(8, 12)+ "-" + walletId.substring(12, 16) + "-" + walletId.substring(16, 20) +"-" + walletId.substring(20, walletId.length());

        String walletPrivateKey1 = walletPrivateKey.substring(2, walletPrivateKey.length()-2);

        return new JoinWalletSecret(walletId1, walletPrivateKey1, coin, network);
    }

}
