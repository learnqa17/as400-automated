import coreAS400.LfaSessionManager;
import org.tn5250j.Session5250;
import org.tn5250j.SessionConfig;
import org.tn5250j.SessionPanel;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.interfaces.ConfigureFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import java.util.Properties;

public class MainApp {
    private Session5250 session;
    private LfaSessionManager lfaSessionManager;
    private Screen5250 screen;

    private JFrame frame;
    private SessionPanel sessionPanel;

    private final String HOST = "CGKAIDA2.AIA.BIZ";
    private final String PORT = "992";
    private final String USER = "DUAT010";
    private final String PASS = "GUKT834BHO";
    private final String WORKSTATIONID = "DUAT010";

    private final int WIDTH = 1024;
    private final int HEIGHT = 768;

    public static void main(String[] args) {

        new MainApp().run();
    }

    // ===================== MAIN FLOW =====================

    public void run() {
        try {

            connect();
            setupCloseHandler();
            waitLoginScreen();
            dumpFullScreen(screen);
            doLogin();
//            waitMainMenu();
//            dumpFullScreen(screen);
//            navigateMainMenu();
//            closeApp();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== CONNECT =====================

    private void connect() throws Exception {

        initSession();
        initUI();

        screen = lfaSessionManager.getSession().getScreen();

        waitForUnlock(screen);

        System.out.println("Connected. Screen size: "
                + screen.getRows() + " x " + screen.getColumns());
    }

    private void initSession() throws Exception {

        File tempDir = File.createTempFile("tn5250j", "settings");
        tempDir.delete();
        tempDir.mkdir();
        System.setProperty("emulator.settingsDirectory", tempDir.getAbsolutePath());
        System.setProperty("user.language", "en");
        System.setProperty("user.country", "US");
        Locale.setDefault(Locale.US);

        ConfigureFactory.getInstance();
        org.tn5250j.tools.LangTool.init();

        Properties props = new Properties();
        props.put(TN5250jConstants.SESSION_HOST, HOST);
        props.put(TN5250jConstants.SESSION_HOST_PORT, PORT);
        props.put(TN5250jConstants.SSL_TYPE, "SSL");
        props.put(TN5250jConstants.SESSION_SCREEN_SIZE, TN5250jConstants.SCREEN_SIZE_24X80_STR);
        props.put(TN5250jConstants.SESSION_TN_ENHANCED, "true");
        props.put(TN5250jConstants.SESSION_CODE_PAGE, "Cp37");
        props.put(TN5250jConstants.SESSION_DEVICE_NAME, WORKSTATIONID);

        SessionConfig config = new SessionConfig(HOST, HOST);
        config.setProperty("font", "Consolas");

        session = new Session5250(props, HOST, HOST, config);

        lfaSessionManager = new LfaSessionManager(session);
        lfaSessionManager.setNoSaveConfigFile();
        lfaSessionManager.connect();

    }

    private void initUI() {

        frame = new JFrame("LFA AUTOMATION PROCCESS GAS TERUSSSS !!!");
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sessionPanel = new SessionPanel(session);

        JPanel main = new JPanel(new BorderLayout());
        main.add(sessionPanel, BorderLayout.CENTER);

        frame.setContentPane(main);
        frame.setVisible(true);

    }

    private void setupCloseHandler() {

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (session != null) {
                        session.disconnect();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // ===================== LOGIN FLOW =====================

    private void waitLoginScreen() throws Exception {
//        waitUntilContains("CPF1296");
        Thread.sleep(2000);
    }

    private void doLogin() throws Exception {

        waitForUnlock(screen);
        screen.setCursor(06, 53);
        screen.sendKeys(USER);
        screen.setCursor(07, 53);
        screen.sendKeys(PASS);
        screen.sendAid(TN5250jConstants.AID_ENTER);

        waitForUnlock(screen);
    }

    // ===================== MAIN MENU =====================

    private void waitMainMenu() throws Exception {
        waitUntilContains("Selection");
    }

    private void navigateMainMenu() throws Exception {

        waitForUnlock(screen);
        screen.setCursor(20, 7);
        screen.sendKeys("90");
        Thread.sleep(1000);
        screen.sendAid(TN5250jConstants.AID_ENTER);

        waitForUnlock(screen);
    }

    // ===================== UTIL =====================

    private void waitForUnlock(Screen5250 screen) throws InterruptedException {

        while (screen.getOIA().getInputInhibited() != 0) {
            Thread.sleep(100);
        }
    }

    private void waitUntilContains(String keyword) throws Exception {

        while (true) {

            waitForUnlock(screen);

            String text = getScreenText(screen);

            if (text.contains(keyword)) {
                break;
            }
            Thread.sleep(200);
        }
    }

    private String getScreenText(Screen5250 screen) {

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

    private void dumpFullScreen(Screen5250 screen) {

        System.out.println("===== FULL SCREEN =====");
        System.out.println(getScreenText(screen));
    }
    private void closeApp() {

        try {
            if (session != null) {
                session.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (frame != null) {
            frame.dispose();
        }
    }
}
