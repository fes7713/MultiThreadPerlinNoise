import Noise.Array.ImageUpdateInterface;
import Noise.Array.PerlinNoiseArray;
import Noise.Array.PerlinNoiseArrayInterface;
import Noise.Array.VariableChanger;
import Noise.FileManager.FileManager;

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

    static int precision = 10000;
    ImageUpdateInterface iui ;

    JSlider centerXSlider;
    JSlider centerYSlider;
    JSlider widthSlider;
    JSlider heightSlider;

    NoiseMapPanel nmp;

    public MapEditor(NoiseMapPanel nmp, ImageUpdateInterface iui){
        GroupLayout layout = new GroupLayout(this);
        if(iui == null)
            throw new IllegalArgumentException("Image update interface cannot be null");
        this.iui = iui;

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

//        centerXSlider = new JSlider(JSlider.HORIZONTAL, -10 * precision, 10 * precision, (int)(PerlinNoiseArray.getNoiseCoefficient() * precision));
//        this.add(centerXSlider);
//        centerYSlider = new JSlider(JSlider.HORIZONTAL, -10 * precision, 10 * precision, (int)(PerlinNoiseArray.getNoiseShift() * precision));
//        this.add(centerYSlider);
//        widthSlider = new JSlider(JSlider.HORIZONTAL, -1 * precision, 1 * precision, (int)(PerlinNoiseArray.getNormalCoefficient() * precision));
//        this.add(widthSlider);
//        heightSlider = new JSlider(JSlider.HORIZONTAL, -50 * precision, 200 * precision, (int)(PerlinNoiseArray.getNormalShift() * precision));
//        this.add(heightSlider);
//
//        JButton saveButton = new JButton("Save");
//        saveButton.addActionListener((event) -> {
//            PerlinNoiseArrayInterface.saveVariables(this);
//        });
//        this.add(saveButton);
//        JButton loadButton = new JButton("Load");
//        loadButton.addActionListener((event)->{
//            String filename = FileManager.askForFileNameFromListInDir(this, "variables", "Select variable file", "Variable load form");
//            PerlinNoiseArrayInterface.loadVariable("variables", filename, this);
//        });
//        this.add(loadButton);
//        JButton cancelButton = new JButton("Cancel");
//        this.add(cancelButton);
//
//        List<JSlider> sliders = Stream.of(centerXSlider, centerYSlider, widthSlider, heightSlider).toList();
//        List<JLabel> labels = Stream.of(centerXValue, centerYValue, widthValue, heightValue).toList();
//
//        List<Consumer<Float>> setters = new ArrayList<>(
//                Arrays.asList(nmp::set,
//                        nmp::setNoiseShift,
//                        nmp::setNormalCoefficient,
//                        nmp::setNormalShift
//                ));
//
//        sliders.forEach((slider) -> {
//            slider.setPaintTicks(true);
//            slider.setMajorTickSpacing(precision * 10);
//        });
//
//        IntStream.range(0, sliders.size())
//                .boxed()
//                .forEach((index) -> {
//
//                    labels.get(index).setText(sliders.get(index).getValue() / (float)precision + "");
//                    sliders.get(index).addChangeListener((event) -> {
//                        labels.get(index).setText(sliders.get(index).getValue() / (float)precision + "");
//                        setters.get(index).accept(sliders.get(index).getValue() / (float)precision);
//                        iui.update();
//                    });
//
//                });
//
//
//        GroupLayout.SequentialGroup hGroup
//                = layout.createSequentialGroup();
//
//        hGroup.addGroup(layout.createParallelGroup()
//                .addComponent(noiseSteepnessLabel)
//                .addComponent(noiseShiftLabel)
//                .addComponent(normalSteepnessLabel)
//                .addComponent(normalShiftLabel));
//
//        hGroup.addGroup(layout.createParallelGroup()
//                .addComponent(centerXValue)
//                .addComponent(centerYValue)
//                .addComponent(widthValue)
//                .addComponent(heightValue));
//
//        hGroup.addGroup(layout.createParallelGroup()
//                .addComponent(centerXSlider)
//                .addComponent(centerYSlider)
//                .addComponent(widthSlider)
//                .addComponent(heightSlider)
//                .addGroup(layout.createSequentialGroup()
//                        .addComponent(saveButton)
//                        .addComponent(loadButton)
//                        .addComponent(cancelButton)));
//
//        layout.setHorizontalGroup(hGroup);
//
//        GroupLayout.SequentialGroup vGroup
//                = layout.createSequentialGroup();
//
//        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//                .addComponent(noiseSteepnessLabel)
//                .addComponent(centerXValue)
//                .addComponent(centerXSlider));
//
//        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//                .addComponent(noiseShiftLabel)
//                .addComponent(centerYValue)
//                .addComponent(centerYSlider));
//
//        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//                .addComponent(normalSteepnessLabel)
//                .addComponent(widthValue)
//                .addComponent(widthSlider));
//
//        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//                .addComponent(normalShiftLabel)
//                .addComponent(heightValue)
//                .addComponent(heightSlider));
//
//        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
//                .addComponent(saveButton)
//                .addComponent(loadButton)
//                .addComponent(cancelButton));
//
//        layout.setVerticalGroup(vGroup);
//    }
//
//    public void showVariableChanger()
//    {
//        JFrame frame = new JFrame("Variable Changer");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.add(this);
//        frame.pack();
//        frame.setVisible(true);
//    }
//
//    public void updateData()
//    {
//        List<JSlider> sliders = Stream.of(centerXSlider, centerYSlider, widthSlider, heightSlider).toList();
//        List<Supplier<Float>> getters = new ArrayList<>(
//                Arrays.asList(PerlinNoiseArray::getNoiseCoefficient,
//                        PerlinNoiseArray::getNoiseShift,
//                        PerlinNoiseArray::getNormalCoefficient,
//                        PerlinNoiseArray::getNormalShift));
//        IntStream.range(0, sliders.size())
//                .boxed()
//                .forEach((index) -> {
//                    sliders.get(index).setValue((int)(getters.get(index).get() * precision));
//                });
    }

    public static void main(String[] args)
    {
        VariableChanger vc = new VariableChanger(()-> {
            System.out.println("Updating image");
        });
        vc.showVariableChanger();

    }
}
