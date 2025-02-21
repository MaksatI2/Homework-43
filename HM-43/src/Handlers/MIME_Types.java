package Handlers;

import java.util.HashMap;
import java.util.Map;

public class MIME_Types {
    private static final Map<String, String> MIME_TYPES = initMimeTypes();

    private static Map<String, String> initMimeTypes() {
        Map<String, String> mimeTypes = new HashMap<>();
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpeg", "image/jpg");
        return mimeTypes;
    }

    public static String getMimeType(String extension) {
        return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
    }
}