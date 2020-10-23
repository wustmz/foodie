package org.foodie.resource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author wustmz
 */
@Component
@ConfigurationProperties(prefix = "file")
@PropertySource("classpath:file-upload-dev.properties")
@Data
public class FileUpload {

    private String imageUserFaceLocation;
    private String imageServerUrl;

}
