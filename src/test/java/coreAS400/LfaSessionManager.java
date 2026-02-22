package coreAS400;

import org.tn5250j.Session5250;
import org.tn5250j.SessionPanel;
import org.tn5250j.keyboard.KeyMnemonic;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

import static org.tn5250j.TN5250jConstants.*;

public class LfaSessionManager extends SessionPanel {

    private static final long serialVersionUID = 1L;

    public LfaSessionManager(Session5250 session) {
        super(session);

        this.sessionProperties = new Properties();
        this.sessionProperties.putAll(session.getConfiguration().getProperties());

        this.sessionProperties.put(SESSION_LOCALE, Locale.getDefault());
        this.getSession().getConfiguration().addSessionConfigListener(this);
    }

    @Override
    public void connect() {
        failIfConnected();

        if (!isSignificant(this.user)) {
            connectSimple();
        } else {
            if (this.embeddedSignon)
                connectEmbedded();
            else
                connectSimulated();

            Runnable runnable = new Runnable() {
                int tryConnection;

                @Override
                public void run() {
                    if (tryConnection++ < 50 && // If it is still not
                            // connected after 3
                            // seconds,
                            // stop with trying
                            !isConnected()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        SwingUtilities.invokeLater(this);
                    } else {
                        doAfterSignon();
                        doInitialCommand();
                        doVisibility();
                    }
                }
            };
            runnable.run();
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();

        Rectangle rect = this.getBounds();

        if (prevRect == null) {
            prevRect = rect;

            // Recalculate layout & repaint saat pertama kali
            this.revalidate();
            this.repaint();
            return;
        }

        if (rect.getHeight() != prevRect.getHeight() ||
                rect.getWidth() != prevRect.getWidth()) {

            this.revalidate();
            this.repaint();
        }

        prevRect = rect;
    }


    /**
     * If the <code>preferredSize</code> has been set to a non-<code>null</code>
     * value just returns it. Otherwise, the preferred size is calculated from
     * the font size to fill a rectangle of <code>80 x 24</code> character.
     * <p>
     * So this overrules the normal behaviour to delegate the preferred size
     * first to the UI component or the layoutmanager in case the UI returns
     * nothing.
     *
     * @return the value of the <code>preferredSize</code> property
     * @see #setPreferredSize
     * @see ComponentUI
     */
    @Override
    public Dimension getPreferredSize() {
        if (preferredSize == null)
            this.setPreferredSize(LfaSessionManager.deriveOptimalSize(this, this.getFont(), 80, 24));

        return super.getPreferredSize();
    }

    /**
     * Sets the preferred size of this component.
     *
     * @param preferredSize to use when laying out this component. If
     *                      <code>preferredSize</code> is <code>null</code>, the UI will
     *                      be asked for the preferred size.
     * @beaninfo preferred: true bound: true description: The preferred size of
     * the component.
     */
    @Override
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
        super.setPreferredSize(preferredSize);
    }

    // ============================================================================
    // P r i v a t e M e t h o d s a n d F i e l d s
    // ============================================================================
    private void connectSimple() {

        super.connect();
    }

    private void connectEmbedded() {
        if (isSignificant(user))
            sessionProperties.put("SESSION_CONNECT_USER", user);

        if (Objects.nonNull(password))
            sessionProperties.put("SESSION_CONNECT_PASSWORD", password);

        if (isSignificant(program))
            sessionProperties.put("SESSION_CONNECT_PROGRAM", program);

        if (isSignificant(menu))
            sessionProperties.put("SESSION_CONNECT_MENU", menu);

        if (isSignificant(library))
            sessionProperties.put("SESSION_CONNECT_LIBRARY", library);

        super.connect();
    }

    private void connectSimulated() {
        StringBuilder sb = new StringBuilder();

        if (isSignificant(user))
            sb.append(user);
        if (!isFieldLength(user))
            sb.append(KeyMnemonic.TAB.mnemonic);

        if (isSignificant(password))
            sb.append(password);
        if (!isFieldLength(password))
            sb.append(KeyMnemonic.TAB.mnemonic);

        if (isSignificant(program) || isSignificant(menu) || isSignificant(library)) {
            if (isSignificant(program))
                sb.append(program);
            if (!isFieldLength(program))
                sb.append(KeyMnemonic.TAB.mnemonic);

            if (isSignificant(menu))
                sb.append(menu);
            if (!isFieldLength(menu))
                sb.append(KeyMnemonic.TAB.mnemonic);

            if (isSignificant(library))
                sb.append(library);
        }
        sb.append(KeyMnemonic.ENTER.mnemonic);

        super.connect();

        this.getScreen().sendKeys(sb.toString());
    }

    private void doAfterSignon() {
        if (isSignificant(afterSignon))
            this.getScreen().sendKeys(afterSignon);
    }

    private void doInitialCommand() {
        if (isSignificant(initialCommand)) {
            this.getScreen().sendKeys(initialCommand + KeyMnemonic.ENTER.mnemonic);
        }
    }

    private void doVisibility() {
        if (!isVisible() && visibilityInterval > 0) {
            Timer t = new Timer(visibilityInterval, new DoVisible());
            t.setRepeats(false);
            t.start();
        } else if (!isVisible()) {
            new DoVisible().run();
        }
    }

    private boolean isFieldLength(String param) {
        return Objects.nonNull(param) && param.length() == 10;
    }

    private void failIfConnected() {
        if (Objects.nonNull(session.getVT()) && isConnected())
            throw new IllegalStateException("Cannot change property after being connected!");
    }

    private void failIfNot10(String param) {
        if (Objects.nonNull(param) && param.length() > 13)
            throw new IllegalArgumentException("The length of the parameter cannot exceed 10 positions!");
    }

    private Dimension preferredSize;
    private Rectangle prevRect;
    private Properties sessionProperties;

    private boolean embeddedSignon;
    private String user;
    private String password;
    private String library;
    private String menu;
    private String program;
    private String initialCommand;
    private String afterSignon;
    private int visibilityInterval;

    // ============================================================================
    // U t i l i t y M e t h o d s
    // ============================================================================
    private static boolean isSignificant(String param) {
        if (Objects.nonNull(param) && param.length() != 0)
            return true;

        return false;
    }

    private static Dimension deriveOptimalSize(JComponent comp, Font f, int nrChars, int nrLines) {
        return deriveOptimalSize(comp, f, comp.getBorder(), nrChars, nrLines);
    }

    private static Dimension deriveOptimalSize(JComponent comp, Font f, Border brdr, int nrChars, int nrLines) {
        if (comp == null)
            return null;

        FontMetrics fm = null;
        Graphics g = comp.getGraphics();

        if (Objects.nonNull(g))
            fm = g.getFontMetrics(f);
        else
            fm = comp.getFontMetrics(f);

        Insets insets = brdr == null ? new Insets(0, 0, 0, 0) : brdr.getBorderInsets(comp);
        int height = fm.getHeight() * nrLines + insets.top + insets.bottom;
        int width = nrChars * fm.charWidth('M') + insets.left + insets.right;

        return new Dimension(width + 2, height);
    }

    public void setNoSaveConfigFile() {
        this.sesConfig.removeProperty("saveme");
    }

    private class DoVisible implements ActionListener, Runnable {

        @Override
        public void actionPerformed(ActionEvent event) {
            SwingUtilities.invokeLater(this);
        }

        @Override
        public void run() {
            LfaSessionManager.this.setVisible(true);

            // Refresh layout & repaint
            LfaSessionManager.this.revalidate();
            LfaSessionManager.this.repaint();

            LfaSessionManager.this.requestFocusInWindow();
        }
    }

}
