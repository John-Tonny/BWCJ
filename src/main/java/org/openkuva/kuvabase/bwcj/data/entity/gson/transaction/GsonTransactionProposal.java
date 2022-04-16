
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

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.*;
import org.openkuva.kuvabase.bwcj.domain.utils.messageEncrypt.SjclMessageEncryptor;
import org.openkuva.kuvabase.bwcj.data.entity.gson.wallet.GsonEncryptMessage;

import java.util.List;

public class GsonTransactionProposal implements ITransactionProposal {

    @SerializedName("version")
    @Expose
    public int version;
    @SerializedName("createdOn")
    @Expose
    public int createdOn;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("walletId")
    @Expose
    public String walletId;
    @SerializedName("creatorId")
    @Expose
    public String creatorId;
    @SerializedName("coin")
    @Expose
    public String coin;
    @SerializedName("network")
    @Expose
    public String network;
    @SerializedName("outputs")
    @Expose
    public GsonOutput[] outputs = null;
    @SerializedName("amount")
    @Expose
    public String amount;
    @SerializedName("message")
    @Expose
    public Object message;
    @SerializedName("payProUrl")
    @Expose
    public Object payProUrl;
    @SerializedName("changeAddress")
    @Expose
    public GsonChangeAddress changeAddress;
    @SerializedName("inputs")
    @Expose
    public GsonInput[] inputs = null;
    @SerializedName("walletM")
    @Expose
    public int walletM;
    @SerializedName("walletN")
    @Expose
    public int walletN;
    @SerializedName("requiredSignatures")
    @Expose
    public int requiredSignatures;
    @SerializedName("requiredRejections")
    @Expose
    public int requiredRejections;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("inputPaths")
    @Expose
    public List<String> inputPaths = null;
    @SerializedName("actions")
    @Expose
    public GsonAction[] actions = null;
    @SerializedName("outputOrder")
    @Expose
    public List<Integer> outputOrder = null;
    @SerializedName("fee")
    @Expose
    public long fee;
    @SerializedName("feeLevel")
    @Expose
    public String feeLevel;
    @SerializedName("feePerKb")
    @Expose
    public long feePerKb;
    @SerializedName("excludeUnconfirmedUtxos")
    @Expose
    public boolean excludeUnconfirmedUtxos;
    @SerializedName("addressType")
    @Expose
    public String addressType;
    @SerializedName("customData")
    @Expose
    // public GsonCustomData customData;
    public String customData;
    @SerializedName("proposalSignature")
    @Expose
    public String proposalSignature;
    @SerializedName("isInstantSend")
    @Expose
    public boolean isInstantSend;
    @SerializedName("derivationStrategy")
    @Expose
    public String derivationStrategy;
    @SerializedName("creatorName")
    @Expose
    public String creatorName;
    @SerializedName("txid")
    @Expose
    private String txid;
    @SerializedName("broadcastedOn")
    @Expose
    public int broadcastedOn;
    @SerializedName("proposalSignaturePubKey")
    @Expose
    public Object proposalSignaturePubKey;
    @SerializedName("proposalSignaturePubKeySig")
    @Expose
    public Object proposalSignaturePubKeySig;
    @SerializedName("raw")
    @Expose
    public String[] raw;

    @SerializedName("atomicswap")
    @Expose
    public GsonAtomicswapData atomicswap;
    @SerializedName("atomicswapAddr")
    @Expose
    public String atomicswapAddr;
    @SerializedName("atomicswapSecretHash")
    @Expose
    public String atomicswapSecretHash;

    @SerializedName("txExtends")
    @Expose
    public GsonTxExtends txExtends;

    @SerializedName("txType")
    @Expose
    public int txType;

    @SerializedName("maxPriorityFeePerGas")
    @Expose
    public long maxPriorityFeePerGas;

    @SerializedName("maxFeePerGas")
    @Expose
    public long maxFeePerGas;

    @SerializedName("accessList")
    @Expose
    public String[] accessList;

    @SerializedName("from")
    @Expose
    public String from;

    @SerializedName("tokenAddress")
    @Expose
    public String tokenAddress;

    @SerializedName("multisigContractAddress")
    @Expose
    public String multisigContractAddress;

