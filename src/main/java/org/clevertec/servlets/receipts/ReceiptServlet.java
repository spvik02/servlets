package org.clevertec.servlets.receipts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.clevertec.adapters.LocalDateAdapter;
import org.clevertec.adapters.LocalTimeAdapter;
import org.clevertec.models.Receipt;
import org.clevertec.providers.Factory;
import org.clevertec.providers.ReceiptProvider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/api/receipts")
public class ReceiptServlet extends HttpServlet {
    ReceiptProvider receiptProvider = Factory.getReceiptProvider();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
            .registerTypeAdapter(LocalDate.class, new LocalTimeAdapter().nullSafe())
            .create();

    /**
     * Writes to response json with all stocks
     *
     * @param req  an {@link HttpServletRequest} object that
     *             contains the request the client has made
     *             of the servlet
     * @param resp an {@link HttpServletResponse} object that
     *             contains the response the servlet sends
     *             to the client
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Receipt> receiptList = receiptProvider.findAll();
        String json = gson.toJson(receiptList);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(json);
            resp.setContentType("application/json");
            resp.setStatus(200);
        }
    }

    /**
     * Inserts entity with new data passed in the request body and sends response with status 201.
     * Sends error with status code 500 if entity with specified id already exists.
     *
     * @param req  an {@link HttpServletRequest} object that
     *             contains the request the client has made
     *             of the servlet
     * @param resp an {@link HttpServletResponse} object that
     *             contains the response the servlet sends
     *             to the client
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject body = new Gson().fromJson(req.getReader(), JsonObject.class);
        Receipt receipt = gson.fromJson(body.get("data").toString(), Receipt.class);

        int res = receiptProvider.insert(receipt);
        resp.setStatus(201);
    }

    /**
     * Updates entity with new data passed in the request body and sends response with status 200.
     * Sends error with status 500 if any field was empty or invalid and as a result of it it wasn't possible to update entity.
     * Sends error with status 400 if the field with specified id wasn't found.
     *
     * @param req  the {@link HttpServletRequest} object that
     *             contains the request the client made of
     *             the servlet
     * @param resp the {@link HttpServletResponse} object that
     *             contains the response the servlet returns
     *             to the client
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject body = new Gson().fromJson(req.getReader(), JsonObject.class);
        Receipt receipt = gson.fromJson(body.get("data").toString(), Receipt.class);

        int res = receiptProvider.update(receipt);

        resp.setStatus(res == 1 ? 200 : 400);
    }
}
