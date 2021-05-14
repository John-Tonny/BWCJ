
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

package org.openkuva.kuvabase.bwcj.data.entity.gson.masternode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodeCollateral;

public class GsonMasternodeCollateral implements IMasternodeCollateral {
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("satoshis")
    @Expose
    public long satoshis;
    @SerializedName("amount")
    @Expose
    public float amount;
    @SerializedName("scriptPubKey")
    @Expose
    public String scriptPubKey;
    @SerializedName("txid")
    @Expose
    public String txid;
    @SerializedName("vout")
    @Expose
    public int vout;
    @SerializedName("locked")
    @Expose
    public boolean locked;
    @SerializedName("coinbase")
    @Expose
    public boolean coinbase;
    @SerializedName("confirmations")
    @Expose
    public long confirmations;
    @SerializedName("path")
    @Expose
    public String path;
    @SerializedName("publicKeys")
    @Expose
    public String[] publicKeys;

    public GsonMasternodeCollateral() {
    }

    public GsonMasternodeCollateral(IMasternodeCollateral origin) {
        address = origin.getAddress();
        satoshis = origin.getSatoshis();
        amount = origin.getAmount();
        scriptPubKey = origin.getScriptPubKey();
        txid = origin.getTxid();
        vout = origin.getVout();
        locked = origin.getLocked();
        coinbase = origin.getCoinbase();
        confirmations = origin.getConfirmations();
        path = origin.getPath();
        publicKeys = origin.getPublicKeys();
    }

    @Override
    public String getAddress() { return address; }

    @Override
    public long getSatoshis() { return satoshis; }

    @Override
    public float getAmount() { return amount;}

    @Override
    public String getScriptPubKey() { return scriptPubKey; }

    @Override
    public String getTxid() { return txid; }

    @Override
    public int getVout() { return vout; }

    @Override
    public boolean getLocked() { return locked; }

    @Override
    public boolean getCoinbase() { return coinbase; }

    @Override
    public long getConfirmations() { return confirmations; }

    @Override
    public String getPath() { return path; }

    @Override
    public String[] getPublicKeys() { return publicKeys;}

}
