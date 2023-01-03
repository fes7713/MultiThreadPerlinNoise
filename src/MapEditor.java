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

        JLabel centerXValue = new JLabel("0");
        this.add(centerXValue);
        JLabel centerYValue = new JLabel("0");
        this.add(centerYValue);
        JLabel widthValue = new JLabel("0");
        this.add(widthValue);
        JLabel heightValue = new JLabel("0");
        this.add(heightValue);

        centerXSlider = new JSlider(JSlider.HORIZONTAL, -100000, 100000, 0);
        this.add(centerXSlider);
        centerYSlider = new JSlider(JSlider.HORIZONTAL, -100000, 100000, 0);
        this.add(centerYSlider);
        widthSlider = new JSlider(JSlider.HORIZONTAL, -100000, 100000, 0);
        this.add(widthSlider);
        heightSlider = new JSlider(JSlider.HORIZONTAL, -100000, 100000, 0);
        this.add(heightSlider);

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

        List<JSlider> sliders = Stream.of(centerXSlider, centerYSlider, widthSlider, heightSlider).toList();
        List<JLabel> labels = Stream.of(centerXValue, centerYValue, widthValue, heightValue).toList();

        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(nmp::setCenterX,
                        nmp::setCenterY,
                        nmp::setMapWidth,
                        nmp::setMapWidth
                ));

        IntStream.range(0, sliders.size())
                .boxed()
                .forEach((index) -> {

                    labels.get(index).setText(sliders.get(index).getValue() + "");
                    sliders.get(index).addChangeListener((event) -> {
                        labels.get(index).setText(sliders.get(index).getValue() + "");
                        setters.get(index).accept((float)sliders.get(index).getValue());
                        meui.update();
                    });

                });


        GroupLayout.SequentialGroup hGroup
                = layout.createSequentialGroup();

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(centerXLabel)
                .addComponent(centerYLabel)
                .addComponent(mapWidthLabel)
                .addComponent(mapHeightLabel));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(centerXValue)
                .addComponent(centerYValue)
                .addComponent(widthValue)
                .addComponent(heightValue));

        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(centerXSlider)
                .addComponent(centerYSlider)
                .addComponent(widthSlider)
                .addComponent(heightSlider)
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
