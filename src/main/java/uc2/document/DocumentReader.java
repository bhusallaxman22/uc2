package uc2.document;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentReader {
    boolean supports(Path path);

    String read(Path path) throws IOException;
}
