package cn.merryyou.blockchain;

import cn.merryyou.blockchain.util.StringUtil;

import java.util.Date;

public class Block {

    /**
     * Current block's hash
     */
    public String hash;

    /**
     * Previous block's hash
     */
    public String previousHash;

    /**
     * Current block's data
     */
    private final String data;

    /**
     * Timestamp
     */
    private long timeStamp;

    private int nonce;


    public Block(String hash, String previousHash, String data) {
        this.hash = hash;
        this.previousHash = previousHash;
        this.data = data;
    }

    public Block(String data, String previousHash) {
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        timeStamp +
                        nonce +
                        data);
        return calculatedhash;
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }
}