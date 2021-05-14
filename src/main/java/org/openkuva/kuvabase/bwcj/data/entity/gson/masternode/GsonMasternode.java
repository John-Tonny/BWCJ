
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

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternode;

public class GsonMasternode extends GsonMasternodeStatus implements IMasternode {
    @SerializedName("createOn")
    @Expose
    public long createOn;
    @SerializedName("walletId")
    @Expose
    public String walletId;
    @SerializedName("txid")
    @Expose
    public String txid;
    @SerializedName("masternodeKey")
    @Expose
    public String masternodeKey;
    @SerializedName("coin")
    @Expose
    public String coin;
    @SerializedName("network")
    @Expose
    public String network;



    public GsonMasternode() {
    }

    public GsonMasternode(IMasternode origin) {
        super(origin);
        createOn = origin.getCreatedOn();
        walletId = origin.getWalletId();
        txid = origin.getTxid();
        masternodeKey = origin.getMasternodeKey();
        coin = origin.getCoin();
        network = origin.getNetwork();
    }

    @Override
    public long getCreatedOn() { return createOn; }

    @Override
    public String getWalletId() { return walletId; }

    @Override
    public String getTxid() { return txid; }

    @Override
    public String getMasternodeKey() { return masternodeKey;}

    @Override
    public String getCoin() { return coin; }

    @Override
    public String getNetwork() { return network; }
}
