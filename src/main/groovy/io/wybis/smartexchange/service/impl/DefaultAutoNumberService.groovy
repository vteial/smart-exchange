package io.wybis.smartexchange.service.impl

import com.google.appengine.api.datastore.Transaction
import groovyx.gaelyk.GaelykBindings
import groovyx.gaelyk.logging.GroovyLogger
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.model.AutoNumber
import io.wybis.smartexchange.service.AutoNumberService

@GaelykBindings
class DefaultAutoNumberService implements AutoNumberService {

    GroovyLogger log = new GroovyLogger(DefaultAutoNumberService.class.getName())

    @Override
    public long getNextNumber(SessionDto sessionUser, String key) {
        long anValue = 0

        Transaction tm = datastore.beginTransaction()

        try {
            anValue = this.doNextNumber(sessionUser, key)
            tm.commit()
        }
        catch (Throwable t) {
            tm.rollback()
            throw t
        }

        return anValue
    }

    private long doNextNumber(SessionDto sessionUser, String key) {
        AutoNumber an = AutoNumber.get(key)

        if (an) {
            an.preUpdate(sessionUser.id)
        } else {
            an = new AutoNumber()
            an.id = key
            an.prePersist(sessionUser.id)
        }
        //log.info an.toString()
        an.value += 1
        an.save()
        //log.info an.toString()

        return an.value
    }
}
