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

import org.openkuva.kuvabase.bwcj.service.bitcoreWalletService.interfaces.exception.InvalidParamsException;
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
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicArray;


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
    private final String ERC20ManagerAddr = "0xeEc8C8875dC98FfB5da5CD2e83102Aab962C96C3";
    private final String RelayAddr = "0x62aa89614d2ec79dc7Db2A0e84026bBD02b3d7fD";
    public EthTransactionBuilder(INetworkParametersBuilder networkParametersBuilder) {
        this.networkParametersBuilder = networkParametersBuilder;
    }

    public RawTransaction buildTx(ITransactionProposal tp) {
        // NetworkParameters network = networkParametersBuilder.fromTP(tp);

        String data = "";
        List<Type> parametersList = new ArrayList<Type>();
        List<TypeReference<?>> outList = new ArrayList<>();
        BigInteger amount ;
        String toAddress;
        if(tp.getRelay()!=null) {
            // vcl-eth bridge
            if (tp.getRelay().getCmd() == 1) {
                amount = new BigInteger(tp.getOutputs()[0].getAmount());
                parametersList.add(new Address(ERC20ManagerAddr));
                parametersList.add(new Uint256(amount));
                Function function = new Function("approve", parametersList, outList);
                data = FunctionEncoder.encode(function);
                amount = new BigInteger("0");
                toAddress = tp.getTokenAddress();
            } else if (tp.getRelay().getCmd() == 2) {
                amount = new BigInteger(tp.getOutputs()[0].getAmount());
                parametersList.add(new Uint256(amount));
                parametersList.add(new Uint32(new Long(tp.getRelay().getAssetGuid()).longValue()));
                parametersList.add(new Utf8String(tp.getRelay().getSysAddr()));
                Function function = new Function("freezeBurnERC20", parametersList, outList);
                data = FunctionEncoder.encode(function);
                amount = new BigInteger("0");
                toAddress = ERC20ManagerAddr;
            } else if (tp.getRelay().getCmd() == 3 || tp.getRelay().getCmd() == 4) {
                parametersList.add(new Uint64(tp.getRelay().getNevmBlockNumber()));
                String txBytes = tp.getRelay().getTxBytes();
                if (txBytes.startsWith("0x")) {
                    txBytes = txBytes.substring(2);
                }
                parametersList.add(new DynamicBytes(Utils.HEX.decode(txBytes)));
                parametersList.add(new Uint256(tp.getRelay().getTxIndex()));

                String[] txSibling = tp.getRelay().getTxSibling();
                List<Uint256> lsTxSibling = new ArrayList<>();
                for (int i = 0; i < txSibling.length; i++) {
                    if (txSibling[i].startsWith("0x")) {
                        txSibling[i] = txSibling[i].substring(2);
                    }
                    lsTxSibling.add(new Uint256(new BigInteger(txSibling[i], 16)));
                }
                parametersList.add(new DynamicArray(lsTxSibling));

                String sysBlockHeader = tp.getRelay().getSyscoinBlockHeader();
                if (sysBlockHeader.startsWith("0x")) {
                    sysBlockHeader = sysBlockHeader.substring(2);
                }
                parametersList.add(new DynamicBytes(Utils.HEX.decode(sysBlockHeader)));

                if (tp.getRelay().getCmd() == 3) {
                    Function function = new Function("relayTx", parametersList, outList);
                    data = FunctionEncoder.encode(function);
                } else {
                    Function function = new Function("relayAssetTx", parametersList, outList);
                    data = FunctionEncoder.encode(function);
                }
                amount = new BigInteger("0");
                toAddress = RelayAddr;
            } else {
                throw new InvalidParamsException("cmd is invalid");
            }
        }else if(tp.getToken()!=null) {
            //erc 721
            if (tp.getToken().getType() == 1) {
                if (tp.getToken().getCmd() == 1) {
                    parametersList.add(new Address(tp.getFrom()));
                    parametersList.add(new Address(tp.getOutputs()[0].getToAddress()));
                    parametersList.add(new Uint256(new BigInteger(tp.getToken().getId())));
                    Function function = new Function("safeTransferFrom", parametersList, outList);
                    data = FunctionEncoder.encode(function);
                    amount = new BigInteger("0");
                    toAddress = tp.getTokenAddress();
                } else if (tp.getToken().getCmd() == 2) {
                    parametersList.add(new Address(tp.getOutputs()[0].getToAddress()));
                    parametersList.add(new Uint256(new BigInteger(tp.getToken().getId())));
                    parametersList.add(new Utf8String(tp.getToken().getUri()));
                    Function function = new Function("mint", parametersList, outList);
                    data = FunctionEncoder.encode(function);
                    amount = new BigInteger("0");
                    toAddress = tp.getTokenAddress();;
                } else if (tp.getToken().getCmd() == 3) {
                    parametersList.add(new Address(tp.getOutputs()[0].getToAddress()));
                    parametersList.add(new Utf8String(tp.getToken().getUri()));
                    Function function = new Function("mintNFT", parametersList, outList);
                    data = FunctionEncoder.encode(function);
                    amount = new BigInteger("0");
                    toAddress = tp.getTokenAddress();;
               } else {
                    throw new InvalidParamsException("token cmd is invalid");
                }
            } else if(tp.getToken().getType() == 2){
                // erc1155
                if (tp.getToken().getCmd() == 1) {
                    parametersList.add(new Address(tp.getFrom()));
                    parametersList.add(new Address(tp.getOutputs()[0].getToAddress()));
                    parametersList.add(new Uint256(new BigInteger(tp.getToken().getId())));
                    parametersList.add(new Uint256(new BigInteger(tp.getOutputs()[0].getAmount())));
                    String mdata = tp.getToken().getData();
                    if(mdata == null){
                        parametersList.add(new DynamicBytes(new byte[]{}));
                    }else {
                        parametersList.add(new DynamicBytes(mdata.getBytes()));
                    }
                    Function function = new Function("safeTransferFrom", parametersList, outList);
                    data = FunctionEncoder.encode(function);
                    amount = new BigInteger("0");
                    toAddress = tp.getTokenAddress();
                } else if (tp.getToken().getCmd() == 2) {
                    parametersList.add(new Address(tp.getOutputs()[0].getToAddress()));
                    parametersList.add(new Uint256(new BigInteger(tp.getToken().getId())));
                    parametersList.add(new Uint256(new BigInteger(tp.getOutputs()[0].getAmount())));
                    Function function = new Function("mint", parametersList, outList);
                    data = FunctionEncoder.encode(function);
                    amount = new BigInteger("0");
                    toAddress = tp.getTokenAddress();;
                } else {
                    throw new InvalidParamsException("token cmd is invalid");
                }
            } else {
                throw new InvalidParamsException("token type is not supported");
            }
        } else {
            amount = new BigInteger(tp.getOutputs()[0].getAmount());
            toAddress = tp.getOutputs()[0].getToAddress();
            if(tp.getTokenAddress() != null ) {
                parametersList.add(new Address(toAddress));
                parametersList.add(new Uint256(amount));
                Function function = new Function("transfer", parametersList, outList);
                data = FunctionEncoder.encode(function);
                amount = new BigInteger("0");
                toAddress = tp.getTokenAddress();
            }
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

