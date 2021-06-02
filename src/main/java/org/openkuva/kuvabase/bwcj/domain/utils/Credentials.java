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

package org.openkuva.kuvabase.bwcj.domain.utils;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.repository.exception.NotFoundException;

import static org.bitcoinj.core.NetworkParameters.fromID;

public final class Credentials implements ICredentials {
    private byte[] walletPrivateKey;
    private byte[] seedWords;
    private NetworkParameters networkParameters;
    private String networkId;
    private String sharedPrivateKey;
    private String personalPrivateKey;

    public Credentials() {
    }

    private byte[] getWalletPrivateKeyBytes() {
        return walletPrivateKey;
    }

    @Override
    public ECKey getWalletPrivateKey() {
        byte[] walletPrivateKeyBytes = getWalletPrivateKeyBytes();
        return ECKey
                .fromPrivate(walletPrivateKeyBytes);
    }

    @Override
    public void setWalletPrivateKey(ECKey walletPrivateKey) {
        this.walletPrivateKey = walletPrivateKey.getPrivKeyBytes();
    }

    @Override
    public void setSeed(byte[] seedWords) {
        this.seedWords = seedWords;
    }

    @Override
    public byte[] getSeed() {
        return this.seedWords;
    }

    @Override
    public NetworkParameters getNetworkParameters() {
        if (this.networkId.isEmpty()) {
            throw new NotFoundException("network does not set");
        }

        return fromID(this.networkId);
    }

    @Override
    public void setNetworkParameters(NetworkParameters networkParameters) {
        this.networkParameters = networkParameters;
        this.networkId = networkParameters.getId();
    }

    @Override
    public void setSharedEncryptingKey(String sharedEncryptingKey) {
        this.sharedPrivateKey = sharedEncryptingKey;
    }


    @Override
    public void setPersonalEncryptingKey(String personalEncryptingKey) {
        this.personalPrivateKey = personalEncryptingKey;
    }

    @Override
    public String getSharedEncryptingKey() {
        return this.sharedPrivateKey;
    }

    @Override
    public String getPersonalEncryptingKey() {
        return this.personalPrivateKey;
    }
}
