
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

package org.openkuva.kuvabase.bwcj.data.entity.gson.transaction;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.openkuva.kuvabase.bwcj.data.entity.gson.wallet.GsonEncryptMessage;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IAction;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IInput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput2;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransaction;
import org.openkuva.kuvabase.bwcj.domain.utils.messageEncrypt.SjclMessageEncryptor;

public class GsonTransaction implements ITransaction {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("txid")
    @Expose
    public String txid;
    @SerializedName("confirmations")
    @Expose
    public long confirmations;
    @SerializedName("blockheight")
    @Expose
    public long blockheight;
    @SerializedName("fees")
    @Expose
    public int fees;
    @SerializedName("time")
    @Expose
    public long time;
    @SerializedName("size")
    @Expose
    public String size;
    @SerializedName("amount")
    @Expose
    public String amount;
    @SerializedName("action")
    @Expose
    public String action;
    @SerializedName("addressTo")
    @Expose
    public String addressTo;
    @SerializedName("outputs")
    @Expose
    public GsonOutput2[] outputs = null;
    @SerializedName("dust")
    @Expose
    public boolean dust;
    @SerializedName("encryptedMessage")
    @Expose
    public String encryptedMessage;
    @SerializedName("message")
    @Expose
    public Object message;
    @SerializedName("creatorName")
    @Expose
    public String creatorName;
    @SerializedName("hasUnconfirmedInputs")
    @Expose
    public boolean hasUnconfirmedInputs;
    @SerializedName("customData")
    @Expose
    public String  customData;

    private String sharedEncryptingKey;

    public GsonTransaction() {
    }

    public GsonTransaction(ITransaction origin) {
        id = origin.getId();
        txid = origin.getTxid();
        confirmations = origin.getConfirmations();
        blockheight = origin.getBlockheight();
        fees = origin.getFees();
        time = origin.getTime();
        size = origin.getSize();
        amount = origin.getAmount();
        action = origin.getAction();
        addressTo = origin.getAddressTo();
        outputs = mapOutputs(origin.getOutputs());
        dust = origin.getDust();
        encryptedMessage = origin.getEncryptedMessage();
        message = origin.getMessage();
        hasUnconfirmedInputs = origin.getHasUnconfirmedInputs();
        customData = origin.getCustomData();

    }

    private static GsonAction[] mapActions(IAction[] origin) {
        if (origin == null) {
            return null;
        }

        GsonAction[] result = new GsonAction[origin.length];
        for (int i = 0; i < origin.length; i++) {
            result[i] = new GsonAction(origin[i]);
        }
        return result;
    }

    private static GsonOutput2[] mapOutputs(IOutput2[] origin) {
        if (origin == null) {
            return null;
        }

        GsonOutput2[] result = new GsonOutput2[origin.length];
        for (int i = 0; i < origin.length; i++) {
            result[i] = new GsonOutput2(origin[i]);
        }
        return result;
    }

    private static GsonInput[] mapInputs(IInput[] origin) {
        if (origin == null) {
            return null;
        }

        GsonInput[] result = new GsonInput[origin.length];
        for (int i = 0; i < origin.length; i++) {
            result[i] = new GsonInput(origin[i]);
        }
        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTxid() {
        return txid;
    }

    @Override
    public long getConfirmations() {
        return confirmations;
    }

    @Override
    public long getBlockheight() {
        return blockheight;
    }

    @Override
    public int getFees() {
        return fees;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getSize() {
        return size;
    }

    @Override
    public String getAmount() {
        return amount;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public String getAddressTo() {
        return addressTo;
    }

    @Override
    public GsonOutput2[] getOutputs() {
        return outputs;
    }

    @Override
    public boolean getDust() {
        return dust;
    }

    @Override
    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    @Override
    public Object getMessage() {
        return message;
    }

    @Override
    public String getCreatorName() {
        return creatorName;
    }

    @Override
    public boolean getHasUnconfirmedInputs() {
        return hasUnconfirmedInputs;
    }

    @Override
    public String getCustomData() {
        if(this.sharedEncryptingKey == null) return customData;
        try {
            Gson gson = new Gson();
            GsonEncryptMessage enMsg  = gson.fromJson(customData, GsonEncryptMessage.class);
            if(enMsg.getCt()!=null && enMsg.getIv()!=null) {
                return new SjclMessageEncryptor()
                        .decrypt(
                                customData,
                                sharedEncryptingKey);
            }
            return customData;
        }catch (Exception e){
            return customData;
        }
    }

    public void setSharedEncryptingKey(String sharedEncryptingKey) {
        this.sharedEncryptingKey = sharedEncryptingKey;
    }
}
