package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private static final String DATE_TIME_UTC = "<p>%s UTC</p>";
    /** 1 - Додаємо до класу анотацію @WebServlet(value = "/time")
     *  2 - реалізуємо один з методів HttpServlet, в нашому випадку doGet
     *  3 - На панелі Мавен клікаємо Lifecycle/clean, Lifecycle/package
     *  4 - Повинен з'явитися target з war file TimeServlet.war
     *      (за назву war файлу відповідає запис у pom.xml <build><finalName>TimeServlet</finalName>
     *  5 - Запускаемо application server Томкат та заходимо до адреси localhost:8080
     *  6 - натискаемо кнопочку "Manager app" та на сторінці http://localhost:8080/manager/html
     *      додаємо наш war file, для цього на сторінці "Выберите WAR файл для "
     *      натискаємо кнопочку "choise file" та вибираемо E:\JavaProjects\Servlet_M10\target\TimeServlet.war
     *      після натискаемо кнопочку "развернуть" або "deploy". Повинен з'явитися новий запис
     *  7 - набираемо адресу нашого сервлета localhost:8080/TimeServlet/time. Повинена з'явитися наша сторінка
     *      Зараз наш сервлет знаходиться в c:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps\TimeServlet\
     * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writeOut = resp.getWriter();
        writeOut.print("<html><boby>");
        writeOut.print("<h3>It is my first servlet with dateTime in UTC format</h3>");
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.size()>0) {
            String tz = req.getParameterMap().get("timezone")[0];
            if (tz.length()>1) {
                tz = String.format("<p>%s</p>", updateDateTimeForTimeZone(tz));
                writeOut.print(tz);
            }
        }  else {
            OffsetDateTime now = OffsetDateTime.now( ZoneOffset.UTC );
            String dts = now.toString().replace("T", " ").substring(0,19);
            writeOut.print(String.format(DATE_TIME_UTC, dts));
        }
        // TODO get query parameters
        parameterMap.forEach((name, value) -> {
            writeOut.print("<p>ParamName = ${name}; ParamValue = ${value}</p>"
                    .replace("${name}", name)
                    .replace("${value}", Arrays.toString(value)));
        });

        writeOut.print("</boby></html>");
        writeOut.close();
    }
    private static String updateDateTimeForTimeZone(String tZone) throws UnsupportedEncodingException {
        String tz = URLEncoder.encode(tZone, "UTF-8").replace("%27",""); // %27UTC+2%27
        try {
            ZoneId zoneId = ZoneId.of(tz);
            DateTimeFormatter zDTFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
            ZonedDateTime zonedDateTime = LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(zoneId);

            String utcFormatter = zonedDateTime.format(zDTFormatter);
            return utcFormatter;
        } catch(DateTimeException e) {
            return "Invalid time zone! Exception: "+e.getMessage();
        }
    }
}
