/*
 * Copyright (c)  2019 One Kuva LLC, known as OpenKuva.org
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
 *      * Neither the name of the copyright holder nor the names of its
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

package org.openkuva.kuvabase.bwcj.domain.utils.transactions;

import org.openkuva.kuvabase.bwcj.domain.utils.INetworkParametersBuilder;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;

import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.SignedRawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.TransactionDecoder;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign.SignatureData;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.Address;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bitcoinj.core.Utils;


public class EthTransactionBuilder {
    private final INetworkParametersBuilder networkParametersBuilder;
    private final boolean isCompact = false;
    private long chainId = 90L;
    public EthTransactionBuilder(INetworkParametersBuilder networkParametersBuilder) {
        this.networkParametersBuilder = networkParametersBuilder;
    }

    public RawTransaction buildTx(ITransactionProposal tp) {
        // NetworkParameters network = networkParametersBuilder.fromTP(tp);

        String data = "";
        BigInteger amount = new BigInteger(tp.getOutputs()[0].getAmount());
        String toAddress = tp.getOutputs()[0].getToAddress();
        if(tp.getTokenAddress() != null) {
            Address address = new Address(toAddress);
            Uint256 value = new Uint256(amount);
            List<Type> parametersList = new ArrayList<>();
            parametersList.add(address);
            parametersList.add(value);
            List<TypeReference<?>> outList = new ArrayList<>();
            Function function = new Function("transfer", parametersList, outList);
            data = FunctionEncoder.encode(function);
            amount = new BigInteger("0");
            toAddress = tp.getTokenAddress();
        }
        return RawTransaction.createTransaction(chainId,
                new BigInteger(Long.toString(tp.getNonce())),
                new BigInteger(Long.toString(tp.getGasLimit())),
                toAddress,
                amount,
                data,
                new BigInteger(Long.toString(tp.getMaxPriorityFeePerGas())),
                new BigInteger(Long.toString(tp.getMaxFeePerGas()))
        );
    }

    public byte[] getSerialize(ITransactionProposal tp) {
        RawTransaction rawTransaction = this.buildTx(tp);
        return TransactionEncoder.encode(rawTransaction);
    }

    public List<String> getSignatures(ITransactionProposal tp, String privKey) {
        List<String> result = new ArrayList<>();
        RawTransaction rawTransaction = this.buildTx(tp);
        ECKeyPair ecKeyPair = ECKeyPair.create(Utils.HEX.decode(privKey));
        Credentials credentials = Credentials.create(ecKeyPair);
        byte[] signData = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);

        SignedRawTransaction signedRawTransaction = (SignedRawTransaction)TransactionDecoder.decode(Utils.HEX.encode(signData));
        SignatureData data =  signedRawTransaction.getSignatureData();
        String r = Utils.HEX.encode(data.getR());
        String v = Utils.HEX.encode(data.getV());
        int intV = Integer.parseInt(v, 16);
        String sig = "0x";
        byte[] ss = data.getS();
        if(intV == 28 ) {
            if(isCompact) {
                ss[0] |= 0x80;
            }
        }
        String s = Utils.HEX.encode(ss);
        sig += r + s;
        if(!isCompact){
            if(intV == 28){
                sig += "1c";
            }else {
                sig += "1b";
            }
        }
        result.add(sig);
        return result;
    }


}

