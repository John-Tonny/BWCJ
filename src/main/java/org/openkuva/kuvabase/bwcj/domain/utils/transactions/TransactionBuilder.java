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

import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IInput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.IOutput;
import org.openkuva.kuvabase.bwcj.data.entity.interfaces.transaction.ITransactionProposal;
import org.openkuva.kuvabase.bwcj.domain.useCases.transactionProposal.signTxp.SignTxpUseCase;
import org.openkuva.kuvabase.bwcj.domain.utils.INetworkParametersBuilder;
import org.openkuva.kuvabase.bwcj.domain.utils.transactions.IndexedTransactionSignature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TransactionBuilder {
    private final INetworkParametersBuilder networkParametersBuilder;

    public TransactionBuilder(INetworkParametersBuilder networkParametersBuilder) {
        this.networkParametersBuilder = networkParametersBuilder;
    }

    public Transaction buildTx(ITransactionProposal tp) {
        NetworkParameters network = networkParametersBuilder.fromTP(tp);

        Transaction transaction = new Transaction(network);

        // john
        if(tp.getTxExtends() != null && tp.getTxExtends().getVersion() > 0){
          transaction.setVersion(tp.getTxExtends().getVersion());
        }else {
            transaction.setVersion(2);
        }
        // john
        // transaction.setType(Transaction.Type.TRANSACTION_NORMAL);

        for (IOutput output : tp.getOutputs()) {
            // john 20220219
            long amount = Long.valueOf(output.getAmount());
            if(tp.getTxExtends() != null && tp.getTxExtends().getVersion() > 0 && tp.getTxExtends().getOutScripts()!= null) {
                if (amount == 0) {
                    transaction.addOutput(
                            Coin.valueOf(amount),
                            tp.getTxExtends().getOutScripts());
                } else{
                    transaction.addOutput(
                            Coin.valueOf(amount),
                            Address.fromString(network,
                                    output.getToAddress()));
                }
            }else {
                transaction.addOutput(
                        Coin.valueOf(amount),
                        Address.fromString(network,
                                output.getToAddress()));
            }
        }

        for (IInput input : tp.getInputs()) {
            transaction.addInput(
                    new TransactionInput(
                            network,
                            transaction,
                            /*new ScriptBuilder()
                                    .build()
                                    .getProgram(),*/
                            Utils.HEX.decode(input.getScriptPubKey()),  // john
                            new TransactionOutPoint(
                                    network,
                                    new FreeStandingTransactionOutput(
                                            network,
                                            new UTXO(
                                                    Sha256Hash.wrap(input.getTxid()),
                                                    input.getVout(),
                                                    Coin.valueOf(input.getSatoshis()),
                                                    0,
                                                    false,
                                                    new Script(Utils.HEX.decode(input.getScriptPubKey()))))),
                            Coin.valueOf(input.getSatoshis())));
        }
        // john
        /*for (TransactionInput transactionInput : transaction.getInputs()) {
            transactionInput.clearScriptBytes();
        }*/

        Coin changeAmount =
                transaction
                        .getFee()
                        .minus(
                                Coin.valueOf(
                                        tp.getFee()));

        if (changeAmount.isGreaterThan(Coin.ZERO)) {
            Script changeScript =
                    ScriptBuilder.createOutputScript(
                            Address.fromString(
                                    network,
                                    tp.getChangeAddress().getAddress()));

            transaction.addOutput(changeAmount, changeScript);
        }

        if(tp.getAtomicswap() != null) {
            transaction.addAtomicswap(tp.getAtomicswapSecretHash(),
                    tp.getAtomicswap().isInitiate(),
                    tp.getAtomicswap().getContract(),
                    tp.getAtomicswap().getSecret(),
                    tp.getAtomicswap().isRedeem(),
                    tp.getAtomicswap().isAtomicSwap(),
                    tp.getAtomicswap().getLockTime());

            if (tp.getAtomicswap().isAtomicSwap()!=null && tp.getAtomicswap().isAtomicSwap().booleanValue() && tp.getAtomicswap().isRedeem()!=null) {
                transaction.getInput(0).setScriptSig(new Script(Utils.HEX.decode(tp.getAtomicswap().getContract())));
                if (!tp.getAtomicswap().isRedeem().booleanValue()) {
                    transaction.getInput(0).setSequenceNumber(TransactionInput.NO_SEQUENCE - 1);
                }
                if(tp.getAtomicswap().getLockTime()!=null) {
                    transaction.setLockTime(tp.getAtomicswap().getLockTime().longValue());
                }
            }
        }

        return sort(transaction, tp.getOutputOrder());
    }
    public static Transaction sort(Transaction transaction, List<Integer> outputOrder) {
        Transaction result = new Transaction(transaction.getParams());
        // john
        result.setVersion((int) transaction.getVersion());
        // result.setType(transaction.getType());

        for (TransactionInput input : transaction.getInputs()) {
            result.addInput(input);
        }

        for (Integer integer : outputOrder) {
            if (integer < transaction.getOutputs().size()) {
                result.addOutput(transaction.getOutput(integer));
            }
        }
        // john
        result.setLockTime(transaction.getLockTime());
        if(transaction.getAtomicswap() != null) {
            result.addAtomicswap(transaction.getAtomicswap().getSecretHash(),
                    transaction.getAtomicswap().isInitiate(),
                    transaction.getAtomicswap().getContract(),
                    transaction.getAtomicswap().getSecret(),
                    transaction.getAtomicswap().isRedeem(),
                    transaction.getAtomicswap().isAtomicSwap(),
                    transaction.getAtomicswap().getLockTime());
        }
        return result;
    }

    public static List<IndexedTransactionSignature> getSignatures(Transaction transaction, DeterministicKey priv, NetworkParameters network) {
        List<IndexedTransactionSignature> result = new ArrayList<>();
        for (int i = 0; i < transaction.getInputs().size(); i++) {
            // john
            if ( transaction.getAtomicswap()!=null && transaction.getAtomicswap().isRedeem()!=null) {
                result.add(
                        new IndexedTransactionSignature(
                                transaction.calculateSignature(
                                        i,
                                        priv,
                                        transaction.getInput(i).getScriptBytes(),
                                        Transaction.SigHash.ALL,
                                        false),
                                i));
            }else{
                TransactionOutput connectedOutput = transaction
                        .getInput(i)
                        .getOutpoint()
                        .getConnectedOutput();

                LegacyAddress legacyAddr = connectedOutput
                        .getAddressFromP2PKHScript(network);
                if(legacyAddr == null){

                    SegwitAddress segwitAddr = connectedOutput
                            .getAddressFromP2WPKHScript(network);
                    if(segwitAddr != null && Arrays.equals(segwitAddr.getHash(), priv.getPubKeyHash())){
                        result.add(
                                new IndexedTransactionSignature(
                                        transaction.calculateWitnessSignature(
                                                i,
                                                priv,
                                                null,
                                                ScriptBuilder.createP2PKHOutputScript(priv),
                                                transaction.getInput(i).getValue(),
                                                Transaction.SigHash.ALL,
                                                false),
                                        i));
                    }
                }else {
                    if(Arrays.equals(legacyAddr.getHash(), priv.getPubKeyHash())) {
                        result.add(
                                new IndexedTransactionSignature(
                                        transaction.calculateSignature(
                                                i,
                                                priv,
                                                connectedOutput.getScriptBytes(),
                                                Transaction.SigHash.ALL,
                                                false),
                                        i));

                    }
                }
            }
        }
        return result;
    }
}
