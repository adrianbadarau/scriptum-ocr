package com.adrianb.scriptum.ocr.web.controller

import com.asprise.ocr.Ocr
import net.sourceforge.tess4j.Tesseract
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.util.ResourceUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

@Controller
@RequestMapping(path = ["/scanner"])
class OcrScanner {

    @Value(value = "\${app.upload-dir:\${user.home}}")
    lateinit var uploadDir: String
    @Value("\${app.tessdata-dir}")
    lateinit var tessdataDir: String

    @PostMapping(path = ["upload-file"])
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val bytes = file.bytes
        val path = Paths.get(uploadDir + file.originalFilename)
        Files.write(path, bytes)
        val converted = convert(file)
        val tesseract = Tesseract()
        tesseract.setDatapath(tessdataDir)
        val text = tesseract.doOCR(converted)
//        Ocr.setUp()
//        val ocr = Ocr()
//        ocr.startEngine(Ocr.LANGUAGE_ENG, Ocr.SPEED_SLOW)
//        val text = ocr.recognize(arrayOf(converted), Ocr.RECOGNIZE_TYPE_TEXT, Ocr.OUTPUT_FORMAT_PLAINTEXT)
        return ResponseEntity.ok(text)
    }

    private fun convert(file: MultipartFile): File {
        val converted = File(file.originalFilename)
        converted.createNewFile()
        val fos = FileOutputStream(converted)
        fos.write(file.bytes)
        fos.close()
        return converted
    }
}
