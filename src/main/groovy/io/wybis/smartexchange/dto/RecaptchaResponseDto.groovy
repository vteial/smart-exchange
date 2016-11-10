package io.wybis.smartexchange.dto

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames = true)
class RecaptchaResponseDto {

    boolean success

    String hostname

    Date challengeTs

    @JsonProperty('challenge_ts')
    Date getChallengeTs() {
        return challengeTs
    }

    List<String> errorCodes

    @JsonProperty('error-codes')
    List<String> getErrorCodes() {
        return errorCodes
    }
}
