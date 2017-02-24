package info.blockchain.wallet.wallet.payload;

import info.blockchain.api.blockexplorer.BlockExplorer;
import info.blockchain.api.data.MultiAddress;
import info.blockchain.wallet.BaseIntegTest;
import info.blockchain.wallet.multiaddress.MultiAddressFactory;
import info.blockchain.wallet.payload.PayloadManager;
import info.blockchain.wallet.payload.data.HDWallet;
import info.blockchain.wallet.payload.data.LegacyAddress;
import info.blockchain.wallet.payload.data.Wallet;
import info.blockchain.wallet.payload.data.WalletWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import retrofit2.Call;

public class PayloadManagerIntegTest extends BaseIntegTest{

    @Test
    public void upgradeV2PayloadToV3() throws Exception {

        //Create a wallet
        PayloadManager.getInstance().create("My HDWallet", "name@email.com", "MyTestWallet");

        Wallet walletBody = PayloadManager.getInstance().getPayload();

        //Remove HD part
        walletBody.setHdWallets(new ArrayList<HDWallet>());

        //Add legacy so we have at least 1 address
        LegacyAddress newlyAdded = walletBody.addLegacyAddress("HDAddress label", null);

        final String guidOriginal = walletBody.getGuid();

        walletBody.upgradeV2PayloadToV3(null, "HDAccount Name2");

        //Check that existing legacy addresses still exist
        Assert.assertEquals(newlyAdded.getAddress(), walletBody.getLegacyAddressList().get(0).getAddress());

        //Check that Guid is still same
        Assert.assertEquals(walletBody.getGuid(), guidOriginal);

        //Check that wallet is flagged as upgraded
        Assert.assertTrue(walletBody.isUpgraded());

        //Check that 1 account exists with keys
        String xpriv = walletBody.getHdWallets().get(0).getAccounts().get(0).getXpriv();
        Assert.assertTrue(xpriv != null && !xpriv.isEmpty());

        //Check that mnemonic exists
        try {
            Assert.assertEquals(walletBody.getHdWallets().get(0).getMnemonic().size(), 12);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("upgradeV2PayloadToV3 failed");
        }
    }

    @Test
    public void recoverFromMnemonic_1() throws Exception {

        String mnemonic = "all all all all all all all all all all all all";
        String seedHex = "0660cc198330660cc198330660cc1983";

        PayloadManager.getInstance().recoverFromMnemonic(mnemonic, "My HDWallet", "name@email.com", "SomePassword");

        Wallet walletBody = PayloadManager.getInstance()
            .getPayload();

        Assert.assertEquals(seedHex, walletBody.getHdWallets().get(0).getSeedHex());
    }

    @Test
    public void recoverFromMnemonic_2() throws Exception {

        String mnemonic = "one defy stock very oven junk neutral weather sweet pyramid celery sorry";
        String seedHex = "9aa737587979dcf2a53fc5dbb5e09467";

        PayloadManager.getInstance().recoverFromMnemonic(mnemonic, "My HDWallet", "name@email.com", "SomePassword");

        Wallet walletBody = PayloadManager.getInstance()
            .getPayload();

        Assert.assertEquals(seedHex, walletBody.getHdWallets().get(0).getSeedHex());
    }

    @Test
    public void recoverFromMnemonic_3() throws Exception {

        String mnemonic = "rural globe champion coral donate glad cotton choice near beyond carpet library";
        String seedHex = "bd8c6898181410c50c31419382b88b40";

        PayloadManager.getInstance().recoverFromMnemonic(mnemonic, "My HDWallet", "name@email.com", "SomePassword");

        Wallet walletBody = PayloadManager.getInstance()
            .getPayload();

        Assert.assertEquals(seedHex, walletBody.getHdWallets().get(0).getSeedHex());

        PayloadManager.getInstance().initializeAndDecrypt(PayloadManager.getInstance().getPayload().getSharedKey(), PayloadManager.getInstance().getPayload().getGuid(), "SomePassword");

//        PayloadManager.getInstance().initializeAndDecrypt("73cae651-f27f-451c-8dc0-df1736444f02",
//            "2b66b3b9-e73c-4436-8470-179f9865bfd1", "aaaaaaaaaA");
//
//        02-24 10:11:58.396 23817-25468/piuk.blockchain.android D/vos: sharedKey: 73cae651-f27f-451c-8dc0-df1736444f02
//        02-24 10:11:58.396 23817-25468/piuk.blockchain.android D/vos: guid: 2b66b3b9-e73c-4436-8470-179f9865bfd1
//        02-24 10:11:58.396 23817-25468/piuk.blockchain.android D/vos: password: aaaaaaaaaA
    }
}
