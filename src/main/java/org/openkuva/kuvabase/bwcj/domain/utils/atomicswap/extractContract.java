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
import org.openkuva.kuvabase.bwcj.domain.utils.atomicswap.AuditContract;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.script.Script;
import org.bitcoinj.core.PublicKey;
import org.bitcoinj.core.Sha256Hash;

public final class ExtractContract {
    private String signAddr;
    private String secret;
    private boolean redeem;
    private boolean atomicSwap;

    public ExtractContract(String rawTx, NetworkParameters params) {
        try {
            this.atomicSwap = false;
            if (rawTx == null) {
                return;
            }
            if (!Utils.isHexString(rawTx)) {
                return;
            }

            Transaction tx = new Transaction(params, Utils.HEX.decode(rawTx));
            Script s = new Script(tx.getInputs().get(0).getScriptBytes());
            if (s.getChunks().size() != 5 || s.getChunks().get(2).data.length != 32) {
                return;
            }

            AuditContract auditContract = new AuditContract(Utils.HEX.encode(s.getChunks().get(4).data), params);
            if (!auditContract.isAtomicSwap()) return;
            this.atomicSwap = auditContract.isAtomicSwap();

            if (auditContract.getSecretHash().compareTo(Utils.HEX.encode(Sha256Hash.hash(s.getChunks().get(2).data)))>0) {
                return;
            }

            this.signAddr = ECKey.fromPublicOnly(s.getChunks().get(1).data).toAddress(params).toBase58();
            this.redeem = false;
            if (this.signAddr.compareTo(auditContract.getRecipientAddr())==0) {
                this.redeem = true;
            }
            this.secret = Utils.HEX.encode(s.getChunks().get(2).data);
        }catch (Exception e) {
            this.atomicSwap = false;
        }
    }

    public String getSignAddr() { return signAddr; }

    public String getSecret() {
        return secret;
    }

    public boolean isAtomicSwap() { return atomicSwap; }

    public boolean isRedeem() {
        return redeem;
    }

}
