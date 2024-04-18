package cn.merryyou.blockchain;

public class BlockChainTest {

    public static void main(String[] args) {
        // First block
        Block firstBlock = new Block("first", "0");
        System.out.println("Hash for block 1: " + firstBlock.hash);

        // Second block
        Block secondBlock = new Block("second", firstBlock.hash);
        System.out.println("Hash for block 2: " + secondBlock.hash);

        // Third block
        Block thirdBlock = new Block("third", secondBlock.hash);
        System.out.println("Hash for block 3: " + thirdBlock.hash);
    }
}