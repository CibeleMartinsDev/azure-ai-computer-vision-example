package org.martins.cibele.utils;

import com.azure.core.util.BinaryData;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.nio.file.Path;

public class FileUploadUtils {

    public static BinaryData getBinaryData(FileUpload file){
        Path pathFile = file.filePath();
        return BinaryData.fromFile(pathFile);
    }
}
