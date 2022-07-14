package com.xwbing.starter.es;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2022年07月05日 2:56 PM
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(EsRestClientProperties.class)
public class EsRestClientAutoConfiguration {
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    public EsRestClientAutoConfiguration() {
        log.info("Initializing moo-elasticsearch-starter EsRestClientAutoConfiguration");
    }

    private RestClientBuilder restClientBuilder(EsRestClientProperties esRestClientProperties) {
        if (StringUtils.isNotBlank(esRestClientProperties.getUsername()) && StringUtils
                .isNotBlank(esRestClientProperties.getPassword())) {
            return secureConnect(esRestClientProperties);
        } else {
            return simpleConnect(esRestClientProperties);
        }
    }

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient(EsRestClientProperties esRestClientProperties) {
        return new RestHighLevelClient(restClientBuilder(esRestClientProperties));
    }

    /**
     * 简单配置
     *
     * @return RestClient链接对象
     */
    private RestClientBuilder simpleConnect(EsRestClientProperties esRestClientProperties) {
        return RestClient.builder(setHttpHosts(esRestClientProperties)).setRequestConfigCallback(
                requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(esRestClientProperties.getConnectTimeout())
                        .setSocketTimeout(esRestClientProperties.getSocketTimeout()));
    }

    /**
     * 安全配置
     *
     * @return RestClient链接对象
     */
    private RestClientBuilder secureConnect(EsRestClientProperties esRestClientProperties) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(esRestClientProperties.getUsername(),
                esRestClientProperties.getPassword());
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        return RestClient.builder(setHttpHosts(esRestClientProperties)).setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
    }

    /**
     * 根据初始化hosts列表，配置httpHosts数组
     *
     * @return
     */
    private HttpHost[] setHttpHosts(EsRestClientProperties esRestClientProperties) {
        List<String> hosts = esRestClientProperties.getHosts();
        if (hosts != null && hosts.size() > 0) {
            HttpHost[] httpHosts = new HttpHost[hosts.size()];
            String scheme = SCHEME_HTTP;
            for (int i = 0; i < hosts.size(); i++) {
                String tmp[] = hosts.get(i).trim().split(":");
                int port = Integer.valueOf(tmp[1]);
                if (port == 443) {
                    scheme = SCHEME_HTTPS;
                }
                HttpHost newHttpHost = new HttpHost(tmp[0], port, scheme);
                httpHosts[i] = newHttpHost;
            }
            return httpHosts;
        }
        return null;
    }
}
