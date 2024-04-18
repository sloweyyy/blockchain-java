import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    public String transactionId; // ID cua giao dich
    public PublicKey sender; // Public key cua nguoi gui
    public PublicKey reciepient; // Public key cua nguoi nhan
    public float value; // Gia tri cua giao dich
    public byte[] signature; // Chu ky cua giao dich, ngan chan bat ky ai khac chi tieu tien trong vi cua minh

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // So luong giao dich duoc tao ra

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction() {

        if(verifySignature() == false) {
            System.out.println("#Chu ky giao dich khong hop le");
            return false;
        }

        // Thu thap cac giao dich dau vao (Dam bao chung khong duoc chi tieu):
        for(TransactionInput i : inputs) {
            i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);
        }

        // Kiem tra xem giao dich co hop le khong:
        if(getInputsValue() < NoobChain.minimumTransaction) {
            System.out.println("Gia tri giao dich qua nho: " + getInputsValue());
            return false;
        }

        // Tao ra cac giao dich dau ra:
        float leftOver = getInputsValue() - value; // Lay gia tri cua cac giao dich dau vao sau do lay phan thua lai:
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.reciepient, value, transactionId)); // Gui gia tri cho nguoi nhan
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); // Gui lai phan thua lai cho nguoi gui

        // Them cac giao dich dau ra vao danh sach UTXO
        for(TransactionOutput o : outputs) {
            NoobChain.UTXOs.put(o.id , o);
        }

        // Xoa cac giao dich dau vao tu danh sach UTXO vi da chi tieu:
        for(TransactionInput i : inputs) {
            if (i.UTXO == null) continue; // Neu giao dich khong duoc tim thay thi bo qua
            NoobChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if (i.UTXO == null)
                continue; // Neu giao dich khong duoc tim thay thi bo qua, hanh vi nay co the khong toi uu.
            total += i.UTXO.value;
        }
        return total;
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
        signature = StringUtil.applyECDSASig(privateKey,data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

    private String calculateHash() {
        sequence++; // Tang so luong giao dich duoc tao ra
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
        );
    }
}
