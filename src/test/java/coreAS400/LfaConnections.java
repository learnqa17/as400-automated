package coreAS400;

import org.tn5250j.Session5250;
import org.tn5250j.SessionConfig;
import org.tn5250j.SessionPanel;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.interfaces.ConfigureFactory;
import utils.Actions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import java.util.Properties;

public class LfaConnections {
    public static Session5250 session;
    public static LfaSessionManager lfaSessionManager;
    public static Screen5250 screen;
    public static JFrame frame;
    public static SessionPanel sessionPanel;
//    public static String USER = "DUAT010";
//    public static String PASS = "U8K9S012OX";
//    private static final String HOST = "CGKAIDA2.AIA.BIZ";
//    private static final String PORT = "992";
//    private static final String WORKSTATIONID = "DUAT010";
    public static String USER = "nanangf";
    public static String PASS = "pwd12345";
    private static final String HOST = "pub400.rzkh.de";
    private static final String PORT = "992";
    private static final String WORKSTATIONID = "DEVTEST1";

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    public static void connect() throws Exception {

        initSession();
        initUI();

        screen = lfaSessionManager.getSession().getScreen();

        Actions.waitForUnlock(screen);

        System.out.println("Connected. Screen size: "
                + screen.getRows() + " x " + screen.getColumns());
    }

    public static void initSession() throws Exception {

        File tempDir = File.createTempFile("tn5250j", "settings");
        tempDir.delete();
        tempDir.mkdir();
        System.setProperty("emulator.settingsDirectory", tempDir.getAbsolutePath());
        System.setProperty("user.language", "en");
        System.setProperty("user.country", "US");
        Locale.setDefault(Locale.US);

        ConfigureFactory.getInstance();
        org.tn5250j.tools.LangTool.init();

        Properties props = getProps();

        SessionConfig config = new SessionConfig(HOST, HOST);
        config.setProperty("font", "Consolas");

        session = new Session5250(props, HOST, HOST, config);

        lfaSessionManager = new LfaSessionManager(session);
        lfaSessionManager.setNoSaveConfigFile();
        lfaSessionManager.connect();

    }

    private static Properties getProps() {
        Properties props = new Properties();
        props.put(TN5250jConstants.SESSION_HOST, HOST);
        props.put(TN5250jConstants.SESSION_HOST_PORT, PORT);
        props.put(TN5250jConstants.SSL_TYPE, "SSL");
        props.put(TN5250jConstants.SESSION_SCREEN_SIZE, TN5250jConstants.SCREEN_SIZE_24X80_STR);
        props.put(TN5250jConstants.SESSION_TN_ENHANCED, "true");
        props.put(TN5250jConstants.SESSION_CODE_PAGE, "Cp37");
        props.put(TN5250jConstants.SESSION_DEVICE_NAME, WORKSTATIONID);
        return props;
    }

    public static void initUI() {

        frame = new JFrame("LFA AUTOMATION PROCCESS GASS TERUSSS !!!");
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

    public static void setupCloseHandler() {

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
}
