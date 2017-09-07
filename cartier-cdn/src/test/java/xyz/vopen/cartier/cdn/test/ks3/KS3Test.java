package xyz.vopen.cartier.cdn.test.ks3;

import com.ksyun.ks3.dto.Bucket;
import com.ksyun.ks3.dto.CannedAccessControlList;
import com.ksyun.ks3.dto.CreateBucketConfiguration;
import com.ksyun.ks3.dto.ObjectListing;
import com.ksyun.ks3.http.HttpClientConfig;
import com.ksyun.ks3.service.Ks3;
import com.ksyun.ks3.service.Ks3Client;
import com.ksyun.ks3.service.Ks3ClientConfig;
import com.ksyun.ks3.service.request.CreateBucketRequest;
import com.ksyun.ks3.service.request.PutObjectRequest;

import java.io.File;
import java.util.List;

/**
 * xyz.vopen.cartier.cdn.test.ks3
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 08/06/2017.
 */
public class KS3Test {

static Ks3 client;
    
    public static void main (String[] args) {

        Ks3ClientConfig config = new Ks3ClientConfig();

        /**
         * 设置服务地址</br>
         * 中国（北京）| ks3-cn-beijing.ksyun.com
         * 中国（上海）| ks3-cn-shanghai.ksyun.com
         * 中国（香港）| ks3-cn-hk-1.ksyun.com
         */
        config.setEndpoint("ks3-cn-beijing.ksyun.com");   //此处以北京region为例
        config.setProtocol(Ks3ClientConfig.PROTOCOL.http);
        /**
         *true表示以   endpoint/{bucket}/{key}的方式访问</br>
         *false表示以  {bucket}.endpoint/{key}的方式访问
         */
        config.setPathStyleAccess(false);

        HttpClientConfig hconfig = new HttpClientConfig();
        //在HttpClientConfig中可以设置httpclient的相关属性，比如代理，超时，重试等。

        config.setHttpClientConfig(hconfig);
        client = new Ks3Client("CHZrwCU0E8SoSw4XD7T", "WIdEPizUB2pnFXbsu0sjg+bnmIzgMTs2GqeTgPG", config);
        //或者：client.setKs3config(config);

        System.out.println(client);

//        List<Bucket> buckets = listBuckets();988
//        
//        if(buckets != null) {
//            for (Bucket bucket : buckets) {
//                System.out.println(bucket.getName());
//            }
//        }


        long start = System.currentTimeMillis();
        putObjectSimple();
        System.out.println(System.currentTimeMillis() - start);
        

    }

    public static List<Bucket> listBuckets(){
        List<Bucket> buckets = client.listBuckets();

        for(Bucket bucket:buckets){
            //获取bucket的创建时间
            bucket.getCreationDate();
            //获取bucket的名称
            bucket.getName();
            //获取bucket的拥有者（用户ID base64后的值）
            bucket.getOwner();
        }

        return buckets;
    }


    /**
     * <p>使用最简单的方式创建一个bucket</p>
     * <p>将使用默认的配置，权限为私有，存储地点为杭州</p>
     */
    public static void createBucketSimple(){
        client.createBucket("ipa-test-4-ios");
    }
    
    
    /**
     * <p>新建bucket的时候配置bucket的存储地点和访问权限</p>
     */
    public static void createBucketWithConfig(){
        CreateBucketRequest request = new CreateBucketRequest("ipa-test-4-ios");
        //配置bucket的存储地点
        CreateBucketConfiguration config = new CreateBucketConfiguration(CreateBucketConfiguration.REGION.BEIJING);
        request.setConfig(config);
        //配置bucket的访问权限
        request.setCannedAcl(CannedAccessControlList.Private);
        //执行操作
        client.createBucket(request);
    }

    /**
     *将new File("<filePath>")这个文件上传至<bucket名称>这个存储空间下，并命名为<key>
     */ 
    public static void putObjectSimple(){
        PutObjectRequest request = new PutObjectRequest("ipa-test-4-ios",
                "2017/06/P.B.S05E09.720p.mp4", new File("/Users/ive/Downloads/P.B.S05E09.720p.mp4"));
        //上传一个公开文件
        //request.setCannedAcl(CannedAccessControlList.PublicRead);
        client.putObject(request);
    }


    /**
     * 列出一个bucket下的object，返回的最大数为1000条
     */
    public static ObjectListing listObjectsSimple(){
        ObjectListing list = client.listObjects("ioss");
        return list;
    }

}
