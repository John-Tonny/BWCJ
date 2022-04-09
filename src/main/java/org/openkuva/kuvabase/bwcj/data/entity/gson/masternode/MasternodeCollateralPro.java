
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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.openkuva.kuvabase.bwcj.data.entity.gson.wallet.AddressInfo;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.credentials.ICredentials;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodeCollateralPro;
import org.openkuva.kuvabase.bwcj.domain.utils.Credentials;
import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidWalletAddressException;

public class MasternodeCollateralPro implements IMasternodeCollateralPro {
    private String INVALID_COLLATERAL_ADDRESS = "Invalid collateral address";

    String collateralId;
    int collateralIndex;
    String collateralPrivKey;
    String host;
    int port;
    String masternodePrivKey;
    String masternodePubKey;
    String ownerAddr;
    String voteAddr;
    String payAddr;
    int reward;
    
    public MasternodeCollateralPro(String collateralId, int collateralIndex, String collateralPrivKey, String host, int port, String masternodePrivKey, String masternodePubKey, String ownerAddr, String voteAddr, String payAddr, int reward) {
        this.collateralId = collateralId;
        this.collateralIndex = collateralIndex;
        this.collateralPrivKey = collateralPrivKey;
        this.host = host;
        this.port = port;
        this.masternodePrivKey = masternodePrivKey;
        this.masternodePubKey = masternodePubKey;
        this.ownerAddr = ownerAddr;
        this.voteAddr = voteAddr;
        this.payAddr = payAddr;
        this.reward = reward;
    }

    public MasternodeCollateralPro(String collateralId, int collateralIndex, String collateralAddress, String path, String host, int port, String masternodePrivKey, String masternodePubKey, ICredentials credentials) throws InvalidWalletAddressException {
        this.collateralId = collateralId;
        this.collateralIndex = collateralIndex;
        AddressInfo addressInfo = credentials.getPrivateByPath(path);
        if(addressInfo.getAddress().compareTo(collateralAddress) != 0){
            addressInfo = credentials.getPrivateByPath(path, true);
            if(addressInfo.getAddress().compareTo(collateralAddress) != 0) {
                throw new InvalidWalletAddressException(INVALID_COLLATERAL_ADDRESS);
            }
        }
        this.collateralPrivKey = addressInfo.getPrivateKey();
        this.host = host;
        this.port = port;
        this.masternodePrivKey = masternodePrivKey;
        this.masternodePubKey = masternodePubKey;
        this.ownerAddr = null;
        this.voteAddr = null;
        this.payAddr = null;
        this.reward = 0;
    }

    public MasternodeCollateralPro(String collateralId, int collateralIndex, String collateralAddress, String path, String host, int port, String masternodePrivKey, String masternodePubKey, ICredentials credentials, String ownerAddr, String voteAddr, String payAddr, int reward) throws InvalidWalletAddressException {
        this.collateralId = collateralId;
        this.collateralIndex = collateralIndex;
        AddressInfo addressInfo = credentials.getPrivateByPath(path);
        if(addressInfo.getAddress().compareTo(collateralAddress) != 0){
            addressInfo = credentials.getPrivateByPath(path, true);
            if(addressInfo.getAddress().compareTo(collateralAddress) != 0) {
                throw new InvalidWalletAddressException(INVALID_COLLATERAL_ADDRESS);
            }
        }
        this.collateralPrivKey = addressInfo.getPrivateKey();
        this.host = host;
        this.port = port;
        this.masternodePrivKey = masternodePrivKey;
        this.masternodePubKey = masternodePubKey;
        this.ownerAddr = ownerAddr;
        this.voteAddr = voteAddr;
        this.payAddr = payAddr;
        this.reward = reward;
    }

    @Override
    public String getCollateralId() { return collateralId; }

    @Override
    public int getCollateralIndex() { return collateralIndex; }

    @Override
    public String getCollateralPrivKey() { return collateralPrivKey;}

    @Override
    public String getHost() { return host; }

    @Override
    public int getPort() { return port; }

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

    @Override
    public int getReward() { return reward; }

}
