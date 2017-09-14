package xyz.vopen.cartier.cdn.ks3;

import com.alibaba.fastjson.JSON;
import com.ksyun.ks3.AutoAbortInputStream;
import com.ksyun.ks3.dto.Bucket;
import com.ksyun.ks3.dto.CannedAccessControlList;
import com.ksyun.ks3.dto.CompleteMultipartUploadResult;
import com.ksyun.ks3.dto.CreateBucketConfiguration;
import com.ksyun.ks3.dto.DeleteMultipleObjectsResult;
import com.ksyun.ks3.dto.GetObjectResult;
import com.ksyun.ks3.dto.HeadBucketResult;
import com.ksyun.ks3.dto.HeadObjectResult;
import com.ksyun.ks3.dto.InitiateMultipartUploadResult;
import com.ksyun.ks3.dto.Ks3Object;
import com.ksyun.ks3.dto.Ks3ObjectSummary;
import com.ksyun.ks3.dto.ObjectListing;
import com.ksyun.ks3.dto.ObjectMetadata;
import com.ksyun.ks3.dto.PartETag;
import com.ksyun.ks3.dto.PutObjectResult;
import com.ksyun.ks3.dto.ResponseHeaderOverrides;
import com.ksyun.ks3.exception.serviceside.NotFoundException;
import com.ksyun.ks3.http.HttpClientConfig;
import com.ksyun.ks3.service.Ks3;
import com.ksyun.ks3.service.Ks3Client;
import com.ksyun.ks3.service.Ks3ClientConfig;
import com.ksyun.ks3.service.request.CompleteMultipartUploadRequest;
import com.ksyun.ks3.service.request.CreateBucketRequest;
import com.ksyun.ks3.service.request.GetObjectRequest;
import com.ksyun.ks3.service.request.HeadObjectRequest;
import com.ksyun.ks3.service.request.InitiateMultipartUploadRequest;
import com.ksyun.ks3.service.request.ListObjectsRequest;
import com.ksyun.ks3.service.request.PutObjectRequest;
import com.ksyun.ks3.service.request.UploadPartRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.cartier.cdn.BaseConfig;
import xyz.vopen.cartier.cdn.CDNHandler;
import xyz.vopen.cartier.cdn.exception.BucketException;
import xyz.vopen.cartier.cdn.exception.CDNException;
import xyz.vopen.cartier.cdn.exception.ObjectException;
import xyz.vopen.cartier.commons.utils.FileUtilx;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * xyz.vopen.cartier.cdn.ks3
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 20/07/2017.
 */
public class Ks3Handler implements CDNHandler {

    private Ks3 client;
    private static Logger logger = LoggerFactory.getLogger(Ks3Handler.class);

    // 100M -> 100 * 1024 KB * 1024 B
    private static long BLOCK_LENGTH = 100 * 1024 * 1024;

    // min download block size 200M
    private static long MIN_DOWNLOAD_FILE_LENGTH = 200 * 1024 * 1024;
    private static long MIN_UPLOAD_FILE_LENGTH = 100 * 1024 * 1024;

    public static final String DEFAULT_ENDPOINT = "ks3-cn-beijing.ksyun.com";
    private Ks3Config ks3Config;
    private Ks3ClientConfig config;

