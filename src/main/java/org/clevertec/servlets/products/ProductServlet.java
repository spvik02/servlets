package org.clevertec.servlets.products;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import java.util.List;

@WebServlet("/api/products")
public class ProductServlet extends HttpServlet {
    ProductProvider productProvider = Factory.getProductProvider();
    Gson gson = new Gson();

    /**
     * Writes to response json with all products
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
        int page;
        int pageSize;
        String sPage = req.getParameter("page");
        String sPageSize = req.getParameter("pagesize");
        try {
            page = parseNumGreaterZeroOrDefault(sPage, 1);
            pageSize = parseNumGreaterZeroOrDefault(sPageSize, 20);
        } catch (NumberFormatException e) {
            resp.sendError(400, "Please, make sure you have specified numbers > 0 for page and/or pagesize");
            return;
        }
        List<Product> productList = productProvider.findAllWithPagination(page, pageSize);
        String json = gson.toJson(productList);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(json);
            resp.setContentType("application/json");
            resp.setStatus(200);
        }
    }

    private int parseNumGreaterZeroOrDefault(String stringValue, int defaultValue) {

        int value;
        if (stringValue == null) value = defaultValue;
        else {
            value = Integer.parseInt(stringValue);
            if (value < 1) throw new NumberFormatException();
        }
        return value;
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
        Product product = gson.fromJson(body.get("data").toString(), Product.class);

        int res = productProvider.insert(product);
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
        Product product = gson.fromJson(body.get("data").toString(), Product.class);

        int res = productProvider.update(product);

        resp.setStatus(res == 1 ? 200 : 400);
    }
}
