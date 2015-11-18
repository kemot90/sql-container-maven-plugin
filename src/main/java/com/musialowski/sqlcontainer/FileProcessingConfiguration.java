package com.musialowski.sqlcontainer;

import com.musialowski.sqlcontainer.enums.StorageMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;

/**
 * @author Tomasz Musiałowski <tomasz.musialowski@generali.pl>
 */
@Builder
@Getter
public class FileProcessingConfiguration {

    @NonNull
    private File outputDirectory;

    @NonNull
    private String packageName;

    @NonNull
    private StorageMethod storageMethod;
}
