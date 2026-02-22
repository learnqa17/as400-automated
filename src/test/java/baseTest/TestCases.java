package baseTest;

import coreAS400.LfaConnections;
import utils.Actions;

import java.nio.file.Files;
import java.nio.file.Paths;

import static coreAS400.LfaConnections.screen;
import static coreAS400.LfaConnections.sessionPanel;
import static utils.Actions.captureScreen;
import static utils.Actions.waitForUnlock;

public class TestCases {
    public static void testConnection() throws Exception {

        LfaConnections.connect();
        LfaConnections.setupCloseHandler();
        Thread.sleep(3000);
        Actions.dumpFullScreen(screen);

        byte[] screenshotBytes = captureScreen(sessionPanel);
        Files.write(Paths.get("test_login_screen.jpg"),screenshotBytes); //save local
        // String base64 = Base64.getEncoder().encodeToString(screenshotBytes):
        // can upload to html report extent or allure report

    }
    public static void testLogin() throws Exception {

        waitForUnlock(screen);
        Actions.typeKeys(6, 53, LfaConnections.USER);
        Actions.typeKeys(7, 53, LfaConnections.PASS);
        Actions.typeEnter(1,1);
        waitForUnlock(screen);
        if (Actions.waitUntilScreenContains("These facilities are solely for the use of authorized employees", 3)) {
            Actions.typeEnter(1, 1);
        }
        if (Actions.waitUntilScreenContains(" Previous sign-on", 5)) {
            Actions.dumpFullScreen(screen);
            Actions.typeEnter(1, 1);
        }
        Actions.dumpFullScreen(screen);

    }
    public static void testCommandEntry() throws Exception {

        Actions.typeKeys(18, 7, "to uat");
        Actions.typeEnter(1,1);
        Actions.typeKeys(18, 7, "d");
        Actions.typeEnter(1,1);
        Actions.dumpFullScreen(screen);

    }
}
