package xyz.vopen.cartier.provision;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.cartier.commons.httpclient.AbstractHttpClient;
import xyz.vopen.cartier.commons.httpclient.HttpClient;
import xyz.vopen.cartier.commons.httpclient.HttpOptions;
import xyz.vopen.cartier.commons.httpclient.HttpParams;
import xyz.vopen.cartier.commons.httpclient.HttpResponse;
import xyz.vopen.cartier.commons.httpclient.HttpStatus;
import xyz.vopen.cartier.provision.exception.AuthenticateException;
import xyz.vopen.cartier.provision.exception.CertException;
import xyz.vopen.cartier.provision.exception.PreLoginException;
import xyz.vopen.cartier.provision.exception.RedirectException;
import xyz.vopen.cartier.provision.exception.RequestException;
import xyz.vopen.cartier.provision.ext.Result;
import xyz.vopen.cartier.provision.ext.response.AddAppIdResponse;
import xyz.vopen.cartier.provision.ext.response.AddDevicesResponse;
import xyz.vopen.cartier.provision.ext.response.CheckPermissionsResponse;
import xyz.vopen.cartier.provision.ext.response.CreateProvisioningProfileResponse;
import xyz.vopen.cartier.provision.ext.response.DeleteDeviceResponse;
import xyz.vopen.cartier.provision.ext.response.DeleteResponse;
import xyz.vopen.cartier.provision.ext.response.EnableDeviceResponse;
import xyz.vopen.cartier.provision.ext.response.GetProvisioningProfileResponse;
import xyz.vopen.cartier.provision.ext.response.GetTeamsResponse;
import xyz.vopen.cartier.provision.ext.response.ListAppIdsResponse;
import xyz.vopen.cartier.provision.ext.response.ListCertResponse;
import xyz.vopen.cartier.provision.ext.response.ListDevicesResponse;
import xyz.vopen.cartier.provision.ext.response.ListProvisioningProfilesResponse;
import xyz.vopen.cartier.provision.ext.response.RegenProvisioningProfileResponse;
import xyz.vopen.cartier.provision.ext.response.RevokeCertificateResponse;
import xyz.vopen.cartier.provision.ext.response.SubmitCertificateResponse;
import xyz.vopen.cartier.provision.ext.response.ValidateDevicesResponse;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 证书处理工具
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.2 - 12/07/2017.
 */
public final class ProvisionProcessor {

    private static Logger logger = LoggerFactory.getLogger(ProvisionProcessor.class);

    // @@@:off
    private ProvisionProcessor () {}
    private static class InstanceHolder {private static ProvisionProcessor INSTANCE = new ProvisionProcessor();}
    public static ProvisionProcessor getInstance () {return InstanceHolder.INSTANCE;}
    public ProvisionHandler getHandler () {return new ProvisionHandler();}
    // @@@:on

    /**
     * 设备类型
     */
    public enum Device {
        iphone, ipad, ipod, tvOS, watch
    }

    /**
     * 处理类
     */
    public static class ProvisionHandler {
        // @@@:off
        private HttpClient httpClient = HttpClient.getInstance();
        private final String UNQI = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        private AtomicInteger iseq = new AtomicInteger(0);
        private Integer seq () {return iseq.incrementAndGet();}
        private volatile Boolean log = Boolean.TRUE;
        public void disableLogStatus () {log = Boolean.FALSE;}
        public void resetLogStatus () {log = Boolean.TRUE;}
        private void logger (Integer iseq, String msg, Object... params) {if (log) logger.info("[" + UNQI + "-" + iseq + "] , " + msg, params);}
        private void error (Integer iseq, String msg, Object... params) {if (log) logger.error("[" + UNQI + "-" + iseq + "] , " + msg, params);}
        private Map<String, String> cookiesHolder = Maps.newHashMap();
        private String buildRequiredHeaderCookie (Integer i) {String cookie = String.format("ds01_a=%s;myacinfo=%s;DSESSIONID=%s;acsso=%s;",cookiesHolder.get("ds01_a"),cookiesHolder.get("myacinfo"),cookiesHolder.get("DSESSIONID"),cookiesHolder.get("acsso"));logger(i, "@Cookie={}", cookie);return cookie;}
        private Map<String, String> headerHolder = Maps.newHashMap();
        // @@@:on
        private void processCookieAndHeader (Integer i, HttpResponse response) {
            if (response != null) {
                logger(i, "@Cookie:");
                Map<String, String> c_temp = response.getCookieMap();
                if (c_temp != null)
                    for (Map.Entry<String, String> entry : c_temp.entrySet()) {
                        logger(i, "\t {} = {}", entry.getKey(), entry.getValue());
                        cookiesHolder.put(entry.getKey(), entry.getValue());
                    }

                logger(i, "@Headers:");
                Map<String, String> h_temp = response.getHeaderMap();
                if (h_temp != null)
                    for (Map.Entry<String, String> entry : h_temp.entrySet()) {
                        logger(i, "\t {} = {}", entry.getKey(), entry.getValue());
                        headerHolder.put(entry.getKey(), entry.getValue());
                    }


                if (headerHolder.containsKey("csrf") && headerHolder.containsKey("csrf_ts")) {
                    logger(i, "@最新的csrf = {}", headerHolder.get("csrf"));
                    logger(i, "@最新的csrf_ts = {}", headerHolder.get("csrf_ts"));
                }
            }
        }

        private ProvisionHandler () {
        }


        //**************************************业务逻辑方法*******************************************//

        public Result login (String username, String password) {
            try {
                if (StringUtils.isNoneBlank(username, password)) {

                    Result temp = requestPreLogin();
                    if (temp.getCode() == Result.Code.SUCCESS.code()) {
                        temp = requestAuthenticate(username, password);
                        if (temp.getCode() == Result.Code.SUCCESS.code()) {
                            return temp;
                        } else {
                            return Result.DefaultResult.FAIL;
                        }
                    }
                }
            } catch (PreLoginException | RequestException | AuthenticateException e) {
                e.printStackTrace();
            }
            return Result.DefaultResult.FAIL;
        }

