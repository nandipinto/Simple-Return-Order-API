package com.tn.returnkey.returnorderapi.service;

import java.io.IOException;

public interface OrderDataProviderService {

    /**
     * Prepare initial items and order data from a CSV file
     * @throws IOException if error occurs when loading and/or saving data into DB
     */
    void prepareOrders() throws Exception;
}
