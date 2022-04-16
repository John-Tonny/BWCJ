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

package org.openkuva.kuvabase.bwcj.domain.useCases.wallet.getWalletBalance;

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.wallet.IWallet;
import org.openkuva.kuvabase.bwcj.data.repository.interfaces.wallet.IWalletRepository;
import org.openkuva.kuvabase.bwcj.domain.utils.CopayersCryptUtils;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.IBitcoreWalletServerAPI;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.CopayerNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class GetWalletBalanceUseCase implements IGetWalletBalanceUseCase {

    private final ICredentials credentials;
    private final IBitcoreWalletServerAPI bwsApi;
    private final IWalletRepository repository;
    private CopayersCryptUtils copayersCryptUtils;

    public GetWalletBalanceUseCase(ICredentials credentials, CopayersCryptUtils copayersCryptUtils, IBitcoreWalletServerAPI bwsApi, IWalletRepository repository) {
        this.credentials = credentials;
        this.copayersCryptUtils = copayersCryptUtils;
        this.bwsApi = bwsApi;
        this.repository = repository;
    }

    public GetWalletBalanceUseCase(ICredentials credentials, IBitcoreWalletServerAPI bwsApi, IWalletRepository repository) {
        this.credentials = credentials;
        this.copayersCryptUtils = this.credentials.getCopayersCryptUtils();
        this.bwsApi = bwsApi;
        this.repository = repository;
    }

    @Override
    public IWallet execute() throws CopayerNotFoundException {
        String personalEncryptingKey =
                copayersCryptUtils.personalEncryptingKey(
                        copayersCryptUtils.entropySource(
                                copayersCryptUtils.requestDerivation(
                                        credentials.getSeed())));
        credentials.setPersonalEncryptingKey(personalEncryptingKey);
        return
                repository.save(
                        bwsApi.getWallets(
                                getUrlOptions(), credentials, copayersCryptUtils));
    }

    private Map<String, String> getUrlOptions() {
        int maxNumber = 99999;
        int minNumber = 10000;
        maxNumber -= minNumber;
        int random = (int) (Math.random() * ++maxNumber) + minNumber;

        Map<String, String> urlOptions = new HashMap<>();

        urlOptions.put("includeExtendedInfo", "1");
        urlOptions.put("twoStep", "1");
        urlOptions.put("r", String.valueOf(random));

        return urlOptions;
    }

}
