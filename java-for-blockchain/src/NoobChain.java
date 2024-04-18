import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class NoobChain {
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

    public static int difficulty = 3; // Do kho cua bai toan dao
    public static float minimumTransaction = 0.1f; // So tien toi thieu cho mot giao dich
    public static Wallet walletA;//
    public static Wallet walletB;
    public static Transaction genesisTransaction; // Giao dich dau tien

    public static void main(String[] args) {
        // Them cac block vao blockchain ArrayList:
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // Cai dat Bouncey castle nhu mot Nha cung cap Bao mat

        // Create wallets:
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        // Tao giao dich genesis, gui 100 NoobCoin cho walletA:
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);     // Tao chu ky cho giao dich genesis
        genesisTransaction.transactionId = "0"; // Tao id cho giao dich co gia tri la 0
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); // Tao ra giao dich dau tien
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); // Luu tru giao dich dau tien trong danh sach UTXOs.

        System.out.println("Tao va khai thac khoi Genesis... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        // Testing
        Block block1 = new Block(genesis.hash);
        System.out.println("\nWalletA co so du: " + walletA.getBalance());
        System.out.println("\nWalletA dang gui 40 NoobCoin cho WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\n:WalletA co so du: " + walletA.getBalance());
        System.out.println("WalletB co so du: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA co gang gui 1000 NoobCoin cho WalletB...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA co so du: " + walletA.getBalance());
        System.out.println("WalletB co so du: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nWalletB dang gui 20 NoobCoin cho WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
        System.out.println("\nWalletA co so du: " + walletA.getBalance());
        System.out.println("WalletB co so du: " + walletB.getBalance());

        isChainValid();

    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); // Mot danh sach tam thoi cua cac giao dich chua duoc chi tieu
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        // Lap qua blockchain de kiem tra tuong thich
        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            // So sanh hash hien tai va hash duoc tinh toan
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Hashes hien tai khong hop le");
                return false;
            }
            // So sanh hash truoc do va hash truoc do duoc dang ky
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Hashes truoc do khong hop le");
                return false;
            }
            // So sanh hash muc tieu va hash hien tai
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#Khoi nay chua duoc dao");
                return false;
            }

            // Lap qua cac giao dich cua khoi hien tai va kiem tra chung
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifySignature()) {
                    System.out.println("#Chu ky giao dich " + t + " khong hop le");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs khong bang Outputs tren giao dich (" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Gia tri dau vao duoc tham chieu cua giao dich " + t + " dang bi thieu");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Gia tri dau vao cua giao dich " + t + " khong hop le");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).recipient != currentTransaction.reciepient) {
                    System.out.println("#Nguoi nhan cua giao dich " + t + " khong dung");
                    return false;
                }
                if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    System.out.println("#Nguoi thuc hien giao dich " + t + " khong dung");
                    return false;
                }

            }

        }
        System.out.println("Blockchain hop le");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
