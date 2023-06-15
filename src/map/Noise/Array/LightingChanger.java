package map.Noise.Array;

import map.Noise.ChunkProvider;

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
    private final JSlider lightingAltitudeSlider;
    private final JSlider lightingStrengthSlider;
    private final JSlider specularBrightnessSlider;
    private final JSlider specularIntensitySlider;
    private final JSlider ambientIntensitySlider;

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
        JLabel lightingAltitudeLabel = new JLabel("Lighting Altitude");
        this.add(lightingAltitudeLabel);
        JLabel lightingStrengthLabel = new JLabel("Lighting Strength");
        this.add(lightingStrengthLabel);
        JLabel specularBrightnessLabel = new JLabel("Specular Brightness");
        this.add(specularBrightnessLabel);
        JLabel specularIntensityLabel = new JLabel("Specular Intensity");
        this.add(specularIntensityLabel);
        JLabel ambientIntensityLabel = new JLabel("Ambient Intensity");
        this.add(specularIntensityLabel);

        JLabel lightingAngleValue = new JLabel("0");
        this.add(lightingAngleValue);
        JLabel lightingAltitudeValue = new JLabel("45");
        this.add(lightingAltitudeValue);
        JLabel lightingStrengthValue = new JLabel("0");
        this.add(lightingStrengthValue);
        JLabel specularBrightnessValue = new JLabel("0");
        this.add(specularBrightnessValue);
        JLabel specularIntensityValue = new JLabel("0");
        this.add(specularIntensityValue);
        JLabel ambientIntensityValue = new JLabel("0");
        this.add(specularIntensityValue);

        lightingAngleSlider = new JSlider(JSlider.HORIZONTAL, -180 * precision, 180 * precision, (int)(chunkProvider.getLightingAngle() * precision));
        this.add(lightingAngleSlider);
        lightingAltitudeSlider = new JSlider(JSlider.HORIZONTAL, 0, 90 * precision, (int)(chunkProvider.getLightingAltitude() * precision));
        this.add(lightingAltitudeSlider);
        lightingStrengthSlider = new JSlider(JSlider.HORIZONTAL, -5 * precision, 5 * precision, (int)(chunkProvider.getLightingStrength() * precision));
        this.add(lightingStrengthSlider);
        specularBrightnessSlider = new JSlider(JSlider.HORIZONTAL, 0, 400 * precision, (int)(chunkProvider.getSpecularBrightness() * precision));
        this.add(specularBrightnessSlider);
        specularIntensitySlider = new JSlider(JSlider.HORIZONTAL, 0, 50 * precision, (int)(chunkProvider.getSpecularIntensity() * precision));
        this.add(specularIntensitySlider);
        ambientIntensitySlider = new JSlider(JSlider.HORIZONTAL, 0, 1 * precision, (int)(chunkProvider.getAmbientIntensity() * precision));
        this.add(ambientIntensitySlider);

        JButton saveButton = new JButton("Save");
        this.add(saveButton);
        JButton loadButton = new JButton("Load");
        this.add(loadButton);
        JButton cancelButton = new JButton("Cancel");
        this.add(cancelButton);

        List<JSlider> sliders = Stream.of(lightingAngleSlider, lightingAltitudeSlider, lightingStrengthSlider, specularBrightnessSlider, specularIntensitySlider, ambientIntensitySlider).toList();
        List<JLabel> labels = Stream.of(lightingAngleValue, lightingAltitudeValue, lightingStrengthValue, specularBrightnessValue, specularIntensityValue, ambientIntensityValue).toList();

        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(chunkProvider::setLightingAngle,
                        chunkProvider::setLightingAltitude,
                        chunkProvider::setLightingStrength,
                        chunkProvider::setSpecularBrightness,
                        chunkProvider::setSpecularIntensity,
                        chunkProvider::setAmbientIntensity));

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
                .addComponent(lightingAltitudeLabel)
                .addComponent(lightingStrengthLabel)
                .addComponent(specularBrightnessLabel)
                .addComponent(specularIntensityLabel)
                .addComponent(ambientIntensityLabel));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(lightingAngleValue)
                .addComponent(lightingAltitudeValue)
                .addComponent(lightingStrengthValue)
                .addComponent(specularBrightnessValue)
                .addComponent(specularIntensityValue)
                .addComponent(ambientIntensityValue));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(lightingAngleSlider)
                .addComponent(lightingAltitudeSlider)
                .addComponent(lightingStrengthSlider)
                .addComponent(specularBrightnessSlider)
                .addComponent(specularIntensitySlider)
                .addComponent(ambientIntensitySlider)
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
                .addComponent(lightingAltitudeLabel)
                .addComponent(lightingAltitudeValue)
                .addComponent(lightingAltitudeSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lightingStrengthLabel)
                .addComponent(lightingStrengthValue)
                .addComponent(lightingStrengthSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(specularBrightnessLabel)
                .addComponent(specularBrightnessValue)
                .addComponent(specularBrightnessSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(specularIntensityLabel)
                .addComponent(specularIntensityValue)
                .addComponent(specularIntensitySlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(ambientIntensityLabel)
                .addComponent(ambientIntensityValue)
                .addComponent(ambientIntensitySlider));

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
        List<JSlider> sliders = Stream.of(lightingAngleSlider, lightingAltitudeSlider, lightingStrengthSlider, specularBrightnessSlider, specularIntensitySlider, ambientIntensitySlider).toList();
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
