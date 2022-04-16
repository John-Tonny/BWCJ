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

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.wallet.IBalance;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.wallet.IByAddress;

public class GsonBalance implements IBalance {

    @SerializedName("availableAmount")
    private String availableAmount;
    @SerializedName("availableConfirmedAmount")
    private String availableConfirmedAmount;
    @SerializedName("byAddress")
    private GsonByAddress[] byAddress;
    @SerializedName("lockedAmount")
    private String lockedAmount;
    @SerializedName("lockedConfirmedAmount")
    private String lockedConfirmedAmount;
    @SerializedName("totalAmount")
    private String totalAmount;
    @SerializedName("totalConfirmedAmount")
    private String totalConfirmedAmount;
    @SerializedName("availableAmountExcludeMasternode")
    private String availableAmountExcludeMasternode;
    @SerializedName("availableConfirmedAmountExcludeMasternode")
    private String availableConfirmedAmountExcludeMasternode;

    public GsonBalance() {
    }

    public GsonBalance(IBalance origin) {
        this.availableAmount = origin.getAvailableAmount();
        this.availableConfirmedAmount = origin.getAvailableConfirmedAmount();
        this.byAddress = mapByAddress(origin.getByAddress());
        this.lockedAmount = origin.getLockedAmount();
        this.lockedConfirmedAmount = origin.getLockedConfirmedAmount();
        this.totalAmount = origin.getTotalAmount();
        this.totalConfirmedAmount = origin.getTotalConfirmedAmount();
        this.availableAmountExcludeMasternode = origin.getAvailableAmountExcludeMasternode();
        this.availableConfirmedAmountExcludeMasternode = origin.getAvailableConfirmedAmountExcludeMasternode();
    }

    private static GsonByAddress[] mapByAddress(IByAddress[] origin) {
        if (origin == null) {
            return null;
        }

        GsonByAddress[] result = new GsonByAddress[origin.length];
        for (int i = 0; i < origin.length; i++) {
            result[i] = new GsonByAddress(origin[i]);
        }
        return result;
    }

    @Override
    public String getAvailableAmount() {
        return availableAmount;
    }

    @Override
    public String getAvailableConfirmedAmount() {
        return availableConfirmedAmount;
    }

    @Override
    public GsonByAddress[] getByAddress() {
        return byAddress;
    }

    @Override
    public String getLockedAmount() {
        return lockedAmount;
    }

    @Override
    public String getLockedConfirmedAmount() {
        return lockedConfirmedAmount;
    }

    @Override
    public String getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String getTotalConfirmedAmount() {
        return totalConfirmedAmount;
    }

    @Override
    public String getAvailableAmountExcludeMasternode() {
        return availableAmountExcludeMasternode;
    }

    @Override
    public String getAvailableConfirmedAmountExcludeMasternode() {
        return availableConfirmedAmountExcludeMasternode;
    }

}

