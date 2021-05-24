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

package org.openkuva.kuvabase.bwcj.data.entity.pojo.transaction;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.wallet.DeterministicSeed;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IAtomicswapData;

import org.openkuva.kuvabase.bwcj.domain.utils.atomicswap.AuditContract;
import org.openkuva.kuvabase.bwcj.domain.utils.atomicswap.ExtractContract;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.CopayerNotFoundException;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidParamsException;

import java.security.SecureRandom;

public class AtomicswapData implements IAtomicswapData {
    private String secretHash;
    private Boolean initiate;

    private String secret;
    private String contract;
    private Boolean redeem;
    private Boolean atomicSwap;
    private Long lockTime;

    public AtomicswapData() {
        ECKey ecKey = new ECKey();
        byte[] btSecret = ecKey.getPrivKeyBytes();
        this.secret = Utils.HEX.encode(btSecret);
        this.secretHash = Utils.HEX.encode(Sha256Hash.hash(btSecret));

        this.initiate = true;
    }

    public AtomicswapData(String secretHash) {
        if (Utils.isHexString(secretHash) && secretHash.length() != 64) {
            throw new InvalidParamsException("secretHash is invalid");
        }
        this.secretHash = secretHash;
        this.initiate = false;
    }

    public AtomicswapData(String contract, String secret, NetworkParameters parameters) throws InvalidParamsException{
        AuditContract auditContract = new AuditContract(contract, parameters);
        this.atomicSwap = auditContract.isAtomicSwap();
        if(!this.atomicSwap){
            throw new InvalidParamsException("contract is invalid");
        }

        if(secret == null) {
            throw new InvalidParamsException("no secret");
        }

        if(!Utils.isHexString(secret) && secret.length() != 64 ) {
            throw new InvalidParamsException("secret is invalid");
        }
        String hash = Utils.HEX.encode(Sha256Hash.hash(Utils.HEX.decode(secret)));
        if(hash.compareTo(auditContract.getSecretHash())!=0){
            throw new InvalidParamsException("secret is unmatch");
        }
        this.redeem = true;
        this.secret = secret;
        this.contract = contract;
    }

    public AtomicswapData(String contract,  NetworkParameters parameters) throws InvalidParamsException{
        AuditContract auditContract = new AuditContract(contract, parameters);
        this.atomicSwap = auditContract.isAtomicSwap();
        if(!this.atomicSwap){
            throw new InvalidParamsException("contract is invalid");
        }
        this.secret = null;
        this.redeem = false;

        this.contract = contract;
    }

    @Override
    public String getSecretHash() {
        return secretHash;
    }

    @Override
    public Boolean isInitiate() {
        return initiate;
    }

    @Override
    public String getSecret() {
        return secret;
    }

    @Override
    public String getContract() {
        return contract;
    }

    @Override
    public Boolean isRedeem() {
        return redeem;
    }

    @Override
    public Boolean isAtomicSwap() {
        return atomicSwap;
    }

    @Override
    public Long getLockTime() {
        return lockTime;
    }

}
