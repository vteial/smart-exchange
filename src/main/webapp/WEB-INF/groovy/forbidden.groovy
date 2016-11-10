import io.wybis.smartexchange.dto.ResponseDto

ResponseDto responseDto = new ResponseDto()

responseDto.type = ResponseDto.FORBIDDEN
responseDto.message = 'Forbidden! You have login to access this resource...'

jsonCategory.respondWithJson(response, responseDto)
