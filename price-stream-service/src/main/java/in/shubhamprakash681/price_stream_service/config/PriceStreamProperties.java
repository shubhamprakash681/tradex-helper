package in.shubhamprakash681.price_stream_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "tradex.prices")
public class PriceStreamProperties {
    private String topic = "tradex.market.prices";
    private long generationIntervalMs = 2000;
    private int historyLimit = 500;
    private List<String> symbols = List.of(
            "NIFTYBEES",
            "SNIFTYBEES",
            "BANKBEES",
            "HDFCBANK",
            "RELIANCE",
            "TCS",
            "INFY",
            "SBIN",
            "ITC",
            "SUNPHARMA");

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getGenerationIntervalMs() {
        return generationIntervalMs;
    }

    public void setGenerationIntervalMs(long generationIntervalMs) {
        this.generationIntervalMs = generationIntervalMs;
    }

    public int getHistoryLimit() {
        return historyLimit;
    }

    public void setHistoryLimit(int historyLimit) {
        this.historyLimit = historyLimit;
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }
}
