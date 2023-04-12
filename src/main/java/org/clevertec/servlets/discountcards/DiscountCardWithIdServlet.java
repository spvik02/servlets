package org.clevertec.servlets.discountcards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.clevertec.adapters.LocalDateAdapter;
import org.clevertec.models.DiscountCard;
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

@WebServlet("/api/discountCards/*")
public class DiscountCardWithIdServlet extends HttpServlet {

    DiscountCardProvider discountCardProvider = Factory.getDiscountCardProvider();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        String sid = req.getPathInfo().substring(1);
        try {
            id = Integer.parseInt(sid);
        } catch (NumberFormatException ex) {
            resp.sendError(400, "Please, pass int as a discount card id in URL after discountCard/");
            return;
        }
        DiscountCard card = discountCardProvider.find(id);
        String json = gson.toJson(card);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(json);
            resp.setContentType("application/json");
            resp.setStatus(200);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        String sid = req.getPathInfo().substring(1);
        try {
            id = Integer.parseInt(sid);
        } catch (NumberFormatException ex) {
            resp.sendError(400, "Please, pass int as a discount card id in URL after discountCard/");
            return;
        }
        int res = discountCardProvider.remove(id);
        if (res == 1) {
            resp.setStatus(200);
        } else if (res == 0) {
            resp.sendError(400, "The item wasn't deleted. Please check if you have passed correct id in URL after discountCard/");
        }
    }
}
