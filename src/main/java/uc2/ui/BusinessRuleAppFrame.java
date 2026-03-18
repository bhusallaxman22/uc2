package uc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import uc2.controller.BusinessRuleController;
import uc2.domain.RequestProcessingResult;
import uc2.domain.RuleUpdateResult;

public final class BusinessRuleAppFrame extends JFrame {
    private final BusinessRuleController controller;
    private final JTextArea rulesArea = new JTextArea();
    private final JTextArea manualUpdateArea = new JTextArea();
    private final JTextField selectedFileField = new JTextField();
    private final JTextArea requestArea = new JTextArea();
    private final JTextArea outputArea = new JTextArea();
    private Path selectedDocument;

    public BusinessRuleAppFrame(BusinessRuleController controller) {
        super("UC2 Business Rule Engine");
        this.controller = controller;
        initialize();
        refreshRuleDisplay();
    }

    private void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 760);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        rulesArea.setEditable(false);
        rulesArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        rulesArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        manualUpdateArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        manualUpdateArea.setText("""
            customer.occupation == "student" && order.totalAmount >= 500 -> give 10% discount
            order.totalAmount >= 1000 -> grant free shipping
            """);

        requestArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        requestArea.setText("""
            customer.occupation=student
            order.totalAmount=1200
            """);

        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        add(buildMainSplitPane(), BorderLayout.CENTER);
        add(new JLabel("Choose manual update or AI-powered update, then submit a request as key=value pairs."),
            BorderLayout.SOUTH);
    }

    private JSplitPane buildMainSplitPane() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Current Business Rules"));
        leftPanel.add(new JScrollPane(rulesArea), BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manual Update", buildManualPanel());
        tabs.addTab("AI-Powered Update", buildAiPanel());
        tabs.addTab("Process Request", buildRequestPanel());

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("System Output"));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabs, outputPanel);
        rightSplitPane.setResizeWeight(0.58);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightSplitPane);
        mainSplitPane.setResizeWeight(0.45);
        return mainSplitPane;
    }

    private JPanel buildManualPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(new JLabel("Enter one business rule per line using: condition -> action1, action2"), BorderLayout.NORTH);
        panel.add(new JScrollPane(manualUpdateArea), BorderLayout.CENTER);

        JButton applyButton = new JButton("Apply Manual Update");
        applyButton.addActionListener(event -> executeManualUpdate());
        panel.add(applyButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildAiPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        form.add(new JLabel("Specification document:"), constraints);

        selectedFileField.setEditable(false);
        constraints.gridx = 1;
        constraints.weightx = 1;
        form.add(selectedFileField, constraints);

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(event -> chooseDocument());
        constraints.gridx = 2;
        constraints.weightx = 0;
        form.add(browseButton, constraints);

        JButton importButton = new JButton("Run AI-Powered Update");
        importButton.addActionListener(event -> executeAiUpdate());
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        form.add(importButton, constraints);

        panel.add(new JLabel("Supported formats: txt, md, docx, pdf"), BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(new JLabel("Submit a request in key=value form. Matching rules execute in ranked order."), BorderLayout.NORTH);
        panel.add(new JScrollPane(requestArea), BorderLayout.CENTER);

        JButton processButton = new JButton("Process Request");
        processButton.addActionListener(event -> executeRequest());
        panel.add(processButton, BorderLayout.SOUTH);
        return panel;
    }

    private void chooseDocument() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Documents", "txt", "md", "docx", "pdf"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedDocument = chooser.getSelectedFile().toPath();
            selectedFileField.setText(selectedDocument.toString());
        }
    }

    private void executeManualUpdate() {
        try {
            RuleUpdateResult result = controller.updateManually(manualUpdateArea.getText());
            refreshRuleDisplay();
            outputArea.setText(formatUpdateResult(result));
        } catch (Exception exception) {
            showError(exception);
        }
    }

    private void executeAiUpdate() {
        if (selectedDocument == null) {
            JOptionPane.showMessageDialog(this, "Select a document first.", "Missing Document", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            RuleUpdateResult result = controller.updateWithDocument(selectedDocument);
            refreshRuleDisplay();
            outputArea.setText(formatUpdateResult(result));
        } catch (Exception exception) {
            showError(exception);
        }
    }

    private void executeRequest() {
        try {
            RequestProcessingResult result = controller.processRequest(requestArea.getText());
            outputArea.setText(formatRequestResult(result));
        } catch (Exception exception) {
            showError(exception);
        }
    }

    private void refreshRuleDisplay() {
        rulesArea.setText(controller.renderRules());
    }

    private String formatUpdateResult(RuleUpdateResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("Update summary").append(System.lineSeparator())
            .append("--------------").append(System.lineSeparator());
        for (String message : result.messages()) {
            builder.append(message).append(System.lineSeparator());
        }

        builder.append(System.lineSeparator())
            .append("Parse trees").append(System.lineSeparator())
            .append("-----------").append(System.lineSeparator());
        for (String parseTree : result.parseTrees()) {
            builder.append(parseTree).append(System.lineSeparator()).append(System.lineSeparator());
        }
        return builder.toString().trim();
    }

    private String formatRequestResult(RequestProcessingResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("Request result").append(System.lineSeparator())
            .append("--------------").append(System.lineSeparator());

        if (result.matchedRules().isEmpty()) {
            builder.append("No rules matched the request.").append(System.lineSeparator());
        } else {
            builder.append("Matched rules:").append(System.lineSeparator());
            for (var rule : result.matchedRules()) {
                builder.append(" - ").append(rule.toDisplayString()).append(System.lineSeparator());
            }
            builder.append(System.lineSeparator())
                .append("Actions:").append(System.lineSeparator());
            for (String action : result.actions()) {
                builder.append(" - ").append(action).append(System.lineSeparator());
            }
        }

        builder.append(System.lineSeparator())
            .append("Evaluation trace").append(System.lineSeparator())
            .append("----------------").append(System.lineSeparator());
        for (String line : result.trace()) {
            builder.append(line).append(System.lineSeparator());
        }
        return builder.toString().trim();
    }

    private void showError(Exception exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void open(BusinessRuleController controller) {
        SwingUtilities.invokeLater(() -> new BusinessRuleAppFrame(controller).setVisible(true));
    }
}
