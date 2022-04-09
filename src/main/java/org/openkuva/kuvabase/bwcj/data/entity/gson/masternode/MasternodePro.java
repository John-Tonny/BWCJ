
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

package org.openkuva.kuvabase.bwcj.data.entity.gson.masternode;

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodePro;

public class MasternodePro implements IMasternodePro {
    String proTxHash;
    String address;
    String masternodePrivKey;
    String masternodePubKey;
    String ownerAddr;
    String voteAddr;
    String payAddr;

    public MasternodePro(String proTxHash, String address, String masternodePrivKey, String masternodePubKey, String ownerAddr, String voteAddr, String payAddr) {
        this.proTxHash = proTxHash;
        this.address = address;
        this.masternodePrivKey = masternodePrivKey;
        this.masternodePubKey = masternodePubKey;
        this.ownerAddr = ownerAddr;
        this.voteAddr = voteAddr;
        this.payAddr = payAddr;
    }

    // ProUpService
    public MasternodePro(String proTxHash, String address, String masternodePrivKey, String payAddr, boolean mode) {
        this.proTxHash = proTxHash;
        this.address = address;
        this.masternodePrivKey = masternodePrivKey;
        this.payAddr = payAddr;
    }

    // ProUpReg
    public MasternodePro(String proTxHash, String masternodePubKey, String voteAddr, String payAddr) {
        this.proTxHash = proTxHash;
        this.masternodePubKey = masternodePubKey;
        this.voteAddr = voteAddr;
        this.payAddr = payAddr;
    }

    // ProRevoke
    public MasternodePro(String proTxHash, String masternodePrivKey) {
        this.proTxHash = proTxHash;
        this.masternodePrivKey = masternodePrivKey;
    }

    @Override
    public String getProTxHash() { return proTxHash; }

    @Override
    public String getAddress() { return address; }

    @Override
    public String getHost() {
        String[] ips = address.split(":");
        return ips[0];
    }

    @Override
    public int getPort() {
        String[] ips = address.split(":");
        try{
            return Integer.parseInt(ips[1]);
        }catch(NumberFormatException e){
            return 9090;
        }
    }

    @Override
    public String getMasternodePrivKey() { return masternodePrivKey; }

    @Override
    public String getMasternodePubKey() { return masternodePubKey; }

    @Override
    public String getOwnerAddr() { return ownerAddr; }

    @Override
    public String getVoteAddr() { return ownerAddr; }

    @Override
    public String getPayAddr() { return ownerAddr; }


}
