package org.clevertec.servlets.products;

import com.google.gson.Gson;
import org.clevertec.models.Product;
import org.clevertec.providers.Factory;
import org.clevertec.providers.ProductProvider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/products/*")
public class ProductWithIdServlet extends HttpServlet {
    ProductProvider productProvider = Factory.getProductProvider();
    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        try {
            id = getIdFromPathInfo(req);
        } catch (NumberFormatException ex) {
            resp.sendError(400, "Please, pass int as a product id in URL after products/");
            return;
        }
        Product product = productProvider.find(id).get();
        String json = gson.toJson(product);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(json);
            resp.setContentType("application/json");
            resp.setStatus(200);
        }
    }

    /**
     * Deletes product with passed id and sends response with status 200.
     * Sends error with status 400 if path parameters is invalid or product with specified id wasn't found.
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
            resp.sendError(400, "Please, pass int as a product id in URL after products/");
            return;
        }
        int res = productProvider.remove(id);
        if (res == 1) {
            resp.setStatus(200);
        } else if (res == 0) {
            resp.sendError(400, "The item wasn't deleted. Please check if you have passed correct id in URL after products/");
        }
    }

    private int getIdFromPathInfo(HttpServletRequest req) {
        String sid = req.getPathInfo().substring(1);
        return Integer.parseInt(sid);
    }
}
