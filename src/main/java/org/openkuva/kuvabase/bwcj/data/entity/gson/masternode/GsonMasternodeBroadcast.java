
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

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openkuva.kuvabase.bwcj.data.entity.interfaces.masternode.IMasternodeBroadcast;

public class GsonMasternodeBroadcast implements IMasternodeBroadcast {
    @SerializedName("response")
    @Expose
    public Object response;

    private Boolean bSuccessful;
    private String errorMessage;

    public GsonMasternodeBroadcast() {
    }

    public GsonMasternodeBroadcast(IMasternodeBroadcast origin) {
        response = origin.getResponse();
    }

    @Override
    public Object getResponse() {
        return response;
    }

    public class GsonMasternodeBroadcastResult {
        @SerializedName("overall")
        @Expose
        public String overall;
        @SerializedName("errorMessage")
        @Expose
        public String errorMessage;

        public GsonMasternodeBroadcastResult(String overall, String errorMessage) {
            this.overall = overall;
            this.errorMessage = errorMessage;
        }

        public String getOverall() { return overall; }
        public String getErrorMessage() { return errorMessage; }
    }

    @Override
    public boolean isSuccessful() {
        if (this.bSuccessful != null) return bSuccessful.booleanValue();
        if (this.response == null){
            return false;
        }
        this.getStatus();
        return this.bSuccessful.booleanValue();
    }

    @Override
    public String getErrorMessage() {
        if (this.bSuccessful != null) return this.errorMessage;
        if (this.response == null){
            return null;
        }
        this.getStatus();
        return this.errorMessage;
    }

    private void getStatus() {
        Gson gson=new Gson();
        String str = gson.toJson(this.getResponse());
        GsonMasternodeBroadcastResult gsonMasternodeBroadcastResult = gson.fromJson(str, GsonMasternodeBroadcastResult.class);
        this.errorMessage = gsonMasternodeBroadcastResult.getErrorMessage();
        if( this.errorMessage!=null){
            this.bSuccessful = false;
        }else {
            this.bSuccessful = true;
        }
    }
}

