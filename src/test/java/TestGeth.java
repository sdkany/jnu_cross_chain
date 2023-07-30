import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

/**
 * @author SDKany
 * @ClassName TestGeth
 * @Date 2023/7/30 11:52
 * @Version V1.0
 * @Description
 */
public class TestGeth {
    public static void main(String[] args) throws IOException {
        String gethURL = "http://10.154.24.12:8545";//"http://81.71.46.41:8546"; //";
        Web3j web3j = Web3j.build(new HttpService(gethURL));
        System.out.println(web3j.ethBlockNumber().send().getBlockNumber());
    }
}
