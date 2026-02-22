package utils;

import org.tn5250j.TN5250jConstants;
import org.tn5250j.framework.tn5250.Screen5250;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static coreAS400.LfaConnections.screen;

public class Actions {

    public static void typeKeys(int x, int y, String v) throws InterruptedException {
        Thread.sleep(500);
        screen.setCursor(x, y);
        Thread.sleep(500);
        screen.sendKeys(v);

    }
    public static void  typeEnter(int x, int y) throws InterruptedException {
        Thread.sleep(500);
        screen.setCursor(x, y);
        Thread.sleep(500);
        screen.sendAid(TN5250jConstants.AID_ENTER);
    }
    public static boolean waitUntilScreenContains(String keyword, int timeoutSec) throws Exception {
        long start = System.currentTimeMillis();
        long timeout = timeoutSec * 1000L;

        while (System.currentTimeMillis() - start < timeout) {

            waitForUnlock(screen);
            String text = getScreenText(screen);
            if (text.contains(keyword)) {
                return true;
            }

            Thread.sleep(500);
        }

        return false;
    }

    public static void waitForUnlock(Screen5250 screen) throws InterruptedException {

        while (screen.getOIA().getInputInhibited() != 0) {
            Thread.sleep(100);
        }
    }
    public static String getScreenText(Screen5250 screen) {

        char[] buffer = screen.getScreenAsChars();
        int rows = screen.getRows();
        int cols = screen.getColumns();

        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < rows; r++) {
            sb.append(new String(buffer, r * cols, cols));
            sb.append("\n");
        }

        return sb.toString();
    }
    public static void dumpFullScreen(Screen5250 screen) {

        System.out.println("===== FULL SCREEN =====");
        System.out.println(getScreenText(screen));
    }
    public static byte[] captureScreen(Component comp) {
        BufferedImage image = new BufferedImage(
                comp.getWidth(),
                comp.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();
        comp.printAll(g2d);
        g2d.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

