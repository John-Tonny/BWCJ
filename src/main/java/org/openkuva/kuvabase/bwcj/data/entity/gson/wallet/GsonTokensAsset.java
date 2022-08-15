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

package org.openkuva.kuvabase.bwcj.data.entity.gson.wallet;

import com.google.gson.annotations.SerializedName;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.wallet.ITokensAsset;

public class GsonTokensAsset implements ITokensAsset {

    @SerializedName("type")
    private String type;

    @SerializedName("name")
    private String name;

    @SerializedName("path")
    private String path;

    @SerializedName("assetGuid")
    private String assetGuid;

    @SerializedName("transfers")
    private int transfers;

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("decimals")
    private int decimals;

    @SerializedName("balance")
    private String balance;

    @SerializedName("totalReceived")
    private String totalReceived;

    @SerializedName("totalSent")
    private String totalSent;

    public GsonTokensAsset() {
    }

    public GsonTokensAsset(ITokensAsset origin) {
        this.type = origin.getType();
        this.name = origin.getName();
        this.path = origin.getPath();
        this.assetGuid = origin.getAssetGuid();
        this.transfers = origin.getTransfers();
        this.symbol = origin.getSymbol();
        this.decimals =origin.getDecimals();
        this.balance = origin.getBalance();
        this.totalReceived = origin.getTotalReceived();
        this.totalSent = origin.getTotalSent();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getAssetGuid() {
        return assetGuid;
    }

    @Override
    public int getTransfers() {
        return transfers;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public int getDecimals() {
        return decimals;
    }

    @Override
    public String getBalance() {
        return balance;
    }

    @Override
    public String getTotalReceived() {
        return totalReceived;
    }

    @Override
    public String getTotalSent() {
        return totalSent;
    }

}

