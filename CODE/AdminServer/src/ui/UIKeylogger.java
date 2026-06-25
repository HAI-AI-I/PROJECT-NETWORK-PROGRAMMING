package ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UIKeylogger extends JFrame {

    private static final Color BG          = new Color(7, 7, 8);
    private static final Color RED         = new Color(239, 35, 49);
    private static final Color RED_DARK    = new Color(120, 10, 18);
    private static final Color TEXT        = new Color(240, 240, 240);
    private static final Color MUTED       = new Color(165, 165, 165);
    private static final Color BORDER      = new Color(80, 28, 32);
    private static final Color PANEL_2     = new Color(20, 22, 26);
    private static final Color KEY_NORMAL  = new Color(200, 230, 200);
    private static final Color KEY_SPECIAL = new Color(255, 180, 50);
    private static final Color TIMESTAMP   = new Color(100, 150, 255);

    private static final DateTimeFormatter TIME_FMT =DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String clientName;

    private JTextPane logArea;
    private StyledDocument doc;
    private JLabel countLabel;

    private int totalKeys = 0;
    private final StringBuilder rawBuffer = new StringBuilder();


    public UIKeylogger(String clientName) {
        this.clientName = clientName;

        setTitle("Keylogger - " + clientName);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        add(buildTitle(),     BorderLayout.NORTH);
        add(buildLogArea(),   BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        appendTimestamp("Session started");
    }

    public void appendKey(String key) {
        totalKeys++;
        countLabel.setText("Keys: " + totalKeys);
        rawBuffer.append(key).append(" ");

        try {
            switch (key) {
                case "Enter":
                    appendStyled(" [Enter]\n", KEY_SPECIAL, true);
                    appendTimestamp(null);
                    break;
                case "Space":
                    appendStyled(" ", KEY_NORMAL, false);
                    break;
                case "Backspace":
                    appendStyled("[⌫]", KEY_SPECIAL, true);
                    break;
                case "Tab":
                    appendStyled("[Tab]", KEY_SPECIAL, true);
                    break;
                case "Caps Lock":
                    appendStyled("[CAPS]", KEY_SPECIAL, true);
                    break;
                case "Shift":
                case "Ctrl":
                case "Alt":
                case "Windows":
                case "Escape":
                    appendStyled("[" + key + "]", KEY_SPECIAL, true);
                    break;
                default:
                    if (key.length() == 1) {
                        appendStyled(key, KEY_NORMAL, false);
                    } else {
                        appendStyled("[" + key + "]", KEY_SPECIAL, true);
                    }
                    break;
            }
            logArea.setCaretPosition(doc.getLength());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearLog() {
        try {
            doc.remove(0, doc.getLength());
            totalKeys = 0;
            countLabel.setText("Keys: 0");
            rawBuffer.setLength(0);
            appendTimestamp("Log cleared — new session");
        } catch (Exception ignored) {}
    }

    public void exportLog() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("keylog_" + clientName + "_"+ LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")) + ".txt"));

        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
            fw.write("=== Keylog Export ===\n");
            fw.write("Client : " + clientName + "\n");
            fw.write("Time   : " + LocalTime.now().format(TIME_FMT) + "\n");
            fw.write("Total  : " + totalKeys + " keys\n\n");
            fw.write(rawBuffer.toString());
            JOptionPane.showMessageDialog(this,
                    "Đã xuất: " + fc.getSelectedFile().getAbsolutePath(),
                    "Export thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JLabel buildTitle() {
        JLabel title = new JLabel("  KEYLOGGER - " + clientName);
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setOpaque(true);
        title.setBackground(BG);
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, RED),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        return title;
    }

    private JPanel buildLogArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel lbTitle = new JLabel(" KEYLOGGER — " + clientName);
        lbTitle.setForeground(RED);
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));

        countLabel = new JLabel("Keys: 0");
        countLabel.setForeground(MUTED);
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        topBar.add(lbTitle,    BorderLayout.WEST);
        topBar.add(countLabel, BorderLayout.EAST);

        logArea = new JTextPane();
        logArea.setEditable(false);
        logArea.setBackground(new Color(8, 9, 11));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        doc = logArea.getStyledDocument();

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getVerticalScrollBar().setBackground(BG);

        wrapper.add(topBar, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton clearBtn  = makeButton(" Clear",  RED_DARK, RED);
        JButton exportBtn = makeButton(" Export", PANEL_2,  BORDER);

        clearBtn.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,"Xoá toàn bộ keylog?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) clearLog();
        });
        exportBtn.addActionListener(e -> exportLog());

        bar.add(clearBtn);
        bar.add(exportBtn);
        bar.add(Box.createHorizontalStrut(20));
        bar.add(legendLabel(" ■ Normal key",  KEY_NORMAL));
        bar.add(legendLabel(" ■ Special key", KEY_SPECIAL));
        return bar;
    }


    private void appendTimestamp(String label) {
        try {
            String ts = "[" + LocalTime.now().format(TIME_FMT) + "]"+ (label != null ? " " + label : "") + "\n";
            appendStyled(ts, TIMESTAMP, false);
        } catch (Exception ignored) {}
    }

    private void appendStyled(String text, Color color, boolean bold) throws Exception {
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, color);
        StyleConstants.setBold(attr, bold);
        doc.insertString(doc.getLength(), text, attr);
    }

    private JButton makeButton(String text, Color bg, Color border) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel legendLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return l;
    }
}