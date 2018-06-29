
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Paths;


public class SaveFile extends HttpServlet {

/*    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.println(loadPage(new File(".").getCanonicalPath()+"/pages/signDoc.html"));
        out.flush();
        out.close();
    }*/

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">

        String fileName = null; // MSIE fix.
        try {
            fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        InputStream fileContent = null;

        fileContent = filePart.getInputStream();


        System.out.println("fileName = " + fileName);

        byte[] buffer = new byte[fileContent.available()];
        fileContent.read(buffer);
        File targetFile = new File("data/" + fileName);
        targetFile = targetFile.getAbsoluteFile();
        targetFile.createNewFile();
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.flush();
        outStream.close();
    }
}