package Noise.Array;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LightingChanger extends JPanel{

    GroupLayout layout;

    static int precision = 10;
    ImageUpdateInterface iui ;

    JSlider lightingAngleSlider;
    JSlider lightingStrengthSlider;

    public LightingChanger(ImageUpdateInterface iui){
        GroupLayout layout = new GroupLayout(this);
        if(iui == null)
            throw new IllegalArgumentException("Image update interface cannot be null");
        this.iui = iui;

        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel lightingAngleLabel = new JLabel("Lighting Angle");
        this.add(lightingAngleLabel);
        JLabel lightingStrengthLabel = new JLabel("Lighting Strength");
        this.add(lightingStrengthLabel);

        JLabel lightingAngleValue = new JLabel("0");
        this.add(lightingAngleValue);
        JLabel lightingStrengthValue = new JLabel("0");
        this.add(lightingStrengthValue);

        lightingAngleSlider = new JSlider(JSlider.HORIZONTAL, -180 * precision, 180 * precision, (int)(PerlinNoiseArray.getLightingAngle() * precision));
        this.add(lightingAngleSlider);
        lightingStrengthSlider = new JSlider(JSlider.HORIZONTAL, -5 * precision, 5 * precision, (int)(PerlinNoiseArray.getLightingStrength() * precision));
        this.add(lightingStrengthSlider);

        JButton saveButton = new JButton("Save");
//        saveButton.addActionListener((event) -> {
//            PerlinNoiseArrayInterface.saveVariables(this);
//        });
        this.add(saveButton);
        JButton loadButton = new JButton("Load");
//        loadButton.addActionListener((event)->{
//            String filename = FileManager.askForFileNameFromListInDir(this, "variables", "Select variable file", "Variable load form");
//            PerlinNoiseArrayInterface.loadVariable("variables", filename, this);
//        });
        this.add(loadButton);
        JButton cancelButton = new JButton("Cancel");
        this.add(cancelButton);

        List<JSlider> sliders = Stream.of(lightingAngleSlider, lightingStrengthSlider).toList();
        List<JLabel> labels = Stream.of(lightingAngleValue, lightingStrengthValue).toList();

        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(PerlinNoiseArray::setLightingAngle,
                        PerlinNoiseArray::setLightingStrength));

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
                .addComponent(lightingAngleLabel)
                .addComponent(lightingStrengthLabel));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(lightingAngleValue)
                .addComponent(lightingStrengthValue));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(lightingAngleSlider)
                .addComponent(lightingStrengthSlider)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addComponent(loadButton)
                        .addComponent(cancelButton)));

        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup
                = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lightingAngleLabel)
                .addComponent(lightingAngleValue)
                .addComponent(lightingAngleSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lightingStrengthLabel)
                .addComponent(lightingStrengthValue)
                .addComponent(lightingStrengthSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(saveButton)
                .addComponent(loadButton)
                .addComponent(cancelButton));

        layout.setVerticalGroup(vGroup);
    }

    public void showLightingChanger()
    {
        JFrame frame = new JFrame("Lighting Changer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    public void updateData()
    {
        List<JSlider> sliders = Stream.of(lightingAngleSlider, lightingStrengthSlider).toList();
        List<Supplier<Float>> getters = new ArrayList<>(
                Arrays.asList(PerlinNoiseArray::getLightingAngle,
                        PerlinNoiseArray::getLightingStrength));
        IntStream.range(0, sliders.size())
                .boxed()
                .forEach((index) -> {
                    sliders.get(index).setValue((int)(getters.get(index).get() * precision));
                });
    }

    public static void main(String[] args)
    {
        LightingChanger vc = new LightingChanger(()-> {
            System.out.println("Updating image");
        });
        vc.showLightingChanger();

    }
}
