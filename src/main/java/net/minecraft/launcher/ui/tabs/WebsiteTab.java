package net.minecraft.launcher.ui.tabs;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.OperatingSystem;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.net.URL;

public class WebsiteTab extends JScrollPane {
    private final JTextPane blog = new JTextPane();
    private final Launcher launcher;

    public WebsiteTab(Launcher launcher) {
        this.launcher = launcher;

        this.blog.setEditable(false);
        this.blog.setMargin(null);
        this.blog.setBackground(Color.DARK_GRAY);
        this.blog.setContentType("text/html");
        this.blog.setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center><h1>Loading page..</h1></center></font></body></html>");
        this.blog.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent he) {
                if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                    try {
                        OperatingSystem.openLink(he.getURL().toURI());
                    } catch (Exception e) {
                        Launcher.getInstance().println("Unexpected exception opening link " + he.getURL(), e);
                    }
            }
        });
        setViewportView(this.blog);
    }

    public void setPage(final String url) {
        Thread thread = new Thread("Update website tab") {
            public void run() {
                try {
                    WebsiteTab.this.blog.setPage(new URL(url));
                } catch (Exception e) {
                    Launcher.getInstance().println("Unexpected exception loading " + url, e);
                    WebsiteTab.this.blog.setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center><h1>Failed to get page</h1><br>" + e.toString() + "</center></font></body></html>");
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public Launcher getLauncher() {
        return this.launcher;
    }
}


