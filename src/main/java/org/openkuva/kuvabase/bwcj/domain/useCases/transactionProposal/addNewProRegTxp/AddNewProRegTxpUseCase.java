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

package org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.addNewProRegTxp;

import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonTxExtends;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodeCollateralPro;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;
import org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction.Output;
import org.openkuva.kuvabase.bwcj.domain.utils.CopayersCryptUtils;
import org.openkuva.kuvabase.bwcj.domain.utils.masternode.ProRegTx;
import org.openkuva.kuvabase.bwcj.domain.utils.messageEncrypt.SjclMessageEncryptor;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.IBitcoreWalletServerAPI;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.address.AddressesRequest;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.address.IAddressesResponse;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InsufficientFundsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidAmountException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidWalletAddressException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.pojo.transaction.TransactionRequest;

import javax.swing.*;
import java.io.IOException;
import java.security.SignatureException;
import java.util.concurrent.ExecutionException;

import static org.openkuva.kuvabase.bwcj.domain.useCases.wallet.DefaultConstants.DEFAULT_WALLET_NAME;

public class AddNewProRegTxpUseCase implements IAddNewProRegTxpUseCase {
    private final IBitcoreWalletServerAPI bwsApi;
    private final ICredentials credentials;
    private final CopayersCryptUtils copayersCryptUtils;
    private final int TX_VERSION_MN_REGISTER = 80;

    public AddNewProRegTxpUseCase(ICredentials credentials, CopayersCryptUtils copayersCryptUtils, IBitcoreWalletServerAPI bwsApi) {
        this.credentials = credentials;
        this.copayersCryptUtils = copayersCryptUtils;
        this.bwsApi = bwsApi;
    }

    public AddNewProRegTxpUseCase(ICredentials credentials, IBitcoreWalletServerAPI bwsApi) {
        this.credentials = credentials;
        this.copayersCryptUtils = this.credentials.getCopayersCryptUtils();
        this.bwsApi = bwsApi;
    }

    @Override
    public ITransactionProposal execute(String customData, boolean excludeMasternode, IMasternodeCollateralPro masternodeCollateralPro) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute("" , customData, excludeMasternode, masternodeCollateralPro);
    }

    @Override
    public ITransactionProposal execute(String msg, String customData, boolean excludeMasternode, IMasternodeCollateralPro masternodeCollateralPro) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(
                msg,
                false,
                customData,
                excludeMasternode,
                masternodeCollateralPro);
    }

    @Override
    public ITransactionProposal execute(String msg, boolean dryRun, String customData, boolean excludeMasternode, IMasternodeCollateralPro masternodeCollateralPro) {
        IAddressesResponse changeAddrResponse = this.bwsApi.postAddresses(
                new AddressesRequest(true, true));
        String enMsg = new SjclMessageEncryptor()
                .encrypt(
                        msg,
                        copayersCryptUtils.sharedEncryptingKey(
                                credentials.getWalletPrivateKey()
                                        .getPrivateKeyAsHex()));
        GsonTxExtends txExtends = null;
        if(masternodeCollateralPro != null) {
            txExtends = new GsonTxExtends(TX_VERSION_MN_REGISTER, masternodeCollateralPro.getMasternodePrivKey());
        }
        ITransactionProposal txp = bwsApi.postTxProposals(
                        new TransactionRequest(
                                copayersCryptUtils.getCoin(),
                                "livenet",
                                new IOutput[]{
                                        new Output(
                                                changeAddrResponse.getAddress(),
                                                "0",
                                                null)},
                                "normal",
                                enMsg,
                                false,
                                dryRun,
                                "move",
                                customData,
                                null,
                                excludeMasternode,
                                txExtends),
                        credentials);

        String ownerAddr = masternodeCollateralPro.getOwnerAddr();
        if(ownerAddr == null ){
            IAddressesResponse ownerAddrResponse = this.bwsApi.postAddresses(
                    new AddressesRequest(true, false));
            ownerAddr = ownerAddrResponse.getAddress();
        }

        String voteAddr = masternodeCollateralPro.getVoteAddr();
        if(voteAddr == null ){
            voteAddr = ownerAddr;
        }

        String payAddr = masternodeCollateralPro.getPayAddr();
        if(payAddr == null ){
            IAddressesResponse payAddrResponse = this.bwsApi.postAddresses(
                    new AddressesRequest(true, false));
            payAddr = payAddrResponse.getAddress();
        }

        ProRegTx proRegTx = new ProRegTx(txp.getInputs(), masternodeCollateralPro.getCollateralId(), masternodeCollateralPro.getCollateralIndex(), masternodeCollateralPro.getCollateralPrivKey(), masternodeCollateralPro.getHost(), masternodeCollateralPro.getPort(), masternodeCollateralPro.getMasternodePubKey(), ownerAddr, voteAddr, payAddr, masternodeCollateralPro.getReward(), this.credentials.getNetworkParameters());
        try {
            txp.getTxExtends().setOutScripts(proRegTx.getOutScripts(true));
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return txp;
    }
}