    /**
     * init
     */
    @Override
    public void initialize (BaseConfig _config) {
        logger.info(" @初始化Ks3");
        if (_config == null) {
            throw new RuntimeException("Ks3 configuration instance is must not be null!");
        }

        if (_config.getConfigProperties() == null) {
            throw new RuntimeException("Ks3 configuration Properties must not be null!");
        }

        if (_config instanceof Ks3Config) {
            ks3Config = (Ks3Config) _config;
        }

        BLOCK_LENGTH = ks3Config.blockLength();
        MIN_DOWNLOAD_FILE_LENGTH = ks3Config.minDownloadFileLength();
        MIN_UPLOAD_FILE_LENGTH = ks3Config.minUploadFileLength();

        config = new Ks3ClientConfig();

        /*
         * 设置服务地址
         * 中国（北京）| ks3-cn-beijing.ksyun.com
         * 中国（上海）| ks3-cn-shanghai.ksyun.com
         * 中国（香港）| ks3-cn-hk-1.ksyun.com
         * 如果使用自定义域名，设置endpoint为自定义域名，同时设置domainMode为true
         */
        config.setEndpoint(DEFAULT_ENDPOINT);   //此处以北京region为例

        /*
         *true：表示以自定义域名访问
         *false：表示以KS3的外网域名或内网域名访问，默认为false
         */
        config.setDomainMode(false);
        config.setProtocol(Ks3ClientConfig.PROTOCOL.https);

        /*
         *true表示以   endpoint/{bucket}/{key}的方式访问
         *false表示以  {bucket}.endpoint/{key}的方式访问
         */
        config.setPathStyleAccess(true);

        HttpClientConfig hconfig = new HttpClientConfig();
        //在HttpClientConfig中可以设置httpclient的相关属性，比如代理，超时，重试等。
        config.setHttpClientConfig(hconfig);

        client = new Ks3Client(ks3Config.getAccessKeyID(), ks3Config.getAccessKeySecret(), config);

    }

    /**
     * release resources
     */
    @Override
    public void destory () {
        // todo
    }

    /**
     * upload file
     *
     * @param filePath
     *         local file path
     * @param bucket
     *         bucket name
     * @param relativePathKey
     *         bucket file key
     */
    @Override
    public void upload (String filePath, String bucket, String relativePathKey) throws BucketException, ObjectException, CDNException {

        logger.info(" @Normal file upload :{} ,Bucket :{} , Key :{}", filePath, bucket, relativePathKey);
        if (filePath == null || filePath.trim().length() == 0) {
            throw new ObjectException("source file param is invalid");
        }

        if (bucket == null || bucket.trim().length() == 0) {
            throw new ObjectException("target bucket param is invalid");
        }

        if (relativePathKey == null || relativePathKey.trim().length() == 0) {
            throw new ObjectException("target bucket file key is invalid");
        }

        try {

            if (client != null) {

                if (ks3Config != null && ks3Config.isAutoCreateBucket()) {
                    checkCreateBucket(bucket);
                } else if (ks3Config != null) {
                    throw new CDNException("Target Bucket [" + bucket + "] is not exist .");
                }

                ObjectMetadata meta = new ObjectMetadata();
                File file = new File(filePath);
//                String md5 = DigestUtils.md5Hex(new FileInputStream(file));
                String md5 = FileUtilx.md5(filePath);
                meta.setUserMeta("x-kss-meta-md5", md5);
                meta.setContentLength(file.length());
                logger.info(" @file MD5: {}", md5);

                FileInputStream inputStream = new FileInputStream(file);
                PutObjectRequest request = new PutObjectRequest(bucket, relativePathKey, inputStream, meta);
                // set file read public
                request.setCannedAcl(CannedAccessControlList.PublicRead);

                PutObjectResult putObjectResult = client.putObject(request);
                logger.info(" @file upload finished ,Response is :{}", putObjectResult);

            } else {
                throw new CDNException("Must need init ks3 client first ,Method :handler.initialize(config) ;");
            }
        } catch (Exception e) {
            throw new ObjectException("File upload error .", e);
        }
    }

    private void checkCreateBucket (String bucket) throws BucketException, CDNException {
        int code = checkBucket(bucket);
        if (code != 0) {
            if (code == 404) {
                createBucket(bucket);
            }
            if (code == 403) {
                throw new BucketException("no permission");
            }
        }
    }

