package io.wybis.smartexchange.service

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.OrderReceipt
import io.wybis.smartexchange.model.Tran
import io.wybis.smartexchange.service.exceptions.OrderException;

interface OrderService {

    OrderReceipt findByOrderReceiptId(long orderReceiptId)

    void add(SessionDto sessionUser, OrderReceipt orderReceipt) throws OrderException

    void onTransaction(SessionDto sessionUser, Tran tran)
}
