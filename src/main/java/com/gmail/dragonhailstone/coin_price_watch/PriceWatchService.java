package com.gmail.dragonhailstone.coin_price_watch;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceWatchService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "price-register", groupId = "price-watch")
    public void listenPriceRegister(String message) {
        System.out.println("Received message: " + message);
        // 메시지 파싱
        String[] parts = message.split(", ");
        String username = parts[0].split(": ")[1];
        String coin = parts[1].split(": ")[1];
        double targetPrice = Double.parseDouble(parts[2].split(": ")[1]);

        // 코인 가격 감시 (예시: 임의의 가격 API 사용)
        new Thread(() -> monitorPrice(username, coin, targetPrice)).start();
    }

    private void monitorPrice(String username, String coin, double targetPrice) {
        while (true) {
            double currentPrice = fetchCoinPrice(coin);
            if (currentPrice == targetPrice) {
                String matchMessage = String.format("User: %s, Coin: %s", username, coin);
                kafkaTemplate.send("price-match", matchMessage);
                break;
            }
            try {
                Thread.sleep(5000); // 5초마다 가격 확인
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private double fetchCoinPrice(String coin) {
        // 실제 API 호출 로직 필요
        return 35400.000000; // 예제: 랜덤 가격 생성
    }
}
