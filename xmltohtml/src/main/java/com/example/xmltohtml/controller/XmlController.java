package com.example.xmltohtml.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/convert")
public class XmlController {

    @PostMapping(value = "/xml-to-html", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> convertXmlUsingXsl(@RequestParam("file") MultipartFile xmlFile) throws Exception {
        // Read XML content
        String xmlContent = new String(xmlFile.getBytes(), StandardCharsets.UTF_8);

        // Extract XSL filename from xml-stylesheet directive
        String xslFileName = extractXslFileName(xmlContent);
        if (xslFileName == null) {
            throw new IllegalArgumentException("No <?xml-stylesheet ...?> found in the XML file.");
        }

        // Load XSL from resources
        ClassPathResource xsltResource = new ClassPathResource(xslFileName);
        if (!xsltResource.exists()) {
            throw new FileNotFoundException("XSL file not found in resources: " + xslFileName);
        }
        Source xsltSource = new StreamSource(xsltResource.getInputStream());

        // Setup custom XMLReader for handling DTD
        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setEntityResolver((publicId, systemId) -> {
            if (systemId != null && systemId.contains("bhashya.dtd")) {
                return new InputSource(new ClassPathResource("bhashya.dtd").getInputStream());
            }
            return null;
        });

        // Use SAXSource to supply XML + reader
        InputSource inputSource = new InputSource(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
        Source xmlSource = new SAXSource(reader, inputSource);

        // Perform transformation
        Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
        StringWriter htmlWriter = new StringWriter();
        transformer.transform(xmlSource, new StreamResult(htmlWriter));
        String htmlContent = htmlWriter.toString();

        // Prepare zip output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            // index.html
            zos.putNextEntry(new ZipEntry("index.html"));
            zos.write(htmlContent.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            // style.css (from resources or fallback)
            try {
                ClassPathResource cssResource = new ClassPathResource("style.css");
                InputStream cssStream = cssResource.getInputStream();
                zos.putNextEntry(new ZipEntry("style.css"));
                zos.write(cssStream.readAllBytes());
                zos.closeEntry();
            } catch (IOException e) {
                zos.putNextEntry(new ZipEntry("style.css"));
                zos.write("/* default styles */\nbody { font-family: serif; }".getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
        }

        // Return ZIP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "converted-html.zip");
        return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
    }

    private String extractXslFileName(String xmlContent) {
        Pattern pattern = Pattern.compile("<\\?xml-stylesheet[^>]*href\\s*=\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(xmlContent);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}
