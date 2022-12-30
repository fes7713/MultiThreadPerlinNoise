package Noise.Array;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class VariableChanger extends JPanel{

    GroupLayout layout;

    static int precision = 1000;
    ImageUpdateInterface iui ;

    public VariableChanger(ImageUpdateInterface iui){
        GroupLayout layout = new GroupLayout(this);
        if(iui == null)
            throw new IllegalArgumentException("Image update interface cannot be null");
        this.iui = iui;

        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel noiseSteepnessLabel = new JLabel("Noise Steepness");
        this.add(noiseSteepnessLabel);
        JLabel noiseShiftLabel = new JLabel("Noise Shift");
        this.add(noiseShiftLabel);
        JLabel normalSteepnessLabel = new JLabel("Normal Steepness");
        this.add(normalSteepnessLabel);
        JLabel normalShiftLabel = new JLabel("Normal Shift");
        this.add(normalShiftLabel);

        JLabel noiseSteepnessValue = new JLabel("0");
        this.add(noiseSteepnessValue);
        JLabel noiseShiftValue = new JLabel("0");
        this.add(noiseShiftValue);
        JLabel normalSteepnessValue = new JLabel("0");
        this.add(normalSteepnessValue);
        JLabel normalShiftValue = new JLabel("0");
        this.add(normalShiftValue);

        JSlider noiseSteepnessSlider = new JSlider(JSlider.HORIZONTAL, -10 * precision, 10 * precision, (int)(PerlinNoiseArray.getNoiseCoefficient() * precision));
        this.add(noiseSteepnessSlider);
        JSlider noiseShiftSlider = new JSlider(JSlider.HORIZONTAL, 0, 100 * precision, (int)(PerlinNoiseArray.getNoiseShift() * precision));
        this.add(noiseShiftSlider);
        JSlider normalSteepnessSlider = new JSlider(JSlider.HORIZONTAL, -1 * precision, 1 * precision, (int)(PerlinNoiseArray.getNormalCoefficient() * precision));
        this.add(normalSteepnessSlider);
        JSlider normalShiftSlider = new JSlider(JSlider.HORIZONTAL, -50 * precision, 200 * precision, (int)(PerlinNoiseArray.getNormalShift() * precision));
        this.add(normalShiftSlider);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener((event) -> {
            PerlinNoiseArrayInterface.saveVariables(this);
        });

        this.add(saveButton);
        JButton loadButton = new JButton("Load");
        this.add(loadButton);
        JButton cancelButton = new JButton("Cancel");
        this.add(cancelButton);

        List<JSlider> sliders = Stream.of(noiseSteepnessSlider, noiseShiftSlider, normalSteepnessSlider, normalShiftSlider).toList();
        List<JLabel> labels = Stream.of(noiseSteepnessValue, noiseShiftValue, normalSteepnessValue, normalShiftValue).toList();

        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(PerlinNoiseArray::setNoiseCoefficient,
                        PerlinNoiseArray::setNoiseShift,
                        PerlinNoiseArray::setNormalCoefficient,
                        PerlinNoiseArray::setNormalShift));

        sliders.forEach((slider) -> {
                    slider.setPaintTicks(true);
                    slider.setMajorTickSpacing(precision * 10);
                });

        IntStream.range(0, sliders.size())
                .boxed()
                .forEach((index) -> {

                    labels.get(index).setText(sliders.get(index).getValue() / (float)precision + "");
                    sliders.get(index).addChangeListener((event) -> {
                        labels.get(index).setText(sliders.get(index).getValue() / (float)precision + "");
                        setters.get(index).accept(sliders.get(index).getValue() / (float)precision);
                        iui.update();
                    });

                });


        GroupLayout.SequentialGroup hGroup
                = layout.createSequentialGroup();

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(noiseSteepnessLabel)
                .addComponent(noiseShiftLabel)
                .addComponent(normalSteepnessLabel)
                .addComponent(normalShiftLabel));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(noiseSteepnessValue)
                .addComponent(noiseShiftValue)
                .addComponent(normalSteepnessValue)
                .addComponent(normalShiftValue));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(noiseSteepnessSlider)
                .addComponent(noiseShiftSlider)
                .addComponent(normalSteepnessSlider)
                .addComponent(normalShiftSlider)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addComponent(loadButton)
                        .addComponent(cancelButton)));

        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup
                = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(noiseSteepnessLabel)
                .addComponent(noiseSteepnessValue)
                .addComponent(noiseSteepnessSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(noiseShiftLabel)
                .addComponent(noiseShiftValue)
                .addComponent(noiseShiftSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(normalSteepnessLabel)
                .addComponent(normalSteepnessValue)
                .addComponent(normalSteepnessSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(normalShiftLabel)
                .addComponent(normalShiftValue)
                .addComponent(normalShiftSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(saveButton)
                .addComponent(loadButton)
                .addComponent(cancelButton));

        layout.setVerticalGroup(vGroup);
    }

    public void showVariableChanger()
    {
        JFrame frame = new JFrame("Variable Changer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);


    }

    public static void main(String[] args)
    {
        VariableChanger vc = new VariableChanger(()-> {
            System.out.println("Updating image");
        });
        vc.showVariableChanger();
    }
}
