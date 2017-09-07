package xyz.vopen.cartier.cdn;

import xyz.vopen.cartier.cdn.exception.BucketException;
import xyz.vopen.cartier.cdn.exception.CDNException;
import xyz.vopen.cartier.cdn.exception.ObjectException;

import java.util.List;

/**
 * CDN operate interface
 * <pre>
 * Methods :
 *  <li>initialize client handler : {@link #initialize(BaseConfig)}</li>
 *
 *  <li>release resource : {@link #destory()}</li>
 *
 *  <li>upload file : {@link #upload(String, String, String)}</li>
 *
 *  <li>upload file with multi threads : {@link #superUpload(String, String, String)}</li>
 *
 *  <li>download file : {@link #download(String, String, String)}</li>
 *
 *  <li>download file with multi threads : {@link #superDownload(String, String, String)}</li>
 *
 *  <li>....</li>
 * </pre>
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 20/07/2017.
 * @see xyz.vopen.cartier.cdn.ks3.Ks3Handler
 */
public interface CDNHandler {

    /**
     * initialize
     *
     * @param config
     *         config
     */
    void initialize (BaseConfig config);

    /**
     * destory
     */
    void destory ();

    /**
     * upload file
     *
     * @param bucket
     *         bucket name
     * @param filePath
     *         source file
     * @param relativePathKey
     *         bucket file key
     *
     * @throws BucketException
     *         bucket exception
     * @throws ObjectException
     *         object exception
     * @throws CDNException
     *         cdn exception
     */
    void upload (String filePath, String bucket, String relativePathKey) throws BucketException, ObjectException, CDNException;

    /**
     * super download
     *
     * @param bucket
     *         bucket name
     * @param filePath
     *         source file
     * @param relativePathKey
     *         bucket file key
     *
     * @throws BucketException
     *         bucket exception
     * @throws ObjectException
     *         object exception
     * @throws CDNException
     *         cdn exception
     */
    void superUpload (String filePath, String bucket, String relativePathKey) throws BucketException, ObjectException, CDNException;

    /**
     * download file
     *
     * @param bucket
     *         bucket
     * @param relativePathKey
     *         bucket file key
     * @param destFilePath
     *         dest file
     *
     * @throws BucketException
     *         bucket exception
     * @throws ObjectException
     *         object exception
     * @throws CDNException
     *         cdn exception
     */
    void download (String bucket, String relativePathKey, String destFilePath) throws BucketException, ObjectException, CDNException;

    /**
     * super download file
     *
     * @param bucket
     *         bucket
     * @param relativePathKey
     *         bucket file key
     * @param destFilePath
     *         dest file
     *
     * @throws BucketException
     *         bucket exception
     * @throws ObjectException
     *         object exception
     * @throws CDNException
     *         cdn exception
     */
    void superDownload (String bucket, String relativePathKey, String destFilePath) throws BucketException, ObjectException, CDNException;


    /**
     * query file list
     *
     * @param bucket
     *         bucket name
     * @param limit
     *         like mysql limit
     *
     * @return file list
     *
     * @throws BucketException
     *         bucket exception
     * @throws ObjectException
     *         object exception
     * @throws CDNException
     *         cdn exception
     */
    List listFiles (String bucket, Integer limit) throws BucketException, ObjectException, CDNException;

    /**
     * delete files
     *
     * @param bucket
     *         bucket name
     * @param relativePathKeys
     *         file keys
     *
     * @throws BucketException
     *         bucket exception
     * @throws ObjectException
     *         object exception
     * @throws CDNException
     *         cdn exception
     */
    void deleteFiles (String bucket, String... relativePathKeys) throws BucketException, ObjectException, CDNException;

    /**
     * check file
     *
     * @param bucket
     *         bucket name
     * @param relativePathKeys
     *         file key
     *
     * @return true is exist ,false is not found
     *
     * @throws ObjectException
     *         object exception
     * @throws CDNException
     *         cdn exception
     */
    Boolean checkFile (String bucket, String relativePathKeys) throws ObjectException, CDNException;

    /**
     * create bucket
     *
     * @param bucketName
     *         bucket name
     *
     * @throws BucketException
     *         bucket exception
     * @throws CDNException
     *         cdn exception
     */
    void createBucket (String bucketName) throws BucketException, CDNException;

    /**
     * check bucket info
     *
     * @param bucketName
     *         bucket name
     *
     * @throws BucketException
     *         bucket exception
     * @throws CDNException
     *         cdn exception
     */
    int checkBucket (String bucketName) throws BucketException, CDNException;

}
