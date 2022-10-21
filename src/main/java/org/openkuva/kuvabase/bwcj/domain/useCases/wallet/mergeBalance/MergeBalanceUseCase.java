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

package org.openkuva.kuvabase.bwcj.domain.useCases.wallet.mergeBalance;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.MnemonicCode;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IInput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.wallet.IWallet;
import org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction.Output;
import org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.addNewTxp.AddNewTxpUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.addNewTxp.IAddNewTxpUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.broadcastTxp.BroadcastTxpUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.broadcastTxp.IBroadcastTxpUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.publishTxp.IPublishTxpUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.publishTxp.PublishTxpUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.signTxp.ISignTxpUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.signTxp.SignTxpUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.wallet.getUtxos.GetUtxosUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.wallet.getUtxos.IGetUtxosUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.wallet.getWalletAddress.GetWalletAddressesUseCase;
import org.openkuva.kuvabase.bwcj.domain.useCases.wallet.getWalletAddress.IGetWalletAddressesUseCase;
import org.openkuva.kuvabase.bwcj.domain.utils.CopayersCryptUtils;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.IBitcoreWalletServerAPI;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.address.IAddressesResponse;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.CopayerNotFoundException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidParamsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeBalanceUseCase implements IMergeBalanceUseCase {

    private final ICredentials[] credentials;
    private IBitcoreWalletServerAPI[] bwsApi;
    private final CopayersCryptUtils[] copayersCryptUtils;
    private final int MATURE_BLOCK = 100;

    public MergeBalanceUseCase(ICredentials credentials, CopayersCryptUtils copayersCryptUtils, IBitcoreWalletServerAPI bwsApi) {
        this.credentials =new ICredentials[1];
        this.credentials[0] = credentials;
        this.copayersCryptUtils = new CopayersCryptUtils[1];
        this.copayersCryptUtils[0] = copayersCryptUtils;
        this.bwsApi = new IBitcoreWalletServerAPI[1];
        this.bwsApi[0] = bwsApi;
    }

    public MergeBalanceUseCase(ICredentials credentials, IBitcoreWalletServerAPI bwsApi) {
        this.credentials =new ICredentials[1];
        this.credentials[0] = credentials;
        this.copayersCryptUtils = new CopayersCryptUtils[1];
        this.copayersCryptUtils[0] = this.credentials[0].getCopayersCryptUtils();
        this.bwsApi = new IBitcoreWalletServerAPI[1];
        this.bwsApi[0] = bwsApi;
    }

    public MergeBalanceUseCase(ICredentials[] credentials, IBitcoreWalletServerAPI[] bwsApi) {
        this.credentials = credentials;
        this.copayersCryptUtils = new CopayersCryptUtils[this.credentials.length];
        for(int i=0;i<this.credentials.length;i++){
            this.copayersCryptUtils[i] = this.credentials[i].getCopayersCryptUtils();
        }
        this.bwsApi = bwsApi;
    }

    @Override
    public String execute(String minAmount, String maxAmount)  {
        IGetWalletAddressesUseCase getWalletAddressesUseCase = new GetWalletAddressesUseCase(this.bwsApi[0]);
        try {
            IAddressesResponse addressInfo = getWalletAddressesUseCase.execute();
            return execute(minAmount, maxAmount, addressInfo.getAddress());
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String execute(String minAmount, String maxAmount, String address)  {
        IAddNewTxpUseCase postTxpUseCase  =
                new AddNewTxpUseCase(
                this.credentials[0],
                this.bwsApi[0]);
        IPublishTxpUseCase publishTxpUseCase = new PublishTxpUseCase(
                        this.bwsApi[0],
                        this.credentials[0]);
        ISignTxpUseCase[] signTxpUseCases = new SignTxpUseCase[this.credentials.length];
        for(int i=0;i< this.credentials.length;i++) {
            signTxpUseCases[i] = new SignTxpUseCase(
                    this.bwsApi[i],
                    this.credentials[i]);
        }
        IBroadcastTxpUseCase broadcastTxpUseCase = new BroadcastTxpUseCase(
                this.credentials[0],
                this.bwsApi[0]);

        IGetUtxosUseCase getUtxosUseCase = new GetUtxosUseCase(this.credentials[0], this.bwsApi[0]);

        long minSatoshis = 0;
        long maxSatoshis = 0;;
        try{
            minSatoshis = Long.valueOf(minAmount) * 100000000;
            maxSatoshis = Long.valueOf(maxAmount) * 100000000;
        }catch (Exception e){
            throw new InvalidParamsException("minAmount or maxAmount is invalid");
        }
        try {
            IInput[] inputs = getUtxosUseCase.execute();
            IOutput[] outputs = new IOutput[1];

            ArrayList<IInput> aInputs = new ArrayList<IInput>();
            long totalSatoshis = 0;
            for(IInput input: inputs){
                if(!input.isSpent() && !input.isLocked()){
                    if (input.getSatoshis() < minSatoshis){
                        if(totalSatoshis >= maxSatoshis){
                            break;
                        }
                        if (!input.isCoinbase() && input.getConfirmations() >= 1){
                            totalSatoshis += input.getSatoshis();
                            aInputs.add(input);
                        }else if(input.isCoinbase() && input.getConfirmations() >= MATURE_BLOCK){
                            totalSatoshis += input.getSatoshis();
                            aInputs.add(input);
                        }
                    }
                }
            }
            if(totalSatoshis < maxSatoshis){
                throw new InvalidParamsException("fund too small!");
            }

            outputs[0]  = new Output(
                    address,
                    String.valueOf(totalSatoshis-100000000),
                    null);
            IInput[] bInputs = aInputs.toArray(new IInput[0]);

            ITransactionProposal proposal = postTxpUseCase.execute(bInputs, outputs,  "", false, "send", "merge");
            ITransactionProposal publishedTxp = publishTxpUseCase.execute(proposal);

            ITransactionProposal signTxp = null;
            for(int i=0;i< this.credentials.length;i++) {
                signTxp = signTxpUseCases[i].execute(publishedTxp);
            }
            ITransactionProposal broadcastTxp =  broadcastTxpUseCase.execute(signTxp.getId());

            return broadcastTxp.getTxid();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> getUrlOptions() {
        int maxNumber = 99999;
        int minNumber = 10000;
        maxNumber -= minNumber;
        int random = (int) (Math.random() * ++maxNumber) + minNumber;

        Map<String, String> urlOptions = new HashMap<>();

        urlOptions.put("r", String.valueOf(random));

        return urlOptions;
    }
}
