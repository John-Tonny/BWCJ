
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

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodeStatus;

public class GsonMasternodeStatus implements IMasternodeStatus {

    @SerializedName("proTxHash")
    @Expose
    public String proTxHash;

    @SerializedName("address")
    @Expose
    public String address;

    @SerializedName("collateraladdress")
    @Expose
    public String payee;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("collateralblock")
    @Expose
    public long collateralBlock;

    @SerializedName("lastpaidtime")
    @Expose
    public long lastpaidTime;

    @SerializedName("lastpaidblock")
    @Expose
    public long lastpaidBlock;

    @SerializedName("owneraddress")
    @Expose
    public String ownerAddr;

    @SerializedName("votingaddress")
    @Expose
    public String voteAddr;

    @SerializedName("payee")
    @Expose
    public String payAddr;

    @SerializedName("pubkeyoperator")
    @Expose
    public String masternoderPubKey;

    @SerializedName("txid")
    @Expose
    public String txid;


    public GsonMasternodeStatus() {
    }


    public GsonMasternodeStatus(IMasternodeStatus origin) {
       proTxHash =origin.getProTxHash();
       address = origin.getAddress();
       payee = origin.getPayee();
       status = origin.getStatus();
       collateralBlock = origin.getCollateralBlock();
       lastpaidTime = origin.getLastpaidTime();
       lastpaidBlock = origin.getLastpaidBlock();

       ownerAddr = origin.getOwnerAddr();
       voteAddr = origin.getVoteAddr();
       payAddr = origin.getPayAddr();

       masternoderPubKey = origin.getMasternodePubKey();

       txid = origin.getTxid();

    }

    @Override
    public String getProTxHash() {
        return proTxHash;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getPayee() { return payee;}

    @Override
    public String getStatus() { return status;}

    @Override
    public long getCollateralBlock() { return collateralBlock; }

    @Override
    public long getLastpaidTime() { return lastpaidTime; }

    @Override
    public long getLastpaidBlock() { return lastpaidBlock; }

    @Override
    public String getOwnerAddr() { return ownerAddr;}

    @Override
    public String getVoteAddr() { return voteAddr;}

    @Override
    public String getPayAddr() { return payAddr;}

    @Override
    public String getMasternodePubKey() { return masternoderPubKey;}

    @Override
    public String getTxid() { return txid; }

}
