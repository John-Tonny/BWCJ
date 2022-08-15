
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

package org.openkuva.kuvabase.bwcj.data.entity.gson.asset;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.asset.IAssetInfo;

public class GsonAssetInfo implements IAssetInfo {
    @SerializedName("assetGuid")
    @Expose
    public String assetGuid;

    @SerializedName("contract")
    @Expose
    public String contract;

    @SerializedName("symbol")
    @Expose
    public String symbol;

    @SerializedName("pubData")
    @Expose
    public Object pubData;

    @SerializedName("notaryKeyID")
    @Expose
    public String notaryKeyID;

    @SerializedName("notaryDetails")
    @Expose
    public Object notaryDetails;

    @SerializedName("totalSupply")
    @Expose
    public String totalSupply;

    @SerializedName("maxSupply")
    @Expose
    public String maxSupply;

    @SerializedName("decimals")
    @Expose
    public int decimals;

    @SerializedName("updateCapabilityFlags")
    @Expose
    public int updateCapabilityFlags;

    public GsonAssetInfo() {
    }

    public GsonAssetInfo(IAssetInfo origin) {
        this.assetGuid = origin.getAssetGuid();
        this.contract = origin.getContract();
        this.symbol = origin.getSymbol();
        this.notaryKeyID = origin.getNotaryKeyID();
        this.notaryDetails = origin.getNotaryDetails();
        this.totalSupply = origin.getTotalSupply();
        this.maxSupply = origin.getMaxSupply();
        this.decimals = origin.getDecimals();
        this.updateCapabilityFlags = origin.getUpdateCapabilityFlags();
    }

    @Override
    public String getAssetGuid() {
        return assetGuid;
    }

    @Override
    public String getContract() {
        return contract;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public Object getPubData() {
        return pubData;
    }

    @Override
    public String getNotaryKeyID() {
        return notaryKeyID;
    }

    @Override
    public Object getNotaryDetails() {
        return notaryDetails;
    }

    @Override
    public String getTotalSupply() {
        return totalSupply;
    }

    @Override
    public String getMaxSupply() {
        return maxSupply;
    }

    @Override
    public int getDecimals() {
        return decimals;
    }

    @Override
    public int getUpdateCapabilityFlags() {
        return updateCapabilityFlags;
    }

}
