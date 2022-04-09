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

import org.bitcoinj.core.Utils;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;

import java.util.List;

public final class AuditContract {
    private boolean atomicSwap;
    private String refundAddr;
    private String recipientAddr;
    private String secretHash;
    private int secretSize;
    private long lockTime;
    private String contractAddr;

    public AuditContract(String contract, NetworkParameters parameters) {
        try{
            this.atomicSwap = false;
            if(contract == null) {
                return;
            }
            if (!Utils.isHexString(contract)){
                return;
            }

            Script s = new Script(Utils.HEX.decode(contract));
            if (s.getChunks().size() != 20) {
                return;
            }

            if(s.getChunks().get(0).opcode == ScriptOpCodes.OP_IF &&
                    s.getChunks().get(1).opcode == ScriptOpCodes.OP_SIZE &&
                    isCanonicalPush(s.getChunks().get(2)) &&
                    s.getChunks().get(3).opcode == ScriptOpCodes.OP_EQUALVERIFY &&
                    s.getChunks().get(4).opcode == ScriptOpCodes.OP_SHA256 &&
                    s.getChunks().get(5).opcode == ScriptOpCodes.OP_DATA_32 &&
                    s.getChunks().get(6).opcode == ScriptOpCodes.OP_EQUALVERIFY &&
                    s.getChunks().get(7).opcode == ScriptOpCodes.OP_DUP &&
                    s.getChunks().get(8).opcode == ScriptOpCodes.OP_HASH160 &&
                    s.getChunks().get(9).opcode == ScriptOpCodes.OP_DATA_20 &&
                    s.getChunks().get(10).opcode == ScriptOpCodes.OP_ELSE &&
                    isCanonicalPush(s.getChunks().get(11)) &&
                    s.getChunks().get(12).opcode == ScriptOpCodes.OP_CHECKLOCKTIMEVERIFY &&
                    s.getChunks().get(13).opcode == ScriptOpCodes.OP_DROP &&
                    s.getChunks().get(14).opcode == ScriptOpCodes.OP_DUP &&
                    s.getChunks().get(15).opcode == ScriptOpCodes.OP_HASH160 &&
                    s.getChunks().get(16).opcode == ScriptOpCodes.OP_DATA_20 &&
                    s.getChunks().get(17).opcode == ScriptOpCodes.OP_ENDIF &&
                    s.getChunks().get(18).opcode == ScriptOpCodes.OP_EQUALVERIFY &&
                    s.getChunks().get(19).opcode == ScriptOpCodes.OP_CHECKSIG){
                this.atomicSwap = true;
            }

            if(!this.atomicSwap){
                return;
            }

            this.secretHash = Utils.HEX.encode(s.getChunks().get(5).data);
            this.recipientAddr = LegacyAddress.fromScriptHash(parameters, s.getChunks().get(9).data).toBase58();
            this.refundAddr = LegacyAddress.fromScriptHash(parameters, s.getChunks().get(16).data).toBase58();
            this.lockTime  = Integer.parseInt(Utils.HEX.encode(Utils.reverseBytes(s.getChunks().get(11).data)), 16);

            this.secretSize = Integer.parseInt(Utils.HEX.encode(s.getChunks().get(2).data), 16);
            if(this.secretSize != 32) {
                this.atomicSwap = false;
                return;
            }

            byte[] contractHash = Utils.sha256hash160(Utils.HEX.decode(contract));
            this.contractAddr = LegacyAddress.fromScriptHash(parameters, contractHash).toBase58();
        }catch (Exception e) {
            this.atomicSwap = false;
        }
    }

    public String getRefundAddr() { return refundAddr; }
    public String getRecipientAddr() { return  recipientAddr; }
    public String getSecretHash() {
        return secretHash;
    }
    public int getSecretSize() { return secretSize; }
    public long getLockTime() { return lockTime; }
    public String getContractAddr() { return contractAddr; }

    public boolean isAtomicSwap() { return atomicSwap; }

    private boolean isCanonicalPush(ScriptChunk scriptChunk) {
        int opcode = scriptChunk.opcode;
        byte[] data = scriptChunk.data;
        int dataLen = data.length;
        if (opcode > ScriptOpCodes.OP_16) {
            return true;
        }

        if (opcode < ScriptOpCodes.OP_PUSHDATA1 && opcode > ScriptOpCodes.OP_0 && (dataLen == 1 && data[0] <= 16)) {
            return false;
        }
        if (opcode == ScriptOpCodes.OP_PUSHDATA1 && dataLen < ScriptOpCodes.OP_PUSHDATA1) {
            return false;
        }
        if (opcode == ScriptOpCodes.OP_PUSHDATA2 && dataLen <= 0xff) {
            return false;
        }
        if (opcode == ScriptOpCodes.OP_PUSHDATA4 && dataLen <= 0xffff) {
            return false;
        }
        return true;
    }


}
