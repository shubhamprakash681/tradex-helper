package in.shubhamprakash681.market_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tradex.market.integration")
public class ExternalMarketProperties {
    private boolean enabled;
    private String baseUrl;
    private String apiKey;
    private long refreshIntervalMs = 30000;
    private boolean publishOnRequest = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public long getRefreshIntervalMs() {
        return refreshIntervalMs;
    }

    public void setRefreshIntervalMs(long refreshIntervalMs) {
        this.refreshIntervalMs = refreshIntervalMs;
    }

    public boolean isPublishOnRequest() {
        return publishOnRequest;
    }

    public void setPublishOnRequest(boolean publishOnRequest) {
        this.publishOnRequest = publishOnRequest;
    }
}
