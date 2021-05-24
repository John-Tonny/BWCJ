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

package org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.retrofit2.gson.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonCustomData;
import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonAtomicswapData;
import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonOutput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IAtomicswapData;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionRequest;

public class GsonTransactionRequest implements ITransactionRequest {
    @SerializedName("outputs")
    @Expose
    private GsonOutput[] outputs;
    @SerializedName("feeLevel")
    @Expose
    private String feeLevel;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("excludeUnconfirmedUtxos")
    @Expose
    private boolean excludeUnconfirmedUtxos;
    @SerializedName("dryRun")
    @Expose
    private boolean dryRun;
    @SerializedName("operation")
    @Expose
    private String operation;
    @SerializedName("customData")
    @Expose
    private GsonCustomData customData;
    @SerializedName("payProUrl")
    @Expose
    private Object payProUrl;
    @SerializedName("excludeMasternode")
    @Expose
    private boolean excludeMasternode;

    public GsonTransactionRequest(ITransactionRequest origin) {
        this.outputs = map(origin.getOutputs());
        this.feeLevel = origin.getFeeLevel();
        this.message = origin.getMessage();
        this.excludeUnconfirmedUtxos = origin.isExcludeUnconfirmedUtxos();
        this.dryRun = origin.isDryRun();
        this.operation = origin.getOperation();
        this.customData = new GsonCustomData(origin.getCustomData());
        this.payProUrl = origin.getPayProUrl();
        this.excludeMasternode = origin.isExcludeMasternode();
    }

    private GsonOutput[] map(IOutput[] outputs) {
        GsonOutput[] result = new GsonOutput[outputs.length];
        for (int i = 0; i < outputs.length; i++) {
            IOutput output = outputs[i];
            result[i] = new GsonOutput(output);
        }
        return result;
    }

    @Override
    public GsonOutput[] getOutputs() {
        return outputs;
    }

    @Override
    public String getFeeLevel() {
        return feeLevel;
    }

    @Override
    public Object getMessage() {
        return message;
    }

    @Override
    public boolean isExcludeUnconfirmedUtxos() {
        return excludeUnconfirmedUtxos;
    }

    @Override
    public boolean isDryRun() {
        return dryRun;
    }

    @Override
    public String getOperation() {
        return operation;
    }

    @Override
    public GsonCustomData getCustomData() {
        return customData;
    }

    @Override
    public Object getPayProUrl() {
        return payProUrl;
    }

    @Override
    public boolean isExcludeMasternode() {
        return excludeMasternode;
    }

}
