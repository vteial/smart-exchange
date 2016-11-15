package io.wybis.smartexchange.service

import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.TransferReceipt
import io.wybis.smartexchange.service.exceptions.ModelNotFoundException
import io.wybis.smartexchange.service.exceptions.TransferException;

interface TransferService {

	void create(SessionDto sessionUser, TransferReceipt tranferReceipt) throws TransferException

	TransferReceipt accept(SessionDto sessionUser, long receiptId) throws ModelNotFoundException, TransferException

	TransferReceipt cancel(SessionDto sessionUser, long receiptId) throws ModelNotFoundException, TransferException

}
