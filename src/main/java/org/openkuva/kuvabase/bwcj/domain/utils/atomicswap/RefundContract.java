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

import org.bitcoinj.core.ECKey.ECDSASignature;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;


public final class RefundContract {
    private String refundContract;

    public RefundContract(String contract, String sigAndPubkey, String secret, NetworkParameters parameters) {
        try{
            if(contract == null) {
                return;
            }
            if (!Utils.isHexString(contract)){
                return;
            }
            if (!Utils.isHexString(sigAndPubkey)){
                return;
            }
            if (!Utils.isHexString(secret)){
                return;
            }
            if (secret.length() != 64){
                return;
            }

            Script s1 = new Script(Utils.HEX.decode(sigAndPubkey));
            if (s1.getChunks().size() != 2) {
                return;
            }

            ECDSASignature ecdsaSignature = ECDSASignature.decodeFromDER(s1.getChunks().get(0).data);
            if(!ecdsaSignature.isCanonical()){
                return;
            }

            AuditContract auditContract = new AuditContract(contract, parameters);
            if(!auditContract.isAtomicSwap()){
                return;
            }

            ScriptBuilder s = new ScriptBuilder();
            s.data(s1.getChunks().get(0).data);
            s.data(s1.getChunks().get(1).data);
            s.addChunk(new ScriptChunk(ScriptOpCodes.OP_0, (byte[])null));
            s.data(Utils.HEX.decode(contract));

            this.refundContract = Utils.HEX.encode(s.build().getProgram());
        }catch (Exception e) {
            this.refundContract = null;
        }
    }

    public String getRefundContract() { return refundContract; }
}
