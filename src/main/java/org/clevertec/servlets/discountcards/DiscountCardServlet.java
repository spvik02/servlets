package org.clevertec.servlets.discountcards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.clevertec.adapters.LocalDateAdapter;
import org.clevertec.models.DiscountCard;
import org.clevertec.models.DiscountCardClassic;
import org.clevertec.providers.DiscountCardProvider;
import org.clevertec.providers.Factory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/api/discountCards")
public class DiscountCardServlet extends HttpServlet {

    DiscountCardProvider discountCardProvider = Factory.getDiscountCardProvider();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<DiscountCard> cards = discountCardProvider.findAll();
        String json = gson.toJson(cards);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(json);
            resp.setContentType("application/json");
            resp.setStatus(200);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        JsonObject body = new Gson().fromJson(req.getReader(), JsonObject.class);
        DiscountCard discountCard = gson.fromJson(body.get("data").toString(), DiscountCardClassic.class);

        int res = discountCardProvider.insert(discountCard);

        resp.setStatus(201);
    }

    /**
     * Updates entity with new data passed in the request body and sends response with status 200.
     * Sends error with status 500 if any field was empty.
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
        DiscountCard discountCard = gson.fromJson(body.get("data").toString(), DiscountCardClassic.class);

        int res = discountCardProvider.update(discountCard);

        resp.setStatus(res == 1 ? 200 : 400);
    }
}