        /**
         * 注销账号下的证书
         * <pre>
         *     <p>1.revoke certs</p>
         *     <p>2.delete appIds</p>
         *     <p>3.revoke profiles</p>
         * </pre>
         */
        public Result revokeAccount (String username, String password) {
            try {
                disableLogStatus();
                Result temp = requestPreLogin();
                if (temp.getCode() == Result.Code.SUCCESS.code()) {
                    temp = requestAuthenticate(username, password);
                    if (temp.getCode() == Result.Code.SUCCESS.code()) {
                        // login success
                        GetTeamsResponse teamsResponse = getTeams();
                        String teamId = teamsResponse.getTeams().get(0).getTeamId();

                        try {
                            // list profiles
                            String[] profileIds = null;
                            ListProvisioningProfilesResponse profilesResponse = listProvisioningProfiles(teamId, "limited", "");
                            if (profilesResponse.getTotalRecords() > 0) {
                                profileIds = new String[profilesResponse.getTotalRecords()];
                                List<ListProvisioningProfilesResponse.ProvisioningProfilesEntity> entities = profilesResponse.getProvisioningProfiles();
                                for (int i = 0; i < entities.size(); i++) {
                                    profileIds[i] = entities.get(i).getProvisioningProfileId();
                                }
                            }

                            if (profileIds != null && profileIds.length > 0) {
                                for (String profileId : profileIds) {
                                    try {
                                        deleteProvisioningProfile(teamId, profileId);
                                    } catch (Throwable ignored) {
                                    }
                                }
                            }

                            System.out.println("Re-invoked all profiles , Result: " + Arrays.toString(profileIds));
                        } catch (Throwable ignored) {
                        }


                        try {
                            String[] appIds = null;
                            ListAppIdsResponse appIdsResponse = listAppIds(teamId);
                            if (appIdsResponse.getTotalRecords() > 0) {
                                appIds = new String[appIdsResponse.getTotalRecords()];
                                List<ListAppIdsResponse.AppIdsEntity> list = appIdsResponse.getAppIds();
                                for (int i = 0; i < list.size(); i++) {
                                    appIds[i] = list.get(i).getAppIdId();
                                }
                            }

                            if (appIds != null && appIds.length > 0) {
                                for (String appId : appIds) {
                                    try {
                                        deleteAppId(teamId, appId);
                                    } catch (Throwable ignored) {
                                    }
                                }
                            }

                            System.out.println("Re-invoked all AppId , Result: " + Arrays.toString(appIds));
                        } catch (Throwable ignored) {
                        }

                        try {

                            String[] certs = null;
                            ListCertResponse listCertResponse = listCertRequests(teamId, "development");
                            if (listCertResponse.getTotalRecords() > 0) {
                                certs = new String[listCertResponse.getTotalRecords()];
                                List<ListCertResponse.CertRequestsEntity> certRequestsEntities = listCertResponse.getCertRequests();
                                for (int i = 0; i < certRequestsEntities.size(); i++) {
                                    certs[i] = certRequestsEntities.get(i).getCertificateId();
                                }
                            }

                            if (certs != null && certs.length > 0) {
                                for (String cert : certs) {
                                    // delete cert
                                    try {
                                        revokeCertificate(teamId, cert);
                                    } catch (Throwable ignored) {
                                    }
                                }
                            }

                            System.out.println("Re-invoked all Certificates , Result: " + Arrays.toString(certs));

                        } catch (Throwable ignored) {
                        }

                        return Result.DefaultResult.SUCCESS;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (RequestException e) {
                e.printStackTrace();
            } catch (PreLoginException e) {
                e.printStackTrace();
            } catch (AuthenticateException e) {
                e.printStackTrace();
            } finally {
                resetLogStatus();
            }
            return Result.DefaultResult.FAIL;
        }

        //********************************公共处理方法**************************************//

        /**
         * 第一步: 预登陆操作
         */
        private Result requestPreLogin () throws PreLoginException, RequestException {

            try {
                int i = seq();
                logger(i, "@请求首页");

                String url = "https://idmsa.apple.com/IDMSWebAuth/login";
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("appIdKey", "891bd3417a7776362562d2197f89480a8547b108fd934911bcbea0110d07f757");
                params.put("path", "/account/");
                params.put("rv", "1");

                logger(i, "@参数:{}", params.toMap());

                httpClient.request(AbstractHttpClient.METHOD.GET, url, params, null, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    return Result.DefaultResult.SUCCESS;
                } else {
                    error(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }


            } catch (Exception e) {
                throw new PreLoginException("@预登录异常", e);
            }
            System.out.println();
            return Result.DefaultResult.FAIL;
        }

        /**
         * 第二步: 登录授权操作
         *
         * @param username
         *         用户名
         * @param password
         *         密码
         */
        private Result requestAuthenticate (String username, String password) throws AuthenticateException, RequestException {

            try {


                int i = seq();
                logger(i, "@请求授权接口");
                String url = "https://idmsa.apple.com/IDMSWebAuth/authenticate";
                logger(i, "@请求地址:{}", url);

                // init response
                HttpResponse response = new HttpResponse(true, true);

                // build param
                HttpParams params = new HttpParams();
                params.put("appIdKey", "891bd3417a7776362562d2197f89480a8547b108fd934911bcbea0110d07f757");
                params.put("path", "/account/");
                params.put("rv", "1");
                params.put("accNameLocked", "false");
                params.put("language", "US-EB");
                params.put("Env", "PROD");
                params.put("referer", "https://developer.apple.com/");
                params.put("scnt", "");
                params.put("appleId", username);
                params.put("accountPassword", password);

                // build cookie
                CookieStore cookieStore = new BasicCookieStore();
                for (Map.Entry<String, String> entry : cookiesHolder.entrySet()) {
                    cookieStore.addCookie(new BasicClientCookie(entry.getKey(), entry.getValue() == null ? "" : entry.getValue()));
                }

                HttpOptions.Builder builder = new HttpOptions.Builder();

                HttpOptions httpOptions = builder.build();

                logger(i, "@参数:{}", params.toMap());
                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());

                    this.processCookieAndHeader(i, response);

                    if (response.getStatusCode() == 302) {
                        // 进行重定向
                        logger(i, "@授权成功,重定向首页操作");

                        return this.redirect2Index();

                    } else {

                        logger(i, "@授权成功,状态码不正确!");
                    }


                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();

            } catch (RedirectException e) {
                throw new AuthenticateException("@授权异常,二级重定向异常", e);
            } catch (Exception e) {
                throw new AuthenticateException("@授权异常", e);
            }
            return Result.DefaultResult.FAIL;
        }

        /**
         * 第三部: 重定向到首页,解析后文需要的字段值(csrf , csrf_ts)
         */
        private Result redirect2Index () throws RedirectException, RequestException {

            try {

                int i = seq();
                logger(i, "@重定向到开发者后台首页");

                String url = "https://developer.apple.com/account/";
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.GET, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // check needed parameters is valid!
                    if (headerHolder.containsKey("csrf") && headerHolder.containsKey("csrf_ts")) {
                        logger(i, "@登录成功.");
                        Map<String, String> pa = Maps.newHashMap();
                        pa.put("csrf", headerHolder.get("csrf"));
                        pa.put("csrf_ts", headerHolder.get("csrf_ts"));
                        logger(i, "response = {}", pa.toString());

                        return Result.newBuilder().result(Result.Code.SUCCESS, pa).build();
                    } else {
                        logger(i, "@登录失败,没有获取到必须要的参数:[csrf | csrf_ts]");
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();

            } catch (Exception e) {
                throw new RedirectException("@重定向异常", e);
            }
            return Result.DefaultResult.FAIL;
        }

        /**
         * 获取TeamId
         *
         * @throws RequestException
         * @see GetTeamsResponse
         */
        public GetTeamsResponse getTeams () throws RequestException {

            try {

                int i = seq();
                logger(i, "@获取苹果TeamId请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/getTeams";
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "application/json");
                // 此 Content-type 必须加 -> 否则会返回415
                headerMap.put("Content-type", "application/json;charset=UTF-8");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/");
                headerMap.put("Connection", "keep-alive");
                headerMap.put("User-Locale", "en_US");

//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        GetTeamsResponse teamsResponse = GetTeamsResponse.fromJson(result, GetTeamsResponse.class);
                        if (teamsResponse != null) {
                            if (teamsResponse.getResultCode() == 0)
                                return teamsResponse;
                            else
                                throw new RuntimeException("获取TeamId状态异常, CODE = " + teamsResponse.getResultCode() + " , MSG = " + teamsResponse.getResultString());
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();

            } catch (Exception e) {
                throw new RequestException("@获取TeamID请求异常", e);
            }
            throw new RequestException("@获取TeamID失败");
        }


        /**
         * 查询所有的证书
         *
         * @param teamId
         *         teamId (can't be null)
         * @param type
         *         类型 development(default) | production | pending
         *
         * @return
         *
         * @throws RequestException
         */
        public ListCertResponse listCertRequests (String teamId, String type) throws RequestException {

            try {

                if (!StringUtils.isNoneBlank(type)) {
                    type = "development";
                }

                int i = seq();
                logger(i, "@查询苹果开发者账号的证书列表请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/certificate/listCertRequests.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s&types=5QPB9NHCEI,BKLRAVXMGM&status=4&certificateStatus=0&type=%s";
                url = String.format(url, Utils.requestId(), teamId, type);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("search", "");
                params.put("nd", System.currentTimeMillis());
                params.put("pageSize", "500");
                params.put("pageNumber", "1");
                params.put("sidx", "sort");
                params.put("sort", "name=asc&certRequestStatusCode=asc");

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/certificate/development");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        ListCertResponse listCertResponse = ListCertResponse.fromJson(result, ListCertResponse.class);
                        if (listCertResponse != null) {
                            if (listCertResponse.getResultCode() == 0)
                                return listCertResponse;
                            else
                                throw new RuntimeException("查询所有证书状态异常, CODE = " + listCertResponse.getResultCode() + " , MSG = " + listCertResponse.getResultString());
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@获取所有证书请求异常", e);
            }

            throw new RequestException("@获取证书列表失败");
        }

        /**
         * 检查权限请求
         *
         * @throws RequestException
         */
        private CheckPermissionsResponse checkPermissions (String teamId, String permissions) throws RequestException {

            try {
                int i = seq();
                logger(i, "@检查权限请求");

                String url = "https://developer.apple.com/services-account/checkPermissions";
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("teamId", teamId);
                params.put("permissions", permissions);

                logger(i, "@参数:{}", params.toMap());

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, null, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());

                    this.processCookieAndHeader(i, response);

                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        CheckPermissionsResponse checkPermissionsResponse = CheckPermissionsResponse.fromJson(response.getResult(), CheckPermissionsResponse.class);
                        if (checkPermissionsResponse != null) {
                            if (checkPermissionsResponse.getResultCode() == 0)
                                return checkPermissionsResponse;
                            else
                                throw new RuntimeException("检查权限状态异常, CODE = " + checkPermissionsResponse.getResultCode() + " , MSG = " + checkPermissionsResponse.getResultString());
                        }
                    }

                } else {
                    error(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }


            } catch (Exception e) {
                throw new RequestException("@预请求异常", e);
            }
            System.out.println();

            throw new RequestException("@检查权限失败");
        }

        private void createIOSCertificate (String teamId) throws RequestException {
            try {
                int i = seq();
                logger(i, "@获取创建证书的页面");

                String url = "https://developer.apple.com/account/cips/json/createIOSCertificate.json?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s";
                url = String.format(url, Utils.requestId(), teamId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();

                logger(i, "@参数:{}", params.toMap());

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, null, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());

                    this.processCookieAndHeader(i, response);


                } else {
                    error(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }


            } catch (Exception e) {
                throw new RequestException("@获取创建证书的页面异常", e);
            }
            System.out.println();
        }

        /**
         * 提交 CSR 文件前置条件请求
         *
         * @throws RequestException
         */
        private void preSubmitCSRRequest () throws RequestException {

            try {
                int i = seq();
                logger(i, "@提交 CSR 文件前置条件请求");

                String url = "https://developer.apple.com/account/cips/json/data/csr_body.html";
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/certificate/create");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.GET, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());

                    this.processCookieAndHeader(i, response);
                } else {
                    error(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }


            } catch (Exception e) {
                throw new RequestException("@预请求异常", e);
            }
            System.out.println();
        }


        /**
         * 提交 CSR 请求
         *
         * @param teamId
         *         teamID
         *
         * @return
         *
         * @throws RequestException
         */
        public SubmitCertificateResponse submitCertificateRequest (String teamId, String csrFilePath) throws CertException, RequestException {

            try {

//                checkPermissions(teamId, "team.certificate.ca.available");

//                createIOSCertificate(teamId);

//                preSubmitCSRRequest();

                int i = seq();
                logger(i, "@提交 CSR 请求,获取证书下载链接请求 ");

                if (!StringUtils.isNoneBlank(csrFilePath)) {
                    throw new CertException("CSR请求文件路径不能为空!");
                }

                File tempFile = new File(csrFilePath);
                if (!tempFile.exists()) {
                    throw new CertException("CSR请求文件不存在");
                }

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/certificate/submitCertificateRequest.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s";
                url = String.format(url, Utils.requestId(), teamId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                // read csr file content

                params.put("csrContent", FileUtils.readFileToString(tempFile, "UTF-8"));
                // 固定值,标识开发证书(development)
                params.put("type", "5QPB9NHCEI");

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/certificate/development/create");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        SubmitCertificateResponse submitCertificateResponse = SubmitCertificateResponse.fromJson(result, SubmitCertificateResponse.class);
                        if (submitCertificateResponse != null) {
                            if (submitCertificateResponse.getResultCode() == 0)
                                return submitCertificateResponse;
                            else
                                throw new RuntimeException("提交CSR状态异常, CODE = " + submitCertificateResponse.getResultCode() + " , MSG = " + submitCertificateResponse.getResultString());
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();

//            } catch (RequestException e) {
//                throw new RequestException("@预请求异常", e);
            } catch (Exception e) {
                throw new RequestException("@提交 CSR 请求异常", e);
            }

            throw new RequestException("@提交CSR失败");
        }


        /**
         * 注销证书(iOS Development = 5QPB9NHCEI)
         *
         * @param teamId
         *         teamId
         * @param certificateId
         *         证书 Id
         *
         * @return
         *
         * @throws RequestException
         * @see <code>CertificateTypes.json</code>
         */
        public RevokeCertificateResponse revokeCertificate (String teamId, String certificateId) throws RequestException {

            try {

                int i = seq();
                logger(i, "@删除证书文件请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/certificate/revokeCertificate.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s&certificateId=%s&type=5QPB9NHCEI";

                url = String.format(url, Utils.requestId(), teamId, certificateId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/identifier/bundle/create");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        RevokeCertificateResponse deleteResponse = RevokeCertificateResponse.fromJson(result, RevokeCertificateResponse.class);

                        if (deleteResponse != null) {
                            if (deleteResponse.getResultCode() == 0)
                                return deleteResponse;
                            else {
                                throw new RuntimeException("删除证书文件异常, CODE = " + deleteResponse.getResultCode() + " , MSG = " + deleteResponse.getResultString());

                            }
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@删除证书文件请求异常", e);
            }

            throw new RequestException("@删除证书文件失败");
        }


        /**
         * 下载证书
         *
         * @throws RequestException
         */
        public void downloadCertificateContent (String teamId, String certificateId, String destCertFilePath) throws CertException, RequestException {

            try {
                int i = seq();
                logger(i, "@下载开发者证书请求 ");

                if (!StringUtils.isNoneBlank(destCertFilePath)) {
                    throw new CertException("证书存储路径不能为空!");
                }

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/certificate/downloadCertificateContent.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s&certificateId=%s&type=5QPB9NHCEI";
                url = String.format(url, Utils.requestId(), teamId, certificateId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/certificate/development/create");
                headerMap.put("Connection", "keep-alive");
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.download(url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    FileUtils.writeByteArrayToFile(new File(destCertFilePath), response.getResultByte(), false);

                    // check
                    if (new File(destCertFilePath).exists()) {
                        // success
                        logger(i, "@证书下载完成,路径:{}", destCertFilePath);
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();

            } catch (Exception e) {
                throw new RequestException("@下载开发者证书请求异常", e);
            }
        }


        /**
         * 查询开发者账号下所有的 AppIds
         *
         * @param teamId
         *
         * @return
         *
         * @throws RequestException
         */
        public ListAppIdsResponse listAppIds (String teamId) throws RequestException {

            try {

                int i = seq();
                logger(i, "@查询苹果开发者账号的AppIds请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/identifiers/listAppIds.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s&onlyCountLists=true";
                url = String.format(url, Utils.requestId(), teamId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("search", "");
                params.put("nd", System.currentTimeMillis());
                params.put("pageSize", "500");
                params.put("pageNumber", "1");
                params.put("sidx", "name");
                params.put("sort", "name%3dasc");

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/identifier/bundle");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        ListAppIdsResponse listAppIdsResponse = ListAppIdsResponse.fromJson(result, ListAppIdsResponse.class);
                        if (listAppIdsResponse != null) {
                            if (listAppIdsResponse.getResultCode() == 0)
                                return listAppIdsResponse;
                            else
                                throw new RuntimeException("查询所有AppId状态异常, CODE = " + listAppIdsResponse.getResultCode() + " , MSG = " + listAppIdsResponse.getResultString());
                        }

                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@获取所有AppIds请求异常", e);
            }


            throw new RequestException("@查询 AppIds 失败");
        }


        /**
         * 添加 AppId
         *
         * @param teamId
         *         teamId
         * @param appIdName
         *         名称
         *
         * @return
         *
         * @throws RequestException
         * @see AddAppIdResponse
         */
        public AddAppIdResponse addAppId (String teamId, String appIdName) throws RequestException {

            try {

                int i = seq();
                logger(i, "@添加AppIds请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/identifiers/addAppId.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s";
                url = String.format(url, Utils.requestId(), teamId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("name", appIdName);
                params.put("identifier", "*");
                params.put("prefix", teamId);
                params.put("type", "wildcard");
                params.put("cloudKitVersion", "1");
                params.put("dataProtectionPermissionLevel", "complete");

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/identifier/bundle/create");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        AddAppIdResponse addAppIdResponse = AddAppIdResponse.fromJson(result, AddAppIdResponse.class);

                        if (addAppIdResponse != null) {
                            if (addAppIdResponse.getResultCode() == 0)
                                return addAppIdResponse;
                            else
                                throw new RuntimeException("添加设备状态异常, CODE = " + addAppIdResponse.getResultCode() + " , MSG = " + addAppIdResponse.getResultString());
                        }

                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@添加AppIds请求异常", e);
            }

            throw new RequestException("@添加 AppIds 失败");
        }


        /**
         * 删除 AppID
         *
         * @param teamId
         *         teamId
         * @param appIdId
         *         appId id
         *
         * @return
         *
         * @throws RequestException
         */
        public DeleteResponse deleteAppId (String teamId, String appIdId) throws RequestException {

            try {

                int i = seq();
                logger(i, "@删除AppIds请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/identifiers/deleteAppId.action";
                url = String.format(url, Utils.requestId(), teamId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("appIdId", appIdId);
                params.put("content-type", "text/x-url-arguments");
                params.put("accept", "application/json");
                params.put("requestId", Utils.requestId());
                params.put("userLocale", "en_US");
                params.put("teamId", teamId);

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/identifier/bundle/create");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        DeleteResponse deleteAppIdResponse = DeleteResponse.fromJson(result, DeleteResponse.class);

                        if (deleteAppIdResponse != null) {
                            if (deleteAppIdResponse.getResultCode() == 0)
                                return deleteAppIdResponse;
                            else
                                throw new RuntimeException("删除AppId异常, CODE = " + deleteAppIdResponse.getResultCode() + " , MSG = " + deleteAppIdResponse.getResultString());
                        }

                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@删除AppIds请求异常", e);
            }

            throw new RequestException("@删除 AppIds 失败");
        }


        /**
         * 查询开发者后台所添加的设备列表(如果是搜索UDID, device -> null , searchUdid != null)
         *
         * @param teamId
         *         teamId
         * @param device
         *         设备类型
         * @param searchUdid
         *         搜索设备号
         *
         * @return
         *
         * @throws RequestException
         * @see Device
         */
        public ListDevicesResponse listDevices (String teamId, Device device, String searchUdid) throws RequestException {

            try {

                int i = seq();
                logger(i, "@查询苹果开发者账号的AppIds请求");
                String url, search = "";

                if (StringUtils.isNoneBlank(searchUdid)) {
                    // 搜索所有的设备
                    url = "https://developer.apple.com/services-account/QH65B2/account/ios/device/listDevices.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s&includeRemovedDevices=true&includeAvailability=true";
                    url = String.format(url, Utils.requestId(), teamId);
                    // 搜索条件
                    search = String.format("name=%s&status=%s&deviceNumber=%s", searchUdid, searchUdid, searchUdid);
                } else {
                    // 按照设备类型查询列表
                    url = "https://developer.apple.com/services-account/QH65B2/account/ios/device/listDevices.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s&includeRemovedDevices=true&includeAvailability=true&deviceClasses=%";
                    url = String.format(url, Utils.requestId(), teamId, device.name());
                }

                logger(i, "@请求地址:{}", url);


                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("search", search);
                params.put("nd", System.currentTimeMillis());
                params.put("pageSize", "500");
                params.put("pageNumber", "1");
                params.put("sidx", "status");
                params.put("sort", "status=asc");

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/device/");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        ListDevicesResponse res = ListDevicesResponse.fromJson(result, ListDevicesResponse.class);
                        if (res != null) {
                            if (res.getResultCode() == 0)
                                return res;
                            else
                                throw new RuntimeException("查询所有设备状态异常, CODE = " + res.getResultCode() + " , MSG = " + res.getResultString());
                        }

                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@获取设备列表请求异常", e);
            }


            throw new RequestException("@查询设备列表失败");
        }


        /**
         * 校验设备有效性
         *
         * @param teamId
         *         teamId
         * @param udid
         *         udid
         * @param device
         *         设备类型
         *
         * @return
         *
         * @throws RequestException
         */
        public ValidateDevicesResponse validateDevice (String teamId, String udid, Device device) throws RequestException {

            try {
                int i = seq();
                logger(i, "@校验设备请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/device/addDevices.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s";
                url = String.format(url, Utils.requestId(), teamId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("deviceNumbers", udid);
                params.put("deviceNames", udid.substring(0, 7));
                params.put("register", "single");
                params.put("deviceClasses", "");

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/device/create");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        ValidateDevicesResponse validateDevicesResponse = ValidateDevicesResponse.fromJson(result, ValidateDevicesResponse.class);
                        if (validateDevicesResponse != null) {
                            if (validateDevicesResponse.getResultCode() == 0) {
                                return validateDevicesResponse;
                            } else {
                                throw new RuntimeException("校验设备状态异常, CODE = " + validateDevicesResponse.getResultCode() + " ,MSG = " + validateDevicesResponse.getResultString());
                            }
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@校验设备请求异常", e);
            }

            throw new RequestException("@校验设备失败");
        }


        /**
         * 单台设备添加
         *
         * @param teamId
         *         teamId
         * @param udid
         *         udid
         * @param device
         *         设备类型
         *
         * @return
         *
         * @throws RequestException
         */
        public AddDevicesResponse addDevice (String teamId, String udid, Device device) throws RequestException {

            try {

                int i = seq();
                logger(i, "@添加设备请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/device/addDevices.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s";
                url = String.format(url, Utils.requestId(), teamId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("deviceNumbers", udid);
                params.put("deviceNames", udid.substring(0, 7));
                params.put("register", "single");
                params.put("deviceClasses", device.name());

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/device/create");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        AddDevicesResponse addDevicesResponse = AddDevicesResponse.fromJson(result, AddDevicesResponse.class);
                        if (addDevicesResponse != null) {
                            if (addDevicesResponse.getResultCode() == 0) {
                                return addDevicesResponse;
                            } else {
                                throw new RuntimeException("添加设备状态异常, CODE = " + addDevicesResponse.getResultCode() + " ,MSG = " + addDevicesResponse.getResultString());
                            }
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@添加设备请求异常", e);
            }

            throw new RequestException("@添加设备失败");
        }

        /**
         * 激活设备状态
         *
         * @param deviceId
         *         与设备号对应的设备ID(苹果内部的设备 ID)
         * @param teamId
         *         teamId
         * @param udid
         *         设备号
         *
         * @throws RequestException
         */
        public EnableDeviceResponse enableDevice (String teamId, String udid, String deviceId) throws RequestException {
            try {

                int i = seq();
                logger(i, "@激活设备请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/device/enableDevice.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s&deviceId=&deviceNumber=%s&displayId=%s";
                url = String.format(url, Utils.requestId(), teamId, udid, deviceId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/device/");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        EnableDeviceResponse enableDeviceResponse = EnableDeviceResponse.fromJson(result, EnableDeviceResponse.class);
                        if (enableDeviceResponse != null) {
                            if (enableDeviceResponse.getResultCode() == 0) {
                                return enableDeviceResponse;
                            } else {
                                throw new RuntimeException("激活设备状态异常, CODE = " + enableDeviceResponse.getResultCode() + " ,MSG = " + enableDeviceResponse.getResultString());
                            }
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@激活设备请求异常", e);
            }

            throw new RequestException("@激活设备失败");
        }


        /**
         * 删除设备
         *
         * @param teamId
         *         teamId
         * @param deviceId
         *         设备唯一标示
         *
         * @return
         *
         * @throws RequestException
         */
        public DeleteDeviceResponse deleteDevice (String teamId, String deviceId) throws RequestException {

            try {

                int i = seq();
                logger(i, "@删除设备请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/device/deleteDevice.action";
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("content-type", "text/x-url-arguments");
                params.put("accept", "application/json");
                params.put("requestId", Utils.requestId());
                params.put("userLocale", "en_US");
                params.put("teamId", teamId);
                params.put("deviceId", deviceId);

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/device/");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        DeleteDeviceResponse deleteDeviceResponse = DeleteDeviceResponse.fromJson(result, DeleteDeviceResponse.class);
                        if (deleteDeviceResponse != null) {
                            if (deleteDeviceResponse.getResultCode() == 0) {
                                return deleteDeviceResponse;
                            } else {
                                throw new RuntimeException("删除设备状态异常, CODE = " + deleteDeviceResponse.getResultCode() + " , MSG = " + deleteDeviceResponse.getResultString());
                            }
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@删除设备请求异常", e);
            }

            throw new RequestException("@删除设备失败");
        }


        /**
         * 查询描述文件列表
         *
         * @param teamId
         *         teamId
         * @param type
         *         类型 (取值production | limited)
         * @param searchProfileName
         *         描述文件名称
         *
         * @return
         *
         * @throws RequestException
         */
        public ListProvisioningProfilesResponse listProvisioningProfiles (String teamId, String type, String searchProfileName) throws RequestException {

            try {

                int i = seq();
                logger(i, "@查询描述文件列表请求");

                if (StringUtils.isNoneBlank(type)) {
                    if (!StringUtils.containsAny(type, "limited", "production")) {
                        throw new RequestException("查询描述文件列表,参数Type取值异常![production | limited]");
                    }
                }

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/profile/listProvisioningProfiles.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s&includeInactiveProfiles=true&onlyCountLists=true&type=%s";
                url = String.format(url, Utils.requestId(), teamId, type);

                String search = "";
                if (StringUtils.isNoneBlank(searchProfileName)) {
                    search = String.format("name=%s&type=%s&status=%s&appId=%s", searchProfileName, searchProfileName, searchProfileName, searchProfileName);
                }

                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("search", search);
                params.put("nd", System.currentTimeMillis());
                params.put("pageSize", "500");
                params.put("pageNumber", "1");
                params.put("sidx", "name");
                params.put("sort", "name=asc");

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/profile");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        ListProvisioningProfilesResponse listProvisioningProfilesResponse = ListProvisioningProfilesResponse.fromJson(result, ListProvisioningProfilesResponse.class);
                        if (listProvisioningProfilesResponse != null) {
                            if (listProvisioningProfilesResponse.getResultCode() == 0) {
                                return listProvisioningProfilesResponse;
                            } else {
                                throw new RuntimeException("查询描述文件列表状态异常, CODE = " + listProvisioningProfilesResponse.getResultCode() + " ,MSG = " + listProvisioningProfilesResponse.getResultString());
                            }
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@激活设备请求异常", e);
            }

            throw new RequestException("@查询描述文件列表失败");
        }


        public GetProvisioningProfileResponse getProvisioningProfile (String teamId, String type, String provisioningProfileId) throws RequestException {

            try {

                int i = seq();
                logger(i, "@查询描述文件详情请求");

                if (StringUtils.isNoneBlank(type)) {
                    if (!StringUtils.containsAny(type, "limited", "production")) {
                        throw new RequestException("查询描述文件请求 ,参数Type取值异常![production | limited]");
                    }
                }

                String url = "https://developer.apple.com:443/services-account/QH65B2/account/ios/profile/getProvisioningProfile.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s&includeInactiveProfiles=true&onlyCountLists=false&type=%s";
                url = String.format(url, Utils.requestId(), teamId, type);

                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("provisioningProfileId", provisioningProfileId);

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/profile");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        GetProvisioningProfileResponse getProvisioningProfileResponse = GetProvisioningProfileResponse.fromJson(result, GetProvisioningProfileResponse.class);
                        if (getProvisioningProfileResponse != null) {
                            if (getProvisioningProfileResponse.getResultCode() == 0) {
                                return getProvisioningProfileResponse;
                            } else {
                                throw new RuntimeException("查询描述文件列表状态异常, CODE = " + getProvisioningProfileResponse.getResultCode() + " ,MSG = " + getProvisioningProfileResponse.getResultString());
                            }
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@查询描述文件详情请求异常", e);
            }

            throw new RequestException("@查询描述文件详情失败");
        }


        /**
         * 更新已经存在的描述文件
         *
         * @param teamId
         * @param provisioningProfileId
         *         描述文件 ID
         * @param distributionType
         *         描述文件分发类型
         * @param provisioningProfileName
         *         描述文件名称
         * @param certificateIds
         *         证书 IDs
         * @param appIdId
         *         APPID
         * @param deviceIds
         *         设备 IDs
         *
         * @return
         *
         * @throws RequestException
         */
        public RegenProvisioningProfileResponse regenProvisioningProfile (String teamId, String provisioningProfileId,
                                                                          String distributionType, String provisioningProfileName,
                                                                          String certificateIds, String appIdId, String[] deviceIds) throws RequestException {
            try {

                int i = seq();
                logger(i, "@更新描述文件");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/profile/regenProvisioningProfile.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s";
                url = String.format(url, Utils.requestId(), teamId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();

                params.put("provisioningProfileId", provisioningProfileId);
                params.put("distributionType", distributionType);
                params.put("subPlatform", "");
                params.put("returnFullObjects", "false");
                params.put("provisioningProfileName", provisioningProfileName);
                params.put("appIdId", appIdId);
                params.put("certificateIds", certificateIds);

                if (ArrayUtils.isNotEmpty(deviceIds)) {
                    for (String deviceId : deviceIds) {
                        params.put("deviceIds", deviceId);
                    }
                }

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/profile/limited/edit");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        RegenProvisioningProfileResponse regenProvisioningProfileResponse = RegenProvisioningProfileResponse.fromJson(result, RegenProvisioningProfileResponse.class);
                        if (regenProvisioningProfileResponse != null) {
                            if (regenProvisioningProfileResponse.getResultCode() == 0) {
                                return regenProvisioningProfileResponse;
                            } else {
                                throw new RuntimeException("更新描述文件状态异常, CODE = " + regenProvisioningProfileResponse.getResultCode() + " , MSG = " + regenProvisioningProfileResponse.getResultString());
                            }
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@更新描述文件请求异常", e);
            }

            throw new RequestException("@更新描述文件失败");
        }


        /**
         * 创建新的描述文件
         *
         * @param teamId
         *         teamId
         * @param appIdName
         *         appID name
         * @param appIdId
         *         appId
         * @param distributionType
         *         描述文件分发类型
         * @param certificateIds
         *         证书ID
         * @param provisioningProfileName
         *         描述文件名称
         * @param deviceIds
         *         设备
         *
         * @return
         *
         * @throws RequestException
         */
        public CreateProvisioningProfileResponse createProvisioningProfile (String teamId, String appIdName, String appIdId, String distributionType,
                                                                            String certificateIds,
                                                                            String provisioningProfileName, String[] deviceIds) throws RequestException {

            try {

                int i = seq();
                logger(i, "@更新描述文件");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/profile/createProvisioningProfile.action?content-type=text/x-url-arguments&accept=application/json&requestId=%s&userLocale=en_US&teamId=%s";
                url = String.format(url, Utils.requestId(), teamId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("subPlatform", "");
                params.put("template", "");
                params.put("certificateIds", certificateIds);
                params.put("returnFullObjects", "false");
                params.put("distributionTypeLabel", "distributionTypeLabel");
                params.put("distributionType", distributionType);
                params.put("appIdId", appIdId);
                params.put("appIdName", appIdName);
                params.put("appIdPrefix", teamId);
                params.put("appIdIdentifier", "*");
                params.put("provisioningProfileName", provisioningProfileName);

                StringBuffer ids = new StringBuffer("");
                if (ArrayUtils.isNotEmpty(deviceIds)) {
                    for (String deviceId : deviceIds) {
                        ids.append(deviceId).append(",");
                    }
                }
                String temp = "";
                if (ids.length() > 0) {
                    temp = ids.substring(0, ids.length() - 1);
                }
                params.put("deviceIds", temp);

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/profile/limited/create");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        CreateProvisioningProfileResponse createProvisioningProfileResponse = CreateProvisioningProfileResponse.fromJson(result, CreateProvisioningProfileResponse.class);
                        if (createProvisioningProfileResponse != null) {
                            if (createProvisioningProfileResponse.getResultCode() == 0) {
                                return createProvisioningProfileResponse;
                            } else {
                                throw new RuntimeException("创建描述文件状态异常, CODE = " + createProvisioningProfileResponse.getResultCode() + " , MSG = " + createProvisioningProfileResponse.getResultString());
                            }
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@更新描述文件请求异常", e);
            }

            throw new RequestException("@创建描述文件失败");
        }


        /**
         * 删除描述文件
         *
         * @param teamId
         *         teamId
         * @param provisioningProfileId
         *         描述文件 ID
         *
         * @return
         *
         * @throws RequestException
         */
        public DeleteResponse deleteProvisioningProfile (String teamId, String provisioningProfileId) throws RequestException {

            try {

                int i = seq();
                logger(i, "@删除描述文件请求");

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/profile/deleteProvisioningProfile.action";
                url = String.format(url, Utils.requestId(), teamId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();
                params.put("provisioningProfileId", provisioningProfileId);
                params.put("content-type", "text/x-url-arguments");
                params.put("accept", "application/json");
                params.put("requestId", Utils.requestId());
                params.put("userLocale", "en_US");
                params.put("teamId", teamId);

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "*/*");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/identifier/bundle/create");
                headerMap.put("Connection", "keep-alive");
                // 次请求必须携带这俩参数
                headerMap.put("csrf", headerHolder.get("csrf"));
                headerMap.put("csrf_ts", headerHolder.get("csrf_ts"));
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.request(AbstractHttpClient.METHOD.POST, url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    String result = response.getResult();
                    if (StringUtils.isNoneBlank(result)) {
                        logger(i, "@请求返回值:{}", result);
                        DeleteResponse deleteResponse = DeleteResponse.fromJson(result, DeleteResponse.class);

                        if (deleteResponse != null) {
                            if (deleteResponse.getResultCode() == 0)
                                return deleteResponse;
                            else {
                                throw new RuntimeException("删除描述文件异常, CODE = " + deleteResponse.getResultCode() + " , MSG = " + deleteResponse.getResultString());

                            }
                        }
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();


            } catch (Exception e) {
                throw new RequestException("@删除描述文件请求异常", e);
            }

            throw new RequestException("@删除描述文件失败");
        }


        /**
         * 下载描述文件
         *
         * @param teamId
         *         teamId
         * @param provisioningProfileId
         *         描述文件 ID
         * @param destMobileProvisionFilePath
         *         描述文件存储路径
         *
         * @throws RequestException
         */
        public void downloadProfileContent (String teamId, String provisioningProfileId, String destMobileProvisionFilePath) throws RequestException {

            try {
                int i = seq();
                logger(i, "@下载描述文件请求 ");

                if (!StringUtils.isNoneBlank(destMobileProvisionFilePath)) {
                    throw new RequestException("描述文件存储路径不能为空!");
                }

                String url = "https://developer.apple.com/services-account/QH65B2/account/ios/profile/downloadProfileContent?teamId=%s&provisioningProfileId=%s";
                url = String.format(url, teamId, provisioningProfileId);
                logger(i, "@请求地址:{}", url);

                HttpResponse response = new HttpResponse(true, true);
                HttpParams params = new HttpParams();

                logger(i, "@参数:{}", params.toMap());

                HttpOptions.Builder builder = new HttpOptions.Builder();

                // build header
                Map<String, String> headerMap = Maps.newHashMap();
                headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                headerMap.put("Content-type", "application/x-www-form-urlencoded");
                headerMap.put("Host", "developer.apple.com");
                headerMap.put("Origin", "https://developer.apple.com");
                headerMap.put("Referer", "https://developer.apple.com/account/ios/profile/limited/edit");
                headerMap.put("Connection", "keep-alive");
//                headerMap.put("Cookie", buildRequiredHeaderCookie(i));

                builder.setHeaders(Utils.newHeaders(headerMap).toArray(new Header[]{}));

                HttpOptions httpOptions = builder.build();

                httpClient.download(url, params, httpOptions, response);

                if (Utils.isRequestSuccess(response)) {

                    logger(i, "@请求状态码:{}", response.getStatusCode());
                    this.processCookieAndHeader(i, response);

                    // 处理结果集
                    FileUtils.writeByteArrayToFile(new File(destMobileProvisionFilePath), response.getResultByte(), false);

                    // check
                    if (new File(destMobileProvisionFilePath).exists()) {
                        // success
                        logger(i, "@描述文件下载完成,路径:{}", destMobileProvisionFilePath);
                    }

                } else {
                    logger(i, "@请求发送失败, 状态码:{}", response.getStatusCode());
                }

                System.out.println();

            } catch (Exception e) {
                throw new RequestException("@下载描述文件请求异常", e);
            }
        }
    }

    /**
     * 工具类
     */
    private static class Utils {
        /**
         * 获取请求参数的requestId
         *
         * @return 返回请求参数ID
         */
        public static String requestId () {
            String requestId = ("f8d76a25-25c8-4bda-y670-" + System.currentTimeMillis());
            return requestId.substring(0, requestId.length() - 1);
        }

        public static boolean isRequestSuccess (HttpResponse response) {
            return response != null && (response.getStatusCode() == HttpStatus.SC_OK || response.getStatusCode() == 302);
        }

        public static List<Header> newHeaders (Map<String, String> source) {
            List<Header> headers = Lists.newArrayList();
            headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"));
            headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate, br"));
            headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4"));
            if (source != null && source.size() > 0) {
                for (Map.Entry<String, String> entry : source.entrySet()) {
                    if (StringUtils.isNoneBlank(entry.getValue()))
                        headers.add(new BasicHeader(entry.getKey(), entry.getValue()));
                }
            }
            return headers;
        }
    }

}
