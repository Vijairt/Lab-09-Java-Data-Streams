import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsApp extends JFrame {
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private Path loadedFilePath;

    public DataStreamsApp() {
        setTitle("Java Data Streams Search");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for search field and buttons
        JPanel topPanel = new JPanel();
        searchField = new JTextField(30);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(loadButton);
        topPanel.add(searchButton);
        topPanel.add(quitButton);
        add(topPanel, BorderLayout.NORTH);

        // Text areas side-by-side
        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        JScrollPane leftScroll = new JScrollPane(originalTextArea);
        JScrollPane rightScroll = new JScrollPane(filteredTextArea);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, rightScroll);
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);

        // Button Actions
        loadButton.addActionListener(new LoadButtonHandler());
        searchButton.addActionListener(new SearchButtonHandler());
        quitButton.addActionListener(e -> System.exit(0));
    }

    private class LoadButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(DataStreamsApp.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                loadedFilePath = fileChooser.getSelectedFile().toPath();
                try (Stream<String> lines = Files.lines(loadedFilePath)) {
                    String content = lines.collect(Collectors.joining("\n"));
                    originalTextArea.setText(content);
                    filteredTextArea.setText(""); // clear old results
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(DataStreamsApp.this, "Error loading file: " + ex.getMessage());
                }
            }
        }
    }

    private class SearchButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (loadedFilePath == null) {
                JOptionPane.showMessageDialog(DataStreamsApp.this, "Please load a file first.");
                return;
            }

            String search = searchField.getText().toLowerCase();
            if (search.isEmpty()) {
                JOptionPane.showMessageDialog(DataStreamsApp.this, "Please enter a search string.");
                return;
            }

            try (Stream<String> lines = Files.lines(loadedFilePath)) {
                List<String> results = lines
                        .filter(line -> line.toLowerCase().contains(search))
                        .collect(Collectors.toList());
                filteredTextArea.setText(String.join("\n", results));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(DataStreamsApp.this, "Error searching file: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataStreamsApp app = new DataStreamsApp();
            app.setVisible(true);
        });
    }
}