    @SerializedName("nonce")
    @Expose
    public long nonce;

    @SerializedName("data")
    @Expose
    public String data;

    @SerializedName("isTokenSwap")
    @Expose
    public boolean isTokenSwap;

    @SerializedName("destinationTag")
    @Expose
    public String destinationTag;

    @SerializedName("gasLimit")
    @Expose
    public long gasLimit;

    private String sharedEncryptingKey;

    public GsonTransactionProposal() {
    }

    public GsonTransactionProposal(ITransactionProposal origin) {
        version = origin.getVersion();
        createdOn = origin.getCreatedOn();
        id = origin.getId();
        walletId = origin.getWalletId();
        creatorId = origin.getCreatorId();
        coin = origin.getCoin();
        network = origin.getNetwork();
        outputs = mapOutputs(origin.getOutputs());
        amount = origin.getAmount();
        message = origin.getMessage();
        payProUrl = origin.getPayProUrl();
        changeAddress =
                origin.getChangeAddress() == null
                        ? null : new GsonChangeAddress(origin.getChangeAddress());
        inputs = mapInputs(origin.getInputs());
        walletM = origin.getWalletM();
        walletN = origin.getWalletN();
        requiredSignatures = origin.getRequiredSignatures();
        requiredRejections = origin.getRequiredRejections();
        status = origin.getStatus();
        inputPaths = origin.getInputPaths();
        actions = mapActions(origin.getActions());
        outputOrder = origin.getOutputOrder();
        fee = origin.getFee();
        feeLevel = origin.getFeeLevel();
        feePerKb = origin.getFeePerKb();
        excludeUnconfirmedUtxos = origin.isExcludeUnconfirmedUtxos();
        addressType = origin.getAddressType();
        /*customData =
                origin.getCustomData() == null
                        ? null : new GsonCustomData(origin.getCustomData());
         */
        customData = origin.getCustomData();
        proposalSignature = origin.getProposalSignature();
        isInstantSend = origin.isInstantSend();
        derivationStrategy = origin.getDerivationStrategy();
        creatorName = origin.getCreatorName();
        broadcastedOn = origin.getBroadcastedOn();
        proposalSignaturePubKey = origin.getProposalSignaturePubKey();
        proposalSignaturePubKeySig = origin.getProposalSignaturePubKeySig();
        raw = origin.getRaw();

        atomicswap = origin.getAtomicswap() == null
                ? null : new GsonAtomicswapData(origin.getAtomicswap());
        atomicswapAddr = origin.getAtomicswapAddr();
        atomicswapSecretHash = origin.getAtomicswapSecretHash();
        txExtends = origin.getTxExtends() == null
                ? null : new GsonTxExtends(origin.getTxExtends());


        txType = origin.getTxType();
        maxPriorityFeePerGas = origin.getMaxPriorityFeePerGas();
        maxFeePerGas = origin.getMaxFeePerGas();
        accessList = origin.getAccessList();
        from = origin.getFrom();
        tokenAddress = origin.getTokenAddress();
        multisigContractAddress = origin.getMultisigContractAddress();
        nonce = origin.getNonce();
        gasLimit = origin.getGasLimit();

    }

    private static GsonAction[] mapActions(IAction[] origin) {
        if (origin == null) {
            return null;
        }

        GsonAction[] result = new GsonAction[origin.length];
        for (int i = 0; i < origin.length; i++) {
            result[i] = new GsonAction(origin[i]);
        }
        return result;
    }

    private static GsonOutput[] mapOutputs(IOutput[] origin) {
        if (origin == null) {
            return null;
        }

        GsonOutput[] result = new GsonOutput[origin.length];
        for (int i = 0; i < origin.length; i++) {
            result[i] = new GsonOutput(origin[i]);
        }
        return result;
    }

