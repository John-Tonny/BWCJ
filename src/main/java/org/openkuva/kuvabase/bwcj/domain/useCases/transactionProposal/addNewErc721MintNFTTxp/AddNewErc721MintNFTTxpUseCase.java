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

package org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.addNewErc721MintNFTTxp;

import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonToken;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
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

public class AddNewErc721MintNFTTxpUseCase implements IAddNewErc721MintNFTTxpUseCase {
    private final IBitcoreWalletServerAPI bwsApi;
    private final ICredentials credentials;
    private final CopayersCryptUtils copayersCryptUtils;

    public AddNewErc721MintNFTTxpUseCase(ICredentials credentials, CopayersCryptUtils copayersCryptUtils, IBitcoreWalletServerAPI bwsApi) {
        this.credentials = credentials;
        this.copayersCryptUtils = copayersCryptUtils;
        this.bwsApi = bwsApi;
    }

    public AddNewErc721MintNFTTxpUseCase(ICredentials credentials, IBitcoreWalletServerAPI bwsApi) {
        this.credentials = credentials;
        this.copayersCryptUtils = this.credentials.getCopayersCryptUtils();
        this.bwsApi = bwsApi;
    }

    @Override
    public ITransactionProposal execute(String tokenAddress,
                                        String toAddress,
                                        String tokenUri) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(tokenAddress, toAddress, tokenUri,  null, null, null);
    }

    @Override
    public ITransactionProposal execute(String tokenAddress,
                                        String toAddress,
                                        String tokenUri,
                                        String maxFeePerGas) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(tokenAddress, toAddress, tokenUri,  null, null, maxFeePerGas);
    }

    @Override
    public ITransactionProposal execute(String tokenAddress,
                                        String toAddress,
                                        String tokenUri,
                                        String msg,
                                        String maxFeePerGas) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(tokenAddress, toAddress, tokenUri, msg, null, maxFeePerGas);
    }

    @Override
    public ITransactionProposal execute(String tokenAddress,
                                        String toAddress,
                                        String tokenUri,
                                        String msg,
                                        String customData,
                                        String maxFeePerGas) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        if (this.copayersCryptUtils.getCoin() != "eth") {
            throw new InvalidParamsException("coin is not support");
        }
        IOutput[] outputs = new IOutput[]{
                new Output(
                        toAddress,
                        "0",
                        null)};

        GsonToken token= new GsonToken(1, 3, null, tokenUri, null);

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
                                false,
                                "erc721-mintNFT",
                                customData,
                                null,
                                false,
                                tokenAddress,
                                token,
                                maxFeePerGas),
                        credentials);
    }
}
