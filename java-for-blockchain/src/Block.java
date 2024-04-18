import java.util.ArrayList;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); // Du lieu se la mot tin nhan don gian.
    public long timeStamp; // so mili giay ke tu 1/1/1970.
    public int nonce; // So nguyen ngau nhien

    //Block Constructor.
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash(); // Dam bao chung ta thuc hien dieu nay sau khi chung ta da dat cac gia tri khac.
    }

    // Tinh toan hash moi dua tren noi dung cua block
    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
        return calculatedhash;
    }

    // Tang gia tri nonce cho den khi dat duoc muc tieu hash.
    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDificultyString(difficulty); // Tao mot chuoi voi do kho * "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block da duoc dao!!! : " + hash);
    }

    // Them giao dich vao block nay
    public boolean addTransaction(Transaction transaction) {
        // Xu ly giao dich va kiem tra xem co hop le khong, tru khi block la block genesis thi bo qua.
        if (transaction == null) return false;
        if ((previousHash != "0")) {
            if ((transaction.processTransaction() != true)) {
                System.out.println("Giao dich khong hop le. Da bi loai bo.");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Giao dich da duoc them vao block");
        return true;
    }
}
