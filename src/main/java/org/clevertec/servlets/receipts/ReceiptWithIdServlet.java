package org.clevertec.servlets.receipts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

@WebServlet("/api/receipts/*")
public class ReceiptWithIdServlet extends HttpServlet {

    ReceiptProvider receiptProvider = Factory.getReceiptProvider();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
            .registerTypeAdapter(LocalDate.class, new LocalTimeAdapter().nullSafe())
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        try {
            id = getIdFromPathInfo(req);
        } catch (NumberFormatException ex) {
            resp.sendError(400, "Please, pass int as a stock id in URL after receipts/");
            return;
        }
        Receipt receipt = receiptProvider.find(id);
        String json = gson.toJson(receipt);
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
            resp.sendError(400, "Please, pass int as a receipt id in URL after receipts/");
            return;
        }
        int res = receiptProvider.remove(id);
        if (res == 1) {
            resp.setStatus(200);
        } else if (res == 0) {
            resp.sendError(400, "The item wasn't deleted. Please check if you have passed correct id in URL after receipts/");
        }
    }

    private int getIdFromPathInfo(HttpServletRequest req) {
        String sid = req.getPathInfo().substring(1);
        return Integer.parseInt(sid);
    }
}
