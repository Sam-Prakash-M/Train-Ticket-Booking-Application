package com.samprakash.ticketbookview;

import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/QR")
public class QRServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String pnr = req.getParameter("pnr");
        if (pnr == null) pnr = "UNKNOWN";

        resp.setContentType("image/png");

        BitMatrix matrix = null;
		try {
			matrix = new MultiFormatWriter().encode(
			        pnr, BarcodeFormat.QR_CODE, 200, 200
			);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        MatrixToImageWriter.writeToStream(matrix, "PNG", resp.getOutputStream());
    }
}
