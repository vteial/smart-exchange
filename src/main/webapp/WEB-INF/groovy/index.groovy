import eu.bitwalker.useragentutils.DeviceType
import eu.bitwalker.useragentutils.UserAgent
import io.wybis.smartexchange.dto.SessionDto
import io.wybis.smartexchange.service.SessionService
import io.wybis.smartexchange.util.Helper

UserAgent userAgent = UserAgent.parseUserAgentString(headers['User-Agent'])

SessionDto sessionDto = session[SessionService.SESSION_USER_KEY]

String path = ''

if (sessionDto == null) {
//    if(userAgent.operatingSystem.deviceType == DeviceType.COMPUTER || userAgent.operatingSystem.deviceType == DeviceType.DMR) {
//        path = '/index-small.html'
//    } else {
//        path = '/index-s.html'
//    }
    path = '/index.html'
} else {
//    if(userAgent.operatingSystem.deviceType == DeviceType.COMPUTER || userAgent.operatingSystem.deviceType == DeviceType.DMR) {
//        path = '/home-small.html'
//    } else {
//        path = '/home-s.html'
//    }
    path = 'home.html'
}

//application.setAttribute(Helper.DUMP_REQUEST_RESPONSE_KEY, true)

redirect path
