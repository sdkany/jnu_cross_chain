package jnu.cross.controller;

import com.alibaba.fastjson.JSONObject;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.methods.response.AppBlock;
import jnu.cross.Response;
import jnu.cross.config.CITAConfig;
import jnu.cross.config.GethConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

/**
 * @author SDKany
 * @ClassName HelloWorldController
 * @Date 2023/7/29 12:08
 * @Version V1.0
 * @Description
 */
@RestController
public class HelloWorldController {
    @RequestMapping("/hello")
    public String hello(@RequestParam(name = "name") String name){
        return "hello world!" + name;
    }

    @RequestMapping("/readCITABlock")
    public String readCITABlock(@RequestParam(name = "blockNumber",required=false) String blockNumber) throws IOException {
        AppBlock.Block block = CITAConfig.citaj.appGetBlockByNumber((blockNumber==null || blockNumber.isEmpty())?DefaultBlockParameter.valueOf("latest"): DefaultBlockParameter.valueOf(new BigInteger(blockNumber)), true).send().getBlock();
        System.out.println("获取到的data为：" + block.getHash());
        return block.getHash(); // 先暂时返回block的hash了
    }

    @RequestMapping("/submitGeth")
    public String writeDataToGeth(@RequestParam(name = "hexData") String hexData) throws ExecutionException, InterruptedException, TransactionException, IOException {
        EthGetTransactionCount ethGetTransactionCount = GethConfig.web3j
                .ethGetTransactionCount(GethConfig.credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        // 获取账户余额，没钱写不了链
//        EthGetBalance ethGetBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
//        System.out.println("balance1 : " + ethGetBalance.getBalance());
//        System.out.println("账号余额(eth)1：" + Convert.fromWei(String.valueOf(ethGetBalance.getBalance()), Convert.Unit.ETHER) + "ETH");
        // 交易给自己
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, new BigInteger("50"), new BigInteger("3000000"),
                GethConfig.credentials.getAddress(), new BigInteger("100"), hexData);

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, GethConfig.chainID, GethConfig.credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = GethConfig.web3j.ethSendRawTransaction(hexValue).sendAsync().get();

        if(ethSendTransaction.getError() != null)
            System.out.println("error : " + ethSendTransaction.getError().getMessage());

        System.out.println("transHash = " + ethSendTransaction.getTransactionHash());

        System.out.println("hexValue : " + hexValue);

        Response response = new Response("本数据已被提交至Geth矿池，等待被挖矿", 0, ethSendTransaction.getTransactionHash());

        return JSONObject.toJSONString(response, true);

        // 等待交易被挖矿
//        TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(
//                GethConfig.web3j,
//                TransactionManager.DEFAULT_POLLING_FREQUENCY,
//                TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
//        TransactionReceipt txReceipt = receiptProcessor.waitForTransactionReceipt(ethSendTransaction.getTransactionHash());
//        System.out.println("success mined!!!!");
//        System.out.println(txReceipt);
//        System.out.println("本数据已被写入Geth：" + GethConfig.web3j.ethGetTransactionByHash(txReceipt.getTransactionHash()).send().getTransaction().get().getInput());
//        System.out.println(GethConfig.web3j.ethGetTransactionByHash(txReceipt.getTransactionHash()).send().getTransaction().get().getInput());

    }

    @RequestMapping("/readGethTransactionState")
    public String readGethTransaction(@RequestParam(name = "transHash") String transHash) throws TransactionException, IOException {
        // 等待交易被挖矿
        //System.out.println("----in readGethTransactionState");
        //System.out.println(transHash);

        Transaction transaction = GethConfig.web3j.ethGetTransactionByHash(transHash).send().getTransaction().get();

        //System.out.println(transaction.getBlockNumberRaw());

        if (transaction == null){
            Response response = new Response("交易" + transHash + "还未被写入到区块！", -1, null);
            return JSONObject.toJSONString(response, true);
        }else{
            //System.out.println("11111");
            //System.out.println(transaction);
//            BigInteger blockNumber = transaction.getBlockNumber();
//            System.out.println(blockNumber.intValue());
//            System.out.println("交易" + transHash + "被写入到" + transaction.getBlockNumber().toString() + "块！");
//            System.out.println(transaction.getInput());
            Response response = new Response("交易" + transHash + "被写入到了以太坊！", 0, transaction.getInput());
            return JSONObject.toJSONString(response, true);
        }
    }
}
