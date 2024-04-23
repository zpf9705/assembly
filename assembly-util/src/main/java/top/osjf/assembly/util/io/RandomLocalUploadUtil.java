package top.osjf.assembly.util.io;

import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.RandomStringUtils;
import top.osjf.assembly.util.encode.DigestUtils;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.assembly.util.system.SystemUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Upload the specified file to a random folder in the specified folder, and all
 * specified files can be configured. By default, the random storage folder is
 * selected from {@link #DEFAULT_RANDOM_CHARS}. After the specified file is uploaded,
 * the formatted path is returned according to the restful style.
 * Users can configure static file searches based on the formatted path.
 *
 * <p>Specify the upload folder to view {@link #setUploadPath(String)}.
 *
 * <p>Specify the character set for the name of a random folder,
 * as shown in method {@link #setRandomUploadFileChars(String)}.
 *
 * <p>The method for generating random ID digits when distinguishing
 * the same file can be found in method {@link #setPreventDuplicateIDLength(int)}.
 *
 * <p>Including extended support for certain types of images, please refer
 * to {@link #isSupportImage(InputStream)}.
 *
 * <p>The usage method can be observed as follows:
 *
 * <pre>
 *         RandomLocalUploadUtil.setUploadPath("/Users/joe/workspace/example");
 *
 *         BytesImage bytesImage = new BytesImage();
 *
 *         String upload = RandomLocalUploadUtil.uploadFileToLocal(bytesImage.getInputStream(),
 *         bytesImage.getOriginalFilename(),"<a href="https://example.com">example access domain</a>");
 *
 *         System.out.println(upload);
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see RandomStringUtils
 * @since 1.1.2
 */
public final class RandomLocalUploadUtil {

    /*** Default random write file character set*/
    private static final String DEFAULT_RANDOM_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * The system-dependent default name-separator character, represented as a
     * string for convenience.  This string contains a single character, namely
     * <p>copy form {@link java.io.File}</p>
     */
    private static final String systemFileSeparator = File.separator;

    /*** The default relative name.*/
    private static final String DEFAULT_RELATIVE_NAME = "image";

    /*** The default file upload address.*/
    private static final String DEFAULT_UPLOAD_PATH =
            SystemUtils.getCurrentProjectPath() + systemFileSeparator + DEFAULT_RELATIVE_NAME;

    /*** Connection point.*/
    private static final String SPOT = ".";

    /*** Random write file character set*/
    private static String randomUploadFileChars = DEFAULT_RANDOM_CHARS;

    /*** To prevent duplicate ID lengths.*/
    private static int preventDuplicateIDLength = 11;

    /*** Storage prefix name.*/
    private static String relativeName = DEFAULT_RELATIVE_NAME;

    /*** File upload address.*/
    private static String uploadPath = DEFAULT_UPLOAD_PATH;

    /*** This class cannot be instantiated.*/
    private RandomLocalUploadUtil() {
    }

    /**
     * Set the file upload address.
     *
     * @param uploadPath the file upload address.
     */
    public static void setUploadPath(String uploadPath) {
        if (StringUtils.isNotBlank(uploadPath)) {
            RandomLocalUploadUtil.uploadPath = uploadPath;
            relativeName = uploadPath.substring(uploadPath.lastIndexOf(systemFileSeparator) + 1);
        }
    }

    /**
     * Set the character set for randomly writing file names.
     *
     * @param randomUploadFileChars the character set for randomly writing file names
     */
    public static void setRandomUploadFileChars(String randomUploadFileChars) {
        if (StringUtils.isNotBlank(randomUploadFileChars)) {
            RandomLocalUploadUtil.randomUploadFileChars = randomUploadFileChars;
        }
    }

    /**
     * Set prevent duplicate ID lengths.
     *
     * @param preventDuplicateIDLength prevent duplicate ID lengths
     */
    public static void setPreventDuplicateIDLength(int preventDuplicateIDLength) {
        if (preventDuplicateIDLength != 0) {
            RandomLocalUploadUtil.preventDuplicateIDLength = preventDuplicateIDLength;
        }
    }

    /**
     * Determine if the current incoming file is an image.
     * <p>Use the reading function of {@link ImageIO} to distinguish,
     * specifically supporting the following image types:<br>
     * <ul>
     *     <li>{@link com.sun.imageio.plugins.bmp.BMPImageReader}BMP</li>
     *     <li>{@link com.sun.imageio.plugins.gif.GIFImageReader}GIF</li>
     *     <li>{@link com.sun.imageio.plugins.jpeg.JPEGImageReader}JPEG</li>
     *     <li>{@link com.sun.imageio.plugins.png.PNGImageReader}PNG</li>
     *     <li>{@link com.sun.imageio.plugins.wbmp.WBMPImageReader}WBMP</li>
     * </ul>
     * <p>When there is an error in the type or format mentioned above, an
     * exception will be read. Therefore, after reading the exception, it is
     * determined that the wrong image type is not the result of an image at all.
     *
     * @param stream Transfer file stream.
     * @return If {@code true} prove that it is the image type supported above, otherwise not.
     */
    public static boolean isSupportImage(InputStream stream) {
        BufferedImage read;
        try {
            read = ImageIO.read(stream);
        } catch (Exception ignored) {
            read = null;
        }
        return Objects.nonNull(read);
    }

    /**
     * Encode the original file name twice, add a unique ID to prevent
     * duplication, and use MD5 original name encoding to prevent Chinese
     * garbled characters.
     *
     * @param originalFilename Original file name.
     * @return Encoded file name.
     * @see #md5EncodeFileName(String)
     */
    public static String encodeFileName(String originalFilename) {
        return IdUtil.nanoId(preventDuplicateIDLength) + SPOT + md5EncodeFileName(originalFilename);
    }

    /**
     * To prevent some encoding issues with Chinese file names, MD5
     * encoding is uniformly applied to the file name prefix here.
     *
     * @param originalFilename Original file name.
     * @return Encoded file name.
     */
    public static String md5EncodeFileName(String originalFilename) {

        if (StringUtils.isBlank(originalFilename)) return originalFilename;

        //Separate the prefix name and suffix file extension.
        String[] qps = originalFilename.split("\\.");

        int length = qps.length;
        //The required items are the name prefix plus the file extension.
        //example for 111.png
        if (length < 2) {
            throw new IllegalArgumentException("Incorrect file name format.");
        }
        int extensionNameIndex = length - 1;
        //If multiple points are split, the last digit is the extension,
        // and the array toString is directly encoded before it.
        String prefix = Arrays.toString(ArrayUtils.remove(qps, extensionNameIndex));

        return DigestUtils.md5Hex(prefix) + SPOT + qps[extensionNameIndex];
    }

    /**
     * Based on the set of variable parameters passed in, return the
     * formatted file echo URL path.
     *
     * @param args Format parameters.
     * @return the formatted file echo URL path.
     */
    public static String formatUrl(String... args) {
        if (ArrayUtils.isEmpty(args)) {
            throw new NullPointerException("Format args not be null");
        }
        return String.join("", args).replaceAll("\\\\", systemFileSeparator);
    }

    /**
     * Return the name of the random storage folder.
     *
     * @return the name of the random storage folder.
     * @see RandomStringUtils
     */
    public static String getRandomSaveFolderName() {
        return RandomStringUtils.random(1, randomUploadFileChars);
    }

    /**
     * Return the random storage folder address, and create it if it does not exist.
     *
     * @param randomSaveFolderName Randomly store file names.
     * @return the random storage folder address
     */
    public static String getRandomSavePath(String randomSaveFolderName) {
        if (StringUtils.isBlank(randomSaveFolderName)) randomSaveFolderName = getRandomSaveFolderName();
        String randomSavePath = whetherEndWithSeparator(uploadPath) + randomSaveFolderName;
        if (!FileManager.exist(randomSavePath)) {
            FileManager.mkdir(randomSavePath);
        }
        return randomSavePath;
    }

    /**
     * Return the random storage folder address, and create it if it does not exist.
     *
     * @param pathV The path to be checked.
     * @return the random storage folder address
     */
    public static String whetherEndWithSeparator(String pathV) {
        if (StringUtils.isBlank(pathV)) return pathV;
        return pathV.endsWith(systemFileSeparator) ? pathV : pathV + systemFileSeparator;
    }

    /**
     * Write the referred file to the specified folder at this level.
     *
     * @param stream           File stream.
     * @param originalFilename The original name of the file.
     * @param accessDomainName Accessing domain names is generally used for static lookup.
     * @return File echo url address.
     * @throws IOException Possible exceptions that may arise from file writing.
     */
    public static String uploadFileToLocal(InputStream stream, String originalFilename, String accessDomainName)
            throws IOException {

        //Get the random storage file name.
        String randomSaveFolderName = getRandomSaveFolderName();

        //Obtain the random  storage address.
        String randomSavePath = getRandomSavePath(randomSaveFolderName);

        //Encode the file name.
        String fileName = encodeFileName(originalFilename);

        //Get the real storage full name.
        String realPth = whetherEndWithSeparator(randomSavePath) + fileName;

        //Create a file.
        File ifFile = new File(realPth);

        if (ifFile.exists()) {
            return null;
        }

        //Write to file.
        try (FileOutputStream out = new FileOutputStream(realPth)) {
            out.write(IoUtils.readBytes(stream));
            out.flush();
        }

        //Return formatted access address.
        return formatUrl(accessDomainName, systemFileSeparator, relativeName, systemFileSeparator,
                randomSaveFolderName, systemFileSeparator, fileName);

    }
}
