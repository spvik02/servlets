package org.clevertec.servlets.receipts;

import com.itextpdf.text.DocumentException;
import org.clevertec.managers.ReceiptGeneratorManager;
import org.clevertec.managers.ReceiptPrintManager;
import org.clevertec.models.Receipt;
import org.clevertec.providers.Factory;
import org.clevertec.utils.ParametersUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/receipts/pdf/*")
public class ReceiptPdfServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ReceiptPrintManager receiptPrintManager = new ReceiptPrintManager();
        ReceiptGeneratorManager receiptGeneratorManager = new ReceiptGeneratorManager();
        String[] parameters = ParametersUtil.splitParameters(req.getPathInfo().substring(1), "&");
        String filePath;

        try {
            Receipt receipt = receiptGeneratorManager.generateReceiptFromParameters(parameters);
            filePath = receiptPrintManager.writeToPdfForSend(receipt, Factory.getProductProvider(), resp.getOutputStream());
        } catch (NumberFormatException e) {
            resp.sendError(400, "Please, make sure passed parameters have correct format " +
                    "(pairs of productId-productQty are separated by &. " +
                    "Add this if DiscountCard was provided[&Card-cardId])");
            return;
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-disposition", "attachment; filename = " + filePath);
    }
}
