import Noise.Array.PerlinNoiseArray;
import Noise.FastNoise;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MapEditor extends JPanel {
    GroupLayout layout;

    MapEditorUpdateInterface meui;

    JSlider centerXSlider;
    JSlider centerYSlider;
    JSlider widthSlider;
    JSlider heightSlider;
    JSlider maskSizeSlider;
    JSlider maskShadowAreaSlider;

    NoiseMapPanel nmp;

    public MapEditor(NoiseMapPanel nmp, MapEditorUpdateInterface iui){
        GroupLayout layout = new GroupLayout(this);
        if(iui == null)
            throw new IllegalArgumentException("Image update interface cannot be null");
        this.meui = iui;

        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel centerXLabel = new JLabel("Center X");
        this.add(centerXLabel);
        JLabel centerYLabel = new JLabel("Center Y");
        this.add(centerYLabel);
        JLabel mapWidthLabel = new JLabel("Width");
        this.add(mapWidthLabel);
        JLabel mapHeightLabel = new JLabel("Height");
        this.add(mapHeightLabel);
        JLabel maskSizeLabel = new JLabel("Mask Shadow Size");
        this.add(maskSizeLabel);
        JLabel maskShadowAreaLabel = new JLabel("Mask Shadow Area");
        this.add(maskShadowAreaLabel);

        JLabel centerXValue = new JLabel("0");
        this.add(centerXValue);
        JLabel centerYValue = new JLabel("0");
        this.add(centerYValue);
        JLabel widthValue = new JLabel("0");
        this.add(widthValue);
        JLabel heightValue = new JLabel("0");
        this.add(heightValue);
        JLabel maskSizeValue = new JLabel("0");
        this.add(maskSizeValue);
        JLabel maskShadowAreaValue = new JLabel("0");
        this.add(maskShadowAreaValue);

        centerXSlider = new JSlider(JSlider.HORIZONTAL, -10000, 10000, (int)nmp.getCenterX());
        this.add(centerXSlider);
        centerYSlider = new JSlider(JSlider.HORIZONTAL, -10000, 10000, (int)nmp.getCenterY());
        this.add(centerYSlider);
        widthSlider = new JSlider(JSlider.HORIZONTAL, 0, 10000, (int)nmp.getMapWidth());
        this.add(widthSlider);
        heightSlider = new JSlider(JSlider.HORIZONTAL, 0, 10000, (int)nmp.getMapHeight());
        this.add(heightSlider);
        maskSizeSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, (int)PerlinNoiseArray.getMaskSize());
        this.add(maskSizeSlider);
        maskShadowAreaSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)PerlinNoiseArray.getMaskShadow());
        this.add(maskShadowAreaSlider);

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

        List<JSlider> sliders = Stream.of(centerXSlider, centerYSlider, widthSlider, heightSlider, maskSizeSlider, maskShadowAreaSlider).toList();
        List<JLabel> labels = Stream.of(centerXValue, centerYValue, widthValue, heightValue, maskSizeValue, maskShadowAreaValue).toList();

        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(nmp::setCenterX,
                        nmp::setCenterY,
                        nmp::setMapWidth,
                        nmp::setMapHeight,
                        (num) -> {
                            PerlinNoiseArray.setMaskSize(num);
                            nmp.setCenterX(nmp.getCenterX());
                        },
                        (num) -> {
                            PerlinNoiseArray.setMaskShadow(num);
                            nmp.setCenterX(nmp.getCenterX());
                        }
                ));

        IntStream.range(0, sliders.size())
                .boxed()
                .forEach((index) -> {

                    labels.get(index).setText(sliders.get(index).getValue() + "");
                    sliders.get(index).addChangeListener((event) -> {
                        float value = sliders.get(index).getValue();
                        if(sliders.get(index) == maskShadowAreaSlider)
                            value /= 10F;
                        labels.get(index).setText(value + "");
                        setters.get(index).accept(value);
                        meui.update();
                    });

                });


        GroupLayout.SequentialGroup hGroup
                = layout.createSequentialGroup();

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(centerXLabel)
                .addComponent(centerYLabel)
                .addComponent(mapWidthLabel)
                .addComponent(mapHeightLabel)
                .addComponent(maskSizeLabel)
                .addComponent(maskShadowAreaLabel));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(centerXValue)
                .addComponent(centerYValue)
                .addComponent(widthValue)
                .addComponent(heightValue)
                .addComponent(maskSizeValue)
                .addComponent(maskShadowAreaValue));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(centerXSlider)
                .addComponent(centerYSlider)
                .addComponent(widthSlider)
                .addComponent(heightSlider)
                .addComponent(maskSizeSlider)
                .addComponent(maskShadowAreaSlider)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addComponent(loadButton)
                        .addComponent(cancelButton)));

        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup
                = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(centerXLabel)
                .addComponent(centerXValue)
                .addComponent(centerXSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(centerYLabel)
                .addComponent(centerYValue)
                .addComponent(centerYSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(mapWidthLabel)
                .addComponent(widthValue)
                .addComponent(widthSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(mapHeightLabel)
                .addComponent(heightValue)
                .addComponent(heightSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(maskSizeLabel)
                .addComponent(maskSizeValue)
                .addComponent(maskSizeSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(maskShadowAreaLabel)
                .addComponent(maskShadowAreaValue)
                .addComponent(maskShadowAreaSlider));

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(saveButton)
                .addComponent(loadButton)
                .addComponent(cancelButton));

        layout.setVerticalGroup(vGroup);
    }

    public void showMapEditor()
    {
        JFrame frame = new JFrame("Map editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    public void updateData()
    {
        List<JSlider> sliders = Stream.of(centerXSlider, centerYSlider, widthSlider, heightSlider).toList();
        List<Supplier<Float>> getters = new ArrayList<>(
                Arrays.asList(nmp::getCenterX,
                        nmp::getCenterY,
                        nmp::getMapWidth,
                        nmp::getMapHeight));
        IntStream.range(0, sliders.size())
                .boxed()
                .forEach((index) -> {
                    sliders.get(index).setValue(getters.get(index).get().intValue());
                });
    }

    public static void main(String[] args)
    {
        NoiseMapPanel panel = new NoiseMapPanel() ;
        MapEditor me = new MapEditor(panel, ()-> {
            System.out.println("Updating image");
        });
        me.showMapEditor();
    }
}
