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

package org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.publishTxp;

import org.bitcoinj.core.Utils;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;
import org.openkuva.kuvabase.bwcj.domain.utils.CommonNetworkParametersBuilder;
import org.openkuva.kuvabase.bwcj.domain.utils.CopayersCryptUtils;
import org.openkuva.kuvabase.bwcj.domain.utils.transactions.EthTransactionBuilder;
import org.openkuva.kuvabase.bwcj.domain.utils.transactions.TransactionBuilder;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.IBitcoreWalletServerAPI;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidParamsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.pojo.publish.PublishRequest;


public class PublishTxpUseCase implements IPublishTxpUseCase {
    private final IBitcoreWalletServerAPI bwsApi;
    private final ICredentials credentials;
    private final CopayersCryptUtils copayersCryptUtils;

    public PublishTxpUseCase(IBitcoreWalletServerAPI bwsApi, ICredentials credentials, CopayersCryptUtils copayersCryptUtils) {
        this.bwsApi = bwsApi;
        this.credentials = credentials;
        this.copayersCryptUtils = copayersCryptUtils;
    }

    public PublishTxpUseCase(IBitcoreWalletServerAPI bwsApi, ICredentials credentials) {
        this.bwsApi = bwsApi;
        this.credentials = credentials;
        this.copayersCryptUtils = this.credentials.getCopayersCryptUtils();
    }

    @Override
    public ITransactionProposal execute(ITransactionProposal toPublish) {
        String outScripts = null;
        String msg = "";
        if(toPublish.getRelay()!=null && this.copayersCryptUtils.getCoin() != "eth"){
            throw new InvalidParamsException("coin is not support");
        }
        if(toPublish.getToken()!=null && this.copayersCryptUtils.getCoin() != "eth"){
            throw new InvalidParamsException("coin is not support");
        }
        if(toPublish.getAsset()!=null && this.copayersCryptUtils.getCoin() != "vcl"){
            throw new InvalidParamsException("coin is not support");
        }

        if(toPublish.getCoin().equalsIgnoreCase( "vcl")){
            if(toPublish.getTxExtends()!=null && toPublish.getTxExtends().getVersion()>0 && toPublish.getTxExtends().getOutScripts()!= null){
                outScripts = toPublish.getTxExtends().getOutScripts();
            }

            TransactionBuilder transactionBuilder = new TransactionBuilder(new CommonNetworkParametersBuilder());
            msg = Utils.HEX.encode(transactionBuilder.buildTx(toPublish).unsafeBitcoinSerialize1());
        }else if(toPublish.getCoin().equalsIgnoreCase( "eth")){
             EthTransactionBuilder ethTransactionBuilder = new EthTransactionBuilder(new CommonNetworkParametersBuilder());
             msg = "0x" + Utils.HEX.encode(ethTransactionBuilder.getSerialize(toPublish));
        }else{
            throw new InvalidParamsException("coin is not support");
        }
        return
                bwsApi.postTxProposalsTxIdPublish(
                        toPublish.getId(),
                        new PublishRequest(
                                copayersCryptUtils.signMessage(
                                        msg,
                                        copayersCryptUtils.requestDerivation(
                                                credentials.getSeed())
                                                .getPrivateKeyAsHex()), outScripts),
                        credentials);
    }
}
