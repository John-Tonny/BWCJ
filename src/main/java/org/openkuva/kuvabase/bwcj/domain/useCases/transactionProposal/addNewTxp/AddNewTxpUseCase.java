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

package org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.addNewTxp;

import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonAsset;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IInput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;
import org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction.Output;
import org.openkuva.kuvabase.bwcj.domain.utils.CopayersCryptUtils;
import org.openkuva.kuvabase.bwcj.domain.utils.messageEncrypt.SjclMessageEncryptor;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.IBitcoreWalletServerAPI;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InsufficientFundsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidAmountException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidParamsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidWalletAddressException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.pojo.transaction.TransactionRequest;

import java.math.BigDecimal;


public class AddNewTxpUseCase implements IAddNewTxpUseCase {
    private final IBitcoreWalletServerAPI bwsApi;
    private final ICredentials credentials;
    private final CopayersCryptUtils copayersCryptUtils;
    private final boolean isAsset;

    public AddNewTxpUseCase(ICredentials credentials, CopayersCryptUtils copayersCryptUtils, IBitcoreWalletServerAPI bwsApi) {
        this.credentials = credentials;
        this.copayersCryptUtils = copayersCryptUtils;
        this.bwsApi = bwsApi;
        this.isAsset = false;
    }

    public AddNewTxpUseCase(ICredentials credentials, IBitcoreWalletServerAPI bwsApi) {
        this.credentials = credentials;
        this.copayersCryptUtils = this.credentials.getCopayersCryptUtils();
        this.bwsApi = bwsApi;
        this.isAsset = false;
    }

    public AddNewTxpUseCase(ICredentials credentials, CopayersCryptUtils copayersCryptUtils, IBitcoreWalletServerAPI bwsApi, boolean isAsset) {
        this.credentials = credentials;
        this.copayersCryptUtils = copayersCryptUtils;
        this.bwsApi = bwsApi;
        this.isAsset = isAsset;
    }

    public AddNewTxpUseCase(ICredentials credentials, IBitcoreWalletServerAPI bwsApi, boolean isAsset) {
        this.credentials = credentials;
        this.copayersCryptUtils = this.credentials.getCopayersCryptUtils();
        this.bwsApi = bwsApi;
        this.isAsset = isAsset;
    }

    @Override
    public ITransactionProposal execute(String address, String amount, String msg, boolean dryRun, String customData, boolean excludeMasternode) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(null, address, amount, msg, dryRun, "send", customData, excludeMasternode);
    }

    @Override
    public ITransactionProposal execute(String tokenAddress, String address, String amount, String msg, boolean dryRun, String customData, boolean excludeMasternode) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(tokenAddress, address, amount, msg, dryRun, "send", customData, excludeMasternode);
    }

    @Override
    public ITransactionProposal execute(String address, String amount, String msg, boolean dryRun, String operation, String customData, boolean excludeMasternode) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(null, address, amount, msg, dryRun, operation, customData, excludeMasternode);
    }

    @Override
    public ITransactionProposal execute(String tokenAddress, String address, String amount, String msg, boolean dryRun, String operation, String customData, boolean excludeMasternode) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        String satoshis = null;
        try {
            if (this.copayersCryptUtils.getCoin() == "vcl") {
                satoshis = new BigDecimal(amount).movePointRight(8).toString();
            } else if (this.copayersCryptUtils.getCoin() == "eth") {
                satoshis = new BigDecimal(amount).movePointRight(18).toString();
            } else {
                throw new InvalidParamsException("coin is not support");
            }
        }catch(NumberFormatException e){
            throw new InvalidParamsException("amount is invalid");
        }
        return execute(
                tokenAddress,
                new IOutput[]{
                        new Output(
                                address,
                                satoshis,
                                null)},
                msg,
                dryRun,
                operation,
                customData,
                excludeMasternode);
    }

    @Override
    public ITransactionProposal execute(IOutput[] outputs, String msg, boolean dryRun, String operation, String customData, boolean excludeMasternode) {
        return execute(null, outputs, msg, dryRun, operation, customData, excludeMasternode);
    }

    @Override
    public ITransactionProposal execute(IInput[] inputs, IOutput[] outputs, String msg, boolean dryRun, String operation, String customData) {
        return execute(null, inputs, outputs, msg, dryRun, operation, customData, true);
    }

    @Override
    public ITransactionProposal execute(String tokenAddress, IOutput[] outputs, String msg, boolean dryRun, String operation, String customData, boolean excludeMasternode) {
        GsonAsset asset = null;
        if (this.copayersCryptUtils.getCoin() == "vcl" && this.isAsset){
            asset = new GsonAsset(2, null, null, null);
            if(tokenAddress != null  ) {
                throw new InvalidParamsException("tokenAddress is not supported");
            }
        }
        if(outputs.length == 0){
            throw new InvalidParamsException("outputs cannot be zero");
        }
        if(this.copayersCryptUtils.getCoin() == "eth" && outputs.length>1){
            throw new InvalidParamsException("outputs length must be 1");
        }

        String enMsg = new SjclMessageEncryptor()
                .encrypt(
                        msg,
                        copayersCryptUtils.sharedEncryptingKey(
                                credentials.getWalletPrivateKey()
                                        .getPrivateKeyAsHex()));
        return
                bwsApi.postTxProposals(
                        new TransactionRequest(
                                copayersCryptUtils.getCoin(),
                                "livenet",
                                outputs,
                                "normal",
                                enMsg,
                                false,
                                dryRun,
                                operation,
                                customData,
                                null,
                                excludeMasternode,
                                tokenAddress,
                                asset
                            ),
                        credentials);
    }

    @Override
    public ITransactionProposal execute(String tokenAddress, IInput[] inputs, IOutput[] outputs, String msg, boolean dryRun, String operation, String customData, boolean excludeMasternode) {
        GsonAsset asset = null;
        if (this.copayersCryptUtils.getCoin() == "vcl" && this.isAsset){
            asset = new GsonAsset(2, null, null, null);
            if(tokenAddress != null  ) {
                throw new InvalidParamsException("tokenAddress is not supported");
            }
        }
        if(outputs.length == 0){
            throw new InvalidParamsException("outputs cannot be zero");
        }
        if(this.copayersCryptUtils.getCoin() == "eth" && outputs.length>1){
            throw new InvalidParamsException("outputs length must be 1");
        }

        String enMsg = new SjclMessageEncryptor()
                .encrypt(
                        msg,
                        copayersCryptUtils.sharedEncryptingKey(
                                credentials.getWalletPrivateKey()
                                        .getPrivateKeyAsHex()));
        if(inputs == null || inputs.length == 0) {
            return
                    bwsApi.postTxProposals(
                            new TransactionRequest(
                                    copayersCryptUtils.getCoin(),
                                    "livenet",
                                    outputs,
                                    "normal",
                                    enMsg,
                                    false,
                                    dryRun,
                                    operation,
                                    customData,
                                    null,
                                    excludeMasternode,
                                    tokenAddress,
                                    asset
                            ),
                            credentials);
        }else{
            return
                bwsApi.postTxProposals(
                    new TransactionRequest(
                            copayersCryptUtils.getCoin(),
                            "livenet",
                            inputs,
                            outputs,
                            "normal",
                            enMsg,
                            false,
                            dryRun,
                            operation,
                            customData,
                            null,
                            excludeMasternode,
                            tokenAddress,
                            asset
                    ),
                    credentials);
        }
    }
}
