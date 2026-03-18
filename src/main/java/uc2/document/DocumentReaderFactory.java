package uc2.document;

import java.nio.file.Path;
import java.util.List;

public final class DocumentReaderFactory {
    private final List<DocumentReader> readers;

    public DocumentReaderFactory(List<DocumentReader> readers) {
        this.readers = List.copyOf(readers);
    }

    public DocumentReader readerFor(Path path) {
        return readers.stream()
            .filter(reader -> reader.supports(path))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unsupported file format. Please use txt, md, docx, or pdf."
            ));
    }
}
