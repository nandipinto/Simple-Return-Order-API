package com.tn.returnkey.returnorderapi.service.impl;

import com.tn.returnkey.returnorderapi.entity.Order;
import com.tn.returnkey.returnorderapi.model.DefaultCsvInputFields;
import com.tn.returnkey.returnorderapi.repo.OrderRepository;
import com.tn.returnkey.returnorderapi.service.OrderDataProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderDataProviderServiceImpl implements OrderDataProviderService {

    static final String DEFAULT_ORDER_DATA_FILE = "data/orders.csv";
    static final String DELIMITER = ",";

    @Autowired
    private OrderRepository orderRepository;

    @PostConstruct
    public void init() throws Exception {
        prepareOrders();
    }

    @Override
    @Transactional
    public void prepareOrders() throws Exception {
        log.debug("Preparing default Order data...");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_ORDER_DATA_FILE);
        try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); BufferedReader reader = new BufferedReader(streamReader)) {

            int lineNo = 0;
            String line = null;
            List<Order> orders = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                lineNo++;

                if (lineNo > 1 && StringUtils.hasText(line)) {
                    String[] data = line.split(DELIMITER);
                    if (data.length >= DefaultCsvInputFields.values().length) {
                        String orderId = data[DefaultCsvInputFields.ORDER_ID.ordinal()];
                        String emailAddress = data[DefaultCsvInputFields.EMAIL_ADDRESS.ordinal()];
                        String sku = data[DefaultCsvInputFields.SKU.ordinal()];
                        Integer qty = 0;
                        try {
                            qty = Integer.parseInt(data[DefaultCsvInputFields.QUANTITY.ordinal()]);
                        }
                        catch (NumberFormatException e) {
                        }
                        double price = 0.0;
                        try {
                            price = Double.parseDouble(data[DefaultCsvInputFields.PRICE.ordinal()]);
                        }
                        catch (NumberFormatException e) {
                        }

                        String itemName = data[DefaultCsvInputFields.ITEM_NAME.ordinal()];
                        Order order = orderRepository.save(Order.builder().orderId(orderId).email(emailAddress).sku(sku).quantity(qty).price(price).itemName(itemName).build());
                    }
                }
            }
        }
        log.debug("Done preparing default Orders data...");
    }
}
