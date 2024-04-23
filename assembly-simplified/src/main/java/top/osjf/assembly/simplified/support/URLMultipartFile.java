package top.osjf.assembly.simplified.support;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.ContentType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The {@link MultipartFile} implementation class in URL format,
 * which implements reading remote files.
 * @see MultipartFile
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.1.9
 */
public class URLMultipartFile implements MultipartFile {

    /*** Remote file address.*/
    private final String url;

    /*** The content of the file.*/
    private final byte[] content;

    /**
     * Create a new {@code URLMultipartFile} with the given url.
     * @param url The remote address of the file.
     */
    public URLMultipartFile(String url) {
        this(url, null);
    }

    /**
     * Create a new {@code URLMultipartFile} with the given url and content.
     * @param url     The remote address of the file.
     * @param content the content of the file.
     */
    public URLMultipartFile(String url, byte[] content) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url must no be null");
        }
        this.url = url;
        this.content = ArrayUtil.isEmpty(content) ? getUrlBytes(url) : content;
    }

    /**
     * Return the data stream content of the remote file.
     * @param url The remote address of the file.
     * @return the data stream content of the remote file.
     */
    private byte[] getUrlBytes(@NonNull String url) {
        try {
            return FileCopyUtils.copyToByteArray(new URL(url).openStream());
        } catch (IOException e) {
            throw new IllegalArgumentException("Get bytes with" + url + "filed");
        }
    }

    /**
     * Return the original file name of the remote file address.
     * @param url The remote address of the file.
     * @return the original file name of the remote file address.
     */
    private static String getUrlOriginalFilename(@NonNull String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    @NonNull
    @Override
    public String getName() {
        return this.url;
    }

    @Override
    public String getOriginalFilename() {
        return getUrlOriginalFilename(this.url);
    }

    @Override
    public String getContentType() {
        return ContentType.MULTIPART.toString();
    }

    @Override
    public boolean isEmpty() {
        return (this.content.length == 0);
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @NonNull
    @Override
    public byte[] getBytes() {
        return this.content;
    }

    @NonNull
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(@NonNull File file) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, file);
    }
}
