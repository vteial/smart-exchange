import io.wybis.smartexchange.util.Helper

Map<String, String> responseDto = ['ping': 'Ping Pong!']

if (params.dump) {
    boolean dump = false
    if (params.dump == 'true') {
        dump = true
    }
    if (params.dump == 'false') {
        dump = false
    }
    application[Helper.DUMP_REQUEST_RESPONSE_KEY] = dump;
}

jsonCategory.respondWithJson(response, responseDto)

