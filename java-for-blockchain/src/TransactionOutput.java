import java.security.PublicKey;

public class TransactionOutput {

    public String id;
    public PublicKey recipient; // Public key cua nguoi nhan
    public float value; // So luong NoobCoin ma nguoi nhan nhan duoc
    public String parentTransactionId; // ID cua giao dich cha

    // Constructor
    public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
        this.recipient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
    }

    // Kiem tra xem coin co thuoc ve ban khong
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }
}
