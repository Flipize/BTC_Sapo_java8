package myPackage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class BtcServlet extends HttpServlet
{

    private static String getBtcPrice(String api_url, JsonParser jsonParser) throws IOException {
        URL url = new URL(api_url);
        URLConnection request = url.openConnection();
        request.connect();
        JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));

        JsonObject rootobj = root.getAsJsonObject();
        JsonElement bpi = rootobj.get("bpi");

        JsonObject bpiOb = bpi.getAsJsonObject();
        JsonObject usdOb = bpiOb.getAsJsonObject("USD");
        String rate = usdOb.get("rate").toString();
        rate = rate.substring(1, rate.length() - 6);
        return rate;
    }

    private static String getXrpPrice(String api_url, JsonParser jsonParser) throws IOException {
        URL url = new URL(api_url);
        URLConnection request = url.openConnection();
        request.connect();
        JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));

        JsonObject rootobj = root.getAsJsonObject();
        String xrpRate = rootobj.get("USD").toString();


        return xrpRate;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("image/png");
        String sURL = "https://api.coindesk.com/v1/bpi/currentprice.json";
        JsonParser jp = new JsonParser();

        String xrpUrl = "https://min-api.cryptocompare.com/data/price?fsym=XRP&tsyms=USD";
        JsonParser jp2 = new JsonParser();



        try {
            String rate = getBtcPrice(sURL, jp);

            BufferedImage img = null;
            try {
                img = ImageIO.read(new File(getClass().getResource("/images/original.png").toURI()));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }



            Calendar cal = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
            String time = sdf.format(cal.getTime());

            BufferedImage bufferedImage = img;

            Graphics2D valueG2d;
            Graphics2D timeG2d;
            Graphics2D xrp;

            if (bufferedImage != null) {

                valueG2d = bufferedImage.createGraphics();
                valueG2d.setColor(Color.yellow);
                valueG2d.setFont(new Font("SanSerif", Font.PLAIN, 30));
                valueG2d.drawString("BTC: $" + rate, img.getWidth() - 220, img.getHeight() - 43);

                timeG2d = bufferedImage.createGraphics();
                timeG2d.setColor(Color.yellow);
                timeG2d.setFont(new Font("SanSerif", Font.PLAIN, 20));
                timeG2d.drawString("updated: " + time, img.getWidth() - 190, img.getHeight() - 20);

                xrp = bufferedImage.createGraphics();
                xrp.setColor(Color.yellow);
                xrp.setFont(new Font("SanSerif", Font.PLAIN, 30));
                xrp.drawString("XRP: $" + getXrpPrice(xrpUrl, jp2), img.getWidth() - 220, img.getHeight() - 76);



                valueG2d.dispose();
                timeG2d.dispose();
                xrp.dispose();



                try {
                    ImageIO.write(bufferedImage, "png", res.getOutputStream());

                } catch (Exception e) {
                    System.out.println("Exception occured: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}