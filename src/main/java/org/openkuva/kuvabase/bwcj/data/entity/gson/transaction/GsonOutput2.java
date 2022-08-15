
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

package org.openkuva.kuvabase.bwcj.data.entity.gson.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonAssetInfo;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IAssetInfo;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput2;

public class GsonOutput2 implements IOutput2 {
    @SerializedName(value="value")
    @Expose
    private String amount;

    @SerializedName("n")
    @Expose
    private String n;

    @SerializedName("hex")
    @Expose
    private String hex;

    @SerializedName("addresses")
    @Expose
    private String[] addresses;

    @SerializedName("isAddress")
    @Expose
    private boolean isAddress;

    @SerializedName("assetInfo")
    @Expose
    private GsonAssetInfo assetInfo;

    @SerializedName("spent")
    @Expose
    private boolean spent;

    public GsonOutput2() {
    }

    public GsonOutput2(IOutput2 origin) {
        this.amount = origin.getAmount();
        this.n = origin.getN();
        this.hex = origin.getHex();
        this.addresses = origin.getAddresses();
        this.isAddress = origin.getIsAddress();
        this.assetInfo = origin.getAssetInfo();
    }

    @Override
    public String getAmount() {
        return amount;
    }

    @Override
    public String getN() {
        return n;
    }

    @Override
    public String getHex() {
        return hex;
    }

    @Override
    public String[] getAddresses() {
        return addresses;
    }

    @Override
    public boolean getIsAddress() {
        return isAddress;
    }

    @Override
    public GsonAssetInfo getAssetInfo() {
        return assetInfo;
    }

    @Override
    public boolean getSpent() {
        return spent;
    }
}