    private static GsonInput[] mapInputs(IInput[] origin) {
        if (origin == null) {
            return null;
        }

        GsonInput[] result = new GsonInput[origin.length];
        for (int i = 0; i < origin.length; i++) {
            result[i] = new GsonInput(origin[i]);
        }
        return result;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public int getCreatedOn() {
        return createdOn;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getWalletId() {
        return walletId;
    }

    @Override
    public String getCreatorId() {
        return creatorId;
    }

    @Override
    public String getCoin() {
        return coin;
    }

    @Override
    public String getNetwork() {
        return network;
    }

    @Override
    public GsonOutput[] getOutputs() {
        return outputs;
    }

    @Override
    public String getAmount() {
        return amount;
    }

    @Override
    public Object getMessage() {
        if(this.sharedEncryptingKey == null) return message;
        try {
            Gson gson = new Gson();
            GsonEncryptMessage enMsg  = gson.fromJson(message.toString(), GsonEncryptMessage.class);
            if(enMsg.getCt()!=null && enMsg.getIv()!=null) {
                String msg1 = new SjclMessageEncryptor()
                        .decrypt(
                                message.toString(),
                                sharedEncryptingKey);
                return msg1;
            }
            return message;
        }catch (Exception e){
            return message;
        }
    }

    @Override
    public Object getPayProUrl() {
        return payProUrl;
    }

    @Override
    public GsonChangeAddress getChangeAddress() {
        return changeAddress;
    }

    @Override
    public GsonInput[] getInputs() {
        return inputs;
    }

    @Override
    public int getWalletM() {
        return walletM;
    }

    @Override
    public int getWalletN() {
        return walletN;
    }

    @Override
    public int getRequiredSignatures() {
        return requiredSignatures;
    }

    @Override
    public int getRequiredRejections() {
        return requiredRejections;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public String getTxid() {
        return txid;
    }

    @Override
    public int getBroadcastedOn() {
        return broadcastedOn;
    }

    @Override
    public List<String> getInputPaths() {
        return inputPaths;
    }

    @Override
    public GsonAction[] getActions() {
        return actions;
    }

    @Override
    public List<Integer> getOutputOrder() {
        return outputOrder;
    }

    @Override
    public long getFee() {
        return fee;
    }

    @Override
    public String getFeeLevel() {
        return feeLevel;
    }

    @Override
    public long getFeePerKb() {
        return feePerKb;
    }

    @Override
    public boolean isExcludeUnconfirmedUtxos() {
        return excludeUnconfirmedUtxos;
    }

    @Override
    public String getAddressType() {
        return addressType;
    }

    @Override
    /*public GsonCustomData getCustomData() {
        return customData;
    }
     */
    public String getCustomData() {
        return customData;
    }

    public String getProposalSignature() {
        return proposalSignature;
    }

    @Override
    public Object getProposalSignaturePubKey() {
        return proposalSignaturePubKey;
    }

    @Override
    public Object getProposalSignaturePubKeySig() {
        return proposalSignaturePubKeySig;
    }

    @Override
    public boolean isInstantSend() {
        return isInstantSend;
    }

    @Override
    public String getDerivationStrategy() {
        return derivationStrategy;
    }

    @Override
    public String getCreatorName() {
        return creatorName;
    }

    @Override
    public String[] getRaw() {
        return raw;
    }

    @Override
    public GsonAtomicswapData getAtomicswap() {
        return atomicswap;
    }

    @Override
    public String getAtomicswapAddr() {
        return atomicswapAddr;
    }

    @Override
    public String getAtomicswapSecretHash() {
        return atomicswapSecretHash;
    }

    @Override
    public GsonTxExtends getTxExtends() {
        return txExtends;
    }

    @Override
    public int getTxType() {
        return txType;
    }

    @Override
    public long getMaxPriorityFeePerGas() {
        return maxPriorityFeePerGas;
    }

    @Override
    public long getMaxFeePerGas() {
        return maxFeePerGas;
    }

    @Override
    public String[] getAccessList() {
        return accessList;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public String getTokenAddress() {
        return tokenAddress;
    }

    @Override
    public String getMultisigContractAddress() {
        return multisigContractAddress;
    }

    @Override
    public long getNonce() { return nonce; }

    @Override
    public String getData() { return data; }

    @Override
    public boolean getIsTokenSwap() { return isTokenSwap; }

    @Override
    public String getDestinationTag() { return destinationTag; }

    @Override
    public long getGasLimit() { return gasLimit; }

    public void setSharedEncryptingKey(String sharedEncryptingKey) {
        this.sharedEncryptingKey = sharedEncryptingKey;
    }

}
