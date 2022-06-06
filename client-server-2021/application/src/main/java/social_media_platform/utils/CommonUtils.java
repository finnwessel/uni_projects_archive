package social_media_platform.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

public class CommonUtils {
    /**
     * Generates an account verification code for eMail verification
     *
     * @return the account verification code
     */
    public static String generateCode() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    /**
     * Loads mail template from resources and returns it as a string
     *
     * @param file the resource
     * @return the email template as a string
     */
    public static String getMailTemplate(String file) {
        String path = "mail_templates/";
        ClassLoader classLoader = CommonUtils.class.getClassLoader();
        URI uri = null;
        try {
            uri = Objects.requireNonNull(classLoader.getResource(path.concat(file))).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String url = String.valueOf(uri).substring(6).replaceAll("%20", " ");
        File template = new File(url);
        String templateAsString = null;
        try {
            templateAsString = FileUtils.readFileToString(template, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return templateAsString;
    }
}
