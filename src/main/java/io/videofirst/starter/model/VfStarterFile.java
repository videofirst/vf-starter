package io.videofirst.starter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import freemarker.template.Template;
import lombok.Data;

@Data
public class VfStarterFile {

    private String id;          // This is the filename is `filename` isn't set
    private String filename;

    private String raw;         // raw content (no templating support)
    private String file;    // template content (using Freemarker)
    private String base64;      // binary file which is base64 encoded
    private String multi;       // template for creating multiple files
    private boolean executable; // executable script

    // Internal

    @JsonIgnore
    private byte[] binary;      // raw binary contents

    @JsonIgnore
    private Template templateFilename;

    @JsonIgnore
    private Template templateContents;

}
