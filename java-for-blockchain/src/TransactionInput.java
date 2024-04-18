public class TransactionInput {
    public String transactionOutputId; // Tham chieu den TransactionOutputs -> transactionId
    public TransactionOutput UTXO; // Chua dau ra giao dich chua duoc chi tieu

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
