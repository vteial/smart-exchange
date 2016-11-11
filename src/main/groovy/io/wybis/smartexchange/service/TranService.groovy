package io.wybis.smartexchange.service

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.TranReceipt
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException
import io.wybis.smartexchange.service.exceptions.TransactionException;

interface TranService {

    TranReceipt findByTranReceiptId(long TranReceiptId)

    void create(SessionDto sessionUser, TranReceipt tranReceipt) throws TransactionException

    TranReceipt accept(SessionDto sessionUser, long receiptId) throws ModelNotFoundException, TransactionException

    TranReceipt cancel(SessionDto sessionUser, long receiptId) throws ModelNotFoundException, TransactionException

}
