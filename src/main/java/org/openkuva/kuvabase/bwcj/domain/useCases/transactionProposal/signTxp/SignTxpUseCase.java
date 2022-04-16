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

package org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.signTxp;

import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.core.Transaction;

import org.openkuva.kuvabase.bwcj.data.entity.gson.wallet.AddressInfo;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IInput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;
import org.openkuva.kuvabase.bwcj.domain.utils.CommonNetworkParametersBuilder;
import org.openkuva.kuvabase.bwcj.domain.utils.CopayersCryptUtils;
import org.openkuva.kuvabase.bwcj.domain.utils.transactions.EthTransactionBuilder;
import org.openkuva.kuvabase.bwcj.domain.utils.transactions.TransactionBuilder;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.IBitcoreWalletServerAPI;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidParamsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.pojo.signatures.SignatureRequest;
import org.openkuva.kuvabase.bwcj.domain.utils.transactions.IndexedTransactionSignature;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.openkuva.kuvabase.bwcj.domain.utils.DeriveUtils.deriveChildByPath;

public class SignTxpUseCase implements ISignTxpUseCase {
    private final IBitcoreWalletServerAPI bwsApi;
    private final ICredentials credentials;
    private final CopayersCryptUtils copayersCryptUtils;

    public SignTxpUseCase(IBitcoreWalletServerAPI bwsApi, ICredentials credentials, CopayersCryptUtils copayersCryptUtils) {
        this.bwsApi = bwsApi;
        this.credentials = credentials;
        this.copayersCryptUtils = copayersCryptUtils;
    }

    public SignTxpUseCase(IBitcoreWalletServerAPI bwsApi, ICredentials credentials) {
        this.bwsApi = bwsApi;
        this.credentials = credentials;
        this.copayersCryptUtils = this.credentials.getCopayersCryptUtils();
    }

    @Override
    public ITransactionProposal execute(ITransactionProposal txToSign) {
        String secret = null;
        DeterministicKey xpriv =
                copayersCryptUtils.derivedXPrivKey(
                        credentials.getSeed(),
                        credentials.getNetworkParameters());

        if(txToSign.getCoin().equalsIgnoreCase("vcl")) {
            List<List<IndexedTransactionSignature>> signaturesLists = new ArrayList<>();
            TransactionBuilder transactionBuilder = new TransactionBuilder(new CommonNetworkParametersBuilder());

            Map<String, DeterministicKey> derived = new HashMap<>();
            LinkedList<DeterministicKey> privs = new LinkedList<>();

            for (IInput input : txToSign.getInputs()) {
                if (derived.get(input.getPath()) == null) {
                    DeterministicKey result = deriveChildByPath(xpriv, input.getPath());
                    derived.put(input.getPath(), result);
                    privs.push(derived.get(input.getPath()));
                }
            }

            Transaction transaction = transactionBuilder.buildTx(txToSign);
            for (int i = 0; i < privs.size(); i++) {
                signaturesLists.add(
                        TransactionBuilder.getSignatures(
                                transaction,
                                privs.get(i),
                                credentials.getNetworkParameters()));
            }

            if (txToSign.getAtomicswap() != null) {
                secret = txToSign.getAtomicswap().getSecret();
            }

            return
                    bwsApi.postTxProposalsTxIdSignatures(
                            txToSign.getId(),
                            new SignatureRequest(
                                     IndexedTransactionSignature.mapSignatures(
                                            IndexedTransactionSignature.sort(
                                                    IndexedTransactionSignature.flat(signaturesLists))), secret),
                            credentials);
        }else if(txToSign.getCoin().equalsIgnoreCase("eth")){
            EthTransactionBuilder ethTransactionBuilder = new EthTransactionBuilder(new CommonNetworkParametersBuilder());

            AddressInfo addressInfo = this.credentials.getPrivateByPath("m/0/0");

            return
                    bwsApi.postTxProposalsTxIdSignatures(
                            txToSign.getId(),
                            new SignatureRequest(
                                    ethTransactionBuilder.getSignatures(txToSign, addressInfo.getPrivateKey()),
                                    secret),
                            credentials);
        }else{
            throw new InvalidParamsException("coin is not support");
        }
    }
}
