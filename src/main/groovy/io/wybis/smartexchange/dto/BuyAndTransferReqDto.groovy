package io.wybis.smartexchange.dto

import groovy.transform.Canonical
import io.wybis.smartexchange.model.Transfer

@Canonical
class BuyAndTransferReqDto {

    long fromAccountId

    long tranAccountId

    String tranType

    long tranBaseUnit

    double tranUnit

    double tranRate

    double tranProfitRate

    List<Transfer> transfers

}
