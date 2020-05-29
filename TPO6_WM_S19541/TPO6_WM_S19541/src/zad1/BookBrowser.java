package zad1;

import org.apache.derby.iapi.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class BookBrowser extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String charset = "ISO8859-2";
        request.setCharacterEncoding(charset);
        response.setContentType("text/html; charset=" + charset);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body>");
        String formFile = getInitParameter("regexFormFile");
        ServletContext context = getServletContext();
        InputStream in = context.getResourceAsStream("/WEB-INF/"+formFile);
        BufferedReader br = new BufferedReader( new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null) out.println(line);

        MyDataBase myDb = new MyDataBase();
        myDb.createDb(getServletContext().getRealPath("WEB-INF/derby_data/Authors.txt"),getServletContext().getRealPath("WEB-INF/derby_data/Books.txt"));
        try {
           String query_string = "select BOOK.title,AUTHOR.FirstName,AUTHOR.LastName,BOOK.Price from BOOK,AUTHOR where BOOK.AuthorId = AUTHOR.AuthorId";
            Enumeration pnams = request.getParameterNames();
            while (pnams.hasMoreElements()) {
                String name = pnams.nextElement().toString().toLowerCase();
                String value = request.getParameter(name).toLowerCase();
                if(value.length()==0)
                    continue;
                switch(name){
                    case "title":
                        query_string += " AND lower(BOOK.title) LIKE '%"+value+"%'";
                        break;
                    case "author":
                        query_string += " AND lower(AUTHOR.LastName) LIKE '%"+value+"%'";
                        break;
                    case "maxprice":
                        query_string += " AND BOOK.Price <= "+value;
                        break;
                }
            }
            ResultSet resultSet = myDb.getResultFromQuery(query_string);
            out.append("<h3>Results:</h3>");
            out.println("<ol>");
            while (resultSet.next()) {
                out.println("<li>");
                out.append(String.format("\"%s\",", resultSet.getString(1)));
                out.append(String.format("%s ", resultSet.getString(2)));
                out.append(String.format("%s,", resultSet.getString(3)));
                out.append(String.format("price: %s", resultSet.getString(4)));
                out.println("</li>");
            }
            out.println("</ol>");
            resultSet.close();
        } catch (Exception ex) {
            out.append(String.format("\"error\": \"%s\"", ex.toString().replace('"', '\'')));
            ex.printStackTrace();
        }
    }
    }

