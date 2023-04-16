package org.clevertec.servlets.stocks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.clevertec.models.Stock;
import org.clevertec.providers.Factory;
import org.clevertec.providers.StockProvider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/stocks")
public class StockServlet extends HttpServlet {

    StockProvider stockProvider = Factory.getStockProvider();
    Gson gson = new Gson();

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
        List<Stock> stockList = stockProvider.findAll();
        String json = gson.toJson(stockList);
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
        Stock stock = gson.fromJson(body.get("data").toString(), Stock.class);

        int res = stockProvider.insert(stock);
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
        Stock stock = gson.fromJson(body.get("data").toString(), Stock.class);

        int res = stockProvider.update(stock);

        resp.setStatus(res == 1 ? 200 : 400);
    }
}
