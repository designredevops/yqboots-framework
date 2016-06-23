package com.yqboots.prototype.project.core.builder;

import com.yqboots.prototype.project.core.ProjectContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Administrator on 2016-06-11.
 */
public class FileBuilderImpl implements FileBuilder {
    private final String template;

    private final String path;

    public FileBuilderImpl(final String template, final String path) {
        this.template = template;
        this.path = path;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public Path getFile(final Path root, final ProjectContext context) throws IOException {
        Path result = Paths.get(root.toAbsolutePath() + getPath());
        if (Files.exists(result)) {
            result.toFile().createNewFile();
        } else {
            Files.createDirectories(result.getParent());
            Files.createFile(result);
        }

        return result;
    }

    public String getPath() {
        return path;
    }
}