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

package org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.addNewProUpRevokeTxp;

import org.bitcoinj.core.NetworkParameters;
import org.openkuva.kuvabase.bwcj.data.entity.gson.masternode.GsonMasternodeBlsSign;
import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonTxExtends;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternode;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodeBlsSign;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ICustomData;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;
import org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction.Output;
import org.openkuva.kuvabase.bwcj.domain.useCases.wallet.postWalletAddress.CreateNewMainAddressesFromWalletUseCase;

import org.openkuva.kuvabase.bwcj.domain.utils.CopayersCryptUtils;
import org.openkuva.kuvabase.bwcj.domain.utils.masternode.ProRevokeTx;
import org.openkuva.kuvabase.bwcj.domain.utils.messageEncrypt.SjclMessageEncryptor;
import org.openkuva.kuvabase.bwcj.domain.utils.transactions.TransactionBuilder;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.IBitcoreWalletServerAPI;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.address.AddressesRequest;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.address.IAddressesResponse;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InsufficientFundsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidAmountException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidParamsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidWalletAddressException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.pojo.transaction.TransactionRequest;

import static org.openkuva.kuvabase.bwcj.domain.useCases.wallet.DefaultConstants.DEFAULT_WALLET_NAME;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodePro;

import java.util.HashMap;
import java.util.Map;

public class AddNewProUpRevokeTxpUseCase implements IAddNewProUpRevokeTxpUseCase {
    private final IBitcoreWalletServerAPI bwsApi;
    private final ICredentials credentials;
    private final CopayersCryptUtils copayersCryptUtils;
    private final int TX_VERSION_MN_UPDATE_REVOKE = 83;

    public AddNewProUpRevokeTxpUseCase(ICredentials credentials, CopayersCryptUtils copayersCryptUtils, IBitcoreWalletServerAPI bwsApi)  {
        this.credentials = credentials;
        this.copayersCryptUtils = copayersCryptUtils;
        this.bwsApi = bwsApi;
    }

    public AddNewProUpRevokeTxpUseCase(ICredentials credentials, IBitcoreWalletServerAPI bwsApi)  {
        this.credentials = credentials;
        this.copayersCryptUtils = this.credentials.getCopayersCryptUtils();
        this.bwsApi = bwsApi;
    }


    @Override
    public ITransactionProposal execute(String customData, boolean excludeMasternode, String txid) throws InvalidParamsException,InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute("", customData, excludeMasternode,  txid, null);
    }

    @Override
    public ITransactionProposal execute(String customData, boolean excludeMasternode, String txid, String masternodePrivKey) throws InvalidParamsException, InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute("", customData, excludeMasternode,  txid, masternodePrivKey);
    }

    @Override
    public ITransactionProposal execute(String msg, String customData, boolean excludeMasternode, String txid, String masternodePrivKey) throws InvalidParamsException, InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(msg, false, customData, excludeMasternode, txid, masternodePrivKey);
    }

    @Override
    public ITransactionProposal execute(String msg, boolean dryRun, String customData, boolean excludeMasternode, String txid, String masternodePrivKey) throws InvalidParamsException, InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        if(txid == null){
            throw new InvalidParamsException("Invalid txid");
        }

        Map<String, String> options = new HashMap<String, String>();
        String proTxHash = null;
        if(masternodePrivKey == null){
            options.put("txid", txid);
            IMasternode[] masternodes = this.bwsApi.getMasternodes(options);
            options.clear();
            if(masternodes.length > 0){
                masternodePrivKey = masternodes[0].getMasternodePrivKey();
                proTxHash = masternodes[0].getProTxHash();
            }else{
                throw new InvalidParamsException("Invalid masternodePrivKey");
            }
            if(masternodePrivKey == null){
                throw new InvalidParamsException("Invalid masternodePrivKey");
            }
            if(proTxHash == null){
                throw new InvalidParamsException("Invalid txid");
            }
        }

        IAddressesResponse changeAddrResponse = this.bwsApi.postAddresses(
                new AddressesRequest(true, true));

        String enMsg = new SjclMessageEncryptor()
                .encrypt(
                        msg,
                        copayersCryptUtils.sharedEncryptingKey(
                                credentials.getWalletPrivateKey()
                                        .getPrivateKeyAsHex()));

        GsonTxExtends txExtends = txExtends = new GsonTxExtends(TX_VERSION_MN_UPDATE_REVOKE, null);

        ITransactionProposal txp = bwsApi.postTxProposals(
                new TransactionRequest(
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

        NetworkParameters params = this.credentials.getNetworkParameters();
        ProRevokeTx proRevokeTx = new ProRevokeTx(txp.getInputs(), proTxHash, masternodePrivKey, this.credentials.getNetworkParameters());
        try {
            String msgHash = proRevokeTx.getMessageHash();
            options.put("msgHash", msgHash);
            options.put("masternodePrivateKey", masternodePrivKey);
            IMasternodeBlsSign masternodeBlsSign = this.bwsApi.getMasternodeBlsSign(options);
            proRevokeTx.setSig(masternodeBlsSign.getSignature());
            txp.getTxExtends().setOutScripts(proRevokeTx.getScripts1());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return txp;
    }
}
