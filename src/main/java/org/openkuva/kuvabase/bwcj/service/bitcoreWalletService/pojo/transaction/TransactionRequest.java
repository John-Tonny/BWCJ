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

package org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.pojo.transaction;

import com.google.common.collect.ImmutableCollection;
import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonTxExtends;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.*;


public class TransactionRequest implements ITransactionRequest {
    private IOutput[] outputs;
    private String feeLevel;
    private Object message;
    private boolean excludeUnconfirmedUtxos;
    private boolean dryRun;
    private String operation;
    // private ICustomData customData;
    private String customData;
    private Object payProUrl;
    private boolean excludeMasternode;
    private GsonTxExtends txExtends;
    private String tokenAddress;

    public TransactionRequest(IOutput[] outputs, String feeLevel, Object message, boolean excludeUnconfirmedUtxos, boolean dryRun, String operation, String customData, Object payProUrl, boolean excludeMasternode) {
        this.outputs = outputs;
        this.feeLevel = feeLevel;
        this.message = message;
        this.excludeUnconfirmedUtxos = excludeUnconfirmedUtxos;
        this.dryRun = dryRun;
        this.operation = operation;
        this.customData = customData;
        this.payProUrl = payProUrl;
        this.excludeMasternode = excludeMasternode;
        this.txExtends = null;
        this.tokenAddress = null;
    }

    public TransactionRequest(IOutput[] outputs, String feeLevel, Object message, boolean excludeUnconfirmedUtxos, boolean dryRun, String operation, String customData, Object payProUrl, boolean excludeMasternode, String tokenAddress) {
        this.outputs = outputs;
        this.feeLevel = feeLevel;
        this.message = message;
        this.excludeUnconfirmedUtxos = excludeUnconfirmedUtxos;
        this.dryRun = dryRun;
        this.operation = operation;
        this.customData = customData;
        this.payProUrl = payProUrl;
        this.excludeMasternode = excludeMasternode;
        this.txExtends = null;
        this.tokenAddress = tokenAddress;
    }

    public TransactionRequest(IOutput[] outputs, String feeLevel, Object message, boolean excludeUnconfirmedUtxos, boolean dryRun, String operation, String customData, Object payProUrl, boolean excludeMasternode, GsonTxExtends txExtends) {
        this.outputs = outputs;
        this.feeLevel = feeLevel;
        this.message = message;
        this.excludeUnconfirmedUtxos = excludeUnconfirmedUtxos;
        this.dryRun = dryRun;
        this.operation = operation;
        this.customData = customData;
        this.payProUrl = payProUrl;
        this.excludeMasternode = excludeMasternode;
        this.txExtends = txExtends;
    }

    @Override
    public IOutput[] getOutputs() {
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
    /* public ICustomData getCustomData() {
        return customData;
    }
     */
    public String getCustomData() {
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

    @Override
    public GsonTxExtends getTxExtends() {
        return txExtends;
    }

    @Override
    public String getTokenAddress() { return tokenAddress; }

}
