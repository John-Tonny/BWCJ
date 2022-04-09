
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

public class GsonMasternode implements IMasternode {
    @SerializedName("createdOn")
    @Expose
    public long createdOn;

    @SerializedName("updatedOn")
    @Expose
    public long updatedOn;

    @SerializedName("walletId")
    @Expose
    String walletId;

    @SerializedName("txid")
    @Expose
    String txid;

    @SerializedName("coin")
    @Expose
    String coin;

    @SerializedName("network")
    @Expose
    String network;

    @SerializedName("masternodePrivKey")
    @Expose
    String masternodePrivKey;

    @SerializedName("masternodePubKey")
    @Expose
    String masternodePubKey;

    @SerializedName("address")
    @Expose
    String address;

    @SerializedName("payee")
    @Expose
    String payee;

    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("proTxHash")
    @Expose
    String proTxHash;

    @SerializedName("collateralBlock")
    @Expose
    long collateralBlock;

    @SerializedName("lastpaidTime")
    @Expose
    long lastpaidTime;

    @SerializedName("lastpaidBlock")
    @Expose
    long lastpaidBlock;

    @SerializedName("ownerAddr")
    @Expose
    String ownerAddr;

    @SerializedName("voteAddr")
    @Expose
    String voteAddr;

    @SerializedName("payAddr")
    @Expose
    String payAddr;

    @SerializedName("reward")
    @Expose
    long reward;

    public GsonMasternode() {
    }

    public GsonMasternode(IMasternode origin) {
        createdOn = origin.getCreatedOn();
        updatedOn = origin.getUpdatedOn();
        walletId = origin.getWalletId();
        txid = origin.getTxid();
        masternodePrivKey = origin.getMasternodePrivKey();
        masternodePubKey = origin.getMasternodePubKey();
        coin = origin.getCoin();
        network = origin.getNetwork();
        address = origin.getAddress();
        payee = origin.getPayee();
        status = origin.getStatus();
        proTxHash = origin.getProTxHash();
        collateralBlock = origin.getCollateralBlock();
        lastpaidTime = origin.getLastpaidTime();
        lastpaidBlock = origin.getLastpaidBlock();
        ownerAddr = origin.getOwnerAddr();
        voteAddr = origin.getVoteAddr();
        payAddr = origin.getPayAddr();
        reward = origin.getReward();
    }

    @Override
    public long getCreatedOn() { return createdOn; }

    @Override
    public long getUpdatedOn() { return updatedOn; }

    @Override
    public String getWalletId() { return walletId; }

    @Override
    public String getTxid() { return txid; }

    @Override
    public String getCoin() { return coin; }

    @Override
    public String getNetwork() {return network; }

    @Override
    public String getMasternodePrivKey() { return masternodePrivKey; }

    @Override
    public String getMasternodePubKey() { return masternodePubKey; }

    @Override
    public String getAddress() {return address; }

    @Override
    public String getPayee() { return payee; }

    @Override
    public String getStatus() { return status; }

    @Override
    public String getProTxHash() { return proTxHash; }

    @Override
    public long getCollateralBlock() { return collateralBlock; }

    @Override
    public long getLastpaidTime() { return lastpaidTime; }

    @Override
    public long getLastpaidBlock() { return lastpaidBlock; }

    @Override
    public String getOwnerAddr() { return ownerAddr; }

    @Override
    public String getVoteAddr() { return voteAddr; }

    @Override
    public String getPayAddr() { return payAddr; }

    @Override
    public long getReward() { return reward; }

    @Override
    public void setMasternodePubKey(String masternodePubKey) {
        this.masternodePubKey = masternodePubKey;
    }

    @Override
    public void setMasternodePrivKey(String masternodePrivKey) {
        this.masternodePrivKey = masternodePrivKey;
    }

    @Override
    public void setVoteAddr(String voteAddr){
        this.voteAddr = voteAddr;
    }

    @Override
    public void setPayAddr(String payAddr){
        this.payAddr = payAddr;
    }

    @Override
    public void setAddress(String host, int port){
        this.address = host + ":" + port;
    }

}
