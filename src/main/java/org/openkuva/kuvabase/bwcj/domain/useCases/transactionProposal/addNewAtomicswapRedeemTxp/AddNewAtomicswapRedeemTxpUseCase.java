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

package org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.addNewAtomicswapRedeemTxp;

import org.bitcoinj.core.NetworkParameters;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IAtomicswapRedeemData;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ICustomData;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;
import org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction.AtomicswapData;
import org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction.AtomicswapRedeemData;
import org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction.Output;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.IBitcoreWalletServerAPI;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InsufficientFundsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidAmountException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidWalletAddressException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.pojo.transaction.TransactionRedeemRequest;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.pojo.transaction.TransactionRequest;

public class AddNewAtomicswapRedeemTxpUseCase implements IAddNewAtomicswapRedeemTxpUseCase {
    private final ICredentials credentials;
    private final IBitcoreWalletServerAPI bwsApi;

    public AddNewAtomicswapRedeemTxpUseCase(ICredentials credentials, IBitcoreWalletServerAPI bwsApi) {
        this.credentials = credentials;
        this.bwsApi = bwsApi;
    }

    @Override
    public ITransactionProposal execute(String address,  String msg, boolean dryRun, String customData, boolean excludeMasternode, String contract, String secret) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(address, msg, dryRun, "send", customData, excludeMasternode, contract, secret);
    }

    @Override
    public ITransactionProposal execute(String address, String msg, boolean dryRun, String operation, String customData, boolean excludeMasternode, String contract, String secret) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(
                new IOutput[]{
                        new Output(
                                address,
                                "0",
                                null)},
                msg,
                dryRun,
                operation,
                customData,
                excludeMasternode,
                contract,
                secret);
    }

    @Override
    public ITransactionProposal execute(IOutput[] outputs, String msg, boolean dryRun, String operation, String customData, boolean excludeMasternode, String contract, String secret) {
        return
                bwsApi.postRedeemTxProposals(
                        new TransactionRedeemRequest(
                                credentials.getCopayersCryptUtils().getCoin(),
                                "livenet",
                                outputs,
                                "normal",
                                msg,
                                false,
                                dryRun,
                                operation,
                                customData,
                                null,
                                excludeMasternode,
                                new AtomicswapRedeemData(contract, secret ,credentials.getNetworkParameters())),
                        credentials);
    }
}
