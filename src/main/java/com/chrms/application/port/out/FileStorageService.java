package com.chrms.application.port.out;

import java.io.InputStream;

public interface FileStorageService {
    String store(InputStream inputStream, String fileName, String contentType);
    byte[] load(String filePath);
    void delete(String filePath);
    boolean exists(String filePath);
}
