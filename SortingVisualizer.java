import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class SortingVisualizer extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BAR_WIDTH = 10;
    private static final int MAX_BAR_HEIGHT = 500;
    private static final int DELAY = 10;
    
    private int[] array;
    private int arraySize;
    private final JPanel arrayPanel;
    private final JComboBox<String> algorithmSelector;
    private final JButton startButton;
    private final JButton resetButton;
    private final JSlider speedSlider;
    private final JSlider sizeSlider;
    private AtomicBoolean sorting = new AtomicBoolean(false);
    private AtomicBoolean stopRequested = new AtomicBoolean(false);
    
    public SortingVisualizer() {
        setTitle("Sorting Algorithm Visualizer");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Initialize control panel
        JPanel controlPanel = new JPanel();
        
        // Algorithm selector
        String[] algorithms = {"Bubble Sort", "Selection Sort", "Insertion Sort", 
                              "Merge Sort", "Quick Sort", "Heap Sort"};
        algorithmSelector = new JComboBox<>(algorithms);
        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algorithmSelector);
        
        // Size slider
        sizeSlider = new JSlider(JSlider.HORIZONTAL, 10, 150, 80);
        sizeSlider.setMajorTickSpacing(30);
        sizeSlider.setMinorTickSpacing(10);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        controlPanel.add(new JLabel("Size:"));
        controlPanel.add(sizeSlider);
        
        // Speed slider
        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 50);
        speedSlider.setMajorTickSpacing(25);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        controlPanel.add(new JLabel("Speed:"));
        controlPanel.add(speedSlider);
        
        // Buttons
        startButton = new JButton("Start");
        resetButton = new JButton("Reset");
        controlPanel.add(startButton);
        controlPanel.add(resetButton);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Initialize array panel
        arrayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (array != null) {
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    int barWidth = Math.max(1, panelWidth / arraySize);
                    
                    for (int i = 0; i < arraySize; i++) {
                        int barHeight = array[i] * panelHeight / MAX_BAR_HEIGHT;
                        int x = i * barWidth;
                        int y = panelHeight - barHeight;
                        g.setColor(Color.BLUE);
                        g.fillRect(x, y, barWidth, barHeight);
                        g.setColor(Color.BLACK);
                        g.drawRect(x, y, barWidth, barHeight);
                    }
                }
            }
        };
        arrayPanel.setBackground(Color.WHITE);
        add(arrayPanel, BorderLayout.CENTER);
        
        // Event listeners
        sizeSlider.addChangeListener(e -> {
            if (!sorting.get()) {
                generateRandomArray();
            }
        });
        
        resetButton.addActionListener(e -> {
            if (sorting.get()) {
                stopRequested.set(true);
            } else {
                generateRandomArray();
            }
        });
        
        startButton.addActionListener(e -> {
            if (!sorting.get()) {
                startSorting();
            } else {
                stopRequested.set(true);
            }
        });
        
        // Initialize with random array
        generateRandomArray();
    }
    
    private void generateRandomArray() {
        arraySize = sizeSlider.getValue();
        array = new int[arraySize];
        Random random = new Random();
        
        for (int i = 0; i < arraySize; i++) {
            array[i] = random.nextInt(MAX_BAR_HEIGHT) + 1;
        }
        
        arrayPanel.repaint();
    }
    
    private void startSorting() {
        String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
        sorting.set(true);
        stopRequested.set(false);
        startButton.setText("Stop");
        algorithmSelector.setEnabled(false);
        sizeSlider.setEnabled(false);
        
        new Thread(() -> {
            switch (selectedAlgorithm) {
                case "Bubble Sort":
                    bubbleSort();
                    break;
                case "Selection Sort":
                    selectionSort();
                    break;
                case "Insertion Sort":
                    insertionSort();
                    break;
                case "Merge Sort":
                    mergeSort(0, arraySize - 1);
                    break;
                case "Quick Sort":
                    quickSort(0, arraySize - 1);
                    break;
                case "Heap Sort":
                    heapSort();
                    break;
            }
            
            // After sorting is complete
            SwingUtilities.invokeLater(() -> {
                sorting.set(false);
                startButton.setText("Start");
                algorithmSelector.setEnabled(true);
                sizeSlider.setEnabled(true);
                stopRequested.set(false);
            });
            
        }).start();
    }
    
    private void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    private void updateDisplay() {
        arrayPanel.repaint();
        try {
            int delay = DELAY * (101 - speedSlider.getValue());
            Thread.sleep(delay / 100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (stopRequested.get()) {
            Thread.currentThread().interrupt();
            generateRandomArray();
            SwingUtilities.invokeLater(() -> {
                sorting.set(false);
                startButton.setText("Start");
                algorithmSelector.setEnabled(true);
                sizeSlider.setEnabled(true);
                stopRequested.set(false);
            });
        }
    }
    
    // Bubble Sort
    private void bubbleSort() {
        for (int i = 0; i < arraySize - 1; i++) {
            for (int j = 0; j < arraySize - i - 1; j++) {
                if (Thread.currentThread().isInterrupted()) return;
                
                if (array[j] > array[j + 1]) {
                    swap(j, j + 1);
                    updateDisplay();
                }
            }
        }
    }
    
    // Selection Sort
    private void selectionSort() {
        for (int i = 0; i < arraySize - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arraySize; j++) {
                if (Thread.currentThread().isInterrupted()) return;
                
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            swap(minIndex, i);
            updateDisplay();
        }
    }
    
    // Insertion Sort
    private void insertionSort() {
        for (int i = 1; i < arraySize; i++) {
            int key = array[i];
            int j = i - 1;
            
            while (j >= 0 && array[j] > key) {
                if (Thread.currentThread().isInterrupted()) return;
                
                array[j + 1] = array[j];
                j = j - 1;
                updateDisplay();
            }
            array[j + 1] = key;
            updateDisplay();
        }
    }
    
    // Merge Sort
    private void mergeSort(int left, int right) {
        if (Thread.currentThread().isInterrupted()) return;
        
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            
            merge(left, mid, right);
        }
    }
    
    private void merge(int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        
        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];
        
        for (int i = 0; i < n1; ++i) {
            leftArray[i] = array[left + i];
        }
        for (int j = 0; j < n2; ++j) {
            rightArray[j] = array[mid + 1 + j];
        }
        
        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (Thread.currentThread().isInterrupted()) return;
            
            if (leftArray[i] <= rightArray[j]) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            k++;
            updateDisplay();
        }
        
        while (i < n1) {
            if (Thread.currentThread().isInterrupted()) return;
            
            array[k] = leftArray[i];
            i++;
            k++;
            updateDisplay();
        }
        
        while (j < n2) {
            if (Thread.currentThread().isInterrupted()) return;
            
            array[k] = rightArray[j];
            j++;
            k++;
            updateDisplay();
        }
    }
    
    // Quick Sort
    private void quickSort(int low, int high) {
        if (Thread.currentThread().isInterrupted()) return;
        
        if (low < high) {
            int partitionIndex = partition(low, high);
            
            quickSort(low, partitionIndex - 1);
            quickSort(partitionIndex + 1, high);
        }
    }
    
    private int partition(int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        
        for (int j = low; j < high; j++) {
            if (Thread.currentThread().isInterrupted()) return -1;
            
            if (array[j] < pivot) {
                i++;
                swap(i, j);
                updateDisplay();
            }
        }
        
        swap(i + 1, high);
        updateDisplay();
        
        return i + 1;
    }
    
    // Heap Sort
    private void heapSort() {
        // Build heap
        for (int i = arraySize / 2 - 1; i >= 0; i--) {
            if (Thread.currentThread().isInterrupted()) return;
            
            heapify(arraySize, i);
        }
        
        // Extract elements from heap one by one
        for (int i = arraySize - 1; i > 0; i--) {
            if (Thread.currentThread().isInterrupted()) return;
            
            swap(0, i);
            updateDisplay();
            
            heapify(i, 0);
        }
    }
    
    private void heapify(int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        
        if (Thread.currentThread().isInterrupted()) return;
        
        if (left < n && array[left] > array[largest])
            largest = left;
        
        if (right < n && array[right] > array[largest])
            largest = right;
        
        if (largest != i) {
            swap(i, largest);
            updateDisplay();
            
            heapify(n, largest);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SortingVisualizer visualizer = new SortingVisualizer();
            visualizer.setVisible(true);
        });
    }
}