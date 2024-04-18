package cn.merryyou.blockchain;

import cn.merryyou.blockchain.util.JsonUtil;

import java.util.ArrayList;

public class BlockChainListTest {

    public static ArrayList<Block> blockChain = new ArrayList();

    public static int difficulty = 5;

    public static void main(String[] args) {
        // Trying to Mine block 1...
        blockChain.get(0).mineBlock(difficulty);

        // Trying to Mine block 2...
        blockChain.get(1).mineBlock(difficulty);

        // Trying to Mine block 3...
        blockChain.get(2).mineBlock(difficulty);

        // Checking if the Blockchain is Valid
        System.out.println("\nBlockchain is Valid: " + isChainValid());

        // Printing the blockchain
        System.out.println(JsonUtil.toJson(blockChain));
    }

    public static Boolean isChainValid() {

        Block currentBlock;
        Block previousBlock;
        boolean flag = true;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // Loop through list to check hashes
        for (int i = 1; i < blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);
            // Compare registered hash and calculated hash
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current hashes not equal");
                flag = false;
            }
            // Compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous hashes not equal");
                flag = false;
            }

            // Check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                flag = false;
            }
        }

        return flag;
    }
}