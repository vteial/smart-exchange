package io.wybis.smartexchange.web.session

import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter

import io.wybis.smartexchange.dto.ResponseDto
import io.wybis.smartexchange.util.Helper

try {

    response.setContentType("text/csv");
    response.setHeader('Content-Disposition', "attachment;filename=current-state.pdf");

    Document document = new Document();
    PdfWriter.getInstance(document, response.getOutputStream());
    document.open();
    document.add(new Paragraph("Welcome to Smart Exchange!"));
    document.close();

    return
}
catch (Throwable t) {
    t.printStackTrace()

//    if(localMode) {
//        t.printStackTrace()
//    }
//    ResponseDto responseDto = new ResponseDto()
//    responseDto.type = ResponseDto.UNKNOWN
//    responseDto.message = 'Current state report failed...'
//    responseDto.data = Helper.getStackTraceAsString(t)
//    log.warning(responseDto.message)
//
//    jsonCategory.respondWithJson(response, responseDto)
}