    @Override
    public void superUpload (String filePath, final String bucket, final String relativePathKey) throws BucketException, ObjectException, CDNException {

        String uploadId = null;
        try {

            // check first
            if (filePath == null || filePath.trim().length() == 0) {
                throw new ObjectException("source file param is invalid");
            }

            if (bucket == null || bucket.trim().length() == 0) {
                throw new ObjectException("target bucket param is invalid");
            }

            if (relativePathKey == null || relativePathKey.trim().length() == 0) {
                throw new ObjectException("target bucket file key is invalid");
            }

            if (!Files.exists(Paths.get(filePath))) {
                throw new ObjectException("Source file is not found on disk.");
            }

            byte[] sourceBytes = IOUtils.toByteArray(Paths.get(filePath).toUri());
            long length = sourceBytes.length;
            if (length < MIN_UPLOAD_FILE_LENGTH) {
                upload(filePath, bucket, relativePathKey);
                return;
            }

            logger.info(" @Super File Upload :{} ,Bucket :{} , Key :{}", filePath, bucket, relativePathKey);

            // check bucket
            if (ks3Config.isAutoCreateBucket()) {
                checkCreateBucket(bucket);
            } else {
                throw new CDNException("Target Bucket [" + bucket + "] is not exist .");
            }

            int blocks = (int) Math.ceil(length * 1.0 / BLOCK_LENGTH);
            logger.info(" @block size :{}", blocks);
            final long[][] range = new long[blocks][];
            long start = 0, end = 0;
            for (int i = 1; i <= blocks; i++) {
                start = (i - 1) * BLOCK_LENGTH;
                end = i * BLOCK_LENGTH;
                if (end >= length) {
                    end = length;
                }
                //
                range[i - 1] = new long[]{ start, end };
            }

            logger.info(" @Block's info :{}", Arrays.toString(range));

            Map<Integer, byte[]> byteParts = new HashMap<>();
            for (int i = 0; i < blocks; i++) {
                long[] temp = range[i];
                long _start = temp[0];
                long _end = temp[1];

                byte[] tempArray = new byte[(int) (_end - _start)];
                System.arraycopy(sourceBytes, (int) _start, tempArray, 0, (int) (_end - _start));
                byteParts.put(i + 1, tempArray);
                logger.info(" @block byte ,index: {} , length : {}", i + 1, tempArray.length);
            }

            InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucket, relativePathKey);
            initiateMultipartUploadRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            ObjectMetadata meta = new ObjectMetadata();
            String md5 = FileUtilx.md5(filePath);
            meta.setUserMeta("x-kss-meta-md5", md5);

            logger.info(" @File MD5: {}", md5);

            initiateMultipartUploadRequest.setObjectMeta(meta);

            InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
            uploadId = result.getUploadId();

            logger.info(" @file blocks init finished ,UploadId is :{}", uploadId);

            final List<PartETag> parts = new ArrayList<PartETag>();
            final Semaphore semaphore = new java.util.concurrent.Semaphore(blocks);

            int index = 0;
            // for
            for (Map.Entry<Integer, byte[]> entry : byteParts.entrySet()) {
                final int partNumber = entry.getKey();
                final byte[] partContentByte = entry.getValue();
                final long partSize = partContentByte.length;

                semaphore.acquire(1);
                final String finalUploadId = uploadId;
                new Thread(new Runnable() {
                    @Override
                    public void run () {
                        try {
                            // ============ START UPLOAD BLOCK ==========
//                            String md5 = DigestUtils.md5Hex(partContentByte);
                            InputStream content = new ByteArrayInputStream(partContentByte);
                            UploadPartRequest request = new UploadPartRequest(bucket, relativePathKey, finalUploadId, partNumber, content, partSize);
//                            request.setContentMD5(md5);
                            logger.info(" @Block Index:{} ,start upload ,MD5: {}", partNumber, "has-no-md5");
                            PartETag partETag = client.uploadPart(request);
                            parts.add(partETag);

                            logger.info(" @Block Index:{} ,upload finished, Response is: {}", partNumber, partETag);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            semaphore.release(1);
                        }
                    }
                }).start();
            }

            semaphore.acquire(blocks);
            logger.info(" @All blocks upload finished ,ready to combine blocks...");
            if (parts.size() == blocks) {
                try {
                    // ============ UPLOAD BLOCK FINISHED ,COMBINE BLOCKS ==========
                    CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucket, relativePathKey, uploadId, parts);
                    CompleteMultipartUploadResult completeMultipartUploadResult = client.completeMultipartUpload(completeMultipartUploadRequest);
                    logger.info(" @Blocks combine finished ,Response :{}", completeMultipartUploadResult);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release(blocks);
                }

            } else {
                throw new RuntimeException("Blocks info error, Expect:" + blocks + " , Actually : " + parts.size());
            }

        } catch (Exception e) {
            if (uploadId != null)
                client.abortMultipartUpload(bucket, relativePathKey, uploadId);

            throw new ObjectException("File upload exception ", e);
        }
    }

    /**
     * download file
     *
     * @param bucket
     *         bucket name
     * @param relativePathKey
     *         bucket file key
     * @param destFilePath
     *         local target download file path
     */
    @Override
    public void download (String bucket, String relativePathKey, String destFilePath) throws BucketException, ObjectException, CDNException {

        try {

            if (client != null) {

                logger.info(" @Normal file download :{} ,Bucket :{} , Key :{}", destFilePath, bucket, relativePathKey);

                if (!checkFile(bucket, relativePathKey)) {
                    throw new ObjectException("Target file is not found on bucket.");
                }

                GetObjectRequest request = new GetObjectRequest(bucket, relativePathKey);
                ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
                overrides.setContentType("text/html");
                request.setOverrides(overrides);
                GetObjectResult result = client.getObject(request);
                logger.info(" @Target File Info :{}", result);
                Ks3Object object = result.getObject();
                ObjectMetadata meta = object.getObjectMetadata();
                String objectMd5 = meta.getUserMeta("x-kss-meta-md5");
                logger.info(" @Original File MD5: {}", objectMd5);
                AutoAbortInputStream inputStream = object.getObjectContent();
                if (inputStream != null) {
                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    FileUtils.writeByteArrayToFile(new File(destFilePath), bytes);
                    inputStream.close();
                    String downloadMd5 = DigestUtils.md5Hex(bytes);
                    if (!objectMd5.equalsIgnoreCase(downloadMd5)) {
                        throw new ObjectException("File is broken");
                    }

                    logger.info(" @File download finished .");
                }
                // EOF
            } else {
                throw new CDNException("Must need init ks3 client first ,Method :handler.initialize(config) ;");
            }
        } catch (Exception e) {
            throw new ObjectException("File download exception ", e);
        }

    }


    @Override
    public void superDownload (final String bucket, final String relativePathKey, String destFilePath) throws BucketException, ObjectException, CDNException {

        try {
            if (client != null) {
                if (!checkFile(bucket, relativePathKey)) {
                    throw new ObjectException("Target file is not found on bucket.");
                }
                HeadObjectRequest request = new HeadObjectRequest(bucket, relativePathKey);
                HeadObjectResult result = client.headObject(request);
                ObjectMetadata metadata = result.getObjectMetadata();
                String objectMd5 = metadata.getUserMeta("x-kss-meta-md5");
                logger.info(" @Original file MD5:{}", objectMd5);
                long length = metadata.getContentLength();
                if (length <= MIN_DOWNLOAD_FILE_LENGTH) {
                    download(bucket, relativePathKey, destFilePath);
                } else {
                    logger.info(" @Super file download :{} ,Bucket :{} , Key :{}", destFilePath, bucket, relativePathKey);
                    int blocks = (int) Math.ceil(length * 1.0 / BLOCK_LENGTH);
                    logger.info(" @Super download blocks size :{}", blocks);

                    final long[][] range = new long[blocks][];
                    long start = 0, end = 0;
                    for (int i = 1; i <= blocks; i++) {
                        start = (i - 1) * BLOCK_LENGTH;
                        end = i * BLOCK_LENGTH;
                        if (end > length) {
                            end = length;
                        }
                        range[i - 1] = new long[]{ start, end };
                    }

                    logger.info(" @Super download blocks info :{}", Arrays.toString(range));

                    final Map<Integer, File> blocksList2 = new HashMap<>();
                    final Semaphore semaphore = new java.util.concurrent.Semaphore(blocks);
                    final File tempDir = new File("/tmp");
                    for (int i = 0; i < blocks; i++) {
                        final int index = i;
                        final long tempStart = range[i][0];
                        final long tempEnd = range[i][1];
                        semaphore.acquire(1);
                        new Thread(new Runnable() {
                            @Override
                            public void run () {
                                try {
                                    GetObjectRequest request = new GetObjectRequest(bucket, relativePathKey);
                                    ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
                                    overrides.setContentType("text/html");
                                    request.setOverrides(overrides);
                                    request.setRange(tempStart, tempEnd);
                                    GetObjectResult result = client.getObject(request);
                                    Ks3Object object = result.getObject();
                                    AutoAbortInputStream inputStream = object.getObjectContent();
                                    if (inputStream != null) {
                                        String tempFilePrefix = "tx_" + relativePathKey.replaceAll("/", "_") + index;
                                        File tempFile = File.createTempFile(tempFilePrefix, ".tmp", tempDir);
                                        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                        blocksList2.put(index, tempFile);
                                    }

                                    logger.info(" @Block index:{} , download done .", index);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    semaphore.release(1);
                                }
                            }
                        }).start();
                    }

                    try {
                        // wait
                        semaphore.acquire(blocks);

                        if (blocksList2.size() != blocks) {
                            throw new ObjectException("File download exception ,file's block is exception ");
                        }

                        logger.info(" @Ready to combine blocks ,blocks's size:{}", blocksList2.size());
                        File destFile = new File(destFilePath);
                        if (!destFile.exists()) {
                            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr--");
                            FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(perms);
                            Files.createFile(Paths.get(destFilePath), fileAttributes);
                        }

                        WritableByteChannel channel = FileChannel.open(Paths.get(destFilePath), StandardOpenOption.WRITE, StandardOpenOption.READ);

                        // combine
                        for (int i = 0; i < blocks; i++) {
                            File tf = blocksList2.get(i);
                            FileChannel fileChannel = FileChannel.open(tf.toPath());
                            long expectTransSize = fileChannel.size();
                            long actuallyTransSize = fileChannel.transferTo(0, expectTransSize, channel);
                            logger.info(" @sub-files transform finish,actual size :{} ,transform size :{} ", expectTransSize, actuallyTransSize);
                        }

//                        String downloadMd5 = DigestUtils.md5Hex(new FileInputStream(destFilePath));
                        String downloadMd5 = FileUtilx.md5(destFilePath);

                        logger.info(" @Downloaded file's MD5 :{} ", downloadMd5);

                        if (objectMd5 != null && objectMd5.trim().length() > 0) {
                            if (!objectMd5.equalsIgnoreCase(downloadMd5)) {
                                throw new ObjectException("File is broken");
                            }
                        }

                        logger.info(" @download done!");

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        semaphore.release(blocks);
                    }
                    System.out.println("done!");
                }

            } else {
                throw new CDNException("Must need init ks3 client first ,Method :handler.initialize(config) ;");
            }
        } catch (Exception e) {
            throw new ObjectException("File download exception ", e);
        }
    }

    /**
     * query file list of bucket
     *
     * @param bucket
     *         bucket name
     * @param limit
     *         like mysql limit
     *
     * @return return file list
     */
    @Override
    public List listFiles (String bucket, Integer limit) throws BucketException, ObjectException, CDNException {

        logger.info(" @check file list,Bucket:{}", bucket);

        try {
            if (client != null) {
                ListObjectsRequest request = new ListObjectsRequest(bucket);
                request.setMaxKeys(limit);
                ObjectListing list = client.listObjects(request);

                if (list != null) {

                    List<Ks3ObjectSummary> ks3ObjectSummaries = list.getObjectSummaries();
                    if (ks3ObjectSummaries != null && ks3ObjectSummaries.size() > 0) {
                        List result = new ArrayList(limit);
                        for (Ks3ObjectSummary summary : ks3ObjectSummaries) {
                            result.add(JSON.toJSONString(summary));
                        }
                        return result;
                    }
                }

            } else {
                throw new CDNException("Must need init ks3 client first ,Method :handler.initialize(config) ;");
            }

        } catch (Exception e) {
            throw new ObjectException("check file list exception ", e);
        }
        return new ArrayList();
    }

    /**
     * delete file
     *
     * @param bucket
     *         bucket name
     * @param relativePathKeys
     *         bucket file key
     */
    @Override
    public void deleteFiles (String bucket, String... relativePathKeys) throws BucketException, ObjectException, CDNException {

        logger.info(" @Ready to delete file ,Bucket:{} ,Keys:{}", bucket, Arrays.toString(relativePathKeys));
        try {
            if (client != null) {
                if (relativePathKeys != null && relativePathKeys.length > 0) {
                    DeleteMultipleObjectsResult result = client.deleteObjects(relativePathKeys, bucket);
                    logger.info(" @Deleted , Response : {}", result);
                }
            } else {
                throw new CDNException("Must need init ks3 client first ,Method :handler.initialize(config) ;");
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public Boolean checkFile (String bucket, String relativePathKeys) throws ObjectException, CDNException {

        logger.info(" @Check File info ,Bucket:{} ,File:{}", bucket, relativePathKeys);
        try {
            HeadObjectRequest request = new HeadObjectRequest(bucket, relativePathKeys);
            HeadObjectResult headObjectResult = client.headObject(request);
            logger.info(" @File info :{}", headObjectResult);
            return true;
        } catch (NotFoundException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * create bucket with name
     *
     * @param bucketName
     *         bucket name
     */
    @Override
    public void createBucket (String bucketName) throws BucketException, CDNException {
        if (bucketName != null && bucketName.trim().length() > 0) {

            logger.info(" @Ready Create Bucket : {}", bucketName);

            if (client != null) {
                // Check first
                if (checkBucket(bucketName) == 0) {
                    return;// exist
                }
                CreateBucketRequest request = new CreateBucketRequest(bucketName);
                CreateBucketConfiguration config = new CreateBucketConfiguration(CreateBucketConfiguration.REGION.BEIJING);
                request.setConfig(config);
                request.setCannedAcl(CannedAccessControlList.PublicReadWrite);
                Bucket bucket = client.createBucket(request);

                logger.info(" @Created, INFO: {} ", bucket);

                if (bucket == null) {
                    throw new BucketException("create bucket:" + bucketName + " fail");
                }

            } else {
                throw new CDNException("Must need init ks3 client first ,Method :handler.initialize(config) ;");
            }
            return;
        }
        throw new BucketException("invalid bucket name");
    }

    /**
     * check bucket info
     *
     * @param bucketName
     *         bucket name
     */
    @Override
    public int checkBucket (String bucketName) throws BucketException, CDNException {

        if (bucketName != null && bucketName.trim().length() > 0) {
            logger.info(" @Bucket :{}", bucketName);
            if (client != null) {
                HeadBucketResult result = client.headBucket(bucketName);
                logger.info(" @Bucket:{} ", result);
                int resultCode = result.getStatueCode();
                switch (resultCode) {
                    case 200:
                        return 0;
                    case 404:
                        return 404;
                    case 403:
                        return 403;
                }
            } else {
                throw new CDNException("Must need init ks3 client first ,Method :handler.initialize(config) ;");
            }
        }
        throw new BucketException("invalid bucket name");
    }


}
