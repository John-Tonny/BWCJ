
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

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodeStatus;

public class GsonMasternodeStatus implements IMasternodeStatus {

    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("payee")
    @Expose
    public String payee;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("protocol")
    @Expose
    public int protocol;
    @SerializedName("daemonversion")
    @Expose
    public String daemonversion;
    @SerializedName("sentinelversion")
    @Expose
    public String sentinelversion;
    @SerializedName("sentinelstate")
    @Expose
    public String sentinelstate;
    @SerializedName("lastseen")
    @Expose
    public long lastseen;
    @SerializedName("activeseconds")
    @Expose
    public long activeseconds;
    @SerializedName("lastpaidtime")
    @Expose
    public long lastpaidtime;
    @SerializedName("lastpaidblock")
    @Expose
    public long lastpaidblock;
    @SerializedName("pingretries")
    @Expose
    public int pingretries;

    public GsonMasternodeStatus() {
    }

    public GsonMasternodeStatus(IMasternodeStatus origin) {
       address = origin.getAddress();
       payee = origin.getPayee();
       status = origin.getStatus();
       protocol = origin.getProtocol();
       daemonversion = origin.getDaemonversion();
       sentinelversion = origin.getSentinelversion();
       sentinelstate = origin.getSentinelstate();
       lastseen = origin.getLastseen();
       activeseconds = origin.getActiveseconds();
       lastpaidtime = origin.getLastpaidtime();
       lastpaidblock = origin.getLastpaidblock();
       pingretries = origin.getPingretries();
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getPayee() { return payee;}

    @Override
    public String getStatus() { return status;}

    @Override
    public int getProtocol() { return protocol; }

    @Override
    public String getDaemonversion() { return daemonversion; }

    @Override
    public String getSentinelversion() { return sentinelversion; }

    @Override
    public String getSentinelstate() { return sentinelstate; }

    @Override
    public long getLastseen() { return lastseen; }

    @Override
    public long getActiveseconds() { return activeseconds; }

    @Override
    public long getLastpaidtime() { return lastpaidtime; }

    @Override
    public long getLastpaidblock() { return lastpaidblock; }

    @Override
    public int getPingretries() { return pingretries; }

}
