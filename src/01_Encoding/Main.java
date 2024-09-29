
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

class SecondFrame extends JFrame {

    private JLabel label;
    private JPanel panel;
    public SecondFrame(String input, int encoding) {
        setTitle("Second Frame");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1500, 1000);
        setLocationRelativeTo(null);

        panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 14, 14));

        GraphComponent graphLayout = new GraphComponent(input, encoding);
        panel.add(graphLayout);

        add(panel);
        /*label = new JLabel(input);


        add(label);*/
        setVisible(true);
    }

    private static class GraphComponent extends JComponent {

        private String signalString;
        private int encoding;

        public GraphComponent(String input, int encoding){
            this.signalString = input;
            this.encoding = encoding;

        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2D = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();

            g2D.setColor(Color.BLACK);

            g2D.drawLine(0, height-50, width, height-50);

            g2D.drawLine(50, 0, 50, height);

            int signalStart = 50;

            int nPoints = signalString.length();
            int[] x_array = new int[(nPoints*2 + 1)*2];
            int[] y_array = new int[nPoints*2*2];

            int arrIndex = 0;
            x_array[0] = 50;


            switch (encoding) {
                case 0:
                    UnipolarNRZ(x_array, y_array, signalStart, height, arrIndex, nPoints);
                    break;

                case 1:
                    BiPolarNRZL(x_array, y_array, signalStart, height-50, arrIndex, nPoints);
                    break;

                case 2:
                    BiPolarNRZI(x_array, y_array, signalStart, height-200, arrIndex, nPoints);
                    break;

                case 3:
                    PolarRZ(x_array, y_array, signalStart, height-50, arrIndex, nPoints);
                    break;

                case 4:
                    BiPolarAMI(x_array, y_array, signalStart, height-200, arrIndex, nPoints);
                    break;

                case 5:
                    PseudoTernary(x_array, y_array, signalStart, height-50, arrIndex, nPoints);
                    break;

                case 6:
                    Manchester(x_array, y_array, signalStart, height-50, arrIndex, nPoints);
                    break;

                case 7:
                    DifferentialManchester(x_array, y_array, signalStart, height-200, arrIndex, nPoints);
                    break;

                case 8:
                    MLT3(x_array, y_array, signalStart, height-50, arrIndex, nPoints);
                    break;

                default:
                    break;
            }

            g2D.setStroke(new BasicStroke(2));
            g2D.setColor(Color.cyan);
            g2D.drawPolyline(x_array, y_array, nPoints*2);

            /*System.out.println(Arrays.toString(x_array));
            System.out.println(Arrays.toString(y_array));*/

            g2D.setColor(Color.BLACK);
            g2D.setFont(new Font("Osaka", Font.PLAIN, 12));
            g2D.drawString("0", 40, height-35);

            float x_pos = 100;
            while(x_pos < width - 50){
                float x_coordinate = (x_pos - 50)/100;
                g2D.drawString(""+x_coordinate, (int)x_pos, height-35);
                x_pos += 50;
            }

            float y_pos = height - 100;
            while(y_pos > 50){
                float y_coordinate = encoding == 0 ? (height - y_pos - 50)/50: encoding == 3 || encoding == 5 || encoding == 6?((height - y_pos - 50)/50) - 2 : encoding == 8 ? ((height - y_pos - 50)/50) - 1 : ((height - y_pos - 50)/50) - 3;
                g2D.drawString(""+y_coordinate, 25, (int)y_pos);
                y_pos -= 50;
            }

            g2D.setColor(Color.RED);
            g2D.setFont(new Font("Osaka", 3, 20));
            g2D.drawString("X", width - 50, height-30);

            g2D.setColor(Color.BLUE);
            g2D.setFont(new Font("Osaka", 3, 20));
            g2D.drawString("Y" , 30, 30);
        }

        private void UnipolarNRZ(int[] x_array, int[] y_array, int signalStart, int height, int arrIndex, int nPoints){
            for(int i = 0; i < nPoints; i+=1){
                if(signalString.charAt(i) == '1'){
                    x_array[arrIndex+1] = signalStart+50;
                    y_array[arrIndex] = height-300;
                    arrIndex++;
                    x_array[arrIndex+1] = signalStart+50;
                    y_array[arrIndex] = height-300;
                    arrIndex++;

                }else if(signalString.charAt(i) == '0'){
                    x_array[arrIndex+1] = signalStart+50;
                    y_array[arrIndex] = height-50;
                    arrIndex++;
                    x_array[arrIndex+1] = signalStart+50;
                    y_array[arrIndex] = height-50;
                    arrIndex++;

                }
                signalStart+=50;
            }
        }

        private void BiPolarNRZL(int[] x_array, int[] y_array, int signalStart, int height, int arrIndex, int nPoints) {
            boolean previousPolarityPositive = true;

            for (int i = 0; i < nPoints; i += 1) {
                if (signalString.charAt(i) == '1') {
                    int polarityMultiplier = previousPolarityPositive ? 1 : -1;

                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (150 * polarityMultiplier) - 150;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (150 * polarityMultiplier) - 150;
                    arrIndex++;

                    previousPolarityPositive = !previousPolarityPositive;
                } else if (signalString.charAt(i) == '0') {
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                }

                signalStart += 50;
            }
        }

        private void BiPolarNRZI(int[] x_array, int[] y_array, int signalStart, int height, int arrIndex, int nPoints) {
            boolean previousLevelPositive = true;

            for (int i = 0; i < nPoints; i += 1) {
                if (signalString.charAt(i) == '1') {
                    int polarityMultiplier = previousLevelPositive ? -1 : 1;

                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (150 * polarityMultiplier);
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (150 * polarityMultiplier);
                    arrIndex++;

                    previousLevelPositive = !previousLevelPositive;
                } else if (signalString.charAt(i) == '0') {
                    int polarityMultiplier = previousLevelPositive ? 1 : -1;

                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (150 * polarityMultiplier);
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (150 * polarityMultiplier);
                    arrIndex++;
                }

                signalStart += 50;
            }
        }

        private void PolarRZ(int[] x_array, int[] y_array, int signalStart, int height, int arrIndex, int nPoints) {
            for (int i = 0; i < nPoints; i += 1) {
                if (signalString.charAt(i) == '1') {
                    x_array[arrIndex + 1] = signalStart + 25;
                    y_array[arrIndex] = height;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 25;
                    y_array[arrIndex] = height;
                    arrIndex++;

                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - 200;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - 200;
                    arrIndex++;
                } else if (signalString.charAt(i) == '0') {
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                }

                signalStart += 50;
            }
        }

        private void BiPolarAMI(int[] x_array, int[] y_array, int signalStart, int height, int arrIndex, int nPoints) {
            boolean previousLevelPositive = true;

            for (int i = 0; i < nPoints; i += 1) {
                if (signalString.charAt(i) == '1') {
                    int polarityMultiplier = previousLevelPositive ? -1 : 1;

                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (150 * polarityMultiplier);
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (150 * polarityMultiplier);
                    arrIndex++;

                    previousLevelPositive = !previousLevelPositive;
                } else if (signalString.charAt(i) == '0') {
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                }

                signalStart += 50;
            }
        }

        private void PseudoTernary(int[] x_array, int[] y_array, int signalStart, int height, int arrIndex, int nPoints) {
            for (int i = 0; i < nPoints; i += 1) {
                if (signalString.charAt(i) == '0') {
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                } else if (signalString.charAt(i) == '1') {
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - 200;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - 200;
                    arrIndex++;
                }

                signalStart += 50;
            }
        }

        private void Manchester(int[] x_array, int[] y_array, int signalStart, int height, int arrIndex, int nPoints) {
            for (int i = 0; i < nPoints; i += 1) {
                if (signalString.charAt(i) == '0') {
                    x_array[arrIndex + 1] = signalStart + 25;
                    y_array[arrIndex] = height;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 25;
                    y_array[arrIndex] = height;
                    arrIndex++;

                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - 200;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - 200;
                    arrIndex++;
                } else if (signalString.charAt(i) == '1') {
                    x_array[arrIndex + 1] = signalStart + 25;
                    y_array[arrIndex] = height - 200;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 25;
                    y_array[arrIndex] = height - 200;
                    arrIndex++;

                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                }

                signalStart += 50;
            }
        }

        private void DifferentialManchester(int[] x_array, int[] y_array, int signalStart, int height, int arrIndex, int nPoints) {
            boolean previousLevelPositive = true;

            for (int i = 0; i < nPoints; i += 1) {
                if (signalString.charAt(i) == '0') {
                    int polarityMultiplier = previousLevelPositive ? -1 : 1;

                    x_array[arrIndex + 1] = signalStart + 25;
                    y_array[arrIndex] = height - (150 * polarityMultiplier);
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 25;
                    y_array[arrIndex] = height - (150 * polarityMultiplier);
                    arrIndex++;

                    previousLevelPositive = !previousLevelPositive;
                } else if (signalString.charAt(i) == '1') {
                    int polarityMultiplier = previousLevelPositive ? -1 : 1;

                    x_array[arrIndex + 1] = signalStart + 25;
                    y_array[arrIndex] = height - (150 * polarityMultiplier);
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 25;
                    y_array[arrIndex] = height - (150 * polarityMultiplier);
                    arrIndex++;

                    previousLevelPositive = !previousLevelPositive;

                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height;
                    arrIndex++;
                }

                signalStart += 50;
            }
        }

        private void MLT3(int[] x_array, int[] y_array, int signalStart, int height, int arrIndex, int nPoints) {
            int previousLevel = 0;
            int currentLevel = 0;

            for (int i = 0; i < nPoints; i += 1) {
                if (signalString.charAt(i) == '0') {
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (100 * previousLevel);
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (100 * previousLevel);
                    arrIndex++;
                } else if (signalString.charAt(i) == '1') {
                    if (currentLevel == 0)
                        currentLevel = previousLevel == 1 ? -1 : 1;
                    else
                        currentLevel = 0;

                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (100 * currentLevel);
                    arrIndex++;
                    x_array[arrIndex + 1] = signalStart + 50;
                    y_array[arrIndex] = height - (100 * currentLevel);
                    arrIndex++;

                    previousLevel = currentLevel;
                }

                signalStart += 50;
            }
        }

    }
}

