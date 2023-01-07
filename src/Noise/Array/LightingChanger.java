package Noise.Array;

import Noise.ChunkProvider;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LightingChanger extends JPanel{
    private final ChunkProvider chunkProvider;
    private final ImageUpdateInterface iui ;

    private final JSlider lightingAngleSlider;
    private final JSlider lightingStrengthSlider;

    private static final int precision = 10;

    public LightingChanger(ChunkProvider chunkProvider, ImageUpdateInterface iui){
        this.chunkProvider = chunkProvider;
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

        lightingAngleSlider = new JSlider(JSlider.HORIZONTAL, -180 * precision, 180 * precision, (int)(chunkProvider.getLightingAngle() * precision));
        this.add(lightingAngleSlider);
        lightingStrengthSlider = new JSlider(JSlider.HORIZONTAL, -5 * precision, 5 * precision, (int)(chunkProvider.getLightingStrength() * precision));
        this.add(lightingStrengthSlider);

        JButton saveButton = new JButton("Save");
        this.add(saveButton);
        JButton loadButton = new JButton("Load");
        this.add(loadButton);
        JButton cancelButton = new JButton("Cancel");
        this.add(cancelButton);

        List<JSlider> sliders = Stream.of(lightingAngleSlider, lightingStrengthSlider).toList();
        List<JLabel> labels = Stream.of(lightingAngleValue, lightingStrengthValue).toList();

        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(chunkProvider::setLightingAngle,
                        chunkProvider::setLightingStrength));

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
                Arrays.asList(chunkProvider::getLightingAngle,
                        chunkProvider::getLightingStrength));
        IntStream.range(0, sliders.size())
                .boxed()
                .forEach((index) -> {
                    sliders.get(index).setValue((int)(getters.get(index).get() * precision));
                });
    }

    public static void main(String[] args)
    {
        ChunkProvider chunkProvider = new ChunkProvider(null, null);
        LightingChanger vc = new LightingChanger(chunkProvider, ()-> {
            System.out.println("Updating image");
        });
        vc.showLightingChanger();

    }
}
