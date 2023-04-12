package org.clevertec.servlets.stocks;

import com.google.gson.Gson;
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

@WebServlet("/api/stocks/*")
public class StockWithIdServlet extends HttpServlet {
    StockProvider stockProvider = Factory.getStockProvider();
    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        try {
            id = getIdFromPathInfo(req);
        } catch (NumberFormatException ex) {
            resp.sendError(400, "Please, pass int as a stock id in URL after stocks/");
            return;
        }
        Stock stock = stockProvider.find(id).get();
        String json = gson.toJson(stock);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(json);
            resp.setContentType("application/json");
            resp.setStatus(200);
        }
    }

    /**
     * Deletes stock with passed id and sends response with status 200.
     * Sends error with status 400 if path parameters is invalid or stock with specified id wasn't found.
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        try {
            id = getIdFromPathInfo(req);
        } catch (NumberFormatException ex) {
            resp.sendError(400, "Please, pass int as a product id in URL after stocks/");
            return;
        }
        int res = stockProvider.remove(id);
        if (res == 1) {
            resp.setStatus(200);
        } else if (res == 0) {
            resp.sendError(400, "The item wasn't deleted. Please check if you have passed correct id in URL after stocks/");
        }
    }

    private int getIdFromPathInfo(HttpServletRequest req) {
        String sid = req.getPathInfo().substring(1);
        return Integer.parseInt(sid);
    }
}
