package in.shubhamprakash681.price_stream_service.controllers;

import in.shubhamprakash681.price_stream_service.dtos.PriceResponse;
import in.shubhamprakash681.price_stream_service.services.PriceQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prices")
public class PriceController {
    private final PriceQueryService priceQueryService;

    public PriceController(PriceQueryService priceQueryService) {
        this.priceQueryService = priceQueryService;
    }

    @GetMapping("/latest")
    List<PriceResponse> latest() {
        return priceQueryService.latest();
    }

    @GetMapping("/{symbol}")
    PriceResponse latestBySymbol(@PathVariable String symbol) {
        return priceQueryService.latestBySymbol(symbol);
    }

    @GetMapping("/history")
    List<PriceResponse> history(@RequestParam(required = false) String symbol,
                                @RequestParam(defaultValue = "100") int limit) {
        return priceQueryService.history(symbol, limit);
    }
}
