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

package org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.addNewProUpRegTxp;

import org.bitcoinj.core.NetworkParameters;
import org.openkuva.kuvabase.bwcj.data.entity.gson.transaction.GsonTxExtends;
import org.openkuva.kuvabase.bwcj.data.entity.gson.wallet.AddressInfo;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternode;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodePro;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ICustomData;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;
import org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction.Output;
import org.openkuva.kuvabase.bwcj.domain.useCases.wallet.postWalletAddress.CreateNewMainAddressesFromWalletUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.wallet.getWalletAddress.GetWalletAddressesUseCase;
import org.openkuva.kuvabase.bwcj.domain.utils.CopayersCryptUtils;
import org.openkuva.kuvabase.bwcj.domain.utils.masternode.ProUpRegTx;
import org.openkuva.kuvabase.bwcj.domain.utils.messageEncrypt.SjclMessageEncryptor;
import org.openkuva.kuvabase.bwcj.domain.utils.transactions.TransactionBuilder;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.IBitcoreWalletServerAPI;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.address.IAddressesResponse;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InsufficientFundsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidAmountException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidParamsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidWalletAddressException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.pojo.transaction.TransactionRequest;

import java.util.HashMap;
import java.util.Map;

import static org.openkuva.kuvabase.bwcj.domain.useCases.wallet.DefaultConstants.DEFAULT_WALLET_NAME;

public class AddNewProUpRegTxpUseCase implements IAddNewProUpRegTxpUseCase {
    private final IBitcoreWalletServerAPI bwsApi;
    private final ICredentials credentials;
    private final CopayersCryptUtils copayersCryptUtils;
    private final int TX_VERSION_MN_UPDATE_REGISTRAR = 82;

    public AddNewProUpRegTxpUseCase(ICredentials credentials, CopayersCryptUtils copayersCryptUtils, IBitcoreWalletServerAPI bwsApi)  {
        this.credentials = credentials;
        this.copayersCryptUtils = copayersCryptUtils;
        this.bwsApi = bwsApi;
    }

    @Override
    public ITransactionProposal execute(String customData, boolean excludeMasternode, String txid, String masternodePubKey, String masternodePrivKey) throws InvalidParamsException, InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute("", customData, excludeMasternode, txid, masternodePubKey, masternodePrivKey, null, null, null);
    }

    @Override
    public ITransactionProposal execute(String msg, String customData, boolean excludeMasternode, String txid, String masternodePubKey, String masternodePrivKey, String ownerPrivKey, String voteAddr, String payAddr) throws InvalidParamsException, InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        return execute(msg, false, customData, excludeMasternode, txid, masternodePubKey, masternodePrivKey, ownerPrivKey, voteAddr, payAddr);
    }

    @Override
    public ITransactionProposal execute(String msg, boolean dryRun, String customData, boolean excludeMasternode, String txid, String masternodePubKey, String masternodePrivKey, String ownerPrivKey, String voteAddr, String payAddr) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException {
        if(txid == null){
            throw new InvalidParamsException("Invalid txid");
        }
        if(masternodePubKey == null ){
            throw new InvalidParamsException("Invalid masternodePubKey");
        }
        if(masternodePrivKey == null ){
            throw new InvalidParamsException("Invalid masternodePrivKey");
        }

        Map<String, String> options = new HashMap<String, String>();
        String proTxHash = null;
        if(ownerPrivKey == null || voteAddr == null || payAddr == null){
            options.put("txid", txid);
            IMasternode[] masternodes = this.bwsApi.getMasternodes(options);
            options.clear();
            if(masternodes.length > 0){
                if(voteAddr == null){
                    voteAddr = masternodes[0].getVoteAddr();
                }
                if(payAddr == null){
                    payAddr = masternodes[0].getPayAddr();
                }
                proTxHash = masternodes[0].getProTxHash();
                String ownerAddr = masternodes[0].getOwnerAddr();
                options.put("address", ownerAddr);
                IAddressesResponse[] ownerResponse = this.bwsApi.getAddresses(options);
                if(ownerResponse.length>0){
                    String path = ownerResponse[0].getPath();
                    AddressInfo addressInfo = this.credentials.getPrivateByPath(path);
                    if(addressInfo.getAddress().compareTo(ownerAddr) != 0){
                        addressInfo = this.credentials.getPrivateByPath(path, true);
                        if(addressInfo.getAddress().compareTo(ownerAddr) != 0) {
                            throw new InvalidWalletAddressException("Invalid owner address");
                        }
                    }
                    ownerPrivKey = addressInfo.getPrivateKey();
                }else{
                    throw new InvalidParamsException("Invalid ownerPrivKey");
                }

            }else{
                throw new InvalidParamsException("Invalid txid");
            }
            if(ownerPrivKey == null){
                throw new InvalidParamsException("Invalid ownerPrivKey");
            }
            if(voteAddr == null){
                throw new InvalidParamsException("Invalid voteAddr");
            }
            if(proTxHash == null){
                throw new InvalidParamsException("Invalid proTxHash");
            }
            if(payAddr == null){
                throw new InvalidParamsException("Invalid payAddr");
            }
        }

        CreateNewMainAddressesFromWalletUseCase createNewMainAddressesFromWalletUseCase = new CreateNewMainAddressesFromWalletUseCase(this.bwsApi);
        IAddressesResponse addressesResponse = createNewMainAddressesFromWalletUseCase.create(true);
        String enMsg = new SjclMessageEncryptor()
                .encrypt(
                        msg,
                        copayersCryptUtils.sharedEncryptingKey(
                                credentials.getWalletPrivateKey()
                                        .getPrivateKeyAsHex()));

        GsonTxExtends txExtends = new GsonTxExtends(TX_VERSION_MN_UPDATE_REGISTRAR, masternodePrivKey);

        ITransactionProposal txp = bwsApi.postTxProposals(
                        new TransactionRequest(
                                new IOutput[]{
                                        new Output(
                                                addressesResponse.getAddress(),
                                                0,
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
        ProUpRegTx proUpRegTx = new ProUpRegTx(txp.getInputs(), proTxHash, ownerPrivKey, masternodePubKey,  voteAddr, payAddr, this.credentials.getNetworkParameters());
        try {
            txp.getTxExtends().setOutScripts(proUpRegTx.getOutScripts(true));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return txp;
    }
}
