package uc2.app;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import uc2.controller.BusinessRuleController;
import uc2.document.DocxDocumentReader;
import uc2.document.PdfDocumentReader;
import uc2.domain.RequestProcessingResult;
import uc2.domain.RuleUpdateResult;

public final class SelfTest {
    private SelfTest() {
    }

    public static void main(String[] args) throws Exception {
        BusinessRuleController controller = ApplicationFactory.createController();

        RuleUpdateResult manualUpdate = controller.updateManually("""
            customer.occupation == "student" && order.totalAmount >= 500 -> give 10% discount
            order.totalAmount >= 1000 -> grant free shipping
            """);
        assertTrue(manualUpdate.rules().size() == 2, "Manual update should create two rules.");

        RequestProcessingResult requestResult = controller.processRequest("""
            customer.occupation=student
            order.totalAmount=1200
            """);
        assertTrue(requestResult.actions().contains("give 10% discount"), "Discount action should fire.");
        assertTrue(requestResult.actions().contains("grant free shipping"), "Shipping action should fire.");

        Path textRules = Files.createTempFile("uc2-rules", ".txt");
        Files.writeString(
            textRules,
            "If customer occupation is student and order total amount is at least 500, then give 10% discount."
        );
        RuleUpdateResult aiUpdate = controller.updateWithDocument(textRules);
        assertTrue(aiUpdate.rules().size() == 1, "AI update should extract one rule from text.");

        Path docxFile = createDocx("""
            If order total amount is greater than 1000, then grant free shipping.
            """);
        String docxText = new DocxDocumentReader().read(docxFile);
        assertTrue(docxText.toLowerCase().contains("free shipping"), "DOCX reader should extract text.");

        Path assignmentPdf = Path.of("UC2-Edit Business Rules-1.pdf");
        if (Files.exists(assignmentPdf)) {
            String pdfText = new PdfDocumentReader().read(assignmentPdf);
            assertTrue(pdfText.toLowerCase().contains("software design patterns"), "PDF reader should extract visible text.");
        }

        System.out.println("Self-test passed.");
    }

    private static Path createDocx(String documentText) throws IOException {
        Path file = Files.createTempFile("uc2-rules", ".docx");
        try (OutputStream outputStream = Files.newOutputStream(file);
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            writeEntry(zipOutputStream, "[Content_Types].xml", """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                  <Default Extension="xml" ContentType="application/xml"/>
                  <Override PartName="/word/document.xml"
                    ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
                </Types>
                """);
            writeEntry(zipOutputStream, "_rels/.rels", """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                  <Relationship Id="rId1"
                    Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument"
                    Target="word/document.xml"/>
                </Relationships>
                """);
            writeEntry(zipOutputStream, "word/document.xml", """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                  <w:body>
                    <w:p><w:r><w:t>%s</w:t></w:r></w:p>
                  </w:body>
                </w:document>
                """.formatted(documentText.trim()));
        }
        return file;
    }

    private static void writeEntry(ZipOutputStream stream, String name, String content) throws IOException {
        stream.putNextEntry(new ZipEntry(name));
        stream.write(content.getBytes(StandardCharsets.UTF_8));
        stream.closeEntry();
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
