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

package org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.addNewProUpServiceTxp;

import org.bitcoinj.core.NetworkParameters;
import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonTxExtends;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternode;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodeBlsSign;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodePro;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ICustomData;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;
import org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction.Output;
import org.openkuva.kuvabase.bwcj.domain.useCases.wallet.postWalletAddress.CreateNewMainAddressesFromWalletUseCase;
import org.openkuva.kuvabase.bwcj.domain.utils.CopayersCryptUtils;
import org.openkuva.kuvabase.bwcj.domain.utils.masternode.ProRevokeTx;
import org.openkuva.kuvabase.bwcj.domain.utils.masternode.ProUpServiceTx;
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

import java.util.HashMap;
import java.util.Map;

import static org.openkuva.kuvabase.bwcj.domain.useCases.wallet.DefaultConstants.DEFAULT_WALLET_NAME;

public class AddNewProUpServiceTxpUseCase implements IAddNewProUpServiceTxpUseCase {
    private final IBitcoreWalletServerAPI bwsApi;
    private final ICredentials credentials;
    private final CopayersCryptUtils copayersCryptUtils;
    private final int TX_VERSION_MN_UPDATE_SERVICE = 81;

    public AddNewProUpServiceTxpUseCase(ICredentials credentials, CopayersCryptUtils copayersCryptUtils, IBitcoreWalletServerAPI bwsApi)  {
        this.credentials = credentials;
        this.copayersCryptUtils = copayersCryptUtils;
        this.bwsApi = bwsApi;
    }

    public AddNewProUpServiceTxpUseCase(ICredentials credentials, IBitcoreWalletServerAPI bwsApi)  {
        this.credentials = credentials;
        this.copayersCryptUtils = this.credentials.getCopayersCryptUtils();
        this.bwsApi = bwsApi;
    }

    @Override
    public ITransactionProposal execute(String customData, boolean excludeMasternode, String txid, String address) throws InvalidParamsException, InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute("", customData, excludeMasternode, txid, address, null ,null);
    }

    @Override
    public ITransactionProposal execute(String msg, String customData, boolean excludeMasternode, String txid, String address, String masternodePrivKey) throws InvalidParamsException, InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(msg, customData, excludeMasternode, txid, address, null, masternodePrivKey);
    }

    @Override
    public ITransactionProposal execute(String msg, String customData, boolean excludeMasternode, String txid, String address, String payAddr, String masternodePrivKey) throws InvalidParamsException, InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(msg, false,  customData, excludeMasternode, txid, address, payAddr ,masternodePrivKey);
    }

    @Override
    public ITransactionProposal execute(String msg, boolean dryRun, String customData, boolean excludeMasternode, String txid, String address, String payAddr, String masternodePrivKey) throws InvalidParamsException, InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException{
        if(txid == null){
            throw new InvalidParamsException("Invalid txid");
        }
        String host = null;
        int port;
        if(address == null){
            throw new InvalidParamsException("Invalid txid");
        }else{
            String[] ips = address.split(":");
            if(ips.length!=2){
                throw new InvalidParamsException("Invalid address");
            }
            host = ips[0];
            try{
                port = Integer.parseInt(ips[1]);
            }catch (NumberFormatException e){
                throw new InvalidParamsException("Invalid address");
            }
        }

        Map<String, String> options = new HashMap<String, String>();
        String proTxHash = null;
        if(masternodePrivKey == null || payAddr == null){
            options.put("txid", txid);
            IMasternode[] masternodes = this.bwsApi.getMasternodes(options);
            options.clear();
            if(masternodes.length > 0){
                if(masternodePrivKey == null) {
                    masternodePrivKey = masternodes[0].getMasternodePrivKey();
                }
                if(payAddr == null){
                    payAddr = masternodes[0].getPayAddr();
                }
                if(masternodes[0].getReward()==0){
                    payAddr = null;
                }
                proTxHash = masternodes[0].getProTxHash();
            }else{
                throw new InvalidParamsException("Invalid txid");
            }
            if(masternodePrivKey == null){
                throw new InvalidParamsException("Invalid masternodePrivKey");
            }
            if(proTxHash == null){
                throw new InvalidParamsException("Invalid proTxHash");
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

        GsonTxExtends txExtends = new GsonTxExtends(TX_VERSION_MN_UPDATE_SERVICE, null);

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
        ProUpServiceTx proUpServiceTx = new ProUpServiceTx(txp.getInputs(), proTxHash, host, port,  masternodePrivKey, payAddr, this.credentials.getNetworkParameters());
        try {
            String msgHash = proUpServiceTx.getMessageHash();
            options.put("msgHash", msgHash);
            options.put("masternodePrivateKey", masternodePrivKey);
            IMasternodeBlsSign masternodeBlsSign = this.bwsApi.getMasternodeBlsSign(options);
            proUpServiceTx.setSig(masternodeBlsSign.getSignature());
            txp.getTxExtends().setOutScripts(proUpServiceTx.getScripts1());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return txp;
    }
}
