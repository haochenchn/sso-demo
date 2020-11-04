package org.apereo.cas.util.http;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.client.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import javax.annotation.PreDestroy;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The factory to build a {@link SimpleHttpClient}.
 *
 * @author Jerome Leleu
 * @since 4.1.0
 */
public class SimpleHttpClientFactoryBean implements FactoryBean<SimpleHttpClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpClientFactoryBean.class);
    /**
     * Max connections per route.
     */
    public static final int MAX_CONNECTIONS_PER_ROUTE = 50;

    private static final int MAX_POOLED_CONNECTIONS = 100;

    private static final int DEFAULT_THREADS_NUMBER = 200;

    private static final int DEFAULT_TIMEOUT = 5000;

    /**
     * The default status codes we accept.
     */
    private static final int[] DEFAULT_ACCEPTABLE_CODES = new int[]{HttpURLConnection.HTTP_OK,
        HttpURLConnection.HTTP_NOT_MODIFIED, HttpURLConnection.HTTP_MOVED_TEMP,
        HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_ACCEPTED,
        HttpURLConnection.HTTP_NO_CONTENT};

    /**
     * 20% of the total of threads in the pool to handle overhead.
     */
    private static final int DEFAULT_QUEUE_SIZE = (int) (DEFAULT_THREADS_NUMBER * 0.2);

    /**
     * The number of threads used to build the pool of threads (if no executorService provided).
     */
    private int threadsNumber = DEFAULT_THREADS_NUMBER;

    /**
     * The queue size to absorb additional tasks when the threads pool is saturated (if no executorService provided).
     */
    private int queueSize = DEFAULT_QUEUE_SIZE;

    /**
     * The Max pooled connections.
     */
    private int maxPooledConnections = MAX_POOLED_CONNECTIONS;

    /**
     * The Max connections per each route connections.
     */
    private int maxConnectionsPerRoute = MAX_CONNECTIONS_PER_ROUTE;

    /**
     * List of HTTP status codes considered valid by the caller.
     */
    private List<Integer> acceptableCodes = IntStream.of(DEFAULT_ACCEPTABLE_CODES).boxed().collect(Collectors.toList());

    private long connectionTimeout = DEFAULT_TIMEOUT;

    private int readTimeout = DEFAULT_TIMEOUT;

    private RedirectStrategy redirectionStrategy = new DefaultRedirectStrategy();

    /**
     * The socket factory to be used when verifying the validity of the endpoint.
     */
    private SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();

    /**
     * The hostname verifier to be used when verifying the validity of the endpoint.
     */
    private HostnameVerifier hostnameVerifier = new NoopHostnameVerifier();

    /**
     * The credentials provider for endpoints that require authentication.
     */
    private CredentialsProvider credentialsProvider;

    /**
     * The cookie store for authentication.
     */
    private CookieStore cookieStore;

    /**
     * Interface for deciding whether a connection can be re-used for subsequent requests and should be kept alive.
     **/
    private ConnectionReuseStrategy connectionReuseStrategy = new DefaultConnectionReuseStrategy();

    /**
     * When managing a dynamic number of connections for a given route, this strategy assesses whether a
     * given request execution outcome should result in a backoff
     * signal or not, based on either examining the Throwable that resulted or by examining
     * the resulting response (e.g. for its status code).
     */
    private ConnectionBackoffStrategy connectionBackoffStrategy = new DefaultBackoffStrategy();

    /**
     * Strategy interface that allows API users to plug in their own logic to control whether or not a retry
     * should automatically be done, how many times it should be retried and so on.
     */
    private ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy = new DefaultServiceUnavailableRetryStrategy();

    /**
     * Default headers to be sent.
     **/
    private Collection<? extends Header> defaultHeaders = new ArrayList<>(0);

    /**
     * Default strategy implementation for proxy host authentication.
     **/
    private AuthenticationStrategy proxyAuthenticationStrategy = new ProxyAuthenticationStrategy();

    /**
     * Determines whether circular redirects (redirects to the same location) should be allowed.
     **/
    private boolean circularRedirectsAllowed = true;

    /**
     * Determines whether authentication should be handled automatically.
     **/
    private boolean authenticationEnabled;

    /**
     * Determines whether redirects should be handled automatically.
     **/
    private boolean redirectsEnabled = true;

    /**
     * The executor service used to create a {@link #buildRequestExecutorService}.
     */
    private ExecutorService executorService;

    @Override
    public SimpleHttpClient getObject() {
        final CloseableHttpClient httpClient = buildHttpClient();
        final FutureRequestExecutionService requestExecutorService = buildRequestExecutorService(httpClient);
        final List<Integer> codes = this.acceptableCodes.stream().sorted().collect(Collectors.toList());
        return new SimpleHttpClient(codes, httpClient, requestExecutorService);
    }

    @Override
    public Class<?> getObjectType() {
        return SimpleHttpClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    /**
     * Build a HTTP client based on the current properties.
     *
     * @return the built HTTP client
     */
    private CloseableHttpClient buildHttpClient() {
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(getConnManage()).build();
        return client;
    }

    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }

    private static PoolingHttpClientConnectionManager getConnManage() {
        try {
            //采用绕过验证的方式处理https请求
            SSLContext sslcontext = createIgnoreVerifySSL();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[] { "TLSv1" },
                    null,
                    NoopHostnameVerifier.INSTANCE);//暂时关闭hostnameVerify
            //设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", sslsf)
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            //HttpClients.custom().setConnectionManager(connManager);
            return connManager;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    /**
     * Build a {@link FutureRequestExecutionService} from the current properties and a HTTP client.
     *
     * @param httpClient the provided HTTP client
     * @return the built request executor service
     */
    private FutureRequestExecutionService buildRequestExecutorService(final CloseableHttpClient httpClient) {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(this.threadsNumber, this.threadsNumber, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(this.queueSize));
        }
        return new FutureRequestExecutionService(httpClient, this.executorService);
    }

    /**
     * Destroy.
     */
    @PreDestroy
    public void destroy() {
        if (this.executorService != null) {
            this.executorService.shutdownNow();
            this.executorService = null;
        }
    }

    /**
     * The type Default http client.
     */
    public static class DefaultHttpClient extends SimpleHttpClientFactoryBean {
    }

    public void setThreadsNumber(final int threadsNumber) {
        this.threadsNumber = threadsNumber;
    }

    public void setQueueSize(final int queueSize) {
        this.queueSize = queueSize;
    }

    public void setMaxPooledConnections(final int maxPooledConnections) {
        this.maxPooledConnections = maxPooledConnections;
    }

    public void setMaxConnectionsPerRoute(final int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public void setAcceptableCodes(final List<Integer> acceptableCodes) {
        this.acceptableCodes = acceptableCodes;
    }

    public void setConnectionTimeout(final long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setRedirectionStrategy(final RedirectStrategy redirectionStrategy) {
        this.redirectionStrategy = redirectionStrategy;
    }

    public void setSslSocketFactory(final SSLConnectionSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public void setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public void setCredentialsProvider(final CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public void setCookieStore(final CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public void setConnectionReuseStrategy(final ConnectionReuseStrategy connectionReuseStrategy) {
        this.connectionReuseStrategy = connectionReuseStrategy;
    }

    public void setConnectionBackoffStrategy(final ConnectionBackoffStrategy connectionBackoffStrategy) {
        this.connectionBackoffStrategy = connectionBackoffStrategy;
    }

    public void setServiceUnavailableRetryStrategy(final ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy) {
        this.serviceUnavailableRetryStrategy = serviceUnavailableRetryStrategy;
    }

    public void setDefaultHeaders(final Collection<? extends Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public void setProxyAuthenticationStrategy(final AuthenticationStrategy proxyAuthenticationStrategy) {
        this.proxyAuthenticationStrategy = proxyAuthenticationStrategy;
    }

    public void setCircularRedirectsAllowed(final boolean circularRedirectsAllowed) {
        this.circularRedirectsAllowed = circularRedirectsAllowed;
    }

    public void setAuthenticationEnabled(final boolean authenticationEnabled) {
        this.authenticationEnabled = authenticationEnabled;
    }

    public void setRedirectsEnabled(final boolean redirectsEnabled) {
        this.redirectsEnabled = redirectsEnabled;
    }

    public void setExecutorService(final ExecutorService executorService) {
        this.executorService = executorService;
    }


}
