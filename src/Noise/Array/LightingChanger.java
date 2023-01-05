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

    static int precision = 100;
    ImageUpdateInterface iui ;

    JSlider lightingXSlider;
    JSlider lightingYSlider;
    JSlider lightingZSlider;
    JSlider lightingAngleSlider;

    public LightingChanger(ImageUpdateInterface iui){
        GroupLayout layout = new GroupLayout(this);
        if(iui == null)
            throw new IllegalArgumentException("Image update interface cannot be null");
        this.iui = iui;

        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel lightingXLabel = new JLabel("Lighting X");
        this.add(lightingXLabel);
        JLabel lightingYLabel = new JLabel("Lighting Y");
        this.add(lightingYLabel);
        JLabel lightingZLabel = new JLabel("Lighting Z");
        this.add(lightingZLabel);
        JLabel lightingAngleLabel = new JLabel("Lighting Angle");
        this.add(lightingAngleLabel);

        JLabel lightingXValue = new JLabel("0");
        this.add(lightingXValue);
        JLabel lightingYValue = new JLabel("0");
        this.add(lightingYValue);
        JLabel lightingZValue = new JLabel("0");
        this.add(lightingZValue);
        JLabel lightingAngleValue = new JLabel("0");
        this.add(lightingAngleValue);

        lightingXSlider = new JSlider(JSlider.HORIZONTAL, -10 * precision, 10 * precision, (int)(PerlinNoiseArray.getLightingX() * precision));
        this.add(lightingXSlider);
        lightingYSlider = new JSlider(JSlider.HORIZONTAL, -10 * precision, 10 * precision, (int)(PerlinNoiseArray.getLightingY() * precision));
        this.add(lightingYSlider);
        lightingZSlider = new JSlider(JSlider.HORIZONTAL, -10 * precision, 10 * precision, (int)(PerlinNoiseArray.getLightingZ() * precision));
        this.add(lightingZSlider);
        lightingAngleSlider = new JSlider(JSlider.HORIZONTAL, -360 * precision, 360 * precision,
                (int)(Math.toDegrees(Math.atan(PerlinNoiseArray.getLightingX() / PerlinNoiseArray.getLightingY())) * precision));
        this.add(lightingAngleValue);

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

        List<JSlider> sliders = Stream.of(lightingXSlider, lightingYSlider, lightingZSlider, lightingAngleSlider).toList();
        List<JLabel> labels = Stream.of(lightingXValue, lightingYValue, lightingZValue, lightingAngleValue).toList();

        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(PerlinNoiseArray::setLightingX,
                        PerlinNoiseArray::setLightingY,
                        PerlinNoiseArray::setLightingZ,
                        (numF) -> {
                            PerlinNoiseArray.setLightingX((float)Math.sin(Math.toRadians(numF)));
                            PerlinNoiseArray.setLightingY((float)Math.cos(Math.toRadians(numF)));
                        }));

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
                .addComponent(lightingXLabel)
                .addComponent(lightingYLabel)
                .addComponent(lightingZLabel)
                .addComponent(lightingAngleLabel));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(lightingXValue)
                .addComponent(lightingYValue)
                .addComponent(lightingZValue)
                .addComponent(lightingAngleValue));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(lightingXSlider)
                .addComponent(lightingYSlider)
                .addComponent(lightingZSlider)
                .addComponent(lightingAngleSlider)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addComponent(loadButton)
                        .addComponent(cancelButton)));

        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup
                = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lightingXLabel)
                .addComponent(lightingXValue)
                .addComponent(lightingXSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lightingYLabel)
                .addComponent(lightingYValue)
                .addComponent(lightingYSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lightingZLabel)
                .addComponent(lightingZValue)
                .addComponent(lightingZSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lightingAngleLabel)
                .addComponent(lightingAngleValue)
                .addComponent(lightingAngleSlider));

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
        List<JSlider> sliders = Stream.of(lightingXSlider, lightingYSlider, lightingZSlider).toList();
        List<Supplier<Float>> getters = new ArrayList<>(
                Arrays.asList(PerlinNoiseArray::getLightingX,
                        PerlinNoiseArray::getLightingY,
                        PerlinNoiseArray::getLightingZ,
                        () -> (float)Math.toDegrees(Math.atan(PerlinNoiseArray.getLightingX() / PerlinNoiseArray.getLightingY()))));
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
