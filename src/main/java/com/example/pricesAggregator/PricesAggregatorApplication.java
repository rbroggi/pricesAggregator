package com.example.pricesAggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.IntStream;


@RestController
@EnableBinding(ConsumerChannel.class)
@SpringBootApplication
public class PricesAggregatorApplication {

	public static Logger logger = LoggerFactory.getLogger(PricesAggregatorApplication.class);
	private Map<String, double[]> ctpPrices = new HashMap<>();
	private Map<String, double[]> tradePrices = new HashMap<>();
	BiFunction<double[], double[], double[]> sumIndexWise = (arrayOne, arrayTwo) -> IntStream.range(0, Math.min(arrayOne.length, arrayTwo.length))
			.mapToDouble( i -> arrayOne[i] + arrayTwo[i])
			.toArray();
	BiFunction<double[], double[], double[]> subIndexWise = (arrayOne, arrayTwo) -> {
		return arrayTwo != null ?
				IntStream.range(0, Math.min(arrayOne.length, arrayTwo.length)).mapToDouble( i -> arrayOne[i] - arrayTwo[i]).toArray() :
				arrayOne; };

	private MessageChannel consumer;
	PricesAggregatorApplication(ConsumerChannel consumerChannel) {
		consumer = consumerChannel.consumer();
	}


    @StreamListener(ConsumerChannel.PRICE_INPUT_CHANNEL)
    public void process(PriceData priceData) {
		logger.info("Inserting " + priceData);
		double[] eventNetPrices = subIndexWise.apply(priceData.getPrices(), tradePrices.get(priceData.getTrade()));
		tradePrices.put(priceData.getTrade(), priceData.getPrices());
        ctpPrices.merge(priceData.getCtp(), eventNetPrices, sumIndexWise);
		logger.info("Inserted. ");
    }

    @GetMapping("/ctp/{ctp}")
	public double[] getCtpPrices(@PathVariable String ctp) {
		return ctpPrices.get(ctp);
	}

	@GetMapping("/ctps")
	public Map<String, double[]> getCtps() {
		return ctpPrices;
	}

	@GetMapping("/trade/{trade}")
	public double[] getTradePrices(@PathVariable String trade) {
		return tradePrices.get(trade);
	}

	@GetMapping("/trades")
	public Map<String, double[]> getTrades() {
		return tradePrices;
	}

	public static void main(String[] args) {
		SpringApplication.run(PricesAggregatorApplication.class, args);
	}
}

class PriceData {
	String ctp;
	String trade;
	double[] prices;


	public String getCtp() {
		return ctp;
	}

	public String getTrade() {
		return trade;
	}

	public double[] getPrices() {
		return prices;
	}

	@Override
	public String toString() {
		return ctp + " " + trade;
	}
}

interface ConsumerChannel {
	String PRICE_INPUT_CHANNEL = "priceInputChannel";

	@Input(PRICE_INPUT_CHANNEL)
	MessageChannel consumer();
}