public class Main extends JFrame{

    private JLabel label;
    private JTextField textField;
    private JComboBox<String> comboBox;
    private JButton button;

    public Main() {

        setTitle("Arya Pathak");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 1000);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        centerPanel.setBackground(new Color(70, 151, 77));

        label = new JLabel("Digital Sequence");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 0, 0); // Add top margin
        centerPanel.add(label, gbc);

        textField = new JTextField(25);
        textField = new JTextField(25);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        centerPanel.add(textField, gbc);

        String[] options = {"Uni-polar NRZ", "Bi-Polar NRZ-L", "Bi-Polar NRZ-I", "Polar Z", "Bi-Polar AMI",
                 "Manchester", "Differentail Manchester", "MLT-3"};
        comboBox = new JComboBox<>(options);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 0, 0); // Add top margin
        centerPanel.add(comboBox, gbc);

        add(centerPanel, BorderLayout.CENTER);

        button = new JButton("Draw Graph");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 0, 0);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String input = textField.getText();
                int encoding = comboBox.getSelectedIndex();

                //dispose();
                SecondFrame secondFrame = new SecondFrame(input, encoding);
            }
        });
        centerPanel.add(button, gbc);

        add(new JPanel(), BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.WEST);
        add(new JPanel(), BorderLayout.EAST);

        setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }

}
