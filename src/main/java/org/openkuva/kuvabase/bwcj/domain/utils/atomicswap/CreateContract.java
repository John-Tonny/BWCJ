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

package org.openkuva.kuvabase.bwcj.domain.utils.atomicswap;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.wallet.DeterministicSeed;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

public final class CreateContract {
    private final long DELAY_TIME = 5 * 60; // 2 * 3600
    private String contract;
    private String secret;

    public CreateContract(String themAddr, String meAddr, boolean initiate, NetworkParameters parameters) {
        ECKey ecKey = new ECKey();
        byte[] btSecret = ecKey.getPrivKeyBytes();
        this.secret = Utils.HEX.encode(btSecret);
        String secretHash = Utils.HEX.encode(Sha256Hash.hash(btSecret));

        long lockTime = new Date().getTime()/1000 + DELAY_TIME;
        if (initiate) {
            lockTime += DELAY_TIME;
        }

        build(secretHash, themAddr, meAddr, initiate, lockTime, parameters);
    }

    public CreateContract(String themAddr, String meAddr, boolean initiate, long lockTime, NetworkParameters parameters) {
        DeterministicSeed deterministicSeed =
                new DeterministicSeed(
                        new SecureRandom(),
                        DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS,
                        "",
                        Utils.currentTimeSeconds());
        byte[] btSecret = deterministicSeed.getSeedBytes();
        this.secret = Utils.HEX.encode(btSecret);
        String secretHash = Utils.HEX.encode(Sha256Hash.hash(btSecret));

        build(secretHash, themAddr, meAddr, initiate, lockTime, parameters);
    }

    public CreateContract(String secretHash, String themAddr, String meAddr, boolean initiate, long lockTime, NetworkParameters parameters) {
        build(secretHash, themAddr, meAddr, initiate, lockTime, parameters);
    }

    private void build(String secretHash, String themAddr, String meAddr, boolean initiate, long lockTime, NetworkParameters parameters) {
        try{
            if (!Utils.isHexString(secretHash)) {
                return;
            }
            if (secretHash.length() != 64) {
                return;
            }

            ScriptBuilder s = new ScriptBuilder();

            s.op(ScriptOpCodes.OP_IF);
            s.op(ScriptOpCodes.OP_SIZE);
            s.data(Utils.HEX.decode("20"));
            s.op(ScriptOpCodes.OP_EQUALVERIFY);

            s.op(ScriptOpCodes.OP_SHA256);
            s.data(Utils.HEX.decode(secretHash));
            s.op(ScriptOpCodes.OP_EQUALVERIFY);

            s.op(ScriptOpCodes.OP_DUP);
            s.op(ScriptOpCodes.OP_HASH160);
            s.data(Address.fromBase58(parameters, themAddr).getHash160());

            s.op(ScriptOpCodes.OP_ELSE);

            s.data(Utils.reverseBytes(Utils.HEX.decode(Long.toHexString(lockTime))));
            s.op(ScriptOpCodes.OP_CHECKLOCKTIMEVERIFY);
            s.op(ScriptOpCodes.OP_DROP);

            s.op(ScriptOpCodes.OP_DUP);
            s.op(ScriptOpCodes.OP_HASH160);
            s.data(Address.fromBase58(parameters, meAddr).getHash160());

            s.op(ScriptOpCodes.OP_ENDIF);

            // Complete the signature check.
            s.op(ScriptOpCodes.OP_EQUALVERIFY);
            s.op(ScriptOpCodes.OP_CHECKSIG);

            this.contract = Utils.HEX.encode(s.build().getProgram());
        }catch (Exception e){
            this.contract = null;
        }
    }

    public String getContract() { return contract; }

    public String getSecret() { return secret; }

}
