package net.minecraft.launcher.ui.popups.profile;

import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.OperatingSystem;
import net.minecraft.launcher.locale.LocaleHelper;
import net.minecraft.launcher.profile.Profile;
import net.minecraft.launcher.profile.ProfileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

public class ProfileEditorPopup extends JPanel
        implements ActionListener {
    private final Launcher launcher;
    private final Profile originalProfile;
    private final Profile profile;
    private ResourceBundle resourceBundle = LocaleHelper.getMessages();
    private final JButton saveButton = new JButton(resourceBundle.getString("save.profile"));
    private final JButton cancelButton = new JButton(resourceBundle.getString("cancel"));
    private final JButton browseButton = new JButton("Open Game Dir");
    private final ProfileInfoPanel profileInfoPanel;
    private final ProfileVersionPanel profileVersionPanel;
    private final ProfileJavaPanel javaInfoPanel;

    public ProfileEditorPopup(Launcher launcher, Profile profile) {
        super(true);

        this.launcher = launcher;
        this.originalProfile = profile;
        this.profile = new Profile(profile);
        this.profileInfoPanel = new ProfileInfoPanel(this);
        this.profileVersionPanel = new ProfileVersionPanel(this);
        this.javaInfoPanel = new ProfileJavaPanel(this);

        this.saveButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        this.browseButton.addActionListener(this);

        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout(0, 5));
        createInterface();
    }

    protected void createInterface() {
        JPanel standardPanels = new JPanel(true);
        standardPanels.setLayout(new BoxLayout(standardPanels, 1));
        standardPanels.add(this.profileInfoPanel);
        standardPanels.add(this.profileVersionPanel);
        standardPanels.add(this.javaInfoPanel);

        add(standardPanels, "Center");

        JPanel buttonPannel = new JPanel();
        buttonPannel.setLayout(new BoxLayout(buttonPannel, 0));
        buttonPannel.add(this.cancelButton);
        buttonPannel.add(Box.createGlue());
        buttonPannel.add(this.browseButton);
        buttonPannel.add(Box.createHorizontalStrut(5));
        buttonPannel.add(this.saveButton);
        add(buttonPannel, "South");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.saveButton) {
            try {
                ProfileManager manager = this.launcher.getProfileManager();
                Map profiles = manager.getProfiles();

                if (!this.originalProfile.getName().equals(this.profile.getName())) {
                    profiles.remove(this.originalProfile.getName());

                    while (profiles.containsKey(this.profile.getName())) {
                        this.profile.setName(this.profile.getName() + "_");
                    }
                }

                // this.profile.refreshUUID();

                profiles.put(this.profile.getName(), this.profile);

                manager.saveProfiles();
                manager.fireRefreshEvent();
            } catch (IOException ex) {
                this.launcher.println("Couldn't save profiles whilst editing " + this.profile.getName(), ex);
            }
            closeWindow();
        } else if (e.getSource() == this.browseButton) {
            OperatingSystem.openFolder(this.profile.getGameDir() == null ? this.launcher.getWorkingDirectory() : this.profile.getGameDir());
        } else {
            closeWindow();
        }
    }

    private void closeWindow() {
        Window window = (Window) getTopLevelAncestor();
        window.dispatchEvent(new WindowEvent(window, 201));
    }

    public Launcher getLauncher() {
        return this.launcher;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public static void showEditProfileDialog(Launcher launcher, Profile profile) {
        JDialog dialog = new JDialog(launcher.getFrame(), LocaleHelper.getMessages().getString("profile.editor"), true);
        ProfileEditorPopup editor = new ProfileEditorPopup(launcher, profile);
        dialog.add(editor);
        //dialog.setPreferredSize(new Dimension(450, 300));
        dialog.pack();
        dialog.setLocationRelativeTo(launcher.getFrame());
        dialog.setVisible(true);
    }
}
