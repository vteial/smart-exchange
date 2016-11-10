package io.wybis.smartexchange.web.console

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.model.User

ResponseDto responseDto = new ResponseDto(type: 0, message: 'Successfully fetched...')

try {
    List<User> models = []

    def dsl = null, fetchLimit = 10

    dsl = datastore.build {
        from User.class.simpleName
        sort desc by updateTime
        limit fetchLimit
    }

    if (params.pageNo) {
        dsl.offset((params.pageNo as int) * fetchLimit)
    }

    def entitys = dsl.execute()
    entitys.each { entity ->
        User model = entity as User
        models << model
    }

    responseDto.data = models
}
catch (Throwable t) {
    if (localMode) {
        t.printStackTrace()
    }
    responseDto.type = ResponseDto.UNKNOWN
    responseDto.message = 'Users fetching failed...'
    responseDto.data = Helper.getStackTraceAsString(t)
    log.severe(responseDto.data)
}

jsonCategory.respondWithJson(response, responseDto)
