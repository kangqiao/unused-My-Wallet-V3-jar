package info.blockchain.wallet.api;

import info.blockchain.wallet.BlockchainFramework;
import info.blockchain.wallet.api.data.FeeOptions;

import io.reactivex.Observable;

public class FeeApi {

    private static FeeEndpoints feeEndpoints;

    private FeeEndpoints getBaseApiInstance() {
        if (feeEndpoints == null) {
            feeEndpoints = BlockchainFramework.getRetrofitApiInstance().
                    create(FeeEndpoints.class);
        }
        return feeEndpoints;
    }

    /**
     * Returns a {@link FeeOptions} object which contains both a "regular" and a "priority" fee
     * option, both listed in Satoshis per byte.
     */
    public Observable<FeeOptions> getFeeOptions() {
        return getBaseApiInstance().getFeeOptions("https://charts.dev.blockchain.info/fees");
    }

}
