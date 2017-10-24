package org.iotacontrolcenter.ui.dialog;


import org.iotacontrolcenter.ui.app.Constants;
import org.iotacontrolcenter.ui.app.Main;
import org.iotacontrolcenter.ui.properties.locale.Localizer;
import org.iotacontrolcenter.ui.properties.source.PropertySource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class IccSettingsDialog extends JDialog {

    private static final long serialVersionUID = -5108543673619516848L;


    public JButton cancel;
    private ActionListener ctlr;
    public JTextField nbrRefreshTimeTextField;
    public JTextField nodeInfoRefreshTimeTextField;
    public JTextField iotaDownloadLinkTextField;
    public JPanel panel;
    private PropertySource propertySource;
    public JCheckBox runRefresh;
    public JButton save;
    private Localizer localizer;

    public IccSettingsDialog(Localizer localizer, PropertySource propertySource, ActionListener ctlr) {
        super();
        this.localizer = localizer;
        this.propertySource = propertySource;
        this.ctlr = ctlr;
        init();
    }

    private void init() {
        setIconImages(Main.icons);
        setTitle(localizer.getLocalText("dialogTitleIccSettings"));
        setModal(true);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        panel = new JPanel(new SpringLayout());

        JLabel nbrRefreshTime = new JLabel(localizer.getLocalText("fieldLabelNeighborRefreshTime") + ":", JLabel.TRAILING);
        panel.add(nbrRefreshTime);
        nbrRefreshTimeTextField = new JTextField(5);
        nbrRefreshTimeTextField.setName(localizer.getLocalText("fieldLabelNeighborRefreshTime"));
        nbrRefreshTimeTextField.setToolTipText(localizer.getLocalText("fieldLabelNeighborRefreshTimeTooltip"));
        nbrRefreshTime.setLabelFor(nbrRefreshTimeTextField);
        panel.add(nbrRefreshTimeTextField);

        JLabel nodeInfoRefreshTime = new JLabel(localizer.getLocalText("fieldLabelNodeInfoRefreshTime") + ":", JLabel.TRAILING);
        panel.add(nodeInfoRefreshTime);
        nodeInfoRefreshTimeTextField = new JTextField(10);
        nodeInfoRefreshTimeTextField.setName(localizer.getLocalText("fieldLabelNodeInfoRefreshTime"));
        nodeInfoRefreshTimeTextField.setToolTipText(localizer.getLocalText("fieldLabelNodeInfoRefreshTimeTooltip"));
        nodeInfoRefreshTime.setLabelFor(nodeInfoRefreshTimeTextField);
        panel.add(nodeInfoRefreshTimeTextField);

        JLabel iotaDownloadLink = new JLabel(localizer.getLocalText("fieldLabelIotaDownloadLink") + ":", JLabel.TRAILING);
        panel.add(iotaDownloadLink);
        iotaDownloadLinkTextField = new JTextField(20);
        iotaDownloadLinkTextField.setName(localizer.getLocalText("fieldLabelIotaDownloadLink"));
        iotaDownloadLinkTextField.setToolTipText(localizer.getLocalText("fieldLabelIotaDownloadLinkTooltip"));
        iotaDownloadLink.setLabelFor(iotaDownloadLinkTextField);
        panel.add(iotaDownloadLinkTextField);

        JLabel runRefreshLabel = new JLabel(localizer.getLocalText("buttonLabelRunIotaRefresh") + ":");
        panel.add(runRefreshLabel);
        runRefresh = new JCheckBox(null, null, true);
        runRefresh.setToolTipText(localizer.getLocalText("buttonLabelRunIotaRefreshTooltip"));
        runRefresh.setAlignmentX(Component.CENTER_ALIGNMENT);
        runRefresh.setActionCommand(Constants.SERVER_ACTION_ICCR_RUN_IOTA_REFRESH);
        runRefresh.addActionListener(ctlr);
        panel.add(runRefresh);

        SpringUtilities.makeCompactGrid(panel,
                4, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

        //setContentPane(panel);
        add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        cancel = new JButton(localizer.getLocalText("buttonLabelCancel"));
        cancel.setActionCommand(Constants.DIALOG_ICC_SETTINGS_CANCEL);
        cancel.addActionListener(ctlr);
        buttonPanel.add(cancel);

        buttonPanel.add(Box.createHorizontalGlue());

        save = new JButton(localizer.getLocalText("buttonLabelSave"));
        save.setActionCommand(Constants.DIALOG_ICC_SETTINGS_SAVE);
        save.addActionListener(ctlr);
        buttonPanel.add(save);

        getRootPane().setDefaultButton(save);

        add(buttonPanel, BorderLayout.SOUTH);

        insertValues();

        pack();
    }

    private void insertValues() {
        nbrRefreshTimeTextField.setText(propertySource.getString(PropertySource.REFRESH_NBRS_PROP));
        nodeInfoRefreshTimeTextField.setText(propertySource.getString(PropertySource.REFRESH_NODEINFO_PROP));
        iotaDownloadLinkTextField.setText(propertySource.getString(PropertySource.IOTA_DLD_LINK_PROP));
        runRefresh.setSelected(propertySource.getRunIotaRefresh());
    }
}
