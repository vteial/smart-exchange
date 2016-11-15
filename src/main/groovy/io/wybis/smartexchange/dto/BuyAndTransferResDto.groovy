package io.wybis.smartexchange.dto

import groovy.transform.Canonical
import io.wybis.smartexchange.model.TranReceipt
import io.wybis.smartexchange.model.Transfer
import io.wybis.smartexchange.model.TransferReceipt

@Canonical
class BuyAndTransferResDto {

    long id

    List<TranReceipt> tranReceipts

    List<TransferReceipt> transferReceipts

}
