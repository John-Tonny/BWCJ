package org.openkuva.kuvabase.bwcj.domain.utils.transactions;

import org.bitcoinj.crypto.TransactionSignature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bitcoinj.core.Utils;

public class IndexedTransactionSignature {
    private final TransactionSignature signature;
    private final int index;

    IndexedTransactionSignature(TransactionSignature signature, int index) {
        this.signature = signature;
        this.index = index;
    }

    public TransactionSignature getSignature() {
        return signature;
    }

    public int getIndex() { return index; }


    public static List<IndexedTransactionSignature> sort(List<IndexedTransactionSignature> toSort) {
        Collections.sort(toSort, (o1, o2) -> Integer.compare(o1.index, o2.index));
        return toSort;
    }

    public static List<IndexedTransactionSignature> flat(List<List<IndexedTransactionSignature>> lists) {
        List<IndexedTransactionSignature> result = new ArrayList<>();
        for (List<IndexedTransactionSignature> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    public static List<String> mapSignatures(List<IndexedTransactionSignature> signatures) {
        List<String> result = new ArrayList<>();
        for (IndexedTransactionSignature signature : signatures) {
            result.add(
                    Utils.HEX.encode(
                            signature.signature.encodeToDER()));

        }
        return result;
    }
}
