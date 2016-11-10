package io.wybis.smartexchange.service;

import io.wybis.smartexchange.dto.SessionDto

interface AutoNumberService {

    long getNextNumber(SessionDto sessionUser, String key)
}
