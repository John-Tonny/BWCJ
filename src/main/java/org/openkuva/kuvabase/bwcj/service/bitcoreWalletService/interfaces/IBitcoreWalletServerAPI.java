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

package org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces;

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.fee.IFeeLevel;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.*;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.*;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.asset.IAssetInfo;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.wallet.IWallet;
import org.openkuva.kuvabase.bwcj.domain.utils.CopayersCryptUtils;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.address.AddressesRequest;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.address.IAddressesResponse;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.broadcast.BroadcastRequest;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.CopayerNotFoundException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InsufficientFundsException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidAmountException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidWalletAddressException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.login.LoginRequest;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.notification.INotificationResponse;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.publish.IPublishRequest;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.signatures.ISignatureRequest;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.wallets.ICreateWalletRequest;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.wallets.ICreateWalletResponse;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.wallets.IJoinWalletRequest;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.wallets.IJoinWalletResponse;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.pojo.masternode.MasternodeBroadcastRequest;

import java.util.Map;

import retrofit2.http.QueryMap;

public interface IBitcoreWalletServerAPI {

    /**
     * POST v2/wallets/
     */
    ICreateWalletResponse postWallets(ICreateWalletRequest createWalletRequest);

    /**
     * POST v3/addresses/
     */
    IAddressesResponse postAddresses(AddressesRequest addressesRequest);

    /**
     * GET v1/addresses/
     */
    IAddressesResponse[] getAddresses(@QueryMap Map<String, String> options);

    /**
     * POST v2/wallets/{wallet_id}/copayers
     */
    IJoinWalletResponse postWalletsWalletIdCopayers(String walletId, IJoinWalletRequest createCopayerRequest);

    /**
     * GET v2/wallets/
     */
    IWallet getWallets(Map<String, String> options, ICredentials credentials, CopayersCryptUtils copayersCryptUtils) throws CopayerNotFoundException;

    /**
     * POST v2/txproposals/
     */
    ITransactionProposal postTxProposals(ITransactionRequest transactionRequest, ICredentials credentials) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException;

    /**
     * GET v1/feelevels
     */
    IFeeLevel[] getFeeLevels(Map<String, String> options);

    /**
     * POST v1/txproposals/{tx_id}/publish
     */
    ITransactionProposal postTxProposalsTxIdPublish(String txId, IPublishRequest publishRequest, ICredentials credentials);

    /**
     * POST v1/txproposals/{tx_id}/signatures
     */
    ITransactionProposal postTxProposalsTxIdSignatures(String txId, ISignatureRequest signatureRequest, ICredentials credentials);

    /**
     * POST v1/txproposals/{tx_id}/broadcast
     */
    ITransactionProposal postTxProposalsTxIdBroadcast(String txId, BroadcastRequest broadcastRequest, ICredentials credentials);

    /**
     * DELETE v1/txproposals/:id/
     */
    ITransactionProposal deleteTxProposal(String txId);

    /**
     * GET v1/txproposals/
     */
    ITransactionProposal[] getPendingTransactionProposals(ICredentials credentials);

    /**
     * GET v1/notifications/
     */
    INotificationResponse[] getNotification(Map<String, String> options);

    /**
     * POST v1/login
     */
    String login(LoginRequest request);

    // john
    /**
     * GET v1/txhistory/
     */
    ITransactionHistory[] getTxHistory(Map<String, String> options, ICredentials credentials);

    // john 20220813
    /**
     * GET v2/txhistory/
     */
    ITransactionHistory2 getTxHistory2(Map<String, String> options, ICredentials credentials);

    /**
     * GET v1/masternode/status/
     */
    IMasternodeStatus getMasternodeStatus(Map<String, String> options);

    /**
     * GET /v1/masternode/collateral/
     */
    IMasternodeCollateral[] getMasternodeCollateral(Map<String, String> options);

    /**
     * GET v1/masternode/ping/
     */
    IMasternodePing getMasternodePing(Map<String, String> options);

    /**
     * GET v1/masternode/broadcast/
     */
    IMasternodeBroadcast broadcastMasternode(MasternodeBroadcastRequest masternodeBroadcastRequest);

    /**
     * GET v1/masternode/
     */
    IMasternode[] getMasternodes(Map<String, String> options);

    /**
     * GET v1/masternode/
     */
    IMasternodeRemove removeMasternodes(Map<String, String> options);


    /**
     * GET v1/masternode/blssign/
     */
    IMasternodeBlsSign getMasternodeBlsSign(Map<String, String> options);

    /**
     * POST v3/atomicswaptxproposals/
     */
    ITransactionProposal postInitiateTxProposals(ITransactionInitiateRequest transactionRequest, ICredentials credentials) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException;

    /**
     * POST v3/atomicswaptxproposals/
     */
    ITransactionProposal postParticipateTxProposals(ITransactionParticipateRequest transactionRequest, ICredentials credentials) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException;

    /**
     * POST v3/redeemtxproposals/
     */
    ITransactionProposal postRedeemTxProposals(ITransactionRedeemRequest transactionRequest, ICredentials credentials) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException;

    /**
     * POST v3/redeemtxproposals/
     */
    ITransactionProposal postRefundTxProposals(ITransactionRefundRequest transactionRequest, ICredentials credentials) throws InsufficientFundsException, InvalidWalletAddressException, InvalidAmountException;

    /**
     * GET v2/txproposals/
     */
    ITransactionProposal[] getPendingAtomicswapTransactionProposals(ICredentials credentials);

    /**
     * GET v1/asset/
     */
    IAssetInfo getAssetInfo(Map<String, String> options);


}