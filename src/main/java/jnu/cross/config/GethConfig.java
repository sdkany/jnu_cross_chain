package jnu.cross.config;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

/**
 * @author SDKany
 * @ClassName GethConfig
 * @Date 2023/7/29 14:59
 * @Version V1.0
 * @Description
 */
public class GethConfig {
    public static String gethURL = "http://10.154.24.12:8545"; //10.154.24.12:8545";
    public static Web3j web3j = Web3j.build(new HttpService(gethURL));
    public static Credentials credentials;
    static {
        try {
            credentials = WalletUtils.loadCredentials("lix", "./src/main/resources/UTC--2023-07-16T15-59-49.181165420Z--18032fb1bb6731060bed83316db4aab0c97e45b4");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }

    public static int chainID = 1337;

}
